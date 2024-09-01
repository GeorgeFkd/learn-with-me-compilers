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
    @BeforeTest
    public void freshRunnerInstance(){
        errorHandler = new LoxStdOutErrorHandler();
        runner = new LoxRunner().withErrorHandler(errorHandler);
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

    @Test(testName="While statements work properly")
    public void testWhileStatements() {

    }

    @Test(testName="For statements work properly")
    public void testForStatements() {

    }

    @Test(testName="Functions work properly")
    public void testFunctions() {
        String source = "fun fib(n) {\n" +
                "  if (n <= 1) return n;\n" +
                "  return fib(n - 2) + fib(n - 1);\n" +
                "}\n" +
                "\n" +
                "print fib(10);";
        runner.run(source);
        assertNoErrors("Simple Fibonacci Function Declaration and Definition");

    }

    public void assertNoErrors(String caseDescription){
        Assert.assertTrue(errorHandler.getErrorMessages().isEmpty(),"For case: " + caseDescription + "should not contain errors");
    }

    @Test(testName="Classes work properly")
    public void testClasses() {
        //constructors
        //inheritance
        //fields
        //methods
        //this, super
    }

        String source = "fun fib(n) {\n" +
            "  if (n <= 1) return n;\n" +
            "  return fib(n - 2) + fib(n - 1);\n" +
            "}\n" +
            "\n" +
            "for (var i = 0; i < 20; i = i + 1) {\n" +
            "  print fib(i);\n" +
            "}";
//    String source =
//            "for (var b = 1; b < 100; b = b + 1) {\n" +
//            "  print b;\n" +
//            "}";
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
//    String source = "class Bacon {\n" +
//            "  eat() {\n" +
//            "    print \"Crunch crunch crunch!\";\n" +
//            "  }\n" +
//            "}\n" +
//            "\n" +
//            "Bacon().eat(); // Prints \"Crunch crunch crunch!\".";
//    String source = "class Doughnut {\n" +
//            "  cook() {\n" +
//            "    print \"Fry until golden brown.\";\n" +
//            "  }\n" +
//            "}\n" +
//            "\n" +
//            "class BostonCream < Doughnut {}\n" +
//            "\n" +
//            "BostonCream().cook();";



}
