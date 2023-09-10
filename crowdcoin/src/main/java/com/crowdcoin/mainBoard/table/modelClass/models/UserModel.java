package com.crowdcoin.mainBoard.table.modelClass.models;

import com.crowdcoin.mainBoard.table.modelClass.DynamicModelClass;
import com.crowdcoin.mainBoard.table.modelClass.TableReadable;

import java.util.HashMap;
import java.util.Map;

public class UserModel {

    private int coinID;
    private Double isGraded;
    private Map<Integer,String> variableMap;

    public UserModel(Integer coinID, Double isGraded, String ... mapArgs) {
        this.variableMap = new HashMap<>();

        this.coinID = coinID;
        this.isGraded = isGraded;

        int prefixIndex = DynamicModelClass.getStartIndex();
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
    public Double isGraded() {
        return this.isGraded;
    }

    @TableReadable(order = 2, columnName = "USERID", isVariable = true)
    public String USERID(int positionToGet) {
        return this.variableMap.get(positionToGet);
    }


}
