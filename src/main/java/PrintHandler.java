import java.util.ArrayList;
import java.util.List;

public abstract class PrintHandler {

    protected List<String> stdOutMessages = new ArrayList<>();

    public List<String> getStdOutMessages() { return stdOutMessages; }

    abstract public void print(String message);


}
