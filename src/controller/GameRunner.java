package controller;

import agents.Agent;
import agents.HumanPlayer;
import agents.Player;
import controller.Map.Map;
import utils.DirectionEnum;

import java.util.ArrayList;
import java.util.Arrays;

//idea is to make this the class where we run store everything and have our main loop
public class GameRunner {
    private Scenario scenario;
    private Map map;

    private HumanPlayer player;
    private int t;

    public GameRunner(Scenario scenario) {
        this.scenario = scenario;
        init(scenario);

    }


    public Map getMap() {
        return map;
    }

    public void init(Scenario scenario){
        player = new HumanPlayer();
        map = new Map(scenario.getMapHeight()+1, scenario.getMapWidth()+1, player);
        map.loadMap(scenario);
        map.printMap();
        int x = 0, y = 0;
        map.addPlayer(player,x,y);
        t = 0;
        run();
    }


    //does nothing yet
    public void step(){
        t++;
        player.update();
        map.movePlayer(player, DirectionEnum.RIGHT.getDirection());
        System.out.println(map.getPlayerPosition(player));
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
            explored = map.isExplored();
            //System.out.println(map.explored() + " of map has been explored");
            //map.printMap();
        }
    }



    public HumanPlayer getPlayer() {
        return player;
    }
}
