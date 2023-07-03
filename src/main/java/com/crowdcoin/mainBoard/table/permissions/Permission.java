package com.crowdcoin.mainBoard.table.permissions;

/**
 * Classes implementing define a certain permission. Typically, permissions are attached to other objects that define their behaviour. Other classes may use this permission to determine appropriate logic for a given object.
 * Implementing classes should only provide setting the permission value boolean on instantiation, nowhere else
 */
public interface Permission {

    /**
     * Gets a boolean that determines if the permission is granted.
     * @return true if the permission is granted, false otherwise
     */
    boolean getPermissionValue();

    /**
     * Gets the name of the permission. This is typically the same as the name of the implementing class (default method gets the simple name of the class)
     * @return the name of the permission as a String
     */
    default String getPermissionName() {
        return this.getClass().getSimpleName();
    }


}
