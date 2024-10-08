import java.util.*;

public class Interpreter implements Expr.Visitor<Object>,Stmt.Visitor<Void>{
    final Environment globals = new Environment();
    private Environment environment = globals;
    private final Map<Expr,Integer> locals = new HashMap<>();
    private final LoxErrorHandler errorHandler;
    private final PrintHandler printHandler;


    void initializeGlobalFunctionsInEnvironment() {
        globals.define("clock", new LoxCallable() {
            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                return (double) System.currentTimeMillis() / 1000.0;
            }

            @Override
            public int arity() {
                return 0;
            }

            @Override
            public String toString() {return "<native fn>";}
        });
    }

    Interpreter(LoxErrorHandler errorHandler,PrintHandler printHandler){
        initializeGlobalFunctionsInEnvironment();
        this.errorHandler = errorHandler;
        this.printHandler = printHandler;
    }

    Interpreter() {
        initializeGlobalFunctionsInEnvironment();
        this.errorHandler = new LoxStdOutErrorHandler();
        this.printHandler = new StdOutPrintHandler();
    }


    void interpret(List<Stmt> statements){
        try {
            for(Stmt s: statements){
                execute(s);
            }
        }catch (RuntimeError e){
            errorHandler.runtimeError(e);
        }
    }
    private void execute(Stmt s){
        if(s != null)s.accept(this);

    }


    private String stringify(Object object){
        if(object == null) return "nil";

        if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }

        if (object instanceof String) {
            return (String)object;
        }

        return object.toString();
    }


    @Override
    public Object visitAssignExpr(Expr.Assign expr) {
        Object value = evaluate(expr.value);
        Integer distance = locals.get(expr);
        if(distance != null) {
            environment.assignAt(distance,expr.name,value);
        }else {
            globals.assign(expr.name,value);
        }
        //environment.assign(expr.name,value);
        return value;
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);
        switch (expr.operator.type) {
            //This is where the intricacies of the language show up
            case MINUS:
                checkNumberOperands(expr.operator,left,right);
                return (double)left - (double) right;
            case SLASH:
                checkNumberOperands(expr.operator,left,right);
                return (double)left / (double)right;
            case STAR:
                //todo if any of those is a number and the other is not
                //implement number * string like python
                checkNumberOperands(expr.operator,left,right);
                return (double)left * (double)right;
            case PLUS:
                if(left instanceof Double && right instanceof Double){
                    return (double)left + (double)right;
                }

                if(left instanceof String && right instanceof String){
                    return (String)left + (String)right;
                }
                //implements "str" + 4 == "str4", 4 + "str" == "4str"
                if(left instanceof String){
                    return left + String.valueOf(right);
                }

                if(right instanceof String){
                    return String.valueOf(left) + right;
                }
                throw new RuntimeError(expr.operator,
                        "Operands must be two numbers or two strings.");
            case GREATER:
                checkNumberOperands(expr.operator,left,right);
                return (double) left > (double) right;
            case GREATER_EQUAL:
                checkNumberOperands(expr.operator,left,right);
                return (double) left >= (double) right;
            case LESS:
                checkNumberOperands(expr.operator,left,right);
                return (double) left < (double) right;
            case LESS_EQUAL:
                checkNumberOperands(expr.operator,left,right);
                return (double) left <= (double) right;
            case BANG_EQUAL:
                return !isEqual(left,right);
            case EQUAL_EQUAL:
                return isEqual(left,right);

        }

        return null;
    }

    private boolean isEqual(Object a,Object b){
        if (a == null && b == null) return true;
        if (a == null) return false;
        return a.equals(b);
    }

    @Override
    public Object visitCallExpr(Expr.Call expr) {
        Object callee = evaluate(expr.callee);
        List<Object> args = new ArrayList<>();
        for(Expr arg: expr.arguments){
            args.add(evaluate(arg));
        }
        if(!(callee instanceof LoxCallable)){
            throw new RuntimeError(expr.paren,"Can only call functions and classes");
        }

        LoxCallable function = (LoxCallable)callee;
        if(args.size() != function.arity()){
            throw new RuntimeError(expr.paren, "Expected " +
                    function.arity() + " arguments but got " +
                    args.size() + ".");
        }
        return function.call(this,args);
    }

    @Override
    public Object visitGetExpr(Expr.Get expr) {
        Object object = evaluate(expr.object);
        if(object instanceof LoxInstance){
            return ((LoxInstance) object).get(expr.name);
        }

        throw new RuntimeError(expr.name,"Only instances have properties");
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.expression);
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitLogicalExpr(Expr.Logical expr) {
        Object left = evaluate(expr.left);

        //short circuiting
        if(expr.operator.type == TokenType.OR){
            if(isTruthy(left)) return left;
        } else {
            if(!isTruthy(left))return left;
        }
        return evaluate(expr.right);
    }

    @Override
    public Object visitSetExpr(Expr.Set expr) {
        //eval object -> raise error -> eval value
        Object object = evaluate(expr.object);

        if(!(object instanceof LoxInstance)){
            throw new RuntimeError(expr.name,"Only instances have fields");
        }

        Object value = evaluate(expr.value);
        ((LoxInstance)object).set(expr.name,value);

        return value;
    }

    @Override
    public Object visitSuperExpr(Expr.Super expr) {
        int distance = locals.get(expr);
        LoxClass superclass = (LoxClass)environment.getAt(distance,"super");
        LoxInstance object = (LoxInstance) environment.getAt(distance-1,"this");
        LoxFunction method = superclass.findMethod(expr.method.lexeme);
        if(method == null) {
            throw new RuntimeError(expr.method,
                    "Undefined property '" + expr.method.lexeme + "'.");
        }

        return method.bind(object);
    }

    @Override
    public Object visitThisExpr(Expr.This expr) {


        return lookupVariable(expr.keyword,expr);
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object right = evaluate(expr.right);
        switch(expr.operator.type) {
            case BANG:
                return !isTruthy(right);
            case MINUS:
                checkNumberOperand(expr.operator,right);
                return -(double)right;
        }

        return null;
    }

    @Override
    public Object visitVariableExpr(Expr.Variable expr) {
        return lookupVariable(expr.name,expr);
    }

    private Object lookupVariable(Token name,Expr expr){
        Integer distance = locals.get(expr);
        if(distance != null){
            return environment.getAt(distance,name.lexeme);
        } else {
            return globals.get(name);
        }
    }



    private Object evaluate(Expr expr){
        return expr.accept(this);
    }

    private boolean isTruthy(Object object){
        //false and nil are falsey, and everything else is truthy
        if(object == null) return false;
        if(object instanceof Boolean) return (boolean)object;
        return true;
    }

    private void checkNumberOperand(Token operator,Object operand){
        if(operand instanceof Double) return;
        throw new RuntimeError(operator,"Operand must be a number.");
    }

    private void checkNumberOperands(Token operator,Object left,Object right){
        if(left instanceof Double && right instanceof Double) return;
        throw new RuntimeError(operator,"Operands must be numbers.");
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        evaluate(stmt.expression);
        return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        Object value = evaluate(stmt.expression);
        printHandler.print(stringify(value));
        return null;
    }

    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        Object value = null;
        if(stmt.initializer != null) {
            value = evaluate(stmt.initializer);
        }

        environment.define(stmt.name.lexeme,value);
        return null;
    }

    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        if(isTruthy(evaluate(stmt.condition))){
            execute(stmt.thenBranch);
        }else if(stmt.elseBranch != null) {
            execute(stmt.elseBranch);
        }
        return null;
    }

    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        executeBlock(stmt.statements, new Environment(environment));
        return null;
    }

    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
        while(isTruthy(evaluate(stmt.condition))){
            execute(stmt.body);
        }
        return null;
    }

    @Override
    public Void visitFunctionStmt(Stmt.Function stmt) {
        LoxFunction function = new LoxFunction(stmt,environment,false);
        environment.define(stmt.name.lexeme,function);
        return null;
    }

    @Override
    public Void visitReturnStmt(Stmt.Return stmt) {
        Object value = null;
        if(stmt.value != null){
            value = evaluate(stmt.value);
        }
        throw new Return(value);
    }

    @Override
    public Void visitClassStmt(Stmt.Class stmt) {
        Object superclass = null;
        if (stmt.superclass != null) {
            superclass = evaluate(stmt.superclass);
            if(!(superclass instanceof LoxClass)){
                throw new RuntimeError(stmt.superclass.name,"Superclass must be a class.");
            }
        }

        environment.define(stmt.name.lexeme,null);

        if(stmt.superclass != null) {
            environment = new Environment(environment);
            environment.define("super",superclass);
        }

        Map<String,LoxFunction> methods = new HashMap<>();
        for(Stmt.Function method: stmt.methods){
            LoxFunction func = new LoxFunction(method,environment,method.name.lexeme.equals("init"));
            methods.put(method.name.lexeme,func);
        }
        LoxClass klass = new LoxClass(stmt.name.lexeme,(LoxClass)superclass,methods);
        if(superclass != null){
            //we pop the environment
            environment = environment.enclosing;
        }

        environment.assign(stmt.name,klass);
        return null;
    }

    protected void executeBlock(List<Stmt> statements, Environment environment) {
        Environment previous = this.environment;
        try {
            this.environment = environment;
            for(Stmt s: statements){
                execute(s);
            }
        } finally {
            this.environment = previous;
        }
    }


    public void resolve(Expr expr, int depth) {
        locals.put(expr,depth);
    }
}
