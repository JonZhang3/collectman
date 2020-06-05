package com.collectman.config;

import org.springframework.boot.autoconfigure.kafka.KafkaProperties;

public class KafkaProducerProperties extends KafkaProperties.Producer {

    private String topic;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
