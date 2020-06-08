package com.collectman.script.kafka;

import com.collectman.common.CollectManException;
import com.collectman.script.ScriptUtils;
import org.mozilla.javascript.*;

public class Kafka extends IdScriptableObject {

    private static final long serialVersionUID = 1L;

    private static final String KAFKA_TAG = "kafka";

    public static void init(Scriptable scope) {
        Kafka obj = new Kafka();
        obj.exportAsJSClass(MAX_PROTOTYPE_ID, scope, false);
    }

    private Kafka() {

    }

    /**
     * clientId: String
     * brokers: String|Array
     * connectionTimeout: int
     * requestTimeout: int
     *
     */
    private static NativeObject commonConfig;

    private static final int
        Id_constructor = 1,
        Id_toString = 2,
        MAX_PROTOTYPE_ID = 2,
        ConstructorId_config = -1,
        ConstructorId_producer = -2,
        ConstructorId_consumer = -3;

    @Override
    protected void fillConstructorProperties(IdFunctionObject ctor) {
        addIdFunctionProperty(ctor, KAFKA_TAG, ConstructorId_config, "config", 1);
        addIdFunctionProperty(ctor, KAFKA_TAG, ConstructorId_producer, "producer", 1);
        addIdFunctionProperty(ctor, KAFKA_TAG, ConstructorId_consumer, "consumer", 1);
        super.fillConstructorProperties(ctor);
    }

    @Override
    protected void initPrototypeId(int id) {
        String name;
        int arity;
        switch (id) {
            case Id_constructor:
                arity = 0;
                name = "constructor";
                break;
            case Id_toString:
                arity = 0;
                name = "toString";
                break;
            default:
                throw new IllegalArgumentException(String.valueOf(id));
        }
        initPrototypeMethod(KAFKA_TAG, id, name, arity);
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
        if (!f.hasTag(KAFKA_TAG)) {
            return super.execIdCall(f, cx, scope, thisObj, args);
        }
        int id = f.methodId();
        switch (id) {
            case ConstructorId_config:
                return config(args);
            case ConstructorId_consumer:
                return consumer(scope, args);
            case ConstructorId_producer:
                return producer(scope, args);
            case Id_constructor:
                return Undefined.instance;
            case Id_toString:
                return "[object Object]";
            default:
                throw new IllegalArgumentException(String.valueOf(id));
        }
    }

    public static Object config(Object[] args) {
        if (args == null || args.length == 0 || args[0] == null) {
            throw new CollectManException("please config the database");
        }
        Object configs = args[0];
        if (!(configs instanceof NativeObject)) {
            throw new CollectManException("the param must be a Object.");
        }
        Kafka.commonConfig = (NativeObject) configs;
        return Undefined.instance;
    }

    public static KafkaProducer producer(Scriptable scope, Object[] args) {
        if (args == null || args.length == 0 || args[0] == null) {
            throw new CollectManException("please config the database");
        }
        if (!(args[0] instanceof NativeObject)) {
            throw new CollectManException("the param must be a Object.");
        }
        NativeObject configs = (NativeObject) args[0];
        ScriptUtils.assign(configs, Kafka.commonConfig);
        Scriptable classPrototype = ScriptableObject.getClassPrototype(scope, KafkaProducer.KAFKA_PRODUCER_TAG);
        KafkaProducer producer = new KafkaProducer(configs);
        producer.setPrototype(classPrototype);
        return producer;
    }

    public static Object consumer(Scriptable scope, Object[] args) {
        if (args == null || args.length == 0 || args[0] == null) {
            throw new CollectManException("please config the database");
        }
        Object configs = args[0];
        if (!(configs instanceof NativeObject)) {
            throw new CollectManException("the param must be a Object.");
        }
        return Undefined.instance;
    }

    @Override
    public String getClassName() {
        return KAFKA_TAG;
    }
}
