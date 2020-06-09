package com.collectman.script;

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
        if (obj == null || Undefined.isUndefined(obj) || obj == UniqueTag.NOT_FOUND) {
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

    public static Integer asInteger(Object obj) {
        double val = ScriptRuntime.toNumber(obj);
        if(Double.isNaN(val)){
            return 0;
        }
        return (int) val;
    }

    public static Long asLong(Object[] args, int index) {
        Object val = checkArgs(args, index);
        if (val == null) {
            return null;
        }
        double number = ScriptRuntime.toNumber(val);
        if (Double.isNaN(number)) {
            return null;
        }
        return (long) number;
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

    public static Double getDoubleFromObject(NativeObject config, String propertyName) {
        Object val = config.get(propertyName, config);
        if(val == null || val == Scriptable.NOT_FOUND) {
            return null;
        }
        double result = ScriptRuntime.toNumber(val);
        if(Double.isNaN(result)) {
            throw ScriptRuntime.typeError("the config [" + propertyName + "] must a number");
        }
        return result;
    }

    public static Integer getIntegerFromObject(NativeObject config, String propertyName) {
        Double result = getDoubleFromObject(config, propertyName);
        return result == null ? null : result.intValue();
    }

    public static Long getLongFromObject(NativeObject config, String propertyName) {
        Double result = getDoubleFromObject(config, propertyName);
        return result == null ? null : result.longValue();
    }

    public static String getStringFromObject(NativeObject config, String propertyName) {
        Object val = config.get(propertyName, config);
        if(val == null || val == Scriptable.NOT_FOUND || Undefined.isUndefined(val)) {
            return null;
        }
        return ScriptRuntime.toString(val);
    }

    public static NativeArray getArrayFromObject(NativeObject obj, String propertyName) {
        Object val = obj.get(propertyName, obj);
        if(val == null || val == Scriptable.NOT_FOUND || Undefined.isUndefined(val)) {
            return null;
        }
        if(val instanceof NativeArray) {
            return (NativeArray) val;
        }
        return null;
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
