package controller;

import agents.Agent;
import agents.HumanPlayer;
import agents.Player;
import controller.Map.Map;
import controller.Map.tiles.Tile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

//idea is to make this the class where we run store everything and have our main loop
public class GameRunner {
    private Scenario scenario;
    private Map map;
    private HumanPlayer HumanPlayer; // something needs to be done about this
    private GameMode gameMode; // seems like there needs to be gameMode 1, gameMode 2...
    private int numOfSteps;
    private int numIntruders;
    private int numGuards;
    private Area spawnAreaIntruders;
    private Area spawnAreaGuards;
    private Area targetArea;
    private ArrayList<Area> walls = new ArrayList<>();
    private ArrayList<TelePortal> teleports = new ArrayList<>();
    private ArrayList<Player> players = new ArrayList<>();
    private HashMap<Player, int[][]> locationHistory = new HashMap<>();
    private ArrayList<Area> shaded;
    //private scaling; // what is this?

//    public GameRunner(int mapHeight, int mapWidth, HumanPlayer player, GameMode gameMode, int numIntruders, int numGuards,
//                      Area spawnAreaIntruders, Area spawnAreaGuards, Area targetArea, ArrayList<Area> walls,
//                      ArrayList<TelePortal> teleports, ArrayList<Player> players) {
//        this.map = new Map(mapHeight, mapWidth);
//        this.numOfSteps = 0;
//        this.player = player;
//        this.gameMode = gameMode;
//        this.numIntruders = numIntruders;
//        this.numGuards = numGuards;
//        this.spawnAreaIntruders = spawnAreaIntruders;
//        this.spawnAreaGuards = spawnAreaGuards;
//        this.targetArea = targetArea;
//        this.walls = walls;
//        this.teleports = teleports;
//        this.players = players;
//    }

    public GameRunner(Scenario scenario) {
        this.scenario = scenario;
        this.map = new Map(scenario.getMapHeight()+1, scenario.getMapWidth()+1);
        map.loadMap(scenario);
        map.printMap();
        init(scenario);
        run();
    }

    public void init(Scenario scenario){
        HumanPlayer = new HumanPlayer();
        map.addPlayer(HumanPlayer, 0, 0);

        this.numOfSteps = 0;
        this.gameMode = gameMode.getWhichGameMode(scenario.getGameMode());
        this.numIntruders = scenario.getNumIntruders();
        this.numGuards = scenario.getNumGuards();
        this.spawnAreaIntruders = scenario.getSpawnAreaIntruders();
        this.spawnAreaGuards = scenario.getSpawnAreaGuards();
        this.targetArea = scenario.getTargetArea();
        this.walls = scenario.getWalls();
        this.teleports = scenario.getTeleportals();
        this.shaded = scenario.getShaded();
        // this.players = scenario;
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
            map.printMap();
        }
    }

    //does nothing yet
    public void step(){
        numOfSteps++;
        HumanPlayer.update();
    }


    public boolean inWall(double x, double y){
        for(int j=0;j<walls.size();j++){
            if(walls.get(j).isHit(x,y)){
                return true;
            }
        }
        return false;
    }

    public double[][] spawnGuards(){
        double[][] tmp = new double[numGuards][4];
        double dx = spawnAreaGuards.rightBoundary * 10 - spawnAreaGuards.leftBoundary * 10;
        double dy = spawnAreaGuards.bottomBoundary * 10 - spawnAreaGuards.topBoundary * 10;

        for(int i=0; i<numGuards; i++){
            tmp[i][0]=spawnAreaGuards.leftBoundary * 10 + Math.random() * dx;
            tmp[i][1]=spawnAreaGuards.topBoundary * 10+ Math.random() * dy;
        }
        return tmp;
    }

    public double[][] spawnIntruders(){
        double[][] tmp = new double[numIntruders][4];
        double dx=spawnAreaIntruders.rightBoundary * 10 - spawnAreaIntruders.leftBoundary * 10;
        double dy=spawnAreaIntruders.bottomBoundary * 10 - spawnAreaIntruders.topBoundary * 10;

        for(int i=0; i<numIntruders; i++){
            tmp[i][0]=spawnAreaIntruders.leftBoundary * 10 + Math.random() * dx;
            tmp[i][1]=spawnAreaIntruders.bottomBoundary * 10 + Math.random() * dy;
        }

        return tmp;
    }

    public Map getMap() {
        return map;
    }

    public HumanPlayer getPlayer() {return HumanPlayer;}

    public Scenario getScenario() {return scenario;}

    public void setScenario(Scenario scenario) {
        this.scenario = scenario;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public int getNumOfSteps() {
        return numOfSteps;
    }

    public void setNumOfSteps(int numOfSteps) {
        this.numOfSteps = numOfSteps;
    }

    public int getNumIntruders() {
        return numIntruders;
    }

    public void setNumIntruders(int numIntruders) {
        this.numIntruders = numIntruders;
    }

    public int getNumGuards() {
        return numGuards;
    }

    public void setNumGuards(int numGuards) {
        this.numGuards = numGuards;
    }

    public Area getSpawnAreaIntruders() {
        return spawnAreaIntruders;
    }

    public void setSpawnAreaIntruders(Area spawnAreaIntruders) {
        this.spawnAreaIntruders = spawnAreaIntruders;
    }

    public Area getSpawnAreaGuards() {
        return spawnAreaGuards;
    }

    public void setSpawnAreaGuards(Area spawnAreaGuards) {
        this.spawnAreaGuards = spawnAreaGuards;
    }

    public Area getTargetArea() {
        return targetArea;
    }

    public void setTargetArea(Area targetArea) {
        this.targetArea = targetArea;
    }

    public ArrayList<Area> getWalls() {
        return walls;
    }

    public void setWalls(ArrayList<Area> walls) {
        this.walls = walls;
    }

    public ArrayList<TelePortal> getTeleports() {
        return teleports;
    }

    public void setTeleports(ArrayList<TelePortal> teleports) {
        this.teleports = teleports;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }
}
