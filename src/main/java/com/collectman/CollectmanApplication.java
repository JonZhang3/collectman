package com.collectman;

import com.collectman.script.Global;
import com.collectman.script.Database;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.ScriptableObject;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import sun.font.ScriptRun;

import java.io.FileReader;
import java.io.IOException;

@SpringBootApplication
@EnableScheduling
public class CollectmanApplication {

    public static void main(String[] args) {
        try {
            Context context = Context.enter();
            ScriptableObject scope = context.initStandardObjects();
            scope.defineFunctionProperties(new String[]{"isError", "print"}, Global.class, ScriptableObject.READONLY);
            //////////////////////
            Database.init(scope);
            //////////////////////
            Object result = context.evaluateReader(scope, new FileReader("/Volumes/Transcend/code/my/collectman/src/main/resources/script.js"), "", 1, null);
            System.out.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Context.exit();
        }
    }

    public static void test() {
        System.out.println(123);
    }

}
