package com.example.quartsdemo.test;

import org.quartz.*;

import java.util.ArrayList;

/**
 * 实现数据的自动注入
 */
public class DumbJob implements Job {

    String jobSays;
    float myFloatValue;
    ArrayList state;

    public DumbJob() {
    }

    public void execute(JobExecutionContext context)
            throws JobExecutionException
    {
        JobKey key = context.getJobDetail().getKey();

        JobDataMap dataMap = context.getMergedJobDataMap();  // Note the difference from the previous example

//        state.add(new Date());

//        System.out.println("state-->"+state);

        System.err.println("Instance " + key + " of DumbJob says: " + jobSays + ", and val is: " + myFloatValue);
    }

    public void setJobSays(String jobSays) {
        this.jobSays = jobSays;
    }

    public void setMyFloatValue(float myFloatValue) {
        myFloatValue = myFloatValue;
    }

    public void setState(ArrayList state) {
        state = state;
    }

}
