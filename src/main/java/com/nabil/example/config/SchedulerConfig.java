package com.nabil.example.config;

import com.nabil.example.job.SimpleJob;
import com.nabil.example.model.QuartzCronTriggersModel;
import com.nabil.example.repository.QuartzCronTriggersRepository;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

@Configuration
@EnableScheduling
public class SchedulerConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerConfig.class);

    private ApplicationContext applicationContext;

    private DataSource dataSource;

    private Environment env;

    private QuartzCronTriggersRepository quartzCronTriggersRepository;

    @Autowired
    public SchedulerConfig(ApplicationContext applicationContext,
                           @Qualifier("myDS") DataSource dataSource,
                           Environment env,
                           QuartzCronTriggersRepository quartzCronTriggersRepository){
        this.applicationContext = applicationContext;
        this.dataSource = dataSource;
        this.env = env;
        this.quartzCronTriggersRepository = quartzCronTriggersRepository;
    }

    @Bean
    public JobDetailFactoryBean simpleJobDetail() {
        JobDetailFactoryBean bean = new JobDetailFactoryBean();
        bean.setJobClass(SimpleJob.class);
        bean.setDurability(true);
        bean.setDescription("Simple Job Desc");
        bean.setName("simpleJob");
        bean.setGroup("SIMPLE JOB");
        return bean;
    }

    @Bean
    public CronTriggerFactoryBean simpleJobTrigger() {
        QuartzCronTriggersModel quartzCronTriggersModel = quartzCronTriggersRepository.findBySchedName("scheduler-test");
        CronTriggerFactoryBean cron = new CronTriggerFactoryBean();
        cron.setJobDetail(simpleJobDetail().getObject());
        cron.setBeanName("simpleJobTrigger");
        cron.setGroup("SIMPLE JOB");
        if (quartzCronTriggersModel != null){
            cron.setCronExpression(quartzCronTriggersModel.getCronExpression());
        } else {
            cron.setCronExpression(env.getProperty("test-cron"));
        }

        return cron;
    }

    @Bean
    public SchedulerFactoryBean quartzScheduler() {
        SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
        scheduler.setOverwriteExistingJobs(true);
        scheduler.setSchedulerName("scheduler-test");

        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        scheduler.setJobFactory(jobFactory);

        scheduler.setDataSource(dataSource);
        scheduler.setWaitForJobsToCompleteOnShutdown(true);
        scheduler.setQuartzProperties(quartzProperties());

        Trigger[] triggers = {
                simpleJobTrigger().getObject()
        };

        scheduler.setTriggers(triggers);
        return scheduler;
    }

    private Properties quartzProperties() {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
        Properties properties = null;
        try {
            propertiesFactoryBean.afterPropertiesSet();
            properties = propertiesFactoryBean.getObject();
        } catch (IOException e) {
            LOGGER.error("Unable to load quartz.properties", e);
        }
        return properties;
    }

}
