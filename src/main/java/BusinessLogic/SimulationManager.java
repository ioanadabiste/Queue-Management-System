package BusinessLogic;

import Model.*;
import GUI.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SimulationManager implements Runnable {
    private int timeLimit;
    private int maxProcessingTime;
    private int minProcessingTime;
    private int maxArrivalTime;
    private int minArrivalTime;
    private int numberOfServers;
    private int numberOfClients;
    private StringBuilder logBuffer;
    private ConcreteStrategyTime.SelectionPolicy selectionPolicy;

    private Scheduler scheduler;
    private List<Task> generatedTasks;
    private PrintWriter logWriter;
    private AtomicInteger currentTime;
    private SimulationFrame simulationFrame;

    private Map<Integer, Integer> clientsInSystemPerTimeUnit;
    private List<Task> allTasks;

    public SimulationManager(int numberOfClients, int numberOfServers, int timeLimit,
                             int minArrivalTime, int maxArrivalTime,
                             int minProcessingTime, int maxProcessingTime,
                             ConcreteStrategyTime.SelectionPolicy selectionPolicy) {
        this.numberOfClients = numberOfClients;
        this.numberOfServers = numberOfServers;
        this.timeLimit = timeLimit;
        this.minArrivalTime = minArrivalTime;
        this.maxArrivalTime = maxArrivalTime;
        this.minProcessingTime = minProcessingTime;
        this.maxProcessingTime = maxProcessingTime;
        this.selectionPolicy = selectionPolicy;
        this.currentTime = new AtomicInteger(0);
        this.logBuffer = new StringBuilder();
        this.clientsInSystemPerTimeUnit = new HashMap<>();

        try {
            this.logWriter = new PrintWriter(new FileWriter("simulation_log1.txt"));
            this.logWriter=new PrintWriter(new FileWriter("simulation_log2.txt"));
            this.logWriter=new PrintWriter(new FileWriter("simulation_log3.txt"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to create log file", e);
        }

        this.scheduler = new Scheduler(numberOfServers, Integer.MAX_VALUE);
        scheduler.changeStrategy(selectionPolicy);
        this.generatedTasks = generateRandomTasks();
        this.allTasks = new ArrayList<>(generatedTasks);
    }

    private List<Task> generateRandomTasks() {
        List<Task> tasks = new ArrayList<>();
        Random random = new Random();

        for (int i = 1; i <= numberOfClients; i++) {
            int arrivalTime = minArrivalTime + random.nextInt(maxArrivalTime - minArrivalTime + 1);
            int serviceTime = minProcessingTime + random.nextInt(maxProcessingTime - minProcessingTime + 1);
            tasks.add(new Task(i, arrivalTime, serviceTime));
        }

        tasks.sort(Comparator.comparingInt(Task::getArrivalTime));
        return tasks;
    }

    @Override
    public void run() {
        logWriter.println("Simulation started with parameters:");
        logWriter.println("Number of clients: " + numberOfClients);
        logWriter.println("Number of servers: " + numberOfServers);
        logWriter.println("Time limit: " + timeLimit);
        logWriter.println("Arrival time range: [" + minArrivalTime + ", " + maxArrivalTime + "]");
        logWriter.println("Service time range: [" + minProcessingTime + ", " + maxProcessingTime + "]");
        logWriter.println("Strategy: " + selectionPolicy);
        logWriter.println("Initial tasks: " + generatedTasks);

        while (currentTime.get() <= timeLimit && (!generatedTasks.isEmpty() || hasActiveServers())) {
            dispatchTasks();
            logStatus(currentTime.get());
            updateClientsCountForPeak(currentTime.get());

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

            currentTime.incrementAndGet();
        }

        scheduler.stopServers();
        logResults();
        logWriter.close();

        if (simulationFrame != null) {
            simulationFrame.simulationComplete();
        }
    }

    private void dispatchTasks() {
        Iterator<Task> iterator = generatedTasks.iterator();
        while (iterator.hasNext()) {
            Task task = iterator.next();
            if (task.getArrivalTime() <= currentTime.get()) {
                scheduler.dispatchTask(task, currentTime.get());
                iterator.remove();

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private void logResults() {
        double avgWaitingTime = calculateAverageWaitingTime();
        double avgServiceTime = calculateAverageServiceTime();
        int peakHour = calculatePeakHour();

        logWriter.println("\nSimulation results:");
        logWriter.println("Average waiting time: " + avgWaitingTime);
        logWriter.println("Average service time: " + avgServiceTime);
        logWriter.println("Peak hour: " + peakHour);

        System.out.println("\nSimulation results:");
        System.out.println("Average waiting time: " + avgWaitingTime);
        System.out.println("Average service time: " + avgServiceTime);
        System.out.println("Peak hour: " + peakHour);
    }

    public double calculateAverageWaitingTime() {
        int totalWaitingTime = 0;
        int totalClients = 0;

        for (Server server : scheduler.getServers()) {
            for (Task task : server.getCompletedTasks()) {
                totalWaitingTime +=  task.getArrivalTime();
                totalClients++;
            }
        }

        return totalClients > 0 ? (double) totalWaitingTime / totalClients : 0;
    }

    private double calculateAverageServiceTime() {
        int totalServiceTime = 0;
        int totalClients = 0;

        for (Server server : scheduler.getServers()) {
            for (Task task : server.getCompletedTasks()) {
                totalServiceTime += task.getOriginalServiceTime();
                totalClients++;
            }
        }

        return totalClients > 0 ? (double) totalServiceTime / totalClients : 0;
    }

    private int calculatePeakHour() {
        int peakTime = -1;
        int maxClients = -1;

        for (Map.Entry<Integer, Integer> entry : clientsInSystemPerTimeUnit.entrySet()) {
            if (entry.getValue() > maxClients) {
                maxClients = entry.getValue();
                peakTime = entry.getKey();
            }
        }

        return peakTime;
    }

    private void updateClientsCountForPeak(int time) {
        int totalClientsInQueues = 0;
        for (Server server : scheduler.getServers()) {
            totalClientsInQueues += server.getQueueSize();
        }
        clientsInSystemPerTimeUnit.put(time, totalClientsInQueues);
    }

    private boolean hasActiveServers() {
        for (Server server : scheduler.getServers()) {
            if (server.getQueueSize() > 0) {
                return true;
            }
        }
        return false;
    }

    private void logStatus(int currentTime) {
        String timeMsg = "Time " + currentTime + "\n";
        logBuffer.append(timeMsg);
        logWriter.println(timeMsg);
        System.out.println(timeMsg);

        String waitingClientsMsg = "Waiting clients: " + generatedTasks + "\n";
        logBuffer.append(waitingClientsMsg);
        logWriter.println(waitingClientsMsg);
        System.out.println(waitingClientsMsg);

        for (Server server : scheduler.getServers()) {
            String status = server.getStatus() + "\n";
            logBuffer.append(status);
            logWriter.println(status);
            System.out.println(status);
        }

        logBuffer.append("\n");
        logWriter.println();
        System.out.println();
    }

    public String getLog() {
        return logBuffer.toString();
    }

    public void setSimulationFrame(SimulationFrame frame) {
        this.simulationFrame = frame;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }
}