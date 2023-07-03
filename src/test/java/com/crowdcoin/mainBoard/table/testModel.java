package com.crowdcoin.mainBoard.table;

public class testModel {

    private int value1;
    private String value2;
    private double value3;

    public testModel(Integer value1,String value2,Double value3) {
        this.value1=value1;
        this.value2=value2;
        this.value3=value3;
    }

    @TableReadable (order = 2,columnName = "test")
    public int testMethod1() {
        return this.value1;
    }

    @TableReadable (order = 0,columnName = "test")
    public String testMethod2() {
        return this.value2;
    }

    @TableReadable (order = 1,columnName = "test")
    public double testMethod3() {
        return this.value3;
    }

    public String dummyMethod() {
        return "false";
    }
}
