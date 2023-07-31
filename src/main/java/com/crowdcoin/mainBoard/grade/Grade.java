package com.crowdcoin.mainBoard.grade;

/**
 * Grading Enums used for grading coins
 */
public enum Grade {
    Pass(2,2,2.999),Exceed(3,3,3.999),Fail(1,1,1.999);
    private int gradeCode;
    private double gradeLowRange;
    private double gradeHighRange;

    /**
     * Instantiates a Grade Enum with a Grade code
     * @param gradeCode an Integer value used to compare other Grade Codes. The higher the grade code, the more pretentious the Grade is
     * @param lowRange the lower bound used to check if a value is in a grade range
     * @param highRange the upper bound used to check if a value is in a grade range
     */
    Grade(int gradeCode, double lowRange, double highRange) {
        this.gradeLowRange = lowRange;
        this.gradeHighRange = highRange;
        this.gradeCode = gradeCode;
    }

    /**
     * Gets the given Enum Grade Code
     * @return the Grade Code as an Integer
     */
    public int getGradeCode() {
        return this.gradeCode;
    }
    public boolean isInRange(double value) {
        return (value >= this.gradeLowRange && value <= this.gradeHighRange);
    }
}
