package Model;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.*;

public class Server implements Runnable {
    private BlockingQueue<Task> tasks;
    private List<Task> completedTasks;
    private AtomicInteger waitingPeriod;
    private volatile boolean isRunning;
    private final int id;
    private AtomicInteger currentTaskTimeLeft;
    private Task currentTask;

    public Server(int id) {
        this.id = id;
        this.tasks = new LinkedBlockingQueue<>();
        this.completedTasks = new ArrayList<>();
        this.waitingPeriod = new AtomicInteger(0);
        this.isRunning = true;
        this.currentTaskTimeLeft = new AtomicInteger(0);
        this.currentTask = null;
    }

    public void addTask(Task newTask, int currentTime) {
        newTask.setQueueEntryTime(currentTime);
        tasks.add(newTask);
        waitingPeriod.addAndGet(newTask.getServiceTime());
    }

    public void run() {
        while (isRunning || !tasks.isEmpty() || currentTask != null) {
            try {
                if (currentTask == null) {
                    currentTask = tasks.poll();
                    if (currentTask != null) {
                        currentTaskTimeLeft.set(currentTask.getServiceTime());
                    }
                }

                if (currentTask != null && currentTaskTimeLeft.get() > 0) {
                    Thread.sleep(1000);
                    currentTask.decrement();
                    currentTaskTimeLeft.decrementAndGet();
                }

                if (currentTask != null && currentTaskTimeLeft.get() == 0) {
                    waitingPeriod.addAndGet(-currentTask.getOriginalServiceTime());
                    completedTasks.add(currentTask);
                    currentTask = null;
                }

                if (currentTask == null) {
                    Thread.sleep(100);
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public String getStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("Queue ").append(id).append(": ");

        if (tasks.isEmpty() && currentTask == null) {
            sb.append("closed");
        } else {
            int queueSize = tasks.size() + (currentTask != null ? 1 : 0);
            sb.append(queueSize).append(" clients\n");

            if (currentTask != null) {
                sb.append("Current: ").append(currentTask.toString())
                        .append(" (Time left: ").append(currentTaskTimeLeft.get()).append(")");
            }

            sb.append("\nWaiting time: ").append(getWaitingPeriod());
        }

        return sb.toString();
    }

    public void stop() {
        isRunning = false;
    }

    public int getWaitingPeriod() {
        int total = 0;
        for (Task task : tasks) {
            total += task.getServiceTime();
        }
        return total + currentTaskTimeLeft.get();
    }

    public int getQueueSize() {
        return tasks.size() + (currentTask != null ? 1 : 0);
    }

    public Task[] getTasks() {
        Task[] queueArray = tasks.toArray(new Task[0]);
        if (currentTask != null) {
            Task[] allTasks = new Task[queueArray.length + 1];
            allTasks[0] = currentTask;
            System.arraycopy(queueArray, 0, allTasks, 1, queueArray.length);
            return allTasks;
        }
        return queueArray;
    }

    public List<Task> getCompletedTasks() {  // ✅ Adăugat getter pentru task-urile finalizate
        return completedTasks;
    }

    public int getId() {
        return id;}
}