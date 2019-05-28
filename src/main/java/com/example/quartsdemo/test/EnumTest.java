package com.example.quartsdemo.test;

public enum EnumTest {

    SPRING("SPRING", 1),
    SUMMER("SUMMER", 2);

    private String name;

    private int val;


    public static EnumTest getEnum(String name) {
        for (EnumTest enumTest : EnumTest.values()) {
            System.out.println(enumTest);

            if (enumTest.getName().equalsIgnoreCase(name)){
                System.out.println(enumTest.getName());
                return enumTest;
            }
        }
        return null;
    }

    EnumTest(String name, int val) {
        this.name = name;
        this.val = val;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVal() {
        return val;
    }

    public void setVal(int val) {
        this.val = val;
    }


    //    @Test
//    public void DemoTest() {
//
//        System.out.println(EnumTest.SPRING);
//
//    }


}
