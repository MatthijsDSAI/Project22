package agents;

import controller.Area;
import controller.Hearing.Hearing;
import controller.Map.Map;
import controller.Map.MapUpdater;
import controller.Map.tiles.Tile;
import controller.Scenario;
import controller.Visibility.Visibility;
import exploration.Exploration;
import exploration.FrontierBasedExploration;
import exploration.RandomExploration;
import javafx.scene.paint.Color;
import utils.DirectionEnum;
import utils.Utils;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public abstract class Agent{
    double audiostdeviation;
    int x_position,y_position, angle;
    public String a_name;
    double baseSpeed;
    double soundproduced;
    boolean isSprinting;
    public Map ownMap;
    private ArrayList<Tile> visibleTiles = new ArrayList<>();
    private Marker[] marker = new Marker[5]; // 5 types of markers
    private Tile agentPosition;
    public Exploration exploration;
    Color[] c = {Color.RED, Color.ORANGE, Color.GREEN, Color.WHITE, Color.LAVENDER, Color.BROWN, Color.YELLOW, Color.PINK}; // color vector for markers
    private ArrayList<Tile> hearingTiles;
    public Tile startingTile;
    public boolean hasFoundTargetArea = false;
    public Tile targetArea;

    /*
     * The agent class
     * An agent tracks it's own position relative to its starting position.
     */
    public Agent(Tile tile)
    {
        this.x_position = tile.getX();
        this.y_position = tile.getY();
        this.a_name = "Agent";
        this.audiostdeviation=10;
        angle= DirectionEnum.getAngleFromDirection(Utils.getRandomDirection());
        startingTile = tile;
        agentPosition = tile;
    }

    public abstract void createExplorationAlgorithm(String exploration, Map map);

    public void rotate(int angle){
        this.angle = this.angle + angle;
        this.angle = Utils.TransFormIntoValidAngle(angle);
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

    public boolean canHearSound(Map map) {
        this.hearingTiles = Hearing.computeHearingTiles(map, this);
        boolean canHear = false;
        for(Tile tile : hearingTiles)
        {
            if(tile.hasSound()!= false)
            {
                canHear=true;
                break;
            }
        }
        return canHear;
    }

    public void produceSound(Map map){
        this.hearingTiles = Hearing.computeHearingTiles(map, this);
        for(Tile tile : hearingTiles)
        {
            //Changed this so that we set a value instead of a boolean
            //Not exactly sure on implementation as I've seen that you've done some stuff on this already
            //Easiest option would be to find the distance to the origin and scale linearly. Because we can access
            //coordinates of the tiles, we can simply say that sqrt((x2-x1)^2+(y2-y1^2)) = distance to the origin of
            //the sound. Then we decide on a range the sound has, and scale linearly to get the value on each tile.


            //TODO: implement this
            //tile.setSound();
        }
    }

    public double getAngle(){
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    public void initializeEmptyMap(Map map){
        this.ownMap = Map.createEmptyMap(map);
    }

    public void computeVisibleTiles(Map map){
        this.visibleTiles = Visibility.computeVisibleTiles(map, this);
        for(Tile tile : visibleTiles){
            tile.setExplored(true);
            ownMap.setTile(tile.clone());
            tile.setCurrentlyViewed(true);
        }
    }

    public ArrayList<Tile> getVisibleTiles() {
        return visibleTiles;
    }

    public void setAgentPosition(Tile tile){
        agentPosition = tile;
    }
    
    public void createMarkers(int number_markers, int distance) {
        for(int i=0;i<5;i++)
            marker[i].setSpecifics(number_markers,distance);
    }
    
    public void addMarkers(int i, Color c, Map map){
        marker[i].addMarker(this, c, map);
        marker[i].setNumber_markers(marker[i].getNumber_markers()-1);
    }

    public Tile getAgentPosition(){
        return agentPosition;
    }

    public int getX_position() {
        return agentPosition.getX();
    }

    public int getY_position() {
        return agentPosition.getY();
    }

    public int getSpeed() {
        return (int) baseSpeed;
    }

    public void setSprinting(boolean sprint){this.isSprinting=sprint;}

    public boolean getIsSprinting(){return isSprinting;}

    public Exploration getExploration() {
        return exploration;
    }

    public Tile getStartingTile(){
        return startingTile;
    }

    public boolean hasFoundTargetArea(){
        if(!hasFoundTargetArea){
            for(Tile tile : visibleTiles){
                if(tile.toString().equals("TargetArea")){
                    hasFoundTargetArea = true;
                    targetArea = tile;
                }
            }
        }
        return hasFoundTargetArea;
    }

    public Tile getTargetArea(){
        return  targetArea;
    }

    public abstract Object getType();

    public abstract Color getColor();
}
