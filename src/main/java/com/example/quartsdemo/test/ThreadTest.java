package com.example.quartsdemo.test;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadTest {

   private final static ThreadLocal  threadLocal = new ThreadLocal();

   private static Semaphore sp = new Semaphore(10);

    public static void main(String[] args) {


        AtomicInteger a = new AtomicInteger(10);
        for (int i = 0; i < 20; i++) {
            new Thread(()->{
                try {
                    sp.acquire();
                    System.out.println(Thread.currentThread().getName());
            // threadLocal.set(a.getAndDecrement());
            //  System.out.println(threadLocal.get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    sp.release();
                }


            }).start();
        }


    }

}
