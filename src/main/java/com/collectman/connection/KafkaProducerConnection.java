package com.collectman.connection;

import com.collectman.config.KafkaProducerProperties;
import com.google.gson.Gson;
import com.tuples.Tuple;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class KafkaProducerConnection implements Connection {

    private static final Gson GSON = new Gson();

    private KafkaProducer<String, Object> producer;
    private final String topic;
    private final Map<String, Object> config;

    public KafkaProducerConnection(KafkaProducerProperties properties) {
        this.topic = properties.getTopic();
        config = properties.buildProperties();
    }

    @Override
    public void connect() {
        producer = new KafkaProducer<>(config);
    }

    @Override
    public void close() {
        if(producer != null) {
            producer.close();
        }
    }

    @Override
    public Object execute(final Tuple values) {
        Object value = values.getValue(0);
        if(value instanceof List) {
            List<Object> list = (List) value;
            for (Object obj : list) {
                String content = GSON.toJson(obj);
                ProducerRecord<String, Object> record = new ProducerRecord<>(topic, UUID.randomUUID().toString(), content);
                producer.send(record);
            }
            producer.flush();
        }
        return null;
    }

}
