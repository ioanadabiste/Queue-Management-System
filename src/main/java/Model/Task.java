package Model;

public class Task {
    private int arrivalTime;
    private int serviceTime;
    private int ID;
    private int originalServiceTime;
    private int queueEntryTime;

    public Task(int ID, int arrivalTime, int serviceTime) {
        this.arrivalTime = arrivalTime;
        this.serviceTime = serviceTime;
        this.originalServiceTime = serviceTime;
        this.ID = ID;
        this.queueEntryTime = -1; // -1 înseamnă că încă nu a intrat în coadă
    }

    public void setQueueEntryTime(int time) {
        if (this.queueEntryTime == -1) {
            this.queueEntryTime = time;
        }
    }

    public int getQueueEntryTime() {
        return queueEntryTime;
    }

    public int getWaitingTime(int currentTime) {
        if (queueEntryTime == -1) return 0;
        return currentTime - queueEntryTime;
    }

    public void decrement() {
        if (serviceTime > 0) {
            serviceTime--;
        }
    }

    public int getOriginalServiceTime() {
        return originalServiceTime;
    }

    public int getId() {
        return ID;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getServiceTime() {
        return serviceTime;
    }

    @Override
    public String toString() {
        return "(" + ID + "," + arrivalTime + "," + serviceTime+")";
    }
}