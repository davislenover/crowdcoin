package com.crowdcoin.mainBoard.grade;

import com.crowdcoin.networking.sqlcom.data.SQLTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains Grading tools for determining the Grade of a specified coinID
 */
public class GradeTools {

    private static int gradeColumnStartIndex = 2;
    private List<Integer> gradeList;

    /**
     * Creates a GradeTools object. Operates on the corresponding coinID specified
     * @param coinID the given coinID to check the Grade for
     * @param table the given SQLTable accessing the grading table within the SQL database
     */
    public GradeTools(String coinID, SQLTable table) {
        this.gradeList = getGradeList(coinID,table);
    }

    /**
     * Gets the grade average from a given coinID. Iterates over all grader cells and returns the average from all their responses
     * @return the grade average as a Double value
     */
    public double getGradeAverage() {
        int sum = 0;
        for (Integer grade : this.gradeList) {
            sum+=grade;
        }
        return (sum/this.gradeList.size());
    }

    /**
     * Gets the Grade enum based on the average from a given coinID. The average is looked up in grade ranges and the Grade that contains the corresponding average within it's range is returned.
     * If more than one Grade range overlap (and the average exists within both), the Grade checked first will be returned
     * @return the corresponding Grade based on the average calculation by {@link GradeTools#getGradeAverage()}. If no grade range is found, returns {@link Grade#Fail}
     */
    public Grade getGrade() {

        double average = getGradeAverage();
        for (Grade grade : Grade.values()) {
            if (grade.isInRange(average)) {
                return grade;
            }
        }
        return Grade.Fail;
    }

    /**
     * Checks if all graders have graded the corresponding coinID
     * @return true if all graders have graded the coinID, false otherwise
     */
    public boolean hasEveryoneGradedID() {
        for (Integer grade : this.gradeList) {
            if (grade.intValue() == 0) {
                return false;
            }
        }
        return true;
    }

    // Gets the grade list as a list of Integers
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
