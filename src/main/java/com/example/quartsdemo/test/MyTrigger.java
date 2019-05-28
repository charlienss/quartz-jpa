//package com.example.quartsdemo.test;
//
//
//import org.quartz.Calendar;
//import org.quartz.JobExecutionContext;
//import org.quartz.JobExecutionException;
//import org.quartz.Trigger;
//
//import java.util.Date;
//
//public class MyTrigger extends Trigger {
//
//    @Override
//    public void triggered(Calendar calendar) {
//
//    }
//    @Override
//    public Date computeFirstFireTime(Calendar calendar) {
//        return null;
//    }
//    @Override
//    public int executionComplete(JobExecutionContext jobExecutionContext, JobExecutionException e) {
//        return 0;
//    }
//
//    @Override
//    public boolean mayFireAgain() {
//        return false;
//    }
//
//    @Override
//    public Date getStartTime() {
//        return null;
//    }
//
//    @Override
//    public void setStartTime(Date date) {
//
//    }
//
//    @Override
//    public void setEndTime(Date date) {
//
//    }
//
//    @Override
//    public Date getEndTime() {
//        return null;
//    }
//
//    @Override
//    public Date getNextFireTime() {
//        return null;
//    }
//
//    @Override
//    public Date getPreviousFireTime() {
//        return null;
//    }
//
//    @Override
//    public Date getFireTimeAfter(Date date) {
//        return null;
//    }
//
//    @Override
//    public Date getFinalFireTime() {
//        return null;
//    }
//
//    @Override
//    protected boolean validateMisfireInstruction(int i) {
//        return false;
//    }
//
//    @Override
//    public void updateAfterMisfire(Calendar calendar) {
//
//    }
//
//    @Override
//    public void updateWithNewCalendar(Calendar calendar, long l) {
//
//    }
//}
