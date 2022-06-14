package controller;

import GUI.MapGui;
import agents.Guard;
import agents.Intruder;
import controller.Map.Map;
import controller.Map.MapUpdater;
import utils.Config;
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
    private GraphicsConnector gc;
    private String guardExploration = "CombinedGuard";
    private String intruderExploration = "BaseLineIntruder";
    private Map map;
    private int t;
    private int guardWins = 0;
    private int intruderWins = 0;
    private int gamesPlayed = 0;
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
        boolean LOOP = Scenario.config.getLoop();
        if(LOOP){
            int numberOfGames = Scenario.config.getNumberOfGames();
            run(gameMode, numberOfGames);
        }
        else{
            run(gameMode, 1);
        }

    }

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

    public void run(int gameMode, int numberOfGames) {
        if(!Scenario.config.GUI){
            init(guardExploration, intruderExploration);
        }
        Thread t = new Thread(() -> {
            while(gamesPlayed<numberOfGames) {
                reinitialise();
                Utils.sleep(20);
                loop(gameMode);
                if(config.isTRAINING()){
                    //perform learning
                }
                if(config.isEXPERIMENT()){
                    printData();
                }
                gamesPlayed++;
            }
        });
        t.start();
    }


    public void loop(int gameMode){
        if(gameMode==0)
            loopExploration();
        else if(gameMode==1)
            loopGuardIntruderGame();
    }

    public void loopGuardIntruderGame(){
        boolean areaReached = false;
        boolean noIntrudersLeft = false;
        while ((!areaReached && !noIntrudersLeft)) {
            step();
            areaReached = Map.checkTargetArea(map, this.t);
            noIntrudersLeft = Map.noIntrudersLeft(map);
            this.t++;
        }
        incrementWins(areaReached, noIntrudersLeft);
    }

    public void loopExploration(){
        boolean explored = false;
        while ((!explored)) {
            step();
            if (config.DEBUG) {
                System.out.println("Timestep: " + this.t + " at: " + Calendar.getInstance().getTime());
                System.out.println(100 * Map.explored(map) + "% of map has been explored");
            }
            explored = Map.isExplored(map);
            this.t++;
        }
    }

    public void step(){
        for(int j=0; j<Scenario.config.getTimeStepSize(); j++) {
            MapUpdater.moveGuards(map, j);
            if(getGameMode()==1) {
                MapUpdater.moveIntruders(map, j);
            }
        }
    }

    private void reinitialise() {
        if(gamesPlayed>0){
            initMap();
            init(guardExploration, intruderExploration);
            if(config.GUI){
                gc.setMap(map);
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

    public void incrementWins(boolean areaReached, boolean noIntrudersLeft){
        if(areaReached){
            intruderWins++;
        }
        else if(noIntrudersLeft){
            guardWins++;
        }
    }
    private void printData() {
        System.out.println(guardExploration + " wins: " + guardWins + ", " + intruderExploration + " wins: " + intruderWins);
        System.out.println("Turns until win: " + t);
    }
}
