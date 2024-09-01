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
//    String source = "fun fib(n) {\n" +
//            "  if (n <= 1) return n;\n" +
//            "  return fib(n - 2) + fib(n - 1);\n" +
//            "}\n" +
//            "\n" +
//            "for (var i = 0; i < 20; i = i + 1) {\n" +
//            "  print fib(i);\n" +
//            "}";
//    String source =
//            "for (var b = 1; b < 100; b = b + 1) {\n" +
//            "  print b;\n" +
//            "}";
//    String source = "var a = 1;\n" +
//            "{\n" +
//            "  var a = a + 2;\n" +
//            "  print a;\n" +
//            "}";
//    String source = "class DevonshireCream {\n" +
//            "  serveOn() {\n" +
//            "    return \"Scones\";\n" +
//            "  }\n" +
//            "}\n" +
//            "\n" +
//            "print DevonshireCream; // Prints \"DevonshireCream\".";
//    String source = "class Bacon {\n" +
//            "  eat() {\n" +
//            "    print \"Crunch crunch crunch!\";\n" +
//            "  }\n" +
//            "}\n" +
//            "\n" +
//            "Bacon().eat(); // Prints \"Crunch crunch crunch!\".";
    String source = "class Doughnut {\n" +
            "  cook() {\n" +
            "    print \"Fry until golden brown.\";\n" +
            "  }\n" +
            "}\n" +
            "\n" +
            "class BostonCream < Doughnut {}\n" +
            "\n" +
            "BostonCream().cook();";
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
    Resolver resolver = new Resolver(Lox.interpreter);
    resolver.resolve(statements);
    if(Lox.hadError) return;

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
