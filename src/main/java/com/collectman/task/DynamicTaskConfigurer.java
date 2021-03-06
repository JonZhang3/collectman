package com.collectman.task;

import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.FixedDelayTask;
import org.springframework.scheduling.config.FixedRateTask;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.LinkedHashSet;
import java.util.Set;

@Component
public class DynamicTaskConfigurer implements SchedulingConfigurer {

    private static ScheduledTaskRegistrar registrar;
    private static final Set<ScheduledTask> scheduledTasks = new LinkedHashSet<>(16);

    @Override
    public void configureTasks(ScheduledTaskRegistrar registrar) {
        DynamicTaskConfigurer.registrar = registrar;
    }

    public static void addCronTask(CronTask task) {
        ScheduledTask scheduledTask = registrar.scheduleCronTask(task);
        if(scheduledTask != null) {
            scheduledTasks.add(scheduledTask);
        }
    }

    public static void addFixedDelayTask(FixedDelayTask task) {
        ScheduledTask scheduledTask = registrar.scheduleFixedDelayTask(task);
        if(scheduledTask != null) {
            scheduledTasks.add(scheduledTask);
        }
    }

    public static void addFixedRateTask(FixedRateTask task) {
        ScheduledTask scheduledTask = registrar.scheduleFixedRateTask(task);
        if(scheduledTask != null) {
            scheduledTasks.add(scheduledTask);
        }
    }

    public static Set<ScheduledTask> getScheduledTasks() {
        return scheduledTasks;
    }

    @PreDestroy
    public void destroy() {
        registrar.destroy();
    }

}
