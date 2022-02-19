package controller;

import agents.Agent;
import agents.HumanPlayer;
import agents.Player;
import controller.Map.Map;

import java.util.ArrayList;
import java.util.Arrays;

//idea is to make this the class where we run store everything and have our main loop
public class GameRunner {
    private Scenario scenario;
    private Map map;
    private HumanPlayer player;
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
//    private ArrayList<Area> shaded; //what is this?
    //private scaling; // what is this?

    public GameRunner () {

    }

    public GameRunner(Scenario scenario) {
        this.scenario = scenario;
        map = new Map(scenario.getMapHeight()+1, scenario.getMapWidth()+1);
        map.loadMap(scenario);
        map.printMap();
        init();
    }

    public Map getMap() {
        return map;
    }

    public void init(){
        player = new HumanPlayer();
        int x = 0, y = 0;
        map.addPlayer(player,x,y);
        numOfSteps = 0;
        run();
    }


    //does nothing yet
    public void step(){
        numOfSteps++;
        player.update();

    }


    public boolean inWall(double x, double y){
        boolean tmp = false;
        for(int j=0;j<walls.size();j++){
            if(walls.get(j).isHit(x,y)){
                tmp=true;
            }
        }
        return(tmp);
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



    public HumanPlayer getPlayer() {
        return player;
    }

    public Scenario getScenario() {
        return scenario;
    }

    public void setScenario(Scenario scenario) {
        this.scenario = scenario;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public void setPlayer(HumanPlayer player) {
        this.player = player;
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
