package agents;

import controller.Map.Map;
import utils.DirectionEnum;

import java.util.ArrayList;

public abstract class Player {

    private DirectionEnum directionEnum;
    protected int x;
    protected int y;

    protected double[][] guardPositions;
    protected int[] guardStates;
    protected Map map;
    public Player(DirectionEnum directionEnum){
        //agent knows only its position relative to the starting position
        this.directionEnum = directionEnum;
        x=0;
        y=0;
    }

    public abstract void update();
    public void printPosition(){
        System.out.println("x: " + x + ", y: " + y);
    }
    public void rotate(int alpha){
        int angle = this.directionEnum.getAngle() + alpha;
        directionEnum = directionEnum.getDirection(angle);
    }

}
