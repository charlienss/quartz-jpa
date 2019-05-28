package com.example.quartsdemo.test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogTest {


    public static void main(String[] args) {
        log.warn("JobBeanFactory global switch shutdown, paused job name:{}", "jingba");
        System.out.println(String.format("测试:%s--%d","jjkkk",2));
    }


}
