package controller.player;

import controller.Scenario;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;



public class HumanPlayer extends Player implements KeyListener {
    private boolean W = false;
    private boolean A = false;
    private boolean S = false;
    private boolean D = false;

    //idea to have something basic to test with but still have control
    //next step would be to "see", so when something is within it's radius then store that in the empty scenario somehow
    public HumanPlayer(Scenario scenario, int x, int y) {
        super(scenario, x, y);
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
