package BusinessLogic;
import java.util.List;

import Model.Server;
import Model.*;
public interface Strategy {

        void addTask(List<Server> servers, Task t, int currentTime);


}
