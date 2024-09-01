import java.util.HashMap;
import java.util.Map;

public class Environment {

    final Environment enclosing;
    private final Map<String,Object> values = new HashMap<>();

    Environment() {
        enclosing = null;
    }

    Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    void define(String name,Object value) {
        values.put(name,value);
    }

    Object get(Token name){
        if(values.containsKey(name.lexeme)){
            return values.get(name.lexeme);
        }
        //this is a recursive call
        if(enclosing != null) return enclosing.get(name);

        throw new RuntimeError(name,"Undefined variable '" + name.lexeme + "'.");
    }

    Object getAt(int distance, String name) {
        return ancestor(distance).values.get(name);
    }

    Environment ancestor(int distance) {
        Environment environment = this;
        for (int i = 0; i < distance; i++) {
            environment = environment.enclosing;
        }

        return environment;
    }

    void assign(Token name, Object value) {
        if(values.containsKey(name.lexeme)){
            values.put(name.lexeme,value);
            return;
        }
        if(enclosing != null ){
            //not sure i understand this
            enclosing.assign(name,value);
            return;
        }

        throw new RuntimeError(name,"Undefined variable '" + name.lexeme + "'.");
    }

    public void assignAt(Integer distance, Token name, Object value) {
        ancestor(distance).values.put(name.lexeme,value);
    }
}
