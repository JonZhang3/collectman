package com.collectman.script;

import org.mozilla.javascript.ScriptRuntime;

public class Global {

    public static boolean isError(Object error) {
        return ScriptRuntime.isError(error);
    }

    public static void print(String message) {
        System.out.println(message);
    }

}
