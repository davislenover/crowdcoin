package com.crowdcoin.mainBoard.table;

import com.crowdcoin.mainBoard.table.permissions.Permission;
import com.crowdcoin.mainBoard.table.permissions.Privileged;

import java.util.ArrayList;
import java.util.List;

/**
 * A class to house information about a column from an SQL database (such as column permissions). Classes looking to find more information other than the name of the column should look to this class to provide such info.
 */
public class Column implements Privileged {

    private List<Permission> permissions;
    private String columnName;
    private Class columnDataType;

    public Column(String columnName,Class columnDataType) {
        this.columnName = columnName;
        this.permissions = new ArrayList<>();
        this.columnDataType = columnDataType;
    }

    /**
     * Gets the column name from the database
     * @return
     */
    public String getColumnName() {
        return this.columnName;
    }

    /**
     * Gets the column data type
     * @return the Class of the data type
     */
    public Class getColumnDataType() {
        return this.columnDataType;
    }

    @Override
    public boolean containsPermission(Permission permission) {
        return this.permissions.contains(permission);
    }

    @Override
    public boolean containsPermission(String permission) {

        for (Permission perm : this.permissions) {
            if (perm.getPermissionName().equals(permission)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean checkPermissionValue(Permission permission) {

        if (this.permissions.contains(permission)) {
            return this.permissions.get(this.permissions.indexOf(permission)).getPermissionValue();
        }

        return false;
    }

    @Override
    public boolean checkPermissionValue(String permission) {

        if (this.containsPermission(permission)) {
            for (Permission perm : this.permissions) {
                if (perm.getPermissionName().equals(permission)) {
                    return perm.getPermissionValue();
                }
            }
        }

        return false;
    }

    @Override
    public boolean addPermission(Permission permission) {

        if (!this.permissions.contains(permission)) {
            return this.permissions.add(permission);
        }

        return false;
    }

    @Override
    public boolean removePermission(int permissionIndex) {
        this.permissions.remove(permissionIndex);
        return true;
    }
}
