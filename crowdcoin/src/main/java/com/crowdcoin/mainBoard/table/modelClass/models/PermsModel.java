package com.crowdcoin.mainBoard.table.modelClass.models;

import com.crowdcoin.mainBoard.table.modelClass.TableReadable;

public class PermsModel {

    private String userName;

    public PermsModel(String userName) {
        this.userName = userName;
    }

    @TableReadable(order = 0, columnName = "userName")
    public String userName() {
        return this.userName;
    }

}
