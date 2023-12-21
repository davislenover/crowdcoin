package com.crowdcoin.mainBoard.table.modelClass.models;

import com.crowdcoin.mainBoard.table.modelClass.TableReadable;

public class CoinModel {
    private Double certNumber;
    private String certGrade;
    private String certCompany;
    private String denomination;
    private String dateOfIssue;
    private Integer subId;

    // When creating a constructor for the model, make sure that the types match that of what is read from the actual database table
    public CoinModel(Double certificationNumber, String certificationGrade, String certificationCompany, String denomination, String dateOfIssue, Integer submissionId) {

        this.certNumber = certificationNumber;
        this.certGrade = certificationGrade;
        this.certCompany = certificationCompany;
        this.denomination = denomination;
        this.dateOfIssue = dateOfIssue;
        this.subId = submissionId;

    }

    @TableReadable(order = 0, columnName = "certificationnumber")
    public Double certNumber() {
        return certNumber;
    }

    @TableReadable (order = 1, columnName = "certificationgrade")
    public String certGrade() {
        return certGrade;
    }

    @TableReadable (order = 2, columnName = "certificationcompany")
    public String certCompany() {
        return certCompany;
    }

    @TableReadable (order = 3, columnName = "denomination")
    public String denomination() {
        return denomination;
    }

    @TableReadable (order = 4, columnName = "dateofissue")
    public String dateOfIssue() {
        return dateOfIssue;
    }

    @TableReadable (order = 5, columnName = "submissionid")
    public Integer submissionId() {
        return subId;
    }

}
