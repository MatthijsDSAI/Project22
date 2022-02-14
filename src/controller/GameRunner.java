package controller;

import agents.Agent;
import agents.HumanPlayer;
import agents.Player;

import java.util.ArrayList;

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
        players.add(new HumanPlayer(scenario, 10, 10));
        t = 0;
    }

    public void step(){
        t++;
        player.update();
    }

    public void run(){
        boolean explored = false;
        while(!explored){
            run();
            explored = player.getEmptyScenario().isExplored();
        }
    }
}
