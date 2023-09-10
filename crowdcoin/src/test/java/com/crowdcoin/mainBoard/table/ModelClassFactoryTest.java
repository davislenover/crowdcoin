package com.crowdcoin.mainBoard.table;

import com.crowdcoin.exceptions.modelClass.InvalidVariableMethodParameterCount;
import com.crowdcoin.exceptions.modelClass.InvalidVariableMethodParameterTypeException;
import com.crowdcoin.exceptions.modelClass.MultipleVariableMethodsException;
import com.crowdcoin.exceptions.modelClass.NotZeroArgumentException;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

class ModelClassFactoryTest {

    @Test
    public void modelClassTest() throws NotZeroArgumentException, MultipleVariableMethodsException, InvalidVariableMethodParameterTypeException, InvalidVariableMethodParameterCount {

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
    public void testBlankClass() throws NotZeroArgumentException, MultipleVariableMethodsException, InvalidVariableMethodParameterTypeException, InvalidVariableMethodParameterCount {
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
            @TableReadable (columnName = "test")
            public int dummyMethod(int test) {
                return test;
            }

        }
        testClass test = new testClass();
        assertThrows(NotZeroArgumentException.class,() ->  (new ModelClassFactory()).build(test));
    }

    @Test
    public void testClone() throws NotZeroArgumentException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, MultipleVariableMethodsException, InvalidVariableMethodParameterTypeException, InvalidVariableMethodParameterCount {


        int testInteger = 2;
        String testString = "HelloWorld";
        double testDouble = 1.28;
        testModel testClass = new testModel(testInteger,testString,testDouble);
        ModelClassFactory factory = new ModelClassFactory();
        ModelClass builtClass = factory.build(testClass);
        ModelClass builtClone = factory.buildClone(builtClass,1,testString+"Clone",1.5);

        // Make sure builtClass is unchanged
        assertEquals(testString,builtClass.getData(0));
        assertEquals(testDouble,builtClass.getData(1));
        assertEquals(testInteger,builtClass.getData(2));

        // Test clone
        assertEquals(testString+"Clone",builtClone.getData(0));
        assertEquals(1.5,builtClone.getData(1));
        assertEquals(1,builtClone.getData(2));

    }

}