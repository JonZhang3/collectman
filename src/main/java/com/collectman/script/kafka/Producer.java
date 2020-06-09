package com.collectman.script.kafka;

import com.collectman.common.Utils;
import com.collectman.script.ScriptUtils;
import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.mozilla.javascript.*;

import java.time.Duration;
import java.util.Properties;

public class Producer extends IdScriptableObject {

    static final String KAFKA_PRODUCER_TAG = "KafkaProducer";

    public static void init(Scriptable scope) {
        Producer obj = new Producer();
        obj.exportAsJSClass(MAX_PROTOTYPE_ID, scope, false);
    }

    private Producer() {

    }

    private Properties properties;
    private KafkaProducer<String, String> producer;

    // bootstrap.servers
    // acks
    // retries
    Producer(NativeObject configs) {
        this.properties = new Properties();
        Object servers = configs.get("brokers", configs);
        if (servers == null || servers == Scriptable.NOT_FOUND) {
            throw ScriptRuntime.notFoundError(configs, "brokers");
        }
        if (servers instanceof NativeArray) {
            this.properties.put("bootstrap.servers", ScriptUtils.join((NativeArray) servers));
        } else {
            this.properties.put("bootstrap.servers", ScriptRuntime.toString(servers));
        }

        Object acks = configs.get("acks", configs);
        if (acks != null && acks != Scriptable.NOT_FOUND) {
            this.properties.put("acks", ScriptRuntime.toString(acks));
        }

        Integer retries = ScriptUtils.getIntegerFromObject(configs, "retries");
        if (retries == null || retries < 0) {
            retries = 0;
        }
        this.properties.put("retries", retries);

        Long batchSize = ScriptUtils.getLongFromObject(configs, "batchSize");
        if (batchSize != null && batchSize > 0) {
            this.properties.put("batch.size", batchSize);
        }

        Integer lingerMs = ScriptUtils.getIntegerFromObject(configs, "lingerMs");
        if (lingerMs != null && lingerMs > 0) {
            this.properties.put("linger.ms", lingerMs);
        }

        Long bufferMemory = ScriptUtils.getLongFromObject(configs, "bufferMemory");
        if (bufferMemory != null && bufferMemory > 0) {
            this.properties.put("buffer.memory", bufferMemory);
        }

        this.properties.put("key.serializer", StringSerializer.class.getName());
        this.properties.put("value.serializer", StringSerializer.class.getName());
    }

    private static final int
        Id_constructor = 1,
        Id_toString = 2,
        Id_connect = 3,
        Id_send = 4,
        Id_disconnect = 5,
        MAX_PROTOTYPE_ID = 5;

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
            case Id_send:
                return js_send(cx, scope, ScriptUtils.asNativeObject(args, 0));
            case Id_disconnect:
                js_disconnect(ScriptUtils.asLong(args, 0));
                return Undefined.instance;
            default:
                throw new IllegalArgumentException(String.valueOf(id));
        }
    }

    public void js_connect() {
        producer = new KafkaProducer<>(properties);
    }

    public Object js_send(Context cx, Scriptable scope, NativeObject arg) {
        String defaultTopic = ScriptUtils.getStringFromObject(arg, "topic");
        if (Utils.isEmpty(defaultTopic)) {
            throw ScriptRuntime.throwError(cx, scope, "provide the config [topic]");
        }
        Integer defaultTimeout = ScriptUtils.getIntegerFromObject(arg, "timeout");
        if (defaultTimeout != null && defaultTimeout < 0) {
            defaultTimeout = null;
        }
        Long defaultPartition = ScriptUtils.getLongFromObject(arg, "partition");
        if (defaultPartition != null && defaultPartition < 0) {
            defaultPartition = null;
        }
        NativeArray messages = ScriptUtils.getArrayFromObject(arg, "messages");
        if (messages == null || messages.size() == 0) {
            return 0;
        }
        int size = 0;
        for (int i = 0, len = messages.size(); i < len; i++) {
            Object obj = messages.get(i);
            if (obj instanceof NativeObject) {
                NativeObject message = (NativeObject) obj;
                String key = ScriptUtils.getStringFromObject(message, "key");
                Object valueObj = message.get("value", message);
                if (valueObj == null || valueObj == Scriptable.NOT_FOUND || Undefined.isUndefined(valueObj)) {
                    break;
                }
                String value = Utils.GSON.toJson(valueObj);
                Long partition = ScriptUtils.getLongFromObject(message, "partition");
                partition = (partition == null || partition < 0) ? defaultPartition : partition;
                Integer timeout = ScriptUtils.getIntegerFromObject(message, "timeout");
                timeout = (timeout == null || timeout < 0) ? defaultTimeout : timeout;
                size++;
                producer.send(new ProducerRecord<String, String>(defaultTopic, timeout, partition, key, value));
            }
        }
        return size;
    }

    public void js_disconnect(Long ms) {
        if (ms == null || ms <= 0) {
            producer.close();
        } else {
            producer.close(Duration.ofMillis(ms));
        }
    }

    @Override
    public String getClassName() {
        return KAFKA_PRODUCER_TAG;
    }

}
