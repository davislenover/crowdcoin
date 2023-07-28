package com.crowdcoin.mainBoard.grade;

/**
 * Grading Enums used for grading coins
 */
public enum Grade {
    Pass(1),Exceed(2),NA(0);
    private int gradeCode;

    /**
     * Instantiates a Grade Enum with a Grade code
     * @param gradeCode an Integer value used to compare other Grade Codes. The higher the grade code, the more pretentious the Grade is
     */
    Grade(int gradeCode) {
        this.gradeCode = gradeCode;
    }

    /**
     * Gets the given Enum Grade Code
     * @return the Grade Code as an Integer
     */
    public int getGradeCode() {
        return this.gradeCode;
    }
}
