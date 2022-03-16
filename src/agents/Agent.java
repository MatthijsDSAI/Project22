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
import javafx.scene.paint.Color;
import utils.DirectionEnum;
import utils.Utils;

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
    private int viewingDistance;

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

        this.viewingDistance = Scenario.config.getDistanceViewing();
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
    }

    public void setCommunication(Area[] markers, int[] type){
        //To be done
    }

    public void checkarea()
    {
        //if object is a wall turnDirection
    }

    public void turnNorth()
    {
        rotate(-angle);
    }

    public Exploration getAlgo() {
        return algo;
    }

    public void turnNorthWest()
    {
        rotate(-angle+45);
    }

    public void turnEast()
    {
        //rotate(-angle+90);
        rotate(-angle+270);
    }

    public void turnSouthWest()
    {
        rotate(-angle+135);
    }

    public void turnSouth()
    {
        rotate(-angle+180);
    }

    public void turnSouthEast()
    {
        rotate(-angle+225);
    }

    public void turnWest()
    {
        rotate(-angle+90);
    }

    public DirectionEnum getDirection() {
        return direction;
    }

    public void setDirection(DirectionEnum direction) {
        this.direction = direction;
    }
    public void turnNorthEast()
    {
        rotate(-angle+315);
    }

    //I changed this so that it only actually moves forward by one step. So the angle has to be changed beforehand.
    //Also not baseSpeed but 1
    public void move()
    {
        //0  -> north
        if(angle == 0)
        {
            y_position++;
            //checkarea();
        }
        //90 -> east
        if(angle == 90)
        {
            x_position++;
            //checkarea();
        }
        //180 -> South
        if(angle == 180)
        {
            y_position--;
            //checkarea();
        }
        // 270 -> west
        if(angle == 270)
        {
            x_position--;
            //checkarea();
        }
        //check relationship between speed and position when related to time
    }

    public void rotate(int angle){
        angle = this.angle + angle;
        angle = Utils.TransFormIntoValidAngle(angle);
    }


    public String runIntoAgent(String agent1, String agent2)
    {
        if(agent1.equals(agent2)) {
            System.out.println("Agent can't run into itself");
            return null;
        }
        double rand = Math.random();
        if(rand%2==0)
            return agent1;
        return agent2;
    }

    public double getAngle(){
        return angle;
    }

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

    public void setAngle(int angle) {
        this.angle = angle;
    }

    public int getViewingDistance(){
        return viewingDistance;
    }
}
