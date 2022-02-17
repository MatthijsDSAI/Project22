package agents;

import controller.Map.Map;

import java.util.ArrayList;

public abstract class Player {
    protected int x;
    protected int y;

    protected double[][] guardPositions;
    protected int[] guardStates;
    protected Map map;
    public Player(){
        //agent knows only its position relative to the starting position
        x=0;
        y=0;
    }

    public abstract void update();
    public void printPosition(){
        System.out.println("x: " + x + ", y: " + y);
    }

}
