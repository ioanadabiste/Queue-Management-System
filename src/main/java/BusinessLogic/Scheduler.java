package BusinessLogic;

import java.util.ArrayList;
import java.util.List;

import Model.Server;
import Model.*;
public class Scheduler {
    private List<Server> servers;
    private int maxNoServers;
    private int maxTasksPerServer;
    private Strategy strategy;

    public Scheduler(int maxNoServers, int maxTasksPerServer) {
        this.maxNoServers = maxNoServers;
        this.maxTasksPerServer = maxTasksPerServer;
        this.servers = new ArrayList<>();
        for (int i = 0; i < maxNoServers; i++) {
            Server server = new Server(i + 1);
            servers.add(server);
            new Thread(server).start();
        }
    }

    public void changeStrategy(ConcreteStrategyTime.SelectionPolicy policy)
    {
        if (policy == ConcreteStrategyTime.SelectionPolicy.SHORTEST_QUEUE) {
            strategy = new ConcreteStrategyQueue();
        }
        if (policy == ConcreteStrategyTime.SelectionPolicy.SHORTEST_TIME) {
            strategy = new ConcreteStrategyTime();
        }

    }
    public void dispatchTask(Task t, int currentTime) {
        if (strategy != null) {
            strategy.addTask(servers, t, currentTime);
        }
    }
    public void stopServers() {
        for (Server server : servers) {
            server.stop();
        }
    }
    public List<Server> getServers()
    {
        return servers;
    }
}
