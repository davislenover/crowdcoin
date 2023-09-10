package com.ratchet.threading;

import java.util.PriorityQueue;
import java.util.Queue;

/**
 * An extension of {@link VoidTask} which contains a group of {@link VoidTask}s. Treated as one {@link Task<Void>} by {@link TaskManager}. All tasks
 * will run under the same thread
 */
public class VoidGroupTask extends VoidTask {
    private Queue<Task<Void>> tasks = new PriorityQueue<>();

    /**
     * Adds a Task to the group. To define the order of execution for all Tasks within the group, set the {@link TaskPriority} for EACH INDIVIDUAL Task accordingly.
     * Setting the {@link TaskPriority} of the given {@link VoidGroupTask} instance sets the {@link TaskPriority} for the GROUP for {@link TaskManager}
     * @param task
     */
    public void addTask(VoidTask task) {
        this.tasks.add(task);
    }

    @Override
    public Void runTask() throws TaskException {
        while (tasks.peek() != null) {
            this.tasks.poll().runTask();
        }
        return null;
    }
}
