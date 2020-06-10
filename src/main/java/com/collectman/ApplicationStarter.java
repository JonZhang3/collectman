package com.collectman;

import com.collectman.common.AppProperties;
import com.collectman.script.Database;
import com.collectman.script.Global;
import com.collectman.script.SchedulingTask;
import com.collectman.script.kafka.Kafka;
import com.collectman.script.kafka.Producer;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.FileReader;

@Component
public class ApplicationStarter implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationStarter.class);

    @Autowired
    private AppProperties appProperties;

    @Override
    public void run(String... args) throws Exception {
        Context context = Context.enter();
        ScriptableObject scope = context.initStandardObjects();
        scope.defineFunctionProperties(new String[]{"isError", "print"}, Global.class, ScriptableObject.READONLY);
        Database.init(scope);
        SchedulingTask.init(scope);
        Kafka.init(scope);
        Producer.init(scope);
        LOGGER.info("init script context success");
        LOGGER.info("start eval script file");
        context.evaluateReader(scope, new FileReader(appProperties.getScriptFile()),
            "", 1, null);
    }

}
