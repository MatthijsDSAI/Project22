package controller;

import agents.Agent;
import agents.HumanPlayer;
import agents.Player;

import java.util.ArrayList;
//idea is to make this the class where we run store everything and have our main loop
public class GameRunner {
    private Scenario scenario;
    private ArrayList<Player> players;
    private HumanPlayer player;
    private int t;
    public GameRunner(Scenario scenario) {
        this.scenario = scenario;
        init();
    }

    public void init(){
        player = new HumanPlayer(scenario, 10, 10);
        t = 0;
    }

    public void step(){
        t++;
        player.update();
    }

    public void run(){
        boolean explored = false;
        while(!explored){
            try {
                Thread.sleep(50);
            }
            catch(InterruptedException e){
                System.out.println("Threading issue");
            }
            step();
            player.printPosition();
            explored = player.getEmptyScenario().isExplored();
        }
    }
}
