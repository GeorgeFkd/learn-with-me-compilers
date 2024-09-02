import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

@Test(suiteName = "Compiler Tests")
public class LoxTests {


    /*
    Compiler features:
    -> tokens and lexing,
    -> abstract syntax trees,
    recursive descent parsing,
    prefix and infix expressions,
    runtime representation of objects,
    interpreting code using the Visitor pattern,
    lexical scope,
    environment chains for storing variables,
    control flow,
    functions with parameters,
    closures,
    static variable resolution and error detection,
    classes,
    constructors,
    fields,
    methods, and finally,
    inheritance.

     */
    LoxRunner runner = new LoxRunner();
    LoxErrorHandler errorHandler = new LoxStdOutErrorHandler();
    PrintHandler printHandler = new StdOutPrintHandler();
    @BeforeTest(alwaysRun = true)
    public void freshRunnerInstance(){
        errorHandler = new LoxStdOutErrorHandler();
        printHandler = new StdOutPrintHandler();
        runner = new LoxRunner().withErrorHandler(errorHandler).withPrintHandler(printHandler);
    }

    private void runSourceCode(String code){
        runner.run(code);
    }

    private void assertNoErrors(String caseDescription){
        Assert.assertTrue(errorHandler.getErrorMessages().isEmpty(),caseDescription + " should not contain errors");
    }

    private void assertPrintMessagesNonEmpty(String caseDescription){
        Assert.assertTrue(!printHandler.getStdOutMessages().isEmpty(), caseDescription + " should produce a message");
    }

    private void assertMessagesExist(String caseDescription,String ...messages){
        for(String m: messages){
            Assert.assertTrue(printHandler.getStdOutMessages().stream().anyMatch(msg->msg.contains(m)),caseDescription + " should contain the messages: " + messages);
        }
    }



    @Test(testName="Lexer works as expected, simple happy path")
    public void testLexer() {
        String sourceCode = ". and or while if";

    }

    @Test(testName="Parser works as expected, simple happy path")
    public void testParser() {

    }

    @Test(testName="If Statements work properly")
    public void testIfStatements(){

    }

    @Test(testName="Print statements work properly")
    public void testPrintStatements() {
        String caseDescription = "Print Statements";
        String sourceCode = "var a = 5; print(a);";
        runSourceCode(sourceCode);
        assertNoErrors(caseDescription);
        assertPrintMessagesNonEmpty(caseDescription);
        assertMessagesExist(caseDescription,"5");
    }

    @Test(testName="While statements work properly")
    public void testWhileStatements() {

    }

    @Test(testName="For statements work properly")
    public void testForStatements() {
        String caseDescription = "<For statements>";
        String sourceCode = "for(var i = 0; i < 5; i = i + 1)\n" +
                "print(i);";
        runSourceCode(sourceCode);
        assertNoErrors(caseDescription);
        assertPrintMessagesNonEmpty(caseDescription);
        assertMessagesExist(caseDescription,"0","1","2","3","4");
    }

    @Test(testName="Functions work properly")
    public void testFunctions() {
        String caseDescription = "Simple Fibonacci Function Declaration and Definition";
        String source = "fun fib(n) {\n" +
                "  if (n <= 1) return n;\n" +
                "  return fib(n - 2) + fib(n - 1);\n" +
                "}\n" +
                "\n" +
                "print fib(10);";
        runSourceCode(source);
        assertNoErrors(caseDescription);
        assertMessagesExist(caseDescription,"55");
    }





    private String getStdOutMsgAtIndex(int position){
        return printHandler.getStdOutMessages().get(position);
    }

    @Test(testName="Math Expressions work properly")
    public void testMathExpressions() {

    }


    @Test(testName="Inheritance works properly")
    public void testInheritance() {
        String inheritanceCase = "Inheritance";
        String sourceCode = "class Doughnut {\n" +
                "  cook() {\n" +
                "    print \"Fry until golden brown.\";\n" +
                "  }\n" +
                "}\n" +
                "\n" +
                "class BostonCream < Doughnut {}\n" +
                "\n" +
                "BostonCream().cook();";
        runSourceCode(sourceCode);
        assertNoErrors(inheritanceCase);
        assertPrintMessagesNonEmpty(inheritanceCase);
        assertMessagesExist("Fry until golden brown");


        //constructors
        //fields
        //methods
        //this, super
    }

    @Test(testName = "Methods work properly")
    public void testMethods() {
        String caseDescription = "Methods on classes";
        String sourceCode = "class Bacon {\n" +
                "  eat() {\n" +
                "    print \"Crunch crunch crunch!\";\n" +
                "  }\n" +
                "}\n" +
                "\n" +
                "Bacon().eat(); // Prints \"Crunch crunch crunch!\".";
        runner.run(sourceCode);
        assertNoErrors(caseDescription);
        assertPrintMessagesNonEmpty(caseDescription);
        assertMessagesExist(caseDescription,"Crunch crunch crunch!");

    }

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
    String source = "class Bacon {\n" +
            "  eat() {\n" +
            "    print \"Crunch crunch crunch!\";\n" +
            "  }\n" +
            "}\n" +
            "\n" +
            "Bacon().eat(); // Prints \"Crunch crunch crunch!\".";




}
