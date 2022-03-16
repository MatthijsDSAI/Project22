package agents;

import controller.Area;
import controller.Map.Map;
import controller.Map.tiles.Tile;
import controller.Scenario;
import exploration.Baseline;
import exploration.Exploration;
import exploration.FrontierBasedExploration;
import exploration.RandomExploration;
import utils.DirectionEnum;

import java.util.ArrayList;

public abstract class Agent{
    double audiostdeviation;
    int x_position,y_position, angle;
    public String a_name;
    double baseSpeed, range, visangle, visibility,restTime,sprintTime, turn_speed, noiseProd;
    public Map ownMap;
    //not to be used in agent class
    private Tile agentPosition;
    private DirectionEnum currentDirection;
    private String algoName;
    private Exploration algo;

    //Constructor
    public Agent(int x_position, int y_position) {
        this.x_position = x_position;
        this.y_position = y_position;
        this.a_name = "Agent";
        this.baseSpeed = Scenario.config.getBASESPEEDGUARD();
        this.audiostdeviation=10;
        this.currentDirection = DirectionEnum.EAST;
    }

    //Constructor
    public Agent(int x_position, int y_position, String chosenAlgo) {
        this.x_position = x_position;
        this.y_position = y_position;
        this.a_name = "Agent";
        this.baseSpeed = Scenario.config.getBASESPEEDGUARD();
        this.audiostdeviation=10;
        this.currentDirection = DirectionEnum.EAST;
        this.algoName = chosenAlgo;
        createAlgo(chosenAlgo);
    }

    /*
    Accesses the exploration algorithm that was chosen
    @param chosenAlgo - the name of the chosen exploration algorithm
    */
    public void createAlgo(String chosenAlgo) {
         switch (chosenAlgo) {
            case "RandomExploration" -> algo = new RandomExploration();
            case "FrontierBasedExploration" -> algo = new FrontierBasedExploration(1,1);
            case "Baseline" -> algo = new Baseline();
            default -> new RandomExploration();
        }
    }

    /*
    Decided on the direction in which the agent should move
    */
    public DirectionEnum makeMove() {
        return algo.makeMove();
    }

    public void setVelocities(double speed, double rest, double sprint_time, double turn_speed, double noise_level) {
        this.baseSpeed=speed;
        this.restTime = rest;
        this.sprintTime=sprint_time;
        this.turn_speed= turn_speed;
        this.noiseProd = noise_level;
    }

    public void setVisualcap(double range, double angle, double visibility){
        this.range = range;
        this.visangle = angle;
        this.visibility = visibility;
    }

    public void setAudiocap(){
        this.audiostdeviation=10;
    } //TODO

    public void setCommunication(Area[] markers, int[] type){ } //TODO

    /*
    Creates an empty map of the same size as the actual map
    @param map - the map that the agent should explore
    */
    public void initializeEmptyMap(Map map){
        this.ownMap = Map.createEmptyMap(map);
    }

    /*
    Update the tiles on the map that are visible to the agent
    @param map - version of the map
    */
    public void computeVisibleTiles(Map map){
        ArrayList<Tile> visibleTiles = map.computeVisibleTiles(this);
        //Todo: temp just for visualisation
        for(Tile tile : visibleTiles){
            tile.setExplored(true);
            ownMap.setTile(tile.clone());
        }
    }

    public void setAgentPosition(Tile tile){
        agentPosition = tile;
    }

    public Tile getAgentPosition(){
        return agentPosition;
    }

    public int getX_position() {
        return x_position;
    }

    public int getY_position() {
        return y_position;
    }

    public DirectionEnum getCurrentDirection() {
        return currentDirection;
    }

    public void setCurrentDirection(DirectionEnum currentDirection) {
        this.currentDirection = currentDirection;
    }
}
