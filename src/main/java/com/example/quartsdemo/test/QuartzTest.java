package com.example.quartsdemo.test;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @DisallowConcurrentExecution
 * 这个指不要并发访问这个类
 *
 * @PersistJobDataAfterExecution
 * 建议和@DisallowConcurrentExecution连用防止出现数据不一致
 * 告诉Quartz在成功执行了job类的execute方法后（没有发生任何异常），更新JobDetail中JobDataMap的数据
 *
 *
 */

public class QuartzTest {

    public static void main(String[] args) throws SchedulerException {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.start();
        List<Date> dateList = new ArrayList<>();
        // 使用我们定义的HelloJob进行相关的调用
//        JobDetail job = JobBuilder.newJob(HelloJob.class)
        JobDetail job = JobBuilder.newJob(DumbJob.class)
                .withIdentity("job1", "group1")
                .usingJobData("myJob","Hello world!")
                .usingJobData("jobSays","Hello world!")
                .usingJobData("myFloatValue",3.14F )

                .build();

        //触发器的配置

        /**
         * 配置相关的触发器
         */
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger1", "group1")
                .withSchedule(
                        SimpleScheduleBuilder
                                .simpleSchedule()
                                .withIntervalInSeconds(2)
                                .repeatForever()
                ).build();

        // 最后让我们的job按照触发器的配置进行运作
        scheduler.scheduleJob(job, trigger);
        // scheduler.shutdown();
    }
}
