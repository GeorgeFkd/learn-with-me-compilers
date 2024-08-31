import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {
 /*
  program -> declaration* EOF;
  declaration -> classDecl
               | funDecl
               | varDecl
               | statement ;
   classDecl -> "class" IDENTIFIER ( "<" IDENTIFIER )?
                "{" function* "}" ;
   funDecl -> "fun" function ;
   varDecl -> "var" IDENTIFIER ( "=" expression )? ";" ;
   statement -> exprStmt
              | forStmt
              | ifStmt
              | printStmt
              | returnStmt
              | whileStmt
              | block ;
   exprStmt -> expression ";" ;
   forStmt -> "for" "(" ( varDecl | exprStmt | ";" )
                        expression? ";"
                        expression? ")" statement ;
   ifStmt -> "if" "(" expression ")" statement
              ( "else" statement )? ;
   printStmt -> "print" expression ";" ;
   whileStmt -> "while" "(" expression ")" statement;
   block -> "{" declaration* "}";

   expression -> assignment;
   assignment -> ( call "." )? IDENTIFIER "=" assignment
               | logic_or
   logic_or -> logic_and ( "or" logic_and )* ;
   logic_and -> equality ( "and" equality)* ;
   equality -> comparison ( ( "!=" | "==" ) comparison )* ;
   comparison -> term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
   term -> factor ( ( "-" | "+" ) factor )* ;
   factor -> unary ( ( "/" | "* " ) unary )* ;

   unary -> ( "!" | "-" ) unary | call ;
   call -> primary ( "(" arguments? ")" | "." IDENTIFIER )* ;
   primary -> "true" | "false" | "null" | "this"
            | NUMBER | STRING | IDENTIFIER | "(" expression ")"
            | "super" "." IDENTIFIER ;

   function -> IDENTIFIER "(" parameters? ")" block ;
   parameters -> IDENTIFIER ( "," IDENTIFIER )* ;
   arguments -> expression ( "," expression )* ;

   NUMBER -> DIGIT+ ( "." DIGIT+ )? ;
   STRING -> "\"" <any char except "\"" >* "\"";
   IDENTIFIER -> ALPHA ( ALPHA | DIGIT )* ;
   ALPHA -> "a" ... "z" | "A" ... "Z" | "_"
   DIGIT -> "0" ... "9"
  */


  public static void main(String[] args) throws IOException{
//    if(args.length > 1) {
//      System.out.println("Usage: jlox [script]");
//      System.exit(64);
//    } else if (args.length == 1) {
//      runJloxFile(args[0]);
//    } else {
//      runPrompt();
//    }
    String source = "var a = \"global a\";\n" +
            "var b = \"global b\";\n" +
            "var c = \"global c\";\n" +
            "{\n" +
            "  var a = \"outer a\";\n" +
            "  var b = \"outer b\";\n" +
            "  {\n" +
            "    var a = \"inner a\";\n" +
            "    print a;\n" +
            "    print b;\n" +
            "    print c;\n" +
            "  }\n" +
            "  print a;\n" +
            "  print b;\n" +
            "  print c;\n" +
            "}\n" +
            "print a;\n" +
            "print b;\n" +
            "print c;";
    run(source);
    Lox.hadError = false;
  }


  private static void runJloxFile(String filename) throws IOException {
    byte[] bytes = Files.readAllBytes(Paths.get(filename));
    run(new String(bytes, Charset.defaultCharset()));
  }

  private static void run(String code){
    Lexer lexer = new Lexer(code);
    List<Token> tokens = lexer.scanTokens();
    Parser parser = new Parser(tokens);
    List<Stmt> statements = parser.parse();

    if(Lox.hadError) System.exit(65);
    Lox.interpreter.interpret(statements);
    if(Lox.hadRuntimeError) System.exit(70);

    System.out.println(statements);
//
//    for(Token t: tokens) {
//      System.out.println(t);
//    }
  }

  private static void runPrompt() {

  }

}
