package agents;

import controller.EmptyScenario;
import controller.Scenario;

public abstract class Player {
    protected int time;
    protected Scenario scenario;
    protected int x;
    protected int y;

    protected double[][] guardPositions;
    protected int[] guardStates;
    protected Scenario s;

    public Player(Scenario scenario, int x, int y){
        //this here we store an empty representation of the scenario
        //where the agent knows only what it sees
        s = new EmptyScenario(scenario);
        this.x = x;
        this.y = y;
        time = 0;
    }
}
