package com.crowdcoin.mainBoard.table;

import javafx.beans.property.StringProperty;

public class CoinModel {

    private String certCompany;
    private String certNumber;
    private int coinID;
    private String dateOfIssue;
    private String declaredValue;
    private String denomination;
    private String grade;

    // When creating a constructor for the model, make sure that the types match that of what is read from the actual database table
    public CoinModel(String certCompany,String certNumber,Integer coinID,String dateOfIssue,String declaredValue,String denomination,String grade) {

        this.certCompany = certCompany;
        this.certNumber = certNumber;
        this.coinID = coinID;
        this.dateOfIssue = dateOfIssue;
        this.declaredValue = declaredValue;
        this.denomination = denomination;
        this.grade = grade;

    }

    @TableReadable (order = 0)
    public String certCompany() {
        return certCompany;
    }

    @TableReadable (order = 1)
    public String certNumber() {
        return certNumber;
    }

    @TableReadable (order = 2)
    public int coinID() {
        return coinID;
    }

    @TableReadable (order = 3)
    public String dateOfIssue() {
        return dateOfIssue;
    }

    @TableReadable (order = 4)
    public String declaredValue() {
        return declaredValue;
    }

    @TableReadable (order = 5)
    public String denomination() {
        return denomination;
    }

    @TableReadable (order = 6)
    public String grade() {
        return grade;
    }

    public void setCoinID(int newId) {

        this.coinID = newId;

    }

}
