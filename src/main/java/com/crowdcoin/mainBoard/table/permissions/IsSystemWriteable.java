package com.crowdcoin.mainBoard.table.permissions;

public class IsSystemWriteable implements Permission {

    private boolean permissionValue;

    public IsSystemWriteable(boolean setPermission) {
        this.permissionValue = setPermission;
    }

    @Override
    public boolean getPermissionValue() {
        return this.permissionValue;
    }

}
