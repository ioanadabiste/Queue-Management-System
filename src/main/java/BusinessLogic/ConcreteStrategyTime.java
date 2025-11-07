package BusinessLogic;

import Model.Server;
import Model.*;

import java.util.List;

public class ConcreteStrategyTime implements Strategy{

    @Override

    public void addTask(List<Server> servers, Task t, int currentTime) {
        Server bestServer = servers.get(0);
        int minWaitingTime = bestServer.getWaitingPeriod();

        for (Server server : servers) {
            if (server.getWaitingPeriod() < minWaitingTime) {
                minWaitingTime = server.getWaitingPeriod();
                bestServer = server;
            }
        }
        bestServer.addTask(t, currentTime);  // Pasează currentTime
    }

    public enum SelectionPolicy
    {SHORTEST_QUEUE,SHORTEST_TIME}
}
