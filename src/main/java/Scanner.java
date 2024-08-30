import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scanner {

    private final String sourceCode;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    private static final Map<String,TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("and",TokenType.AND);
        keywords.put("class",TokenType.CLASS);
        keywords.put("else",TokenType.ELSE);
        keywords.put("false",TokenType.FALSE);
        keywords.put("for",    TokenType.FOR);
        keywords.put("fun",    TokenType.FUN);
        keywords.put("if",     TokenType.IF);
        keywords.put("null",    TokenType.NIL);
        keywords.put("or",     TokenType.OR);
        keywords.put("print",  TokenType.PRINT);
        keywords.put("return", TokenType.RETURN);
        keywords.put("super",  TokenType.SUPER);
        keywords.put("this",   TokenType.THIS);
        keywords.put("true",   TokenType.TRUE);
        keywords.put("var",    TokenType.VAR);
        keywords.put("while",  TokenType.WHILE);
    }


    Scanner(String sourceCode){
        this.sourceCode = sourceCode;
    }

    List<Token> scanTokens() {
        while(!isAtEnd()){
            start = current;
            scanToken();
        }
        tokens.add(new Token(TokenType.EOF,"",null,line));
        return tokens;
    }
    /*

     */
    private boolean match(char expected){
        if(isAtEnd()) return false;
        if(sourceCode.charAt(current) != expected) return false;
        current++;
        return true;
    }

    /*
    NUMBER -> DIGIT+ ( "." DIGIT+ )? ;
    STRING -> "\"" <any char except "\"" >* "\"";
    IDENTIFIER -> ALPHA ( ALPHA | DIGIT )* ;
    ALPHA -> "a" ... "z" | "A" ... "Z" | "_"
    DIGIT -> "0" ... "9"

     */
    private void scanToken() {
        //principle of maximal munch:
        //When two lexical grammar rules can both match a chunk of code that the scanner is looking at, whichever one matches the most characters wins.
        char c = advance();
        switch (c) {
            //we do those first as they dont have dependencies on other characters
            //being parsed
            case '(' : addToken(TokenType.LEFT_PAREN);break;
            case ')' : addToken(TokenType.RIGHT_PAREN);break;
            case '{' : addToken(TokenType.LEFT_BRACE); break;
            case '}' : addToken(TokenType.RIGHT_BRACE); break;
            case ',' : addToken(TokenType.COMMA); break;
            case '.' : addToken(TokenType.DOT); break;
            case '-' : addToken(TokenType.MINUS); break;
            case '+' : addToken(TokenType.PLUS); break;
            case ';' : addToken(TokenType.SEMICOLON); break;
            case '*' : addToken(TokenType.STAR); break;
            case '!' :
                addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG);
                break;
            case '<' :
                addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS);
                break;
            case '=':
                addToken(match('=') ? TokenType.EQUAL_EQUAL: TokenType.EQUAL);
                break;
            case '>':
                addToken(match('=') ? TokenType.GREATER_EQUAL: TokenType.GREATER);
                break;
            case '/':
                if(match('/')) {
                    //comments go until the end of the line
                    while(peek() != '\n' && !isAtEnd()) advance();
                } else if (match('*')) {
                    //we have a multiline comment
                    while(peek() != '*' && peekNext() !='/' && !isAtEnd()) {
                        if(peek() == '\n') line++;
                        advance();
                    }
                    //gotta advance the two characters found
                    advance(); advance();
                } else {
                    addToken(TokenType.SLASH);
                }

                break;
            case ' ':
            case '\r':
            case '\t':
                //ignore whitespace
                break;
            case '\n':
                line++;
                break;
            case '"':
                string();
                break;
            default:
                if (isDigit(c)) {
                    number();
                }else if(isAlpha(c)){
                    identifier();
                } else {
                    Lox.error(line,"Unexpected character");
                }
                break;
        }
    }

    private void identifier() {
        //we have already checked that the first character isAlpha(c)
        //that's why we can use isAlphaNumeric
        while (isAlphaNumeric(peek())) advance();
        //We check if it is a keyword and add the correct TokenType
        String text = sourceCode.substring(start,current);
        TokenType type = keywords.get(text);
        if(type == null) type = TokenType.IDENTIFIER;
        addToken(type);
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private boolean isAlpha(char c){
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_';
    }
    private void number() {
        while(isDigit(peek())){
            advance();
        }

        //todo if the peekNext digit is not a digit we could add some extra error handling
        if(peek() == '.'  && isDigit(peekNext())){
            //consuming the "."
            advance();

            while (isDigit(peek())) advance();
        }

        addToken(TokenType.NUMBER,Double.parseDouble(sourceCode.substring(start,current)));

    }
    private char peekNext() {
        //lookahead of two characters
        //we dont want to allow arbitrarily far lookahead,(i don't know exactly why yet)
        if (current + 1 >= sourceCode.length()) return '\0';
        return sourceCode.charAt(current + 1);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c<='9';
    }
    private void string(){
        while(peek() != '"' && !isAtEnd()){
          //we support multiline string
          if(peek() == '\n') line++;
          advance();
        }

        if(isAtEnd()){
          Lox.error(line,"Unterminated string");
          return;
        }

        advance();
        //skipping the opening and closing string quotes
        //also this is where we could unescape characters
        //escaping is: some chars have special meaning,
        //if we escape them no special meaning otherwise use special meaning
        String value = sourceCode.substring(start+1,current-1);
        addToken(TokenType.STRING,value);
    }

    private char peek() {
        if(isAtEnd())return '\0';
        //lookahead
        return sourceCode.charAt(current);
    }

    private void addToken(TokenType tokenType) {
        addToken(tokenType,null);
    }

    private void addToken(TokenType type, Object literal){
        String text = sourceCode.substring(start,current);
        tokens.add(new Token(type,text,literal,line));
    }

    private char advance(){
        return sourceCode.charAt(current++);
    }

    boolean isAtEnd() {
        return current >= sourceCode.length();
    }
}
