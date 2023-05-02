package com.crowdcoin.mainBoard.table;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ModelClassFactory {

        private static final String modelPrefix = "tableData_";

        public ModelClass build(Object classInstance) {

            List<Method> methodList = new ArrayList<>();

            for (Method methodCandidate : classInstance.getClass().getMethods()) {

                if (methodCandidate.isAnnotationPresent(TableReadable.class)) {

                    methodList.add(methodCandidate);

                }

            }

            Collections.sort(methodList, Comparator.comparingInt((Method o) -> o.getAnnotation(TableReadable.class).order()));
            return new ModelClass(classInstance,methodList);

        }

}
