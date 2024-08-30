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
    String source = "< < <= >= != ! . - + /* " +
            "hello world" +
            "*/ -";
    run(source);
  }


  private static void runJloxFile(String filename) throws IOException {
    byte[] bytes = Files.readAllBytes(Paths.get(filename));
    run(new String(bytes, Charset.defaultCharset()));
  }

  private static void run(String code){
    Scanner scanner = new Scanner(code);
    List<Token> tokens = scanner.scanTokens();

    for(Token t: tokens) {
      System.out.println(t);
    }
  }

  private static void runPrompt() {

  }

}
