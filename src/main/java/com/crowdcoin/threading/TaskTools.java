package com.crowdcoin.threading;

import java.util.Comparator;

public class TaskTools {
    private static TaskManager taskManager = new TaskManager();

    public static TaskManager getTaskManager() {
        return taskManager;
    }

}
