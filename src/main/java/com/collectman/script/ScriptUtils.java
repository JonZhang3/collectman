package com.collectman.script;

import jdk.nashorn.internal.objects.Global;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Undefined;

public final class ScriptUtils {

    public static String asString(Object[] args, int index) {
        Object val = checkArgs(args, index);
        if(val == null) {
            return null;
        }
        Global
        return ScriptRuntime.toString(val);
    }

    public static Integer asInteger(Object[] args, int index) {
        Object val = checkArgs(args, index);
        if(val == null) {
            return null;
        }
        double number = ScriptRuntime.toNumber(val);
        if(Double.isNaN(number)) {
            return null;
        }
        return (int) number;
    }

    private static Object checkArgs(Object[] args, int index) {
        if (args == null || args.length == 0) {
            return null;
        }
        int argslen = args.length;
        if (argslen - 1 < index) {
            return null;
        }
        Object val = args[index];
        if (val == null || val == Undefined.instance || val == Undefined.SCRIPTABLE_UNDEFINED) {
            return null;
        }
        return val;
    }

}
