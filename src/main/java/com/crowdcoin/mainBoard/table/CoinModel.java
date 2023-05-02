package com.crowdcoin.mainBoard.table;

import javafx.beans.property.StringProperty;

public class CoinModel {

    public String certCompany;
    public String certNumber;
    public int coinID;
    public String dateOfIssue;
    public String declaredValue;
    public String denomination;
    public String grade;

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
}
