package com.example.quartsdemo.test;

import org.quartz.*;

//这个是job的具体内容
public class HelloJob   implements Job {
    public HelloJob() {
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        /**
         * JobExecutionContext中包含对
         * trigger的引用，JobDetail对象引用，以及一些其它信息。
         */
        System.out.println("job1开始执行了");

        JobKey key = context.getJobDetail().getKey();

//        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        JobDataMap jobDataMap = context.getMergedJobDataMap();

        String jobSays = jobDataMap.getString("myJob");


        float myFloatValue = jobDataMap.getFloat("myFloatValue");

//        ArrayList state = (ArrayList)jobDataMap.get("myStateData");
//        state.add(new Date());

        System.err.println("Instance " + key + " of HelloJob says: " + jobSays + ", and val is: " + myFloatValue);


    }
}
