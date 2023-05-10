package com.crowdcoin.mainBoard.toolBar;

import javafx.scene.control.MenuButton;

import java.util.ArrayList;
import java.util.List;

public class MenuGroup {

    // Groups together MenuOptions to form one menu
    private List<MenuOption> menuOptions;
    private String groupName;
    private MenuButton menuButtonObject;

    public MenuGroup(String groupName) {
        this.menuOptions = new ArrayList<>();
        this.groupName = groupName;
        this.menuButtonObject = new MenuButton(this.groupName);

    }

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

    public boolean removeOption(MenuOption option) {
        boolean result = this.menuOptions.remove(option);
        if (result) {
            return this.menuButtonObject.getItems().remove(option.getMenuItem());
        }

        return false;

    }

    public String getGroupName() {
        return this.groupName;
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


}
