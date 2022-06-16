package utils;

public class GameData {
    private int victor; //0 = guards, 1 = intruders
    private int reachedTAFirst; //0 = guards, 1 = intruders
    private int turnsTaken;
    private double mapExploredGuards;
    private double mapExploredIntruders;

    public GameData(int victor, int reachedTAFirst, int turnsTaken, double mapExploredGuards, double mapExploredIntruders) {
        this.victor = victor;
        this.reachedTAFirst = reachedTAFirst;
        this.turnsTaken = turnsTaken;
        this.mapExploredGuards = mapExploredGuards;
        this.mapExploredIntruders = mapExploredIntruders;
    }

    public int getVictor() {
        return victor;
    }

    public void setVictor(int victor) {
        this.victor = victor;
    }

    public int getReachedTAFirst() {
        return reachedTAFirst;
    }

    public void setReachedTAFirst(int reachedTAFirst) {
        this.reachedTAFirst = reachedTAFirst;
    }

    public int getTurnsTaken() {
        return turnsTaken;
    }

    public void setTurnsTaken(int turnsTaken) {
        this.turnsTaken = turnsTaken;
    }

    public double getMapExploredGuards() {
        return mapExploredGuards;
    }

    public void setMapExploredGuards(double mapExploredGuards) {
        this.mapExploredGuards = mapExploredGuards;
    }

    public double getMapExploredIntruders() {
        return mapExploredIntruders;
    }

    public void setMapExploredIntruders(double mapExploredIntruders) {
        this.mapExploredIntruders = mapExploredIntruders;
    }
}
