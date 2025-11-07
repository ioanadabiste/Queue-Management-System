package BusinessLogic;

import Model.Server;
import Model.Task;

import java.util.List;

class ConcreteStrategyQueue implements Strategy {


    public void addTask(List<Server> servers, Task t, int currentTime) {
        Server bestServer = null;
        int minQueueSize = Integer.MAX_VALUE;

        // Găsim serverul cu cea mai mică coadă care are și capacitate disponibilă
        for (Server server : servers) {
            int queueSize = server.getQueueSize();
            if (queueSize < minQueueSize) {
                minQueueSize = queueSize;
                bestServer = server;
            }
        }

        if (bestServer != null) {
            bestServer.addTask(t, currentTime);
        }
    }
}