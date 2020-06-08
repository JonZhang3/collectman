package com.collectman;

import com.collectman.script.Database;
import com.collectman.script.Global;
import com.collectman.script.SchedulingTask;
import com.collectman.script.kafka.Kafka;
import com.collectman.script.kafka.KafkaProducer;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.FileReader;

@Component
public class ApplicationStarter implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        Context context = Context.enter();
        ScriptableObject scope = context.initStandardObjects();
        scope.defineFunctionProperties(new String[]{"isError", "print"}, Global.class, ScriptableObject.READONLY);
        //////////////////////
        Database.init(scope);
        SchedulingTask.init(scope);
        Kafka.init(scope);
//        KafkaProducer.init(scope);
        //////////////////////
        Object result = context.evaluateReader(scope, new FileReader("/Users/jon/Documents/code/collectman/src/main/resources/script.js"), "", 1, null);
        System.out.println(result);
    }

}
