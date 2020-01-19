package com.nabil.example.controller;

import com.cronutils.builder.CronBuilder;
import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

import static com.cronutils.model.field.expression.FieldExpressionFactory.*;

@RestController
@Component
public class SimpleController {

    private SchedulerFactoryBean schedulerFactoryBean;


    @Autowired
    public SimpleController(SchedulerFactoryBean schedulerFactoryBean) {
        this.schedulerFactoryBean = schedulerFactoryBean;
    }

    @GetMapping("/hit")
    public String hit() {
        String cronAsString = "";
        try {

            Cron cron = CronBuilder.cron(CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ))
                    .withYear(always())
                    .withDoW(questionMark())
                    .withMonth(always())
                    .withDoM(on(1))
                    .withHour(on(1))
                    .withMinute(on(0))
                    .withSecond(on(0))
                    .instance();

            cronAsString = cron.asString();
            Scheduler scheduler = schedulerFactoryBean.getScheduler();

            for (String groupName : scheduler.getJobGroupNames()) {
                for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                    //get job's trigger
                    List<Trigger> triggers = Collections.unmodifiableList(scheduler.getTriggersOfJob(jobKey));
                    Trigger oldTrigger = triggers.get(0);
                    TriggerKey triggerKey = oldTrigger.getKey();
                    CronTriggerImpl cronTrigger = (CronTriggerImpl) scheduler.getTrigger(triggerKey);
                    cronTrigger.setCronExpression("0/10 * * * * ? *");
                    scheduler.rescheduleJob(triggerKey, cronTrigger);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cronAsString;
    }
}
