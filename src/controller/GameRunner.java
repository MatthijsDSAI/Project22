package controller;

import GUI.MapGui;
import agents.Guard;
import agents.Intruder;
import controller.Map.Map;
import controller.Map.MapUpdater;
import controller.Map.tiles.Tile;
import exploration.Exploration;
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

    private final Config config = Scenario.config;
    private MapGui gui;
    private Scenario scenario;
    private int gameMode;

    //Fields that change after a training
    private boolean TRAINING = Scenario.config.getTraining();
    private Map map;
    private int t;
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
        else if(TRAINING){
            trainLoop(gameMode);
        }
        else{
            run(gameMode);
        }

    }

    //GUI off. TRAIN on.
    //Pass as string the names of the algorithms you want here.
    //I can not get it to work with GUI for now. Even this was really difficult. However I believe it is quite stable.
    //Questions: ask me.
    public void trainLoop(int gameMode) {
            if(!Scenario.config.GUI){
                init("RandomExploration", "RandomExploration");
            }
            int count = 0;
            while(count<8) {
                Utils.sleep(100);
                System.out.println("ITERATION: " + count);
                train();
                initMap();
                init("RandomExploration", "RandomExploration");
                count++;
            }

    }

    public void train(){
        var ref = new Object() {
            boolean areaReached = false;
            boolean noIntrudersLeft = false;
        };
        while ((!ref.areaReached || !ref.noIntrudersLeft) && t<3) {
            step();
            ref.areaReached = Map.checkTargetArea(map, this.t);
            ref.noIntrudersLeft = Map.noIntrudersLeft(map);
        this.t++;
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
        MapUpdater.initGuards(map, map.getGuards(), guardAlgorithm);
        if (gameMode == 1) {
            MapUpdater.initIntruders(map, map.getIntruders(), intruderAlgorithm);
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
    }




    /*
    * Call made to algorithm to rotate the agents to a certain direction.@a
     */
    private void moveGuards(int j) {
        ArrayList<Guard> guards = map.getGuards();
        for (Guard guard : guards) {
            if (j == 0 || j % (Scenario.config.getTimeStepSize() / guard.getSpeed()) == 0) {
                Utils.sleep(100);
                Exploration explorer = guard.getExploration();
                DirectionEnum dir = explorer.makeMove(guard);
                MapUpdater.moveAgent(map, guard, dir);
                guard.computeVisibleTiles(map);
                MapUpdater.checkIntruderCapture(guard, map);
            }
        }
    }


    private void moveIntruders(int j) {

        if (gameMode == 1) {
            ArrayList<Intruder> intruders = map.getIntruders();
            for (Intruder intruder : intruders) {
                if (j == 0 || j%(Scenario.config.getTimeStepSize()/intruder.getSpeed()) == 0) {
                    Utils.sleep(100);
                    Exploration explorer = intruder.getExploration();
                    DirectionEnum dir = explorer.makeMove(intruder);
                    MapUpdater.moveAgent(map, intruder, dir);
                    intruder.computeVisibleTiles(map);
                    MapUpdater.checkIntruderCapture(intruder, map);
                }
            }
        }
    }



    public Map getMap() {
        return map;
    }

    public ArrayList<Guard> getGuards() {return map.getGuards();}

    public ArrayList<Intruder> getIntruders() {return map.getIntruders();}

    public int getGameMode() {
        return gameMode;
    }

    /*
     * main while loop, one iteration = one timestep. When explored (== 100% covered), stop.
     */
    public void run(int gameMode){
        if(gameMode == 0) {
            var ref = new Object() {
                boolean explored = false;
            };
            Thread t = new Thread(() -> {
                while (!ref.explored) {
                    step();
                    if (config.DEBUG) {
                        System.out.println("Timestep: " + this.t + " at: " + Calendar.getInstance().getTime());
                        System.out.println(100 * Map.explored(map) + "% of map has been explored");
                    }
                    ref.explored = Map.isExplored(map);
                }
            });
            t.start();
        }
        else if(gameMode==1){
            var intruderWin = new Object() {
                boolean areaReached = false;
            };
            var guardWin = new Object() {
                boolean noIntrudersLeft = false;
            };
            new Thread(() -> {
                while (!intruderWin.areaReached || guardWin.noIntrudersLeft) {
                    step();
                    intruderWin.areaReached = Map.checkTargetArea(map, this.t);
                    guardWin.noIntrudersLeft = Map.noIntrudersLeft(map);
                    this.t++;
                }
            }).start();


        }
    }

}
