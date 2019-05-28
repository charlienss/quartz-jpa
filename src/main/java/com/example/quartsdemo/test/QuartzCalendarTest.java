package com.example.quartsdemo.test;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.calendar.HolidayCalendar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.CronScheduleBuilder.dailyAtHourAndMinute;

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

public class QuartzCalendarTest {

    public static void main(String[] args) throws SchedulerException, ParseException {
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


        HolidayCalendar cal =  new HolidayCalendar();
        cal.addExcludedDate(new Date("2019/5/13"));

        scheduler.addCalendar("myHolidays",cal,false,false);

        Trigger t = TriggerBuilder.newTrigger()
                .withIdentity("trigger1", "group1")
//                .withIdentity("myTrigger")
                .forJob("job1")
                .withSchedule(
                        dailyAtHourAndMinute(9,30)
                )
                .build();

        Trigger t2 = TriggerBuilder.newTrigger()
                .withIdentity("myTrigger2")
                .forJob("HelloJob2")
                .withSchedule(
                        dailyAtHourAndMinute(11,30)
                )
                .build();

        //触发器的配置

        /**
         * 配置相关的触发器
         */
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger1", "group1")
//                .withSchedule(
//                        SimpleScheduleBuilder
//                                .simpleSchedule()
//                                .withIntervalInSeconds(2)
//                                .repeatForever()
//                                .withMisfireHandlingInstructionNextWithExistingCount()
//                )
//                .withSchedule(  dailyAtHourAndMinute(15,44))
                .withSchedule(

                        cronSchedule("0 42 10 * * ?")
                )

                .build();

        //设置job的Listener
//        scheduler.getListenerManager().addJobListener(myJobListener, jobKeyEquals(jobKey("myJobName", "myJobGroup")));


        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        Date parse = df.parse("2019-05-21 15:59:10");


        /**
         * simpleTrigger
         */
        SimpleTrigger simpleTrigger = (SimpleTrigger) TriggerBuilder.newTrigger()
                .withIdentity("simpleTriger","group1")
                .startAt(parse)
                .forJob("job1","group1")
                .build()
                ;

        // 最后让我们的job按照触发器的配置进行运作
//        scheduler.scheduleJob(job, trigger);
        scheduler.scheduleJob(job, simpleTrigger);
        // scheduler.shutdown();
    }
}
