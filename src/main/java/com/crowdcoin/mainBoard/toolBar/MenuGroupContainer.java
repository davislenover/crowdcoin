package com.crowdcoin.mainBoard.toolBar;

import javafx.scene.control.ToolBar;

import java.util.ArrayList;
import java.util.List;

public class MenuGroupContainer {

    // Holds MenuGroups
    private ToolBar targetToolbar;
    private List<MenuGroup> menus;

    public MenuGroupContainer(ToolBar targetToolbar) {

        this.menus = new ArrayList<>();

        this.targetToolbar = targetToolbar;
        this.targetToolbar.getItems().clear();

    }

    /**
     * Adds a MenuGroup the ToolBar. MenuGroups cannot contain the same name
     * @param menuGroup the given MenuGroup to add
     * @return true if the MenuGroup was added, false otherwise (i.e., the given MenuGroup shares a name with an element already present in the container)
     * @Note When a MenuGroup is added, the referenced MenuButton from the MenuGroup is cloned and thus, any changes made to the original MenuButton outside of this container will have no effect on the Toolbar
     */
    public boolean addMenuGroup(MenuGroup menuGroup) {

        if (!this.menus.contains(menuGroup)) {

            this.menus.add(menuGroup);

            return this.targetToolbar.getItems().add(menuGroup.getMenuButton());

        }

        return false;

    }

    /**
     * Removes a MenuGroup the ToolBar.
     * @param menuGroup the given MenuGroup to remove
     * @return true if the MenuGroup was remove, false otherwise (i.e., the given MenuGroup shares a name with an element already present in the container)
     */
    public boolean removeMenuGroup(MenuGroup menuGroup) {

        if (this.menus.contains(menuGroup)) {

            int removalIndex = this.menus.indexOf(menuGroup);
            this.menus.remove(menuGroup);

            // Note that the button within the target ToolBar list is a cloned version, thus, removal by passing in the original button object will not work
            // The menus list and the Toolbar list should share indices (i.e., the corresponding MenuGroup index within menus is the same as it's referenced cloned MenuButton object index within the Toolbar list)
            this.targetToolbar.getItems().remove(removalIndex);

            return true;

        }

        return false;

    }


}
