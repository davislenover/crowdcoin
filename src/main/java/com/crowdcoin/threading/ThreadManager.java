package com.crowdcoin.threading;

import java.util.Comparator;
import java.util.PriorityQueue;


public class ThreadManager {

    private PriorityQueue<TaskRunner> tasks;

    public ThreadManager() {
        this.tasks = new PriorityQueue<>(new TaskRunnerComparator());
    }

    public void addTask(TaskRunner taskToAdd) {
        this.tasks.add(taskToAdd);
    }

    public void removeTask(TaskRunner taskToRemove) {
        this.tasks.remove(taskToRemove);
    }

    private class TaskRunnerComparator implements Comparator<TaskRunner> {
        @Override
        public int compare(TaskRunner o1, TaskRunner o2) {

            int o1Priority = o1.getTaskPriority().getPriority();
            int o2Priority = o2.getTaskPriority().getPriority();

            if (o1Priority < o2Priority) {
                return -1;
            }
            if (o1Priority == o2Priority) {
                return 0;
            }
            if (o1Priority > o2Priority) {
                return 1;
            }
            return 0;
        }
    }


}
