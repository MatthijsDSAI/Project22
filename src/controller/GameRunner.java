package controller;

import GUI.MapGui;
import agents.Guard;
import agents.Intruder;
import controller.Map.Map;
import controller.Map.MapUpdater;
import controller.Map.tiles.Tile;
import exploration.BaselineGuard;
import exploration.Exploration;
import utils.Config;
import utils.DirectionEnum;
import utils.Utils;

import java.util.Calendar;
import java.util.ArrayList;
import java.util.Iterator;

/*
* Main class which stores and runs everything.
* Contains the agents and the map.
 */
public class GameRunner {

    private final Config config = Scenario.config;
    private MapGui gui;
    private Scenario scenario;
    private int gameMode;
    private GraphicsConnector gc;
    //Fields that change after a training
    private String guardExploration = "BaseLineIntruder";
    private String intruderExploration = "BaseLineGuard";
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
                this.gc = graphicsConnector;
                gui = new MapGui();
                gui.launchGUI(graphicsConnector);
            }
            catch(IllegalStateException e ){
                System.out.println("There has been an issue with the initialization of the GUI");
            }
        }
        boolean TRAINING = Scenario.config.getTraining();
        if(TRAINING){
            trainLoop(gameMode);
        }
        else{
            run(gameMode);
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
        guardExploration = guardAlgorithm;
        intruderExploration = intruderAlgorithm;
        MapUpdater.initGuards(map, map.getGuards(), guardAlgorithm);
        if (gameMode == 1) {
            MapUpdater.initIntruders(map, map.getIntruders(), intruderAlgorithm);
        }
        t = 0;
    }


    public void trainLoop(int gameMode) {
            if(!Scenario.config.GUI){
                init("RandomExploration", "RandomExploration");
            }
            Thread t = new Thread(() -> {
                int count = 0;
                while(count<30) {
                    Utils.sleep(20);
                    System.out.println("ITERATION: " + count);
                    train();
                    initMap();
                    init(guardExploration, intruderExploration);
                    gc.setMap(map);
                    count++;
                }
            });
        t.start();
    }

    public void train(){
        boolean areaReached = false;
        boolean noIntrudersLeft = false;
        while ((!areaReached && !noIntrudersLeft)) {
            step();
            areaReached = Map.checkTargetArea(map, this.t);
            noIntrudersLeft = Map.noIntrudersLeft(map);
            this.t++;
        }
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
                while (!intruderWin.areaReached && !guardWin.noIntrudersLeft) {
                    step();
                    intruderWin.areaReached = Map.checkTargetArea(map, this.t);
                    guardWin.noIntrudersLeft = Map.noIntrudersLeft(map);
                    this.t++;
                }
                if(intruderWin.areaReached){
                    System.out.println("INTRUDER WIN");
                }
                if(guardWin.noIntrudersLeft){
                    System.out.println("GUARD WIN");
                }
            }).start();
        }
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
        Iterator<Guard> iter = guards.iterator();
        while(iter.hasNext()) {
            Guard guard = iter.next();
            if (j == 0 || j % (Scenario.config.getTimeStepSize() / guard.getSpeed()) == 0) {
                Utils.sleep(20);
                Exploration explorer = guard.getExploration();
                DirectionEnum dir = explorer.makeMove(guard);
                MapUpdater.moveAgent(map, guard, dir);
                MapUpdater.refresh(map, guard.getVisibleTiles());
                guard.computeVisibleTiles(map);
                MapUpdater.checkIntruderCapture(guard, map);
            }
        }
    }


    private void moveIntruders(int j) {

        if (gameMode == 1) {
            ArrayList<Intruder> intruders = map.getIntruders();
            Iterator<Intruder> iter = intruders.iterator();
            while(iter.hasNext()) {
                Intruder intruder = iter.next();
                if (j == 0 || j%(Scenario.config.getTimeStepSize()/intruder.getSpeed()) == 0) {
                    Utils.sleep(20);
                    Exploration explorer = intruder.getExploration();
                    DirectionEnum dir = explorer.makeMove(intruder);
                    MapUpdater.moveAgent(map, intruder, dir);
                    MapUpdater.refresh(map, intruder.getVisibleTiles());
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
}
