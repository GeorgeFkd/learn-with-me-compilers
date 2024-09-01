import java.util.ArrayList;
import java.util.List;

public class LoxStdOutErrorHandler extends LoxErrorHandler {
    static boolean hadError = false;
    static boolean hadRuntimeError = false;


    public void error(int line, String err){
        String msg = "Error in line: " + line + "with message: " + err;
        System.out.println(msg);
        errorMessages.add(msg);
    }

    public void error(Token token,String msg){
        if(token.type == TokenType.EOF){
            report(token.line,"at end ",msg);
        } else {
            report(token.line, " at '" + token.lexeme + "'", msg);
        }
    }

    public void report(int line,String location,String err){
        String msg = "Error in line: " + line + "at " + location + " with message: " + err;
        System.out.println(msg);
        errorMessages.add(msg);
        hadError = true;
    }

    public void runtimeError(RuntimeError error) {
        String msg = error.getMessage() +
                "\n[line " + error.token.line + "]";
        System.err.println(msg);
        errorMessages.add(msg);
        hadRuntimeError = true;
    }
}
