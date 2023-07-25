package com.crowdcoin.mainBoard.table;

import java.util.HashMap;
import java.util.Map;

public class userModel {

    private int coinID;
    private Integer isGraded;
    private Map<Integer,String> variableMap;

    public userModel(Integer coinID, Integer isGraded, String ... mapArgs) {
        this.variableMap = new HashMap<>();

        this.coinID = coinID;
        this.isGraded = isGraded;

        int prefixIndex = 1;
        for (String arg : mapArgs) {
            this.variableMap.put(prefixIndex,arg);
            prefixIndex++;
        }

    }

    @TableReadable(order = 0, columnName = "coinID")
    public Integer coinID() {
        return this.coinID;
    }

    @TableReadable(order = 1, columnName = "IsGraded")
    public Integer isGraded() {
        return this.isGraded;
    }

    @TableReadable(order = 2, columnName = "USERID", variableName = "USERID")
    public String USERID(int positionToGet) {
        return this.variableMap.get(positionToGet);
    }


}
