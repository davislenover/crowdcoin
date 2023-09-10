package com.crowdcoin.mainBoard.table;

public class permsModel {

    private String userName;

    public permsModel(String userName) {
        this.userName = userName;
    }

    @TableReadable(order = 0, columnName = "userName")
    public String userName() {
        return this.userName;
    }

}
