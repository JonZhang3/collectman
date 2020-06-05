package com.collectman.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

@Configuration
@ConfigurationProperties(prefix = "job")
public class JobProperties {

    private String name;
    private String cron;
    private long fixedDelay = -1L;
    private long fixedRate = -1L;
    private long initialDelay = -1L;
    private KafkaProducerProperties to;
    private DataBaseProperties from;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public long getFixedDelay() {
        return fixedDelay;
    }

    public void setFixedDelay(long fixedDelay) {
        this.fixedDelay = fixedDelay;
    }

    public long getFixedRate() {
        return fixedRate;
    }

    public void setFixedRate(long fixedRate) {
        this.fixedRate = fixedRate;
    }

    public long getInitialDelay() {
        return initialDelay;
    }

    public void setInitialDelay(long initialDelay) {
        this.initialDelay = initialDelay;
    }

    public KafkaProducerProperties getTo() {
        return to;
    }

    public void setTo(KafkaProducerProperties to) {
        this.to = to;
    }

    public DataBaseProperties getFrom() {
        return from;
    }

    public void setFrom(DataBaseProperties from) {
        this.from = from;
    }

    @Override
    public String toString() {
        return "JobProperties{" +
            "name='" + name + '\'' +
            ", cron='" + cron + '\'' +
            ", to=" + to +
            ", from=" + from +
            '}';
    }
}
