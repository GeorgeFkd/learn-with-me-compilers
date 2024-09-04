import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Resolver implements Expr.Visitor<Void>,Stmt.Visitor<Void>{
    private final Interpreter interpreter;
    private final Stack<Map<String,Boolean>> scopes = new Stack<>();
    private LoxErrorHandler errorHandler = new LoxStdOutErrorHandler();
    private FunctionType currentFunction = FunctionType.NONE;

    private enum FunctionType {
        NONE,
        FUNCTION,
        INITIALIZER,
        METHOD,

    }

    private enum ClassType {
        NONE,
        CLASS,
        SUBCLASS
    }

    private ClassType currentClass = ClassType.NONE;


    public Resolver(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    public Resolver(Interpreter interpreter,LoxErrorHandler errorHandler){
        this.interpreter = interpreter;
        this.errorHandler = errorHandler;
    }

    @Override
    public Void visitAssignExpr(Expr.Assign expr) {
        resolve(expr.value);
        resolveLocal(expr,expr.name);
        return null;
    }

    @Override
    public Void visitBinaryExpr(Expr.Binary expr) {
        resolve(expr.left);
        resolve(expr.right);
        return null;
    }

    @Override
    public Void visitCallExpr(Expr.Call expr) {
        resolve(expr.callee);

        for (Expr argument : expr.arguments) {
            resolve(argument);
        }
        return null;
    }

    @Override
    public Void visitGetExpr(Expr.Get expr) {
        resolve(expr.object);
        return null;
    }

    @Override
    public Void visitGroupingExpr(Expr.Grouping expr) {
        resolve(expr.expression);
        return null;
    }

    @Override
    public Void visitLiteralExpr(Expr.Literal expr) {
        return null;
    }

    @Override
    public Void visitLogicalExpr(Expr.Logical expr) {
        resolve(expr.left);
        resolve(expr.right);
        return null;
    }

    @Override
    public Void visitSetExpr(Expr.Set expr) {
        resolve(expr.value);
        resolve(expr.object);
        return null;
    }

    @Override
    public Void visitSuperExpr(Expr.Super expr) {
        if(currentClass == ClassType.NONE){
            errorHandler.error(expr.keyword,"Can't use 'super' in a class with no superclass.");
        } else if (currentClass != ClassType.SUBCLASS){
            errorHandler.error(expr.keyword,"Can't use 'super' in a class with no superclass");
        }

        resolveLocal(expr,expr.keyword);
        return null;
    }

    @Override
    public Void visitThisExpr(Expr.This expr) {
        if(currentClass == ClassType.NONE) {
            errorHandler.error(expr.keyword,"Can't use 'this' outside of a class");
            return null;
        }
        resolveLocal(expr,expr.keyword);
        return null;
    }

    @Override
    public Void visitUnaryExpr(Expr.Unary expr) {
        resolve(expr.right);
        return null;
    }

    @Override
    public Void visitVariableExpr(Expr.Variable expr) {
        //this condition basically says if the variable is declared but not resolved
        //that means we are trying to do sth like var a = a + 2
        if(!scopes.isEmpty() && scopes.peek().get(expr.name.lexeme) == Boolean.FALSE){
            errorHandler.error(expr.name,"Can't read local variable in its own initializer.");
        }

        resolveLocal(expr,expr.name);
        return null;
    }

    private void resolveLocal(Expr expr, Token name) {
        for(int i = scopes.size() -1 ; i >= 0; i--){
            //we want to see how many hops away the variable is
            //in terms of environments, starting from the innermost
            if (scopes.get(i).containsKey(name.lexeme)){
                interpreter.resolve(expr,scopes.size() - 1 - i);
                return ;
            }
        }
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        resolve(stmt.expression);
        return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        resolve(stmt.expression);
        return null;
    }

    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        declare(stmt.name);
        if(stmt.initializer != null){
            resolve(stmt.initializer);
        }
        define(stmt.name);
        return null;
    }

    private void declare(Token name){
        if(scopes.isEmpty()) return;
        Map<String,Boolean> scope = scopes.peek();
        if(scope.containsKey(name.lexeme)){
            errorHandler.error(name,"Already a variable with this name in this scope");
        }
        scope.put(name.lexeme,false);
    }

    private void define(Token name) {
        if(scopes.isEmpty()) return;
        scopes.peek().put(name.lexeme,true);
    }

    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        resolve(stmt.condition);
        resolve(stmt.thenBranch);
        if(stmt.elseBranch != null) resolve(stmt.elseBranch);
        return null;
    }

    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        beginScope();
        resolve(stmt.statements);
        endScope();
        return null;
    }

    private void beginScope() {
        scopes.push(new HashMap<>());
    }

    void resolve(List<Stmt> statements) {
        for(Stmt s: statements){
            resolve(s);
        }
    }

    void resolve(Stmt stmt) {
        if(stmt != null) stmt.accept(this);
    }

    void resolve(Expr expr){
        expr.accept(this);
    }

    private void endScope() {
        scopes.pop();
    }


    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
        resolve(stmt.condition);
        resolve(stmt.body);
        return null;
    }

    @Override
    public Void visitFunctionStmt(Stmt.Function stmt) {
        declare(stmt.name);
        define(stmt.name);

        resolveFunction(stmt,FunctionType.FUNCTION);
        return null;
    }

    private void resolveFunction(Stmt.Function function,FunctionType type) {
        FunctionType enclosingFunction = currentFunction;
        currentFunction = type;
        beginScope();
        function.params.forEach(p->{ declare(p); define(p); });
        resolve(function.body);
        endScope();
        currentFunction = enclosingFunction;

    }

    @Override
    public Void visitReturnStmt(Stmt.Return stmt) {
        if(currentFunction == FunctionType.NONE) {
            errorHandler.error(stmt.keyword,"Can't return from top-level code.");
        }
        if(stmt.value != null) {
            if(currentFunction == FunctionType.INITIALIZER){
                errorHandler.error(stmt.keyword,"Can't return a value from an initializer");
            }
            resolve(stmt.value);
        }
        return null;
    }



    @Override
    public Void visitClassStmt(Stmt.Class stmt) {
        ClassType enclosingClass = currentClass;
        currentClass = ClassType.CLASS;
        declare(stmt.name);
        define(stmt.name);

        if(stmt.superclass != null && stmt.name.lexeme.equals(stmt.superclass.name.lexeme)){
            errorHandler.error(stmt.superclass.name,"A class can't inherit from itself.");
        }

        if(stmt.superclass != null){
            currentClass = ClassType.SUBCLASS;
            resolve(stmt.superclass);
        }

        if(stmt.superclass != null) {
            beginScope();
            scopes.peek().put("super",true);
        }

        beginScope();
        scopes.peek().put("this",true);
        for(Stmt.Function method:stmt.methods){
            FunctionType decl = FunctionType.METHOD;
            if(method.name.lexeme.equals("init")){
                decl = FunctionType.INITIALIZER;
            }
            resolveFunction(method,decl);
        }
        endScope();
        if(stmt.superclass != null) endScope();

        currentClass = enclosingClass;
        return null;
    }
}
