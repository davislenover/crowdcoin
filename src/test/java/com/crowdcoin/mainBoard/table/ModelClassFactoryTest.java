package com.crowdcoin.mainBoard.table;

import com.crowdcoin.exceptions.modelClass.NotZeroArgumentException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ModelClassFactoryTest {

    @Test
    public void modelClassTest() throws NotZeroArgumentException {

        class testModel {

            private int value1;
            private String value2;
            private double value3;

            public testModel(int value1,String value2,double value3) {
                this.value1=value1;
                this.value2=value2;
                this.value3=value3;
            }

            @TableReadable (order = 2)
            public int testMethod1() {
                return this.value1;
            }

            @TableReadable (order = 0)
            public String testMethod2() {
                return this.value2;
            }

            @TableReadable (order = 1)
            public double testMethod3() {
                return this.value3;
            }

            public String dummyMethod() {
                return "false";
            }

        }

        int testInteger = 2;
        String testString = "HelloWorld";
        double testDouble = 1.28;

        testModel testClass = new testModel(testInteger,testString,testDouble);
        ModelClass builtClass = (new ModelClassFactory()).build(testClass);

        assertEquals(testString,builtClass.getData(0));
        assertEquals(testDouble,builtClass.getData(1));
        assertEquals(testInteger,builtClass.getData(2));
        assertThrows(IndexOutOfBoundsException.class,() -> builtClass.getData(3));

    }

    @Test
    public void testBlankClass() throws NotZeroArgumentException {
        class blankClass {
            public int dummyMethod() {
                return 0;
            }

        }

        blankClass blank = new blankClass();
        ModelClass builtClass = (new ModelClassFactory()).build(blank);
        assertThrows(IndexOutOfBoundsException.class,() -> builtClass.getData(0));


    }

    @Test
    public void testNotZeroArgs() {
        class testClass {
            @TableReadable
            public int dummyMethod(int test) {
                return test;
            }

        }
        testClass test = new testClass();
        assertThrows(NotZeroArgumentException.class,() ->  (new ModelClassFactory()).build(test));
    }

}