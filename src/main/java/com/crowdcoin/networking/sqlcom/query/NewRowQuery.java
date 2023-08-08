package com.crowdcoin.networking.sqlcom.query;

import java.util.List;

public class NewRowQuery implements QueryBuilder {

    private String tableName;
    private List<String> columnNamesToInsertData;
    private List<String> specificData;

    public NewRowQuery(String tableName, List<String> columnNamesToInsertData, List<String> specificData) {
        this.tableName = tableName;
        this.columnNamesToInsertData = columnNamesToInsertData;
        this.specificData = specificData;
    }

    @Override
    public String getQuery() {
        String returnString = "INSERT INTO " + this.tableName + " (";

        for (int index = 0; index < this.columnNamesToInsertData.size(); index++) {

            if (index != this.columnNamesToInsertData.size()-1) {
                returnString+=this.columnNamesToInsertData.get(index)+",";
            } else {
                returnString+=this.columnNamesToInsertData.get(index)+")";
            }
        }

        returnString+=" VALUES (";

        for (int index = 0; index < this.specificData.size(); index++) {

            if (index != this.specificData.size()-1) {
                returnString+="'"+this.specificData.get(index)+"',";
            } else {
                returnString+="'"+this.specificData.get(index)+"')";
            }
        }

        return returnString;
    }
}
