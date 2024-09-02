public class StdOutPrintHandler extends PrintHandler{
    @Override
    public void print(String message) {
        this.stdOutMessages.add(message);
        System.out.println(message);
    }

    StdOutPrintHandler() {
        super();
    }
}
