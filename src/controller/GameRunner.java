package controller;

import GUI.MapGui;
import agents.Agent;
import agents.Guard;
import agents.Intruder;
import agents.TestAgent;
import controller.Map.Map;
import controller.Map.tiles.SpawnAreaIntruders;
import controller.Map.tiles.TeleportalTile;
import controller.Map.tiles.Tile;
import exploration.FrontierBasedExploration;
import javafx.application.Platform;
import utils.Config;
import utils.DirectionEnum;
import utils.Position;

import java.util.ArrayList;
import java.util.Queue;

//idea is to make this the class where we run store everything and have our main loop
public class GameRunner {
    private Map map;
    private final Config config = Scenario.config;
    private Agent agent;
    private ArrayList<Guard> guards;
    private ArrayList<Intruder> intruders;
    private ArrayList<FrontierBasedExploration> explorers;
    private MapGui gui;
    private Scenario scenario;
    private int t;
    private boolean isGameMode1;

    public GameRunner(Scenario scenario) {
        this.scenario = scenario;
        isGameMode1 = (scenario.getGameMode() == 1);
        init();
        GraphicsConnector graphicsConnector = new GraphicsConnector(this);
        gui = new MapGui();
        map.setGraphicsConnector(graphicsConnector);



        if(Scenario.config.GUI){
            try{
                gui.launchGUI(graphicsConnector);
            }
            catch(IllegalStateException e ){
                System.out.println("There has been an issue with the initialization of the GUI");
            }
        }

    }


    public Map getMap() {
        return map;
    }

    public void init(){
        map = new Map(scenario.getMapHeight()+1, scenario.getMapWidth()+1);
        map.loadMap(scenario);
        guards = map.getGuards();
        intruders = map.getIntruders();
        explorers = new ArrayList<>();
        for(int i = 0; i < guards.size(); i++) {
            explorers.add(new FrontierBasedExploration(guards.get(i), map.getTiles()));
        }
        loadGuards();
        if (isGameMode1) {
            loadIntruders();
        }

        t = 0;
    }

    public void loadGuards() { // "loadGuards" and "loadIntruders" can later be combined if either of them doesn't need any additional code
        for (Guard guard: guards) {
            int x = guard.getX_position();
            int y = guard.getY_position();
            map.addAgent(new TestAgent(x,y), x, y);
            guard.setAgentPosition(map.getTile(x,y));
            guard.initializeEmptyMap(map);
            guard.computeVisibleTiles(map);
        }
    }

    public void loadIntruders() {
        for (Intruder intruder: intruders) {
            int x = intruder.getX_position();
            int y = intruder.getY_position();
            map.addAgent(new TestAgent(x, y), x, y);
            intruder.setAgentPosition(map.getTile(x,y));
            intruder.initializeEmptyMap(map);
            intruder.computeVisibleTiles(map);
        }
    }

    //does nothing yet
    public void step(){
        t++;
        //map.moveAgent(agent, DirectionEnum.RIGHT.getDirection());
        //System.out.println(map.getAgentPosition(agent));
        //for(int i =0; i<Scenario.config.getBASESPEEDINTRUDER(); i++){
        //        map.moveAgent(agent, DirectionEnum.EAST);
//        agent.computeVisibleTiles(map);
        // }
        //map.getGraphicsConnector().updateGraphics();
        for (int i = 0; i < guards.size(); i++) {
            Guard guard = guards.get(i);
            Tile curTile = guard.getAgentPosition();
            int curX = curTile.getX();
            int curY = curTile.getY();
            FrontierBasedExploration explorer = explorers.get(i);
            DirectionEnum dir = explorer.step(guard);
            //map.moveAgent(guard, DirectionEnum.NORTH);
            //guard = (Guard) map.turnAgent(guard, dir);
            guard = (Guard) map.moveAgent(guard, dir);
            //guard.setAgentPosition(explorer.getNextTile());
            guard.computeVisibleTiles(map);
        }
        if (isGameMode1) {
            for (Intruder intruder : intruders) {
                map.moveAgent(intruder, DirectionEnum.EAST);
                intruder.computeVisibleTiles(map);
            }
        }
        t++;
    }


    public void run(){
        var ref = new Object() {
            boolean explored = false;
        };

        Thread t = new Thread(() ->{
            while(!ref.explored){
                try {
                    Thread.sleep(Scenario.config.getSleep());
                }
                catch(InterruptedException e){
                    System.out.println("Threading issue");
                }
                step();
                ref.explored = map.isExplored();
                if(config.DEBUG){
                    System.out.println(map.explored() + " of map has been explored");
                    System.out.println(this.t);
                }

        }});
        t.start();

    }



//    public Agent getAgent() {
//        return agent;
//    }


    public ArrayList<Guard> getGuards() {return guards;}

    public ArrayList<Intruder> getIntruders() {return intruders;}

    public boolean isGameMode1() {return isGameMode1;}

    public static void main(String[] args){
        GameRunner g = new GameRunner(new Scenario("testmap2.txt"));
    }
}
