package com.crowdcoin.mainBoard.table.permissions;

public class IsWriteable implements Permission {
    private boolean permissionValue;

    public IsWriteable(boolean setPermission) {
        this.permissionValue = setPermission;
    }

    @Override
    public boolean getPermissionValue() {
        return this.permissionValue;
    }
}
