package com.collectman;

import com.collectman.config.DataBaseProperties;
import com.collectman.config.JobProperties;
import com.collectman.config.KafkaProducerProperties;
import com.collectman.connection.Connection;
import com.collectman.connection.DataBaseConnection;
import com.collectman.connection.KafkaProducerConnection;
import com.collectman.task.DynamicTaskConfigurer;
import com.tuples.Tuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.FixedDelayTask;
import org.springframework.scheduling.config.FixedRateTask;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ApplicationStarter implements CommandLineRunner {

    @Autowired
    private JobProperties properties;

    @Autowired
    private DynamicTaskConfigurer taskConfigurer;

    @Override
    public void run(String... args) {
        String cron = properties.getCron();
        long fixedDelay = properties.getFixedDelay();
        long fixedRate = properties.getFixedRate();
        long initialDelay = properties.getInitialDelay();
        if(fixedDelay > 0){
            taskConfigurer.addFixedDelayTask(new FixedDelayTask(new DataFlowRunnable(properties.getFrom(), properties.getTo()), fixedDelay, initialDelay));
        }
        if(fixedRate > 0) {
            taskConfigurer.addFixedRateTask(new FixedRateTask(new DataFlowRunnable(properties.getFrom(), properties.getTo()), fixedRate, initialDelay));
        }
        if(StringUtils.hasText(cron)) {
            taskConfigurer.addCronTask(new CronTask(new DataFlowRunnable(properties.getFrom(), properties.getTo()), cron));
        }
    }

    private static class DataFlowRunnable implements Runnable {

        private final Connection databaseConnection;
        private final Connection kafkaProducerConnection;

        public DataFlowRunnable(DataBaseProperties fromConfig, KafkaProducerProperties toConfig) {
            databaseConnection = new DataBaseConnection(fromConfig);
            databaseConnection.connect();
            kafkaProducerConnection = new KafkaProducerConnection(toConfig);
            kafkaProducerConnection.connect();
        }

        @Override
        public void run() {
            Object result = databaseConnection.execute(null);
            Tuple tuple = new Tuple();
            tuple.add(result);
            kafkaProducerConnection.execute(tuple);
        }
    }

}
