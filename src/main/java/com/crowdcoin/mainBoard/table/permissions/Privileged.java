package com.crowdcoin.mainBoard.table.permissions;

/**
 * Signifies a class contains one or more {@link com.crowdcoin.mainBoard.table.permissions.Permission}s. Classes can check {@link com.crowdcoin.mainBoard.table.permissions.Permission}s on this object
 */
public interface Privileged {

    /**
     * Checks if a class contains a certain permission. This does not mean the permission values are the same.
     * @param permission the given permission as a permission object
     * @return true if the class contains the given permission object, false otherwise.
     */
    boolean containsPermission(Permission permission);

    /**
     * Checks if a class contains a certain permission. This does not mean the permission values are the same.
     * @param permission the given permission as a String object. The String value must exactly match that of the {@link Permission#getPermissionValue()}
     * @return true if the class contains the given permission object, false otherwise.
     */
    boolean containsPermission(String permission);


    /**
     * Gets the permission value within the instance given a Permission object
     * @param permission the given permission as a Permission object. Only the object has to match to return a valid result, NOT the actual permission value
     * @return true if the permission is allowed, false otherwise (including if the permission does NOT exist within the instance)
     */
    boolean checkPermissionValue(Permission permission);
    /**
     * Gets the permission value within the instance given a Permission object
     * @param permission the given permission as a String. The String value must exactly match that of the {@link Permission#getPermissionValue()}
     * @return true if the permission is allowed, false otherwise (including if the permission does NOT exist within the instance)
     */
    boolean checkPermissionValue(String permission);

    /**
     * Adds a Permission to the instance. Duplicate Permissions should not be allowed
     * @param permission the given permission to add as a Permission object
     * @return if the storage method containing the Permissions in the instance was modified as a result of this method call, false otherwise
     */
    boolean addPermission(Permission permission);

    /**
     * Removes a Permission from the instance
     * @param permissionIndex the given index corresponding to the Permission to be deleted within the storage solution of Permissions within the instance
     * @return if the storage method containing the Permissions in the instance was modified as a result of this method call, false otherwise
     */
    boolean removePermission(int permissionIndex);

}
