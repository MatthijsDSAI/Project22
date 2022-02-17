package agents;

import controller.Scenario;
import utils.Config;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

//keylistener doesnt work without swing i think so might not have been smart
public class HumanPlayer extends Player implements KeyListener {
    private boolean W = false;
    private boolean A = false;
    private boolean S = false;
    private boolean D = false;
    private Config config = Scenario.config;
    //idea to have something basic to test with but still have control
    //next step would be to "see", so when something is within it's radius then store that in the empty scenario somehow
    public HumanPlayer() {
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
        move();
        see();
    }

    public void see(){
        //see if any of the objects on the map are in it's vision
    }

    public void move(){
        if(W){
            moveUp();
        }
        if(A){
            moveLeft();
        }
        if(S){
            moveDown();
        }
        if(D){
            moveRight();
        }
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

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_W){
            W = true;
        }
        if(e.getKeyCode() == KeyEvent.VK_A){
            A = true;
        }
        if(e.getKeyCode() == KeyEvent.VK_S){
            S = true;
        }
        if(e.getKeyCode() == KeyEvent.VK_D){
            D = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_W){
            W = false;
        }
        if(e.getKeyCode() == KeyEvent.VK_A){
            A = false;
        }
        if(e.getKeyCode() == KeyEvent.VK_S){
            S = false;
        }
        if(e.getKeyCode() == KeyEvent.VK_D){
            D = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }





}
