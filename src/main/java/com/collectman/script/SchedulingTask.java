package com.collectman.script;

import com.collectman.task.DynamicTaskConfigurer;
import org.mozilla.javascript.*;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.FixedDelayTask;
import org.springframework.scheduling.config.FixedRateTask;
import org.springframework.util.StringUtils;

public class SchedulingTask extends IdScriptableObject {

    private static final long serialVersionUID = 1L;

    private static final String SCHEDULING_TASK_TAG = "stask";

    public static void init(Scriptable scope) {
        SchedulingTask obj = new SchedulingTask();
        obj.exportAsJSClass(MAX_PROTOTYPE_ID, scope, false);
    }

    private static final int
        Id_constructor = 1,
        Id_toString = 2,
        MAX_PROTOTYPE_ID = 2,
        ConstructorId_addTask = -1;

    private SchedulingTask() {

    }

    @Override
    protected void fillConstructorProperties(IdFunctionObject ctor) {
        addIdFunctionProperty(ctor, SCHEDULING_TASK_TAG, ConstructorId_addTask, "addTask", 2);
        super.fillConstructorProperties(ctor);
    }

    @Override
    protected void initPrototypeId(int id) {
        String name;
        int arity;
        switch (id) {
            case Id_constructor:
                name = "constructor";
                arity = 0;
                break;
            case Id_toString:
                name = "toString";
                arity = 0;
                break;
            default:
                throw new IllegalArgumentException(String.valueOf(id));
        }
        initPrototypeMethod(SCHEDULING_TASK_TAG, id, name, arity);
    }

    @Override
    protected int findPrototypeId(String name) {
        switch (name) {
            case "constructor":
                return Id_constructor;
            case "toString":
                return Id_toString;
            default:
                throw new IllegalStateException(name);
        }
    }

    @Override
    public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        if (!f.hasTag(SCHEDULING_TASK_TAG)) {
            return super.execIdCall(f, cx, scope, thisObj, args);
        }
        int id = f.methodId();
        switch (id) {
            case ConstructorId_addTask:
                return js_addTask(cx, scope, thisObj, ScriptUtils.asNativeObject(args, 0), ScriptUtils.asBaseFunction(args, 1));
            case Id_constructor:
                return null;
            case Id_toString:
                return "[object Object]";
        }
        return super.execIdCall(f, cx, scope, thisObj, args);
    }

    public Object js_addTask(Context cx, Scriptable scope, Scriptable thisObj, NativeObject config, BaseFunction callback) {
        if (config == null) {
            throw ScriptRuntime.throwError(cx, scope, "specify the task config");
        }
        if (callback == null) {
            throw ScriptRuntime.throwError(cx, scope, "specify the task callback");
        }
        String cron = ScriptUtils.asString(config.get("cron", config));
        Long fixedDelay = ScriptUtils.asLong(config.get("fixedDelay", config));
        Long fixedRate = ScriptUtils.asLong(config.get("fixedRate", config));
        Long initialDelay = ScriptUtils.asLong(config.get("initialDelay", config));
        if (initialDelay == null) {
            initialDelay = 0L;
        }
        if (fixedDelay != null && fixedDelay > 0) {
            DynamicTaskConfigurer.addFixedDelayTask(new FixedDelayTask(() -> {
                Context context = ContextFactory.getGlobal().enterContext();
                try {
                    callback.call(context, scope, thisObj, new Object[0]);
                } finally {
                    Context.exit();
                }
            }, fixedDelay, initialDelay));
        } else if (fixedRate != null && fixedRate > 0) {
            DynamicTaskConfigurer.addFixedRateTask(new FixedRateTask(() -> {
                Context context = ContextFactory.getGlobal().enterContext();
                try {
                    callback.call(context, scope, thisObj, new Object[0]);
                } finally {
                    Context.exit();
                }
            }, fixedRate, initialDelay));
        } else if (StringUtils.hasText(cron)) {
            DynamicTaskConfigurer.addCronTask(new CronTask(() -> {
                Context context = ContextFactory.getGlobal().enterContext();
                try {
                    callback.call(context, scope, thisObj, new Object[0]);
                } finally {
                    Context.exit();
                }
            }, cron));
        } else {
            throw ScriptRuntime.throwError(cx, scope, "the config is error");
        }
        return Undefined.instance;
    }

    @Override
    public String getClassName() {
        return SCHEDULING_TASK_TAG;
    }

}
