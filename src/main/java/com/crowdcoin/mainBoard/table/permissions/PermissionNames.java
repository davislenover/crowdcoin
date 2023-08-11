package com.crowdcoin.mainBoard.table.permissions;

public enum PermissionNames {

    ISREADABLE(IsReadable.class.getSimpleName()),ISWRITEABLE(IsWriteable.class.getSimpleName()),ISSYSTEMWRITEABLE(IsSystemWriteable.class.getSimpleName());

    private String name;

    PermissionNames(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

}
