package agents;

import controller.Area;
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
    public Map ownMap;
    private ArrayList<Tile> visibleTiles = new ArrayList<>();
    private Marker[] marker = new Marker[5]; // 5 types of markers
    private Tile agentPosition;
    public Exploration exploration;
    Color[] c = {Color.RED, Color.ORANGE, Color.GREEN, Color.WHITE, Color.LAVENDER, Color.BROWN, Color.YELLOW, Color.PINK}; // color vector for markers

    /*
     * The agent class
     * An agent tracks it's own position relative to its starting position.
     */
    public Agent(int x_position, int y_position)
    {
        this.x_position = x_position;
        this.y_position = y_position;
        this.a_name = "Agent";
        this.audiostdeviation=10;
        angle= DirectionEnum.getAngleFromDirection(Utils.getRandomDirection());
        produceSound();
    }

    public abstract void createExplorationAlgorithm(String exploration, Tile[][] tiles);

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

    public void computeSound(Map map) {
        this.visibleTiles = Visibility.computeVisibleTiles(map, this);
        for(Tile tile : visibleTiles)
            agentPosition.manhattanDist(tile);
    }

    public void produceSound(){
        //if agent is resting
        if(baseSpeed == 0)
            //number of tiles
            soundproduced = 0.0;
        //if agent is sprinting
        if(baseSpeed > 15)
            //number of tiles
            soundproduced = 10;
            //if agent walks
            //number of tiles
        else soundproduced = 5;
        //return soundproduced;
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
    
    public void addMarkers(int i, Color c){
        marker[i].addMarker(this, c);
        marker[i].setNumber_markers(marker[i].getNumber_markers()-1);
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

    public int getSpeed() {
        return (int) baseSpeed;
    }

    public Exploration getExploration() {
        return exploration;
    }
}
