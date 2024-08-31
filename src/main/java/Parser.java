import java.util.ArrayList;
import java.util.List;
import java.util.List;


public class Parser {
    private final List<Token> tokens;
    private int current = 0;
    private TokenType t = TokenType.EOF;
    private static class ParseError extends RuntimeException{};
    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    //The parser is the same vibe with the Lexer(Scanner), it just works
    //on tokens


    private Expr expression() {
        return assignment();
    }

    private Expr assignment() {
        Expr expr = equality();

        //did not understand this section that match
        //instead of looping we got recursion
        if(match(TokenType.EQUAL)){
            Token equals = previous();
            Expr value = assignment();

            if(expr instanceof Expr.Variable) {
                Token name = ((Expr.Variable)expr).name;
                return new Expr.Assign(name,value);
            }

            error(equals,"Invalid assignment target.");
            return null;
        } else {
            return expr;
        }
    }

    private Expr equality() {
        //equality → comparison ( ( "!=" | "==" ) comparison )* ;

        Expr expr = comparison();
        while(match(TokenType.BANG_EQUAL,TokenType.EQUAL_EQUAL)){
            Token operator = previous();
            Expr right = comparison();
            //we do this so we can make a big nested expression that has
            //the previous ones as the left operand
            expr = new Expr.Binary(expr,operator,right);
        }
        return expr;
    }
    private Expr comparison() {
        //comparison     → term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
        Expr expr = term();
        while(match(TokenType.GREATER,TokenType.GREATER_EQUAL,TokenType.LESS,TokenType.LESS_EQUAL)){
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr,operator,right);
        }

        return expr;
    }

    private Expr term() {
        //term → factor ( ( "-" | "+" ) factor )* ;
        Expr expr = factor();
        while(match(TokenType.MINUS,TokenType.PLUS)){
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr,operator,right);
        }
        return expr;
    }

    private Expr factor() {
        Expr expr = unary();

        while(match(TokenType.SLASH,TokenType.STAR)){
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr,operator,right);
        }
        return expr;
    }

    List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();
        while(!isAtEnd()){
            statements.add(declaration());
        }

        return statements;
    }

    private Stmt declaration() {
        try {
            if (match(TokenType.VAR))return varDeclaration();
            return statement();
        } catch(ParseError error){
            synchronize();
            return null;
        }
    }

    private Stmt varDeclaration() {
        Token name = consume(TokenType.IDENTIFIER,"Expected variable name");
        Expr initializer = null;
        if(match(TokenType.EQUAL)){
            initializer = expression();
        }

        consume(TokenType.SEMICOLON,"Expect ';' after variable declaration");
        return new Stmt.Var(name,initializer);
    }

    private Stmt statement() {
        if(match(TokenType.PRINT)) return printStatement();
        return expressionStatement();
    }

    private Stmt printStatement(){
        Expr value = expression();
        consume(TokenType.SEMICOLON,"Expect ';' after value");
        return new Stmt.Print(value);
    }

    private Stmt expressionStatement() {
        Expr expr = expression();
        consume(TokenType.SEMICOLON,"Expected ';' after value");
        return new Stmt.Expression(expr);
    }

    private Expr unary() {
        //unary → ( "!" | "-" ) unary
        //        | primary ;
        if(match(TokenType.BANG,TokenType.MINUS)){
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator,right);
        }


        return primary();
    }

    private Expr primary() {
        if(match(TokenType.FALSE)) return new Expr.Literal(false);
        if(match(TokenType.TRUE)) return new Expr.Literal(true);
        if(match(TokenType.NIL)) return new Expr.Literal(null);

        if(match(TokenType.NUMBER,TokenType.STRING)){
            return new Expr.Literal(previous().literal);
        }

        if(match(TokenType.IDENTIFIER)){
            return new Expr.Variable(previous());
        }

        if(match(TokenType.LEFT_PAREN)){
            Expr expr = expression();
            consume(TokenType.RIGHT_PAREN,"Expect ')' after expression");
            return new Expr.Grouping(expr);
        }
        throw error(peek(),"Expect expression.");
    }

    private Token consume(TokenType type, String msg){
        if(check(type)) return advance();

        throw error(peek(),msg);
    }

    //When we want to synchronize, we throw that ParseError object.
    private ParseError error(Token token, String msg){
        Lox.error(token,msg);
        return new ParseError();
    }

    private void synchronize() {
        advance();
        //We want to discard tokens until we’re right at the beginning of the next statement.
        while (!isAtEnd()){
            if(previous().type == TokenType.SEMICOLON) return;
            switch(peek().type){
                case CLASS:
                case FUN:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                    return;
            }
            //It discards tokens until it thinks it has found a statement boundary.
            advance();
        }
    }




    private boolean match(TokenType ...tokenTypes) {
        for (TokenType type : tokenTypes){
            if(check(type)){
                advance();
                return true;
            }
        }
        return false;
    }

    private boolean check(TokenType t){
        if(isAtEnd()) return false;
        return peek().type.equals(t);
    }

    private Token advance() {
        if(!isAtEnd()) current++;
        return previous();
    }

    private Token peek() {
        return tokens.get(current);
    }

    private boolean isAtEnd() {
        return peek().type == TokenType.EOF;
    }
    private Token previous() {
        return tokens.get(current-1);
    }
}
