package com.crowdcoin.threading;

import com.crowdcoin.mainBoard.table.Observe.Observable;
import com.crowdcoin.mainBoard.table.Observe.Observer;
import com.crowdcoin.mainBoard.table.Observe.ThreadEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;


public class ThreadManager implements Observable<ThreadEvent,String> {

    private PriorityQueue<TaskRunner> tasks;
    private List<Observer<ThreadEvent,String>> subscriptionList;

    public ThreadManager() {
        this.tasks = new PriorityQueue<>(new TaskRunnerComparator());
        this.subscriptionList = new ArrayList<>();
    }

    public void addTask(TaskRunner taskToAdd) {
        this.tasks.add(taskToAdd);
    }

    public void removeTask(TaskRunner taskToRemove) {
        this.tasks.remove(taskToRemove);
    }

    public void clearTasks() {
        this.tasks.clear();
    }

    @Override
    public boolean addObserver(Observer<ThreadEvent, String> observer) {
        return false;
    }

    @Override
    public boolean removeObserver(Observer<ThreadEvent, String> observer) {
        return false;
    }

    @Override
    public void notifyObservers(ThreadEvent event) {

    }

    @Override
    public void clearObservers() {
        this.subscriptionList.clear();
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

    public void runNextTask() {
    }


}
