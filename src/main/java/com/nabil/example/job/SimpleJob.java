package com.nabil.example.job;

import com.nabil.example.service.SimpleService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SimpleJob implements Job {

    private SimpleService simpleService;

    @Autowired
    public void setSimpleService(SimpleService simpleService) {
        this.simpleService = simpleService;
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        simpleService.test();
    }
}
