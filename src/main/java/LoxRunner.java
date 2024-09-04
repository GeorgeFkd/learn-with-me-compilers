import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class LoxRunner {

    private Optional<LoxErrorHandler> errorHandler = Optional.empty();
    private Optional<PrintHandler> printHandler = Optional.empty();
    public LoxRunner withErrorHandler(LoxErrorHandler errorHandler){
        this.errorHandler = Optional.of(errorHandler);
        return this;
    }

    public LoxRunner withPrintHandler(PrintHandler printHandler){
        this.printHandler = Optional.of(printHandler);
        return this;
    }


    public void runJloxFile(String filename) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(filename));
        run(new String(bytes, Charset.defaultCharset()));
    }

    public void run(String code){
        LoxErrorHandler errorHandler = this.errorHandler.orElse(new LoxStdOutErrorHandler());
        PrintHandler printHandler = this.printHandler.orElse(new StdOutPrintHandler());
        Interpreter interpreter = new Interpreter(errorHandler,printHandler);
        Lexer lexer = new Lexer(code,errorHandler);
        List<Token> tokens = lexer.scanTokens();
        Parser parser = new Parser(tokens,errorHandler);
        List<Stmt> statements = parser.parse();

        if(errorHandler.hadError()) System.exit(65);
        Resolver resolver = new Resolver(interpreter);
        resolver.resolve(statements);
        if(errorHandler.hadError()) return;

        interpreter.interpret(statements);
        if(errorHandler.hadRuntimeError()) System.exit(70);
    }

    public static void run(String code, Map<String,Boolean> options){

    }
}
