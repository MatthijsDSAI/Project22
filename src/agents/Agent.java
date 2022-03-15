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

    public Agent(Tile agentpos) {
        this.agentPosition = agentpos;
        this.a_name = "Agent";
        this.baseSpeed = Scenario.config.getBASESPEEDGUARD();
        this.audiostdeviation=10;
    }

    public Agent(int x_position, int y_position) {
        this.x_position = x_position;
        this.y_position = y_position;
        this.a_name = "Agent";
        this.baseSpeed = Scenario.config.getBASESPEEDGUARD();
        this.audiostdeviation=10;
        this.currentDirection = DirectionEnum.EAST;
    }

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

    public void createAlgo(String chosenAlgo) {
         switch (chosenAlgo) {
            case "RandomExploration" -> algo = new RandomExploration();
            case "FrontierBasedExploration" -> new FrontierBasedExploration(1, 1);
            case "Baseline" -> new Baseline();
            default -> new RandomExploration();
        }
    }

    public DirectionEnum makeMove() {
        return algo.makeMove();
    }

    public Agent(double baseSpeed, int x_position, int y_position, DirectionEnum givenDirection) {
        this.a_name="Agent";
        this.baseSpeed = baseSpeed;
        this.x_position = x_position;
        this.y_position = y_position;
        this.audiostdeviation=10;
        this.currentDirection = givenDirection;
    }

    public Agent(String name, double baseSpeed, int x_position, int y_position, DirectionEnum givenDirection) {
        this.a_name=name;
        this.baseSpeed = baseSpeed;
        this.audiostdeviation=10;
        this.x_position = x_position;
        this.y_position = y_position;
        this.currentDirection = givenDirection;
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

    public void initializeEmptyMap(Map map){
        this.ownMap = Map.createEmptyMap(map);
    }

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
