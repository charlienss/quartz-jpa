package com.example.quartsdemo.test;

public class Test01 {


    public static void main(String[] args) {

//        System.out.println(EnumTest.SPRING.getVal());

        EnumTest enumDemo = EnumTest.getEnum("SPRING");


        System.out.println(enumDemo);

        System.out.println(enumDemo.getVal());





    }


}
