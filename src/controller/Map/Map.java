package controller.Map;

import agents.Agent;
import agents.Guard;
import agents.Intruder;
import controller.Area;
import controller.GraphicsConnector;
import controller.Map.tiles.*;
import controller.Scenario;
import controller.TelePortal;
import utils.DirectionEnum;

import java.util.ArrayList;
import java.util.Arrays;

/*
 * EVERYTHING that is directly involved with the map.
 * Messy right now, will require cleaning up in phase 2.
 */
public class Map {
    private Tile[][] tiles;
    private Agent agent;
    private ArrayList<Guard> guards = new ArrayList<>();
    private ArrayList<Intruder> intruders = new ArrayList<>();
    private int horizontalSize;
    private int verticalSize;
    private boolean isExplored = false;

    public Map(int horizontalSize, int verticalSize, Agent agent){
        this.agent = agent;
        this.horizontalSize = horizontalSize;
        this.verticalSize = verticalSize;
        tiles = new Tile[horizontalSize][verticalSize];
        System.out.println(tiles);
    }

    public Map(int row, int col){
        this.horizontalSize = col;
        this.verticalSize = row;
        tiles = new Tile[row][col];
    }

    public static Map createEmptyMap(Map map) {
        int horizontalSize = map.getTiles().length;
        int verticalSize = map.getTiles()[0].length;
        return new Map(horizontalSize, verticalSize);
    }

    public static void addAgent(Map map, Agent agent, int x, int y) {
        map.getTile(x,y).addAgent(agent);

    }
    
    static Tile getTileFromDirection(Map map, Tile agentPosition, DirectionEnum direction) {
        int x = agentPosition.getX();
        int y = agentPosition.getY();
        if(direction.equals(DirectionEnum.EAST)){
            x++;
        }
        else if(direction.equals(DirectionEnum.WEST)){
            x--;
        }
        else if(direction.equals(DirectionEnum.NORTH)){
            y--;
        }
        else if(direction.equals(DirectionEnum.SOUTH)){
            y++;
        }
        return map.getTile(x,y);
    }


    public static boolean isExplored(Map map) {
        return map.isExplored;
    }

    public static double explored(Map map){
        double notExplored = 0;
        double explored = 0;
        for(Tile[] tiles : map.tiles){
            for(Tile tile : tiles){
                if(!tile.isExploredByDefault()) {
                    if(tile.getExplored()){
                        explored++;
                    }
                    else{
                        notExplored++;
                    }
                }
                else{
                    explored++;
                }

            }
        }
        if(explored/(notExplored+explored) == 1 ){
            map.isExplored = true;
        }
        return explored/(notExplored+explored);
    }

    public Tile[][] getTiles() {
        return tiles;
    }

    public void printMap(){
        System.out.println(Arrays.deepToString(tiles).replace("], ", "]\n"));
    }

    public Tile getTile(int x, int y){
        return tiles[y][x];
    }

    public void setTile(int x, int y, Tile tile){
        tiles[y][x] = tile;
    }

    public void setTile(Tile tile){
        tiles[tile.getY()][tile.getX()] = tile;
    }

    public ArrayList<Guard> getGuards() {return guards;}

    public ArrayList<Intruder> getIntruders() {return intruders;}

    public int getHorizontalSize() {
        return horizontalSize;
    }

    public int getVerticalSize() {
        return verticalSize;
    }

    public Map clone(){
        return null;
    }
}
