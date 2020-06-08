package com.collectman.script.kafka;

import com.collectman.script.ScriptUtils;
import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import org.mozilla.javascript.*;

import java.util.Properties;

public class KafkaProducer extends IdScriptableObject {

    static final String KAFKA_PRODUCER_TAG = "kafkaProducer";

    public static void init(Scriptable scope) {
        KafkaProducer obj = new KafkaProducer();
        obj.exportAsJSClass(2, scope, false);
    }

    private KafkaProducer() {

    }

    private Properties properties;

    // bootstrap.servers
    // acks
    // retries
    KafkaProducer(NativeObject configs) {
        this.properties = new Properties();
        Object servers = configs.get("brokers", configs);
        if(servers == null || servers == Scriptable.NOT_FOUND) {
            throw ScriptRuntime.notFoundError(configs, "brokers");
        }
        if(servers instanceof NativeArray) {
            this.properties.put("bootstrap.servers", ScriptUtils.join((NativeArray) servers));
        } else {
            this.properties.put("bootstrap.servers", ScriptRuntime.toString(servers));
        }
        Object acks = configs.get("acks", configs);
        if(acks != null && acks != Scriptable.NOT_FOUND) {
            this.properties.put("acks", ScriptRuntime.toString(acks));
        }

        this.properties.put("key.serializer", StringSerializer.class.getName());
        this.properties.put("value.serializer", StringSerializer.class.getName());
    }

    private static final int
        Id_constructor = 1,
        Id_toString = 2,
        Id_connect = 3,
        Id_send = 4,
        Id_disconnect = 5;

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
                arity = 0;
                name = "toString";
                break;
            case Id_connect:
                arity = 0;
                name = "connect";
                break;
            case Id_send:
                arity = 0;
                name = "send";
                break;
            case Id_disconnect:
                arity = 0;
                name = "disconnect";
                break;
            default:
                throw new IllegalArgumentException(String.valueOf(id));
        }
        initPrototypeMethod(KAFKA_PRODUCER_TAG, id, name, arity);
    }

    @Override
    protected int findPrototypeId(String name) {
        switch (name) {
            case "constructor":
                return Id_constructor;
            case "toString":
                return Id_toString;
            case "connect":
                return Id_connect;
            case "send":
                return Id_send;
            case "disconnect":
                return Id_disconnect;
            default:
                throw new IllegalStateException(name);
        }
    }

    @Override
    public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        if (!f.hasTag(KAFKA_PRODUCER_TAG)) {
            return super.execIdCall(f, cx, scope, thisObj, args);
        }
        int id = f.methodId();
        switch (id) {
            case Id_constructor:
                return Undefined.instance;
            case Id_toString:
                return "[object Object]";
            case Id_connect:
                js_connect();
                return Undefined.instance;
            default:
                throw new IllegalArgumentException(String.valueOf(id));
        }
    }

    public void js_connect() {

    }

    public void js_send() {

    }

    public void js_disconnect() {

    }

    @Override
    public String getClassName() {
        return KAFKA_PRODUCER_TAG;
    }
}
