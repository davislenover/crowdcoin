package com.crowdcoin.mainBoard.grade;

import com.crowdcoin.networking.sqlcom.data.SQLTable;

import java.util.List;

public class GradeTools {

    private static int coinIDColumnIndex = 0;

    public static int getGradeAverage(String coinID, SQLTable sqlTable) {

        try {
            List<List<Object>> row = sqlTable.getSpecificRows(0,coinID,1,0,sqlTable.getNumberOfColumns());
        } catch (Exception e) {

        }

    }

}
