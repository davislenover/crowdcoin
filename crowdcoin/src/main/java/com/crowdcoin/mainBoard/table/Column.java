package com.crowdcoin.mainBoard.table;

import com.crowdcoin.mainBoard.table.permissions.Permission;
import com.crowdcoin.mainBoard.table.permissions.Privileged;
import com.crowdcoin.newwork.names.AliasedName;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * A class to house information about a column from an SQL database (such as column permissions). Classes looking to find more information other than the name of the column should look to this class to provide such info.
 */
public class Column extends AliasedName implements Privileged {

    private List<Permission> permissions;
    private String columnName;
    private Class columnDataType;
    private Method columnDataMethod;
    private int ordinalPosition = 0;
    private boolean isVariable = false;

    public Column(String columnName,Class columnDataType, Method columnDataMethod) {
        super(columnName, "");
        this.columnName = columnName;
        this.permissions = new ArrayList<>();
        this.columnDataType = columnDataType;
        this.columnDataMethod = columnDataMethod;
    }

    public Column(String columnName, String alias, Class columnDataType, Method columnDataMethod) {
        super(columnName, alias);
        this.columnName = columnName;
        this.permissions = new ArrayList<>();
        this.columnDataType = columnDataType;
        this.columnDataMethod = columnDataMethod;
    }

    /**
     * Sets if a given Column contains a variable method (true means the Column object is responsible for an arbitrary number of columns in the database)
     */
    public void setVariable(boolean setBool) {
        this.isVariable = setBool;
    }

    /**
     * Gets if a given Column contains a variable method (true means the Column object is responsible for an arbitrary number of columns in the database)
     */
    public boolean isVariable() {
        return this.isVariable;
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

    /**
     * Gets the ordinal position of the column. Typically set by an {@link com.crowdcoin.networking.sqlcom.data.SQLTable} object
     * @return the ordinal position as an Integer. If not previously set, default 0
     */
    public int getOrdinalPosition() {
        return this.ordinalPosition;
    }

    /**
     * Sets the ordinal position of the column. Typically set by an {@link com.crowdcoin.networking.sqlcom.data.SQLTable} object
     * @param ordinalPosition the ordinal position of the column as an Integer
     */
    public void setOrdinalPosition(int ordinalPosition) {
        this.ordinalPosition = ordinalPosition;
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

    /**
     * Gets the column name formatted for a SELECT query (SELECT). If the Column has an alias, the name and the alias will be concatenated via: "name AS alias"
     * @return a {@link String} object
     */
    public String getColumnAsQuery() {
        return super.getAlias().isEmpty() ? super.getName() : super.getName() + " AS " + super.getAlias();
    }

}
