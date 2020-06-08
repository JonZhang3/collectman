package com.collectman.script;

import com.collectman.common.Utils;
import org.mozilla.javascript.*;

import java.util.Properties;

public final class ScriptUtils {

    public static String asString(Object[] args, int index) {
        Object val = checkArgs(args, index);
        if (val == null) {
            return null;
        }
        return ScriptRuntime.toString(val);
    }

    public static String asString(Object obj) {
        if (obj == null || obj == Undefined.instance
            || obj == Undefined.SCRIPTABLE_UNDEFINED || obj == UniqueTag.NOT_FOUND) {
            return null;
        }
        return ScriptRuntime.toString(obj);
    }

    public static Integer asInteger(Object[] args, int index) {
        Object val = checkArgs(args, index);
        if (val == null) {
            return null;
        }
        double number = ScriptRuntime.toNumber(val);
        if (Double.isNaN(number)) {
            return null;
        }
        return (int) number;
    }

    public static Long asLong(Object obj) {
        double value = ScriptRuntime.toNumber(obj);
        if (Double.isNaN(value)) {
            return 0L;
        }
        return (long) value;
    }

    public static NativeObject asNativeObject(Object[] args, int index) {
        Object val = checkArgs(args, index);
        if (val == null) {
            return null;
        }
        if (!(val instanceof NativeObject)) {
            throw ScriptRuntime.typeError("the " + (index + 1) + " param must a object.");
        }
        return (NativeObject) val;
    }

    public static BaseFunction asBaseFunction(Object[] args, int index) {
        Object val = checkArgs(args, index);
        if (val == null) {
            return null;
        }
        if (!(val instanceof BaseFunction)) {
            throw ScriptRuntime.typeError("the " + (index + 1) + " param must a function.");
        }
        return (BaseFunction) val;
    }

    public static NativeObject assign(NativeObject target, NativeObject... source) {
        for (int i = 1; i < source.length; i++) {
            if ((source[i] == null) || Undefined.instance.equals(source[i])) {
                continue;
            }
            Scriptable s = source[i];
            Object[] ids = s.getIds();
            for (Object key : ids) {
                if (key instanceof String) {
                    Object val = s.get((String) key, target);
                    if ((val != Scriptable.NOT_FOUND) && (val != Undefined.instance)) {
                        target.put((String) key, target, val);
                    }
                } else if (key instanceof Number) {
                    int ii = ScriptRuntime.toInt32(key);
                    Object val = s.get(ii, target);
                    if ((val != Scriptable.NOT_FOUND) && (val != Undefined.instance)) {
                        target.put(ii, target, val);
                    }
                }
            }
        }
        return target;
    }

    public static String join(NativeArray array) {
        if(array == null || array.size() == 0) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        for(int i = 0, len = array.size(); i < len; i++) {
            result.append(ScriptRuntime.toString(array.get(i)));
            if(i < (len - 1)) {
                result.append(",");
            }
        }
        return result.toString();
    }

    public static void putPropertiesIfNull(Properties properties, Object value) {

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
