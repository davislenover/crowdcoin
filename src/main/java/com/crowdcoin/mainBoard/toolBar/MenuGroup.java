package com.crowdcoin.mainBoard.toolBar;

import javafx.scene.control.MenuButton;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MenuGroup implements Cloneable,Iterable<MenuOption> {

    // Groups together MenuOptions to form one menu
    private List<MenuOption> menuOptions;
    private String groupName;
    private MenuButton menuButtonObject;

    public MenuGroup(String groupName) {
        this.menuOptions = new ArrayList<>();
        this.groupName = groupName;
        this.menuButtonObject = new MenuButton(this.groupName);

    }

    /**
     * Add a MenuOption object to the MenuGroup. The MenuButton JavaFX object associated is automatically updated (non-clone). No MenuOption can contain the same name
     * @param option the given MenuOption to add
     * @return true if the given MenuOption object was added, false otherwise (i.e., if the MenuOption shares a name with an element present within the list)
     */
    public boolean addOption(MenuOption option) {

        // contains() calls equals() to check for object matches
        // Thus this checks if a MenuOption already within the list contains the same name
        if (!this.menuOptions.contains(menuOptions)) {

            // Add to list
            boolean result = this.menuOptions.add(option);

            // Check to make sure option was added to internal list before applying to MenuButton object
            if (result) {
                // Add option to MenuButton
                return this.menuButtonObject.getItems().add(option.getMenuItem());
            }
        }

        return false;

    }

    /**
     * Remove a MenuOption object from the MenuGroup. The MenuButton JavaFX object associated is automatically updated (non-clone).
     * @param option the given MenuOption to remove
     * @return true if the given MenuOption object was removed, false otherwise
     */
    public boolean removeOption(MenuOption option) {

        int removalIndex = this.menuOptions.indexOf(option);
        // -1 indicates no such index was found
        if (removalIndex == (-1)) {
            return false;
        }

        this.menuOptions.remove(option);
        // Index corresponds to MenuItem to remove (this is because MenuItem is cloned so attempting to remove original MenuItem will not work)
        this.menuButtonObject.getItems().remove(removalIndex);

        return true;

    }

    /**
     * Remove a MenuOption object from the MenuGroup. The MenuButton JavaFX object associated is automatically updated (non-clone).
     * @param index the given index of the MenuOption to remove
     * @throws IndexOutOfBoundsException if the given index is not within the range of the list
     */
    public void removeOption(int index) throws IndexOutOfBoundsException {
        this.menuOptions.remove(index);
        this.menuButtonObject.getItems().remove(index);
    }

    public String getGroupName() {
        return this.groupName;
    }

    /**
     * Gets the associated MenuButton JavaFX object from the given MenuGroup instance. Note that the MenuButton referenced to the MenuGroup is cloned (as the MenuGroup instance, including all MenuOptions associated, are cloned)/
     * This means any changes to the MenuButton JavaFX object do not affect the original MenuGroup instance
     * @return the cloned MenuButton JavaFX object
     */
    public MenuButton getMenuButton() {
        MenuGroup clonedObject = this.clone();
        return clonedObject.menuButtonObject;
    }


    @Override
    public MenuGroup clone() {

        MenuGroup returnClone = new MenuGroup(this.groupName);

        for (MenuOption currentOption : this) {
            returnClone.addOption(currentOption.clone());
        }

        return returnClone;

    }

    @Override
    public boolean equals(Object o) {

        // Likewise with MenuOptions, MenuGroups are also compared by their names
        try {
            MenuGroup groupToCompare = (MenuGroup) o;
            if (groupToCompare.getGroupName().equals(this.groupName)) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }

        return false;

    }

    @Override
    public int hashCode() {
        return this.groupName.hashCode();
    }


    @Override
    public Iterator iterator() {
        return this.menuOptions.iterator();
    }
}
