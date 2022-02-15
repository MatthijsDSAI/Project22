package agents;

import controller.EmptyScenario;
import controller.Scenario;

public abstract class Player {
    protected Scenario scenario;
    protected EmptyScenario ownScenario;
    protected int x;
    protected int y;

    protected double[][] guardPositions;
    protected int[] guardStates;

    public Player(Scenario scenario, int x, int y){
        //this here we store an empty representation of the scenario
        //where the agent knows only what it sees
        ownScenario = new EmptyScenario(scenario);
        this.scenario = scenario;
        this.x = x;
        this.y = y;
    }

    public abstract void update();
    public void printPosition(){
        System.out.println("x: " + x + ", y: " + y);
    }
    public abstract EmptyScenario getEmptyScenario();
}
