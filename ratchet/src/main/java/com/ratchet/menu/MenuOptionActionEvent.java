package com.ratchet.menu;

public interface MenuOptionActionEvent {

    /**
     * Invoked when a corresponding MenuOption is selected by the user. Intended to perform arbitrary logic that corresponds with the MenuOption selected.
     * @param option, the MenuOption that was selected. This field is automatically inserted into by the MenuOption class
     */
    void menuOptionActionHandler(MenuOption option);

}
