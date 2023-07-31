package com.crowdcoin.mainBoard.grade;

import com.crowdcoin.networking.sqlcom.data.SQLTable;

import java.util.ArrayList;
import java.util.List;

public class GradeTools {

    private static int gradeColumnStartIndex = 2;

    private List<Integer> gradeList;

    public GradeTools(String coinID, SQLTable table) {
        this.gradeList = getGradeList(coinID,table);
    }

    public double getGradeAverage() {
        int sum = 0;
        for (Integer grade : this.gradeList) {
            sum+=grade;
        }
        return (sum/this.gradeList.size());
    }

    public Grade getGrade() {

        double average = getGradeAverage();
        for (Grade grade : Grade.values()) {
            if (grade.isInRange(average)) {
                return grade;
            }
        }
        return Grade.Fail;
    }

    public boolean hasEveryoneGradedID() {
        for (Integer grade : this.gradeList) {
            if (grade.intValue() == 0) {
                return false;
            }
        }
        return true;
    }

    private List<Integer> getGradeList(String coinID, SQLTable sqlTable) {
        try {
            List<Object> row = sqlTable.getRawSpecificRows(0,coinID,1,0,sqlTable.getNumberOfColumns()).get(0);
            List<Integer> gradeList = new ArrayList<>();
            for (int index = gradeColumnStartIndex; index < row.size(); index++) {
                gradeList.add(Integer.valueOf(row.get(index).toString()));
            }
            return gradeList;
        } catch (Exception e) {
            // TODO Error handling
        }
        return null;
    }

}
