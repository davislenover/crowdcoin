package com.crowdcoin.threading;

public class TaskTools {
    private static TaskManager taskManager = new TaskManager();

    public static TaskManager getTaskManager() {
        return taskManager;
    }

}
