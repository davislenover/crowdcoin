package com.crowdcoin.mainBoard.table;

import java.util.HashMap;
import java.util.Map;

public class userModel {

    private int coinID;
    private boolean isGraded;
    private Map<Integer,Object> variableMap;

    public userModel(Integer coinID, Boolean isGraded, Object ... mapArgs) {
        this.variableMap = new HashMap<>();

        this.coinID = coinID;
        this.isGraded = isGraded;

        int prefixIndex = 1;
        for (Object arg : mapArgs) {
            this.variableMap.put(prefixIndex,arg);
            prefixIndex++;
        }

    }

    @TableReadable(order = 0, columnName = "coinID")
    public Integer coinID() {
        return this.coinID;
    }

    @TableReadable(order = 1, columnName = "IsGraded")
    public Boolean isGraded() {
        return this.isGraded;
    }

    @TableReadable(order = 2, columnName = "USERID-", variableName = "USERID-")
    public Object USERID(Integer positionToGet) {
        return this.variableMap.get(positionToGet);
    }


}
