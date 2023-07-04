package com.crowdcoin.mainBoard.table;

import com.crowdcoin.mainBoard.table.permissions.Permission;
import com.crowdcoin.mainBoard.table.permissions.Privileged;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * A class to house information about a column from an SQL database (such as column permissions). Classes looking to find more information other than the name of the column should look to this class to provide such info.
 */
public class Column implements Privileged {

    private List<Permission> permissions;
    private String columnName;
    private Class columnDataType;
    private Method columnDataMethod;

    public Column(String columnName,Class columnDataType, Method columnDataMethod) {
        this.columnName = columnName;
        this.permissions = new ArrayList<>();
        this.columnDataType = columnDataType;
        this.columnDataMethod = columnDataMethod;
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

    /**
     * Gets the corresponding method which is used by the ModelClass to get data for the specific column within the SQL table
     * @return the corresponding method as a Method object
     */
    public Method getColumnDataMethod() {
        return this.columnDataMethod;
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
