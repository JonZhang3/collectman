package com.collectman;

import com.collectman.script.database.Database;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CollectmanApplication {

    public static void main(String[] args) {
//        SpringApplication.run(CollectmanApplication.class, args);
//        NashornScriptEngineFactory engineFactory = new NashornScriptEngineFactory();
//        NashornScriptEngine engine = (NashornScriptEngine) engineFactory.getScriptEngine();
//        engine.registerFuntion("test", findMH());
//        SimpleBindings bindings = new SimpleBindings();
//        bindings.put("db", new Database());
//        bindings.put("kafka", new Kafka());
//        engine.eval(new FileReader("/Users/jon/Documents/code/collectman/src/main/resources/script.js"), bindings);
        try {
            Context context = Context.enter();
            ScriptableObject scope = context.initStandardObjects();
            scope.defineFunctionProperties(new String[]{"test"}, CollectmanApplication.class, ScriptableObject.READONLY);
//            scope.defineOwnProperty(context, "db", null);
            Database.init(scope);
            Object result = context.evaluateString(scope, "String.substring('112222', 2)", "<cmd>", 1, null);
            System.out.println(result);
        } finally {
            Context.exit();
        }
    }

    public static void test() {
        System.out.println(123);
    }

}
