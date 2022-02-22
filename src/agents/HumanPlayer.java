package agents;

import controller.Scenario;
import utils.Config;
import utils.DirectionEnum;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

//keylistener doesnt work without swing i think so might not have been smart
public class HumanPlayer extends Player{

    private Config config = Scenario.config;
    //idea to have something basic to test with but still have control
    //next step would be to "see", so when something is within it's radius then store that in the empty scenario somehow
    public HumanPlayer(DirectionEnum directionEnum) {
        super(directionEnum);
    }


    public double getX(){
        return x;
    }
    public double getY(){
        return y;
    }
    //idea is to change coordinates based on input
    //over 1t
    public void update(){
        move(1,0);
        see();
    }

    public void see(){
        //see if any of the objects on the map are in it's vision
    }

    public void move(int deltaX, int deltaY){
        x+= deltaX;
        y+= deltaY;
    }

    private void moveRight() {
        x+=1;//config.getBASESPEEDINTRUDER();
    }

    private void moveDown() {
        y-=1;//config.getBASESPEEDINTRUDER();
    }

    private void moveLeft() {
        x-=1;//config.getBASESPEEDINTRUDER();
    }

    private void moveUp() {
        y+=1;//config.getBASESPEEDINTRUDER();
    }

   




}
