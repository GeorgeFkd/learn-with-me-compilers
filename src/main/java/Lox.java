public class Lox {
    static final Interpreter interpreter = new Interpreter();
    static boolean hadError = false;
    static boolean hadRuntimeError = false;
    static void error(int line, String err){
        System.out.println("Error in line: " + line + "with message: " + err);
    }

    static void error(Token token,String msg){
        if(token.type == TokenType.EOF){
            report(token.line,"at end ",msg);
        } else {
            report(token.line, " at '" + token.lexeme + "'", msg);
        }
    }

    static void report(int line,String location,String err){
        System.out.println("Error in line: " + line + "at " + location + " with message: " + err);
        hadError = true;
    }

    public static void runtimeError(RuntimeError error) {
        System.err.println(error.getMessage() +
                "\n[line " + error.token.line + "]");
        hadRuntimeError = true;
    }
}
