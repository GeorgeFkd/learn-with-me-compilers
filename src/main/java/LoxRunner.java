import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class LoxRunner {

    private Optional<LoxErrorHandler> errorHandler = Optional.empty();

    public LoxRunner withErrorHandler(LoxErrorHandler errorHandler){
        this.errorHandler = Optional.of(errorHandler);
        return this;
    }


    public void runJloxFile(String filename) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(filename));
        run(new String(bytes, Charset.defaultCharset()));
    }

    public void run(String code){
        LoxErrorHandler errorHandler = this.errorHandler.orElse(new LoxStdOutErrorHandler());
        Interpreter interpreter = new Interpreter(errorHandler);
        Lexer lexer = new Lexer(code);
        List<Token> tokens = lexer.scanTokens();
        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();

        if(errorHandler.hadError()) System.exit(65);
        Resolver resolver = new Resolver(interpreter);
        resolver.resolve(statements);
        if(errorHandler.hadError()) return;

        interpreter.interpret(statements);
        if(errorHandler.hadRuntimeError()) System.exit(70);

        System.out.println(statements);
    }

    public static void run(String code, Map<String,Boolean> options){

    }
}
