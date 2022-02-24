package controller;

import agents.Agent;
import agents.TestAgent;
import controller.Map.Map;
import utils.DirectionEnum;

//idea is to make this the class where we run store everything and have our main loop
public class GameRunner {
    private Scenario scenario;
    private Map map;

    private Agent player;

    private int t;

    public GameRunner(Scenario scenario) {
        this.scenario = scenario;
        init(scenario);

    }


    public Map getMap() {
        return map;
    }

    public void init(Scenario scenario){
        player = new TestAgent(0,0);
        map = new Map(scenario.getMapHeight()+1, scenario.getMapWidth()+1, player);
        map.loadMap(scenario);
        map.printMap();
        int x = 0, y = 0;
        map.addPlayer(player,x,y);
        t = 0;
    }


    //does nothing yet
    public void step(){
        t++;
        map.movePlayer(player, DirectionEnum.RIGHT.getDirection());
        System.out.println(map.getPlayerPosition(player));
        for(int i =0; i<Scenario.config.getBASESPEEDINTRUDER(); i++){
            //player.update();
            //if i understand correctly per timestep we can do 15 things, where 15 is the speed
            //so we can either walk 15 steps, or walk 14 and turn once, etc.
            //which is decided in player.update()
        }
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



    public Agent getPlayer() {
        return player;
    }
}
