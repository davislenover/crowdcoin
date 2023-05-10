package com.crowdcoin.mainBoard.toolBar;


import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;

public class MenuOption implements Cloneable {

    // An option to be placed in a MenuGroup
    private String optionName;
    private MenuOptionActionEvent actionEvent;
    private MenuItem menuItemObject;

    /**
     * Houses a MenuItem object and it's corresponding MenuOptionActionEvent
     * @param optionName the display name of the option
     * @param actionEvent the MenuOptionActionEvent to invoke when the option is selected by the user
     */
    public MenuOption(String optionName, MenuOptionActionEvent actionEvent) {
        this.optionName = optionName;
        this.actionEvent = actionEvent;

        // Create new MenuItem with given name
        this.menuItemObject = new MenuItem(this.optionName);
        // Set to invoke given actionEvent on any action (such as selection the option), passes in itself as parameter
        this.menuItemObject.setOnAction(event -> this.actionEvent.menuOptionActionHandler(this));

    }

    public String getName() {
        return this.optionName;
    }

    /**
     * Gets the MenuItem JavaFX object associated with the class. Note, the given MenuOption instance is cloned and the cloned MenuItem is returned.
     * This means any changes to the MenuItem object do not affect the original MenuOption class
     * @return cloned MenuItem JavaFX object
     */
    public MenuItem getMenuItem() {
        return this.clone().menuItemObject;
    }

    /**
     * Sets the invoked action method (overrides the previous one from instantiation)
     * @param actionEvent the action event to set the MenuOption to
     */
    public void setMenuOptionActionEvent(MenuOptionActionEvent actionEvent) {
        this.actionEvent = actionEvent;
        this.menuItemObject.setOnAction(event -> this.actionEvent.menuOptionActionHandler(this));
    }

    @Override
    public boolean equals(Object o) {

        // To compare two MenuItems, they are considered to be the same if they contain the same name
        // This is the case as it would not make sense to have two of the same option names within a MenuGroup
        try {
            MenuOption compareOption = (MenuOption) o;
            if (compareOption.getName().equals(this.optionName)) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }

        return false;

    }

    @Override
    public int hashCode() {
        // Because MenuOptions are compared by names, simply return the hashCode of the name String
        return this.optionName.hashCode();
    }

    @Override
    public MenuOption clone() {
        return new MenuOption(this.optionName,this.actionEvent);
    }

}
