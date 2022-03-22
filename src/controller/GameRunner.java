package controller;

import GUI.MapGui;
import agents.Guard;
import agents.Intruder;
import agents.TestAgent;
import controller.Map.Map;
import exploration.FrontierBasedExploration;
import utils.Config;
import utils.DirectionEnum;
import utils.Utils;

import java.util.ArrayList;

/*
* Main class which stores and runs everything.
* Contains the agents and the map.
 */
public class GameRunner {
    private Map map;
    private final Config config = Scenario.config;
    private ArrayList<Guard> guards;
    private ArrayList<Intruder> intruders;
    private ArrayList<FrontierBasedExploration> explorers;
    private MapGui gui;
    private Scenario scenario;
    private int t;
    private boolean isGameMode1;


    /*
    *Created off of a Scenario. The map is read in through scenario and the data is transfered through to gamerunner.
    * A Map class is then made based off of this, the scenario is not used further
     */
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
        else{
            run();
        }

    }

    /*
    *Initialize the map, the agents, and their algorithms.
     */
    public void init(){
        map = new Map(scenario.getMapHeight()+1, scenario.getMapWidth()+1);
        map.loadMap(scenario);
        guards = map.getGuards();
        intruders = map.getIntruders();
        explorers = new ArrayList<>();
        for(int i = 0; i < guards.size(); i++) {
            explorers.add(new FrontierBasedExploration(guards.get(i), map.getTiles()));
        }
        initGuards();
        if (isGameMode1) {
            initIntruders();
        }
        t = 0;
    }


    /*
    * The agent movement is done here
     */
    public void step(){
        for(int j=0; j<Scenario.config.getBASESPEEDGUARD(); j++) {
            Utils.sleep(Scenario.config.getSleep());
            moveGuards();
            moveIntruders();
        }
        t++;
    }

    /*
    * main while loop, one iteration = one timestep. When explored (== 100% covered), stop.
     */
    public void run(){
        var ref = new Object() {
            boolean explored = false;
        };

        Thread t = new Thread(() ->{
            while(!ref.explored){
                step();
                ref.explored = map.isExplored();
                if(config.DEBUG){
                    System.out.println(map.explored() + " of map has been explored");
                    System.out.println(this.t);
                }

            }});
        t.start();

    }

    /*
    * Call made to algorithm to rotate the agents to a certain direction.
     */
    private void moveGuards() {
        for (int i = 0; i < guards.size(); i++) {
            Guard guard = guards.get(i);
            FrontierBasedExploration explorer = explorers.get(i);
            DirectionEnum dir = explorer.step(guard);
            guard = (Guard) map.moveAgent(guard, dir);
            guard.computeVisibleTiles(map);
        }
    }

    private void moveIntruders() {
        if (isGameMode1) {
            for (Intruder intruder : intruders) {
                map.moveAgent(intruder, DirectionEnum.EAST);
                intruder.computeVisibleTiles(map);
            }
        }
    }

    /*
    * Initialization of guards, including putting them on the map.
     */
    public void initGuards() { // "loadGuards" and "loadIntruders" can later be combined if either of them doesn't need any additional code
        for (Guard guard: guards) {
            int x = guard.getX_position();
            int y = guard.getY_position();
            map.addAgent(guard, x, y);
            guard.setAgentPosition(map.getTile(x,y));
            guard.initializeEmptyMap(map);
            guard.computeVisibleTiles(map);
        }
    }

    public void initIntruders() {
        for (Intruder intruder: intruders) {
            int x = intruder.getX_position();
            int y = intruder.getY_position();
            map.addAgent(intruder, x, y);
            intruder.setAgentPosition(map.getTile(x,y));
            intruder.initializeEmptyMap(map);
            intruder.computeVisibleTiles(map);
        }
    }


    public Map getMap() {
        return map;
    }

    public ArrayList<Guard> getGuards() {return guards;}

    public ArrayList<Intruder> getIntruders() {return intruders;}

    public boolean isGameMode1() {return isGameMode1;}

}
