package controller;

import GUI.MapGui;
import agents.Guard;
import agents.Intruder;
import com.opencsv.CSVWriter;
import controller.Map.Map;
import controller.Map.MapUpdater;
import utils.CSVData;
import utils.Config;
import utils.GameData;
import utils.Utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.ArrayList;

/*
* Main class which stores and runs everything.
* Contains the agents and the map.
 */
public class GameRunner {

    private final Config config = Scenario.config;
    private final boolean SAVE_CSV = true;
    private String csvString = "";
    private MapGui gui;
    private Scenario scenario;
    private int gameMode;
    private GraphicsConnector gc;
    private String guardExploration = "BaseLineGuard";
    private String intruderExploration = "BaseLineIntruder";
    private Map map;
    private int t;
    private int guardWins = 0;
    private int intruderWins = 0;
    private int gamesPlayed = 0;
    private ArrayList<Integer> explorationValues;
    public GameRunner(Scenario scenario) {
        this.explorationValues = new ArrayList<>();
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
            CSVData csvData = new CSVData("guardType", "intruderType",map.getGuards().size(),map.getIntruders().size(),"mapNAme",null);
            while(gamesPlayed<numberOfGames) {
                reinitialise();
                Utils.sleep(20);
                GameData game = loop(gameMode);
                if(config.isTRAINING()){
                    //perform learning
                }
                if(config.isEXPERIMENT()){
                    printData();
                }
                gamesPlayed++;
                if(SAVE_CSV) csvData.addGame(game);
                if(SAVE_CSV) saveCsv(csvData, "csvData.csv");
            }
            if(SAVE_CSV) saveCsv(csvData, "csvData.csv");

        });
        t.start();
    }


    public GameData loop(int gameMode){
        if(gameMode==0)
            loopExploration();
        else if(gameMode==1)
            return loopGuardIntruderGame();
        return null;
    }

    public GameData loopGuardIntruderGame(){
        boolean areaReached = false;
        boolean noIntrudersLeft = false;
        while ((!areaReached && !noIntrudersLeft)) {
            step();
            Map.checkTargetAreaGuards(map);
            areaReached = Map.checkTargetAreaIntruders(map, this.t);
            noIntrudersLeft = Map.noIntrudersLeft(map);
            this.t++;
        }
        incrementWins(areaReached, noIntrudersLeft);
        int victor = noIntrudersLeft ? 0 : 1;
        GameData gd = new GameData(victor, map.getDiscoveredTAFirst(), this.t, Map.explored(map),Map.exploredForIntruders(map)); //need to add "intruder explored"
        return gd;
    }


    private String intToAgent(int agent){
        if(agent==0) return "Guard";
        else if(agent==1) return "Intruder";
        else return null;
    }
    private void saveCsv(CSVData csvData, String filepath) {
        // first create file object for file placed at location
        // specified by filepath
        File file = new File(filepath);
        try {
            // create FileWriter object with file as parameter
            FileWriter outputfile = new FileWriter(file);

            // create CSVWriter object filewriter object as parameter
            CSVWriter writer = new CSVWriter(outputfile);

            // adding base info to csv
            String[] baseInfo = {"Guard Type", csvData.getGuardType(), "Intruder Type", csvData.getIntruderType(),"Number of Guards", String.valueOf(csvData.getNumGuards()), "Number of Intruders", String.valueOf(csvData.getNumIntruders()), "Map Name", csvData.getMapName(), "Number of games played", String.valueOf(csvData.getGames().size())};
            writer.writeNext(baseInfo);

            // adding header to csv
            String[] header = { "Game Number", "Victor", "Reached TA First", "Turns Taken", "% Map explored Guards", "% Map explored intruders" };
            writer.writeNext(header);

            // add data to csv

            for (int i = 0; i < csvData.getGames().size(); i++) {
                GameData game = csvData.getGames().get(i);
                String[] data = {String.valueOf(i+1),intToAgent(game.getVictor()),intToAgent(game.getReachedTAFirst()), String.valueOf(game.getTurnsTaken()), String.valueOf(game.getMapExploredGuards()), String.valueOf(game.getMapExploredIntruders())};
                writer.writeNext(data);
            }

            // closing writer connection
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loopExploration(){
        boolean explored = false;
        while ((!explored)) {
            step();
            if(config.DEBUG) {
                System.out.println("Timestep: " + this.t + " at: " + Calendar.getInstance().getTime());
                System.out.println(100 * Map.explored(map) + "% of map has been explored");
            }
            Map.explored(map);
            explored = Map.isExplored(map);
            if(explored){
                explorationValues.add(t);
            }
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
        if(gameMode==1) {
            System.out.println(guardExploration + " wins: " + guardWins + ", " + intruderExploration + " wins: " + intruderWins);
            System.out.println("Turns until win: " + t);
        }
        if(gameMode==0){
            System.out.println("Timesteps until full exploration: " + (t-1));
            int sum = 0;
            for(Integer val : explorationValues){
                sum+=val;
            }
            System.out.println("Average timesteps until full exploration: " + (double)sum/explorationValues.size());
            System.out.println("Games played: " + gamesPlayed);
        }
    }
}
