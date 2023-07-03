package com.crowdcoin.mainBoard.table;

public class CoinModel {

    private String certCompany;
    private String certNumber;
    private int coinID;
    private String dateOfIssue;
    private String declaredValue;
    private String denomination;
    private String grade;

    // When creating a constructor for the model, make sure that the types match that of what is read from the actual database table
    public CoinModel(Integer coinID, String denomination, String dateOfIssue,String grade, String certCompany,String certNumber,String declaredValue) {

        this.certCompany = certCompany;
        this.certNumber = certNumber;
        this.coinID = coinID;
        this.dateOfIssue = dateOfIssue;
        this.declaredValue = declaredValue;
        this.denomination = denomination;
        this.grade = grade;

    }

    @TableReadable (order = 4, columnName = "certificationCompany")
    public String certCompany() {
        return certCompany;
    }

    @TableReadable (order = 5, columnName = "certificationNumber")
    public String certNumber() {
        return certNumber;
    }

    @TableReadable (order = 0, columnName = "coinID")
    public int coinID() {
        return coinID;
    }

    @TableReadable (order = 2, columnName = "dateOfIssue")
    public String dateOfIssue() {
        return dateOfIssue;
    }

    @TableReadable (order = 6, columnName = "declaredValue")
    public String declaredValue() {
        return declaredValue;
    }

    @TableReadable (order = 1, columnName = "denomination")
    public String denomination() {
        return denomination;
    }

    @TableReadable (order = 3, columnName = "grade")
    public String grade() {
        return grade;
    }

    public void setCoinID(int newId) {

        this.coinID = newId;

    }

}
