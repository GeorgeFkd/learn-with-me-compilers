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
    Lexer lexer = new Lexer("");
    LoxErrorHandler errorHandler = new LoxStdOutErrorHandler();
    PrintHandler printHandler = new StdOutPrintHandler();
    @BeforeTest(alwaysRun = true)
    public void freshRunnerInstance(){
        errorHandler = new LoxStdOutErrorHandler();
        printHandler = new StdOutPrintHandler();
        runner = new LoxRunner().withErrorHandler(errorHandler).withPrintHandler(printHandler);

    }

    private void runSourceCode(String code){
        if(code.isEmpty()) Assert.fail("No Code was provided");
        runner.run(code);
    }

    private void assertErrorsExist(String caseDescription) {
        Assert.assertFalse(errorHandler.getErrorMessages().isEmpty(),caseDescription + " should contain errors");
    }

    private void assertNoErrors(String caseDescription){
        Assert.assertTrue(errorHandler.getErrorMessages().isEmpty(),caseDescription + " should not contain errors");
    }

    private void assertPrintMessagesNonEmpty(String caseDescription){
        Assert.assertTrue(!printHandler.getStdOutMessages().isEmpty(), caseDescription + " should produce a message");
    }

    private void assertMessagesExist(String caseDescription,String ...messages){
        //means a mistake was made
        if(messages.length == 0) Assert.fail();
        for(String m: messages){
            Assert.assertTrue(printHandler.getStdOutMessages().stream().anyMatch(msg->msg.contains(m)),caseDescription + " should contain the messages: " + messages);
        }
    }



    @Test(testName="Lexer works as expected, simple happy path")
    public void testLexer() {
        String sourceCode = ". and or while if";
//        lexer.scanTokens()

    }

    @Test(testName = "Functions can capture outside closures")
    public void testFunctionClosures(){
        String caseDescription = "Functions and Closures: Simple Case";
        String sourceCode = "var a = 5; fun addFive(b) { return a + b; } print(addFive(4));";
        runSourceCode(sourceCode);
        assertNoErrors(caseDescription);
        assertPrintMessagesNonEmpty(caseDescription);
        assertMessagesExist(caseDescription,"9");
        System.out.println(System.getProperty("java.ext.dirs"));
    }

    @Test(testName = "Nested Functions and Closures")
    public void testNestedFunctions() {
        String caseDescr = "Nested Functions with closures";
        String sourceCode = "fun run(a) { var b = 1; fun addB(c) { return c + b + a; }  return addB(5); } print(run(1));";
        runSourceCode(sourceCode);
        assertNoErrors(caseDescr);
        assertPrintMessagesNonEmpty(caseDescr);
        assertMessagesExist(caseDescr,"7");
    }

    @Test(testName="Semicolon omission produces error")
    public void testSemicolons(){
        String caseDescr = "<Semicolons>";
        String sourceCode = "var a = 1";
        runSourceCode(sourceCode);
        assertErrorsExist(caseDescr);
    }

    @Test(testName="Parser works as expected, simple happy path")
    public void testParser() {

    }

    @Test(testName="If Statements work properly")
    public void testIfStatements(){
        String caseDescr = "<If Statements>";
        String sourceCode = "if(true) print(4); else print(3);";
        runSourceCode(sourceCode);
        assertNoErrors(caseDescr);
        assertPrintMessagesNonEmpty(caseDescr);
        assertMessagesExist(caseDescr,"4");
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
        String caseDescr = "<While> statements";
        String sourceCode = "var a = 1; while(a < 5) { print(a); a = a + 1; }";
        runSourceCode(sourceCode);
        assertNoErrors(caseDescr);
        assertPrintMessagesNonEmpty(caseDescr);
        assertMessagesExist(caseDescr,"1","2","3","4");
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
        //addition,multiplication,division,grouping,exponentiation
        String caseDescr = "Mathematical Expressions";
        String sourceCode = "var a = 3 + 8 ; print(a) ; var b = 3 * 8; print(b) ; var c = 12/3 ; print(c) ; var d = 10 - 4; print(d) ;";
        runSourceCode(sourceCode);
        assertNoErrors(caseDescr);
        assertPrintMessagesNonEmpty(caseDescr);
        assertMessagesExist(caseDescr,"11","24","4","6");

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
        assertMessagesExist(inheritanceCase,"Fry until golden brown");


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

    @Test(testName = "Class Declaration works properly")
    public void testClassDecl() {
        String caseDescr = "<Class Statement>";
        String sourceCode = "class DevonshireCream {\n" +
                "  serveOn() {\n" +
                "    return \"Scones\";\n" +
                "  }\n" +
                "}\n" +
                "\n" +
                "print DevonshireCream; // Prints \"DevonshireCream\"";
        runSourceCode(sourceCode);
        assertNoErrors(caseDescr);
        assertPrintMessagesNonEmpty(caseDescr);
        assertMessagesExist(caseDescr,"DevonshireCream");
    }

    @Test(testName= "Scoping works properly")
    public void testScoping(){
        String caseDescr = "<Scopes and Closures>";
        String sourceCode = "var b = 1;\n" +
                "{\n" +
                "  var a = b + 2;\n" +
                "  print a;\n" +
                "}";
        runSourceCode(sourceCode);
        assertNoErrors(caseDescr);
        assertPrintMessagesNonEmpty(caseDescr);
        assertMessagesExist(caseDescr,"3");
    }

    @Test(testName="Referencing a variable in its initializer produces an error")
    public void testInitRefProducesError() {
        String caseDescr = "Variable Reference edge case";
        //this produces an error as we can't ref a variable in its initializer
        String source = "var a = 1;\n" +
                "{\n" +
                "  var a = a + 2;\n" +
                "  print a;\n" +
                "}";
        runSourceCode(source);
        assertErrorsExist(caseDescr);
    }





}
