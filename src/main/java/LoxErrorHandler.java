import java.util.ArrayList;
import java.util.List;

abstract class LoxErrorHandler {


    abstract void error(int line, String err);
    abstract void error(Token token,String msg);
    abstract void report(int line,String location,String err);
    abstract void runtimeError(RuntimeError error);
    protected List<String> errorMessages = new ArrayList<>();

    public List<String> getErrorMessages() { return errorMessages;}
    private boolean hadError = false;
    private boolean hadRuntimeError = false;

    public boolean hadError() {return hadError;}
    public boolean hadRuntimeError() {return hadRuntimeError;}
}
