package controller;

import GUI.MapGui;
import agents.Agent;
import agents.Guard;
import agents.Intruder;
import controller.Map.Map;
import controller.Map.MapUpdater;
import exploration.Exploration;
import exploration.FrontierBasedExploration;
import utils.Config;
import utils.DirectionEnum;
import utils.Utils;

import java.util.Calendar;
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
    private MapGui gui;
    private Scenario scenario;
    private int t;
    private int gameMode;
    static int guardCount = 0;
    static int intruderCount = 0;
    private Map[] pastMaps;

    /*
    *Created off of a Scenario. The map is read in through scenario and the data is transfered through to gamerunner.
    * A Map class is then made based off of this, the scenario is not used further
     */
    public GameRunner(Scenario scenario) {
        this.scenario = scenario;
        gameMode = scenario.getGameMode();
        initMap();
        if(Scenario.config.GUI){
            try{
                GraphicsConnector graphicsConnector = new GraphicsConnector(this);
                gui = new MapGui();
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
    public void initMap(){
        map = new Map(scenario.getMapHeight()+1, scenario.getMapWidth()+1);
        MapUpdater.loadMap(map, scenario);
    }

    public void init(String guardAlgorithm, String intruderAlgorithm){
        Scenario.config.computeStepSize();
        guards = map.getGuards();
        intruders = map.getIntruders();

        MapUpdater.initGuards(map, guards, guardAlgorithm);
        if (gameMode == 1) {
            MapUpdater.initIntruders(map, intruders, intruderAlgorithm);
        }
        t = 0;
    }


    /*
    * The agent movement is done here
     */
    public void step(){
        for(int j=0; j<Scenario.config.getTimeStepSize(); j++) {
            moveGuards(j);
            moveIntruders(j);
        }
        System.out.println(guardCount);
        System.out.println(intruderCount);
        guardCount = 0;
        intruderCount = 0;
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
                if(config.DEBUG){
                    System.out.println("Timestep: "+ this.t + " at: " + Calendar.getInstance().getTime());
                    System.out.println(100*Map.explored(map) + "% of map has been explored");
                }
                ref.explored = Map.isExplored(map);
            }});
        t.start();

    }

    /*
    * Call made to algorithm to rotate the agents to a certain direction.@a
     */
    private void moveGuards(int j) {
        for (int i = 0; i < guards.size(); i++) {
            Guard guard = guards.get(i);
            if(j==0 || j%(Scenario.config.getTimeStepSize()/guard.getSpeed())==0){
                Utils.sleep(100);
                Exploration explorer = guard.getExploration();
                DirectionEnum dir = explorer.makeMove(guard);
                MapUpdater.moveAgent(map, guard, dir);
                guard.computeVisibleTiles(map);
                guardCount++;
            }
        }
    }


    private void moveIntruders(int j) {
        if (gameMode == 1) {
            for (Intruder intruder : intruders) {
                if (j == 0 || j%(Scenario.config.getTimeStepSize()/intruder.getSpeed()) == 0) {
                    Utils.sleep(100);
                    Exploration explorer = intruder.getExploration();
                    DirectionEnum dir = explorer.makeMove(intruder);
                    MapUpdater.moveAgent(map, intruder, dir);
                    intruder.computeVisibleTiles(map);
                    intruderCount++;
                    intruder.sprint();
                    intruder.handleRest();
                }
            }
        }
    }



    public Map getMap() {
        return map;
    }

    public ArrayList<Guard> getGuards() {return guards;}

    public ArrayList<Intruder> getIntruders() {return intruders;}

    public int getGameMode() {
        return gameMode;
    }
}
