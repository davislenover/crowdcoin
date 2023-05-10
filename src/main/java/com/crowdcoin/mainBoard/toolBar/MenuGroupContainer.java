package com.crowdcoin.mainBoard.toolBar;

import javafx.scene.control.ToolBar;

import java.util.List;

public class MenuGroupContainer {

    // Holds MenuGroups
    private ToolBar targetToolbar;
    private List<MenuGroup> menus;

    public MenuGroupContainer(ToolBar targetToolbar) {

        this.targetToolbar = targetToolbar;
        this.targetToolbar.getItems().clear();

    }

    public boolean addMenuGroup(MenuGroup menuGroup) {

        if (!this.menus.contains(menuGroup)) {

            this.menus.add(menuGroup);

            return this.targetToolbar.getItems().add(menuGroup.getMenuButton());

        }

        return false;

    }

    public boolean removeMenuGroup(MenuGroup menuGroup) {

        if (this.menus.contains(menuGroup)) {

            this.menus.remove(menuGroup);

            return this.targetToolbar.getItems().remove(menuGroup.getMenuButton());

        }

        return false;

    }


}
