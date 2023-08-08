package com.crowdcoin.networking.sqlcom.query;

import java.util.List;

public class InsertIntoRowQuery implements QueryBuilder {

    private String tableName;
    private List<String> columnsToChange;

    private List<String> correspondingDataToWrite;
    private String whereColumn;
    private String whereDataInColumn;

    public InsertIntoRowQuery(String tableName, List<String> columnsToChange, List<String> correspondingDataToWrite, String whereColumn, String whereDataInColumn) {
        this.tableName = tableName;
        this.columnsToChange = columnsToChange;
        this.correspondingDataToWrite = correspondingDataToWrite;
        this.whereColumn = whereColumn;
        this.whereDataInColumn = whereDataInColumn;
    }


    @Override
    public String getQuery() {
        String returnString = "UPDATE " + this.tableName + " SET";

        for (int index = 0; index < this.correspondingDataToWrite.size(); index++) {

            if (index != this.correspondingDataToWrite.size()-1) {
                returnString+= " " + this.columnsToChange.get(index) + " = " + "'" + this.correspondingDataToWrite.get(index) + "',";
            } else {
                returnString+= " " + this.columnsToChange.get(index) + " = " + "'" + this.correspondingDataToWrite.get(index) + "'";
            }

        }

        returnString+= " WHERE " + this.whereColumn + " = " + "'" + this.whereDataInColumn + "'";

        return returnString;
    }
}
