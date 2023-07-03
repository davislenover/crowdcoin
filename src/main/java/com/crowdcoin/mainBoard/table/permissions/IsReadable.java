package com.crowdcoin.mainBoard.table.permissions;

public class IsReadable implements Permission {

    private boolean permissionValue;

    public IsReadable(boolean setPermission) {
        this.permissionValue = setPermission;
    }

    @Override
    public boolean getPermissionValue() {
        return this.permissionValue;
    }

}
