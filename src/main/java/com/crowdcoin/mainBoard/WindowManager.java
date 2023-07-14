package com.crowdcoin.mainBoard;

import com.crowdcoin.FXTools.StageManager;
import com.crowdcoin.mainBoard.table.Observe.Observer;
import com.crowdcoin.mainBoard.table.Observe.WindowEvent;
import com.crowdcoin.mainBoard.table.Observe.WindowEventType;
import com.crowdcoin.mainBoard.window.PopWindow;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores PopWindows for Tabs. Used to close and open windows when a tab is switched/opened on a TabPane
 */
public class WindowManager implements Observer<WindowEvent,String> {

    public List<PopWindow> windows;

    /**
     * Creates a new window manager
     */
    public WindowManager() {
        this.windows = new ArrayList<>();
    }

    /**
     * Adds a PopWindow to the WindowManager. WindowManager will add itself as an observer of the PopWindow
     * @param window the given PopWindow to add
     * @return true if the collection storing PopWindows within the WindowManager was modified, false if the PopWindow is a duplicate (i.e., the PopWindow was not added) or otherwise
     */
    public boolean addWindow(PopWindow window) {

        if (!this.windows.contains(window)) {
            window.addObserver(this);
           return this.windows.add(window);
        }

        return false;

    }

    /**
     * Removes a PopWindow to the WindowManager. WindowManager will remove itself as an observer of the PopWindow
     * @param window the given PopWindow to remove
     * @return true if the collection storing PopWindows within the WindowManager was modified, false if the PopWindow was not found within the collection or otherwise
     */
    public boolean removeWindow(PopWindow window) {

        if (this.windows.contains(window)) {
            window.removeObserver(this);
            return this.windows.remove(window);
        }

        return false;

    }

    /**
     * Minimize all windows within the WindowManager. This does NOT fully close the windows (i.e., no data is changed within the PopWindow other than that the scene is set to close), but rather they can be re-opened by calling {@link WindowManager#openAllWindows()}
     */
    public void closeAllWindows() {
        for (PopWindow window : this.windows) {
            window.minimizeWindow();
        }
    }

    /**
     * Open all windows within the WindowManager. {@link PopWindow#start(Stage)} MUST have been called before this method (as a stage is needed to control). This does not change any data within the PopWindow other than that the scene is set to show
     */
    public void openAllWindows() {
        for (PopWindow window : this.windows) {
            window.showWindow();
        }
    }

    @Override
    public void removeObserving() {
        for (PopWindow window : this.windows) {
            window.removeObserver(this);
        }
    }

    @Override
    public void update(WindowEvent event) {

        // Remove the observed window from the list if it was closed
        if (event.getEventType() == WindowEventType.CLOSE_WINDOW) {

            int hashCode = Integer.valueOf(event.getEventData().get(0));

            PopWindow windowToRemove = null;
            for (PopWindow window : this.windows) {
                if (window.hashCode() == hashCode) {
                    windowToRemove = window;
                    break;
                }
            }

            if (windowToRemove != null) {
                this.removeWindow(windowToRemove);
            }
        }

    }
}
