import java.util.List;
import java.util.List;


public class Parser {
    private final List<Token> tokens;
    private int current = 0;
    private TokenType t = TokenType.EOF;
    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }
}
