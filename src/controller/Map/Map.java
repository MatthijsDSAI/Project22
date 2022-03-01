package controller.Map;

import agents.Agent;
import controller.Area;
import controller.Map.tiles.*;
import controller.Scenario;
import controller.TelePortal;
import utils.DirectionEnum;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;

public class Map {
    private Tile[][] tiles;
    private String[][] test;
    private Agent agent;
    private Tile agentPosition;
    private Tile tileVersion;

    public Map(int horizontalSize, int verticalSize, Agent agent){
        this.agent = agent;

        tiles = new Tile[horizontalSize][verticalSize];
        this.agentPosition = tiles[0][0];
        test = new String[horizontalSize][verticalSize];
    }


    public Map(int row, int col){
        tiles = new Tile[row][col];
    }

    public static Map createEmptyMap(Map map) {
        int horizontalSize = map.getTiles().length;
        int verticalSize = map.getTiles().length;
        return new Map(horizontalSize, verticalSize);
    }

    public void computeAgentVision(){
        ArrayList<Tile> visibleTiles = new ArrayList<>();
        visibleTiles.add(agentPosition);
        double angle = agent.getAngle();

    }
    public void addAgent(Agent agent, int x, int y) {
        tiles[y][x].addAgent(agent);
    }

    public void moveAgentFromTo(Agent agent, int xFrom, int yFrom, int xTo, int yTo){
        Tile tile = tiles[yTo][xTo];
        if(tile.isWalkable()) {
            tiles[yFrom][xFrom].removeAgent();
            tiles[yTo][xTo].addAgent(agent);
            setAgentPosition(tiles[yTo][xTo]);
        }
        else{
            throw new RuntimeException("Can not move to tile " + xTo + ", " + yTo);
        }
    }

    public void moveAgent(Agent agent, String direction){
        Tile fromTile = getAgentPosition(agent);
        Tile toTile = getTileFromDirection(agentPosition, direction);



        changeTiles(fromTile, toTile);

        checkTeleport(fromTile, toTile);


    }

    public void checkTeleport(Tile fromTile, Tile toTile){
        if(toTile.toString().equals("TelePortal")){
            TeleportalTile teleportalTile = (TeleportalTile) tiles[toTile.getY()][toTile.getX()];
            int[] teleportTo = teleportalTile.teleport();
            fromTile = toTile;
            toTile = tiles[teleportTo[0]][teleportTo[1]];
            changeTiles(fromTile, toTile);
        }
    }
    public void changeTiles(Tile fromTile, Tile toTile){
        if(fromTile.isWalkable()) {
            tiles[fromTile.getY()][fromTile.getX()].removeAgent();
            tiles[toTile.getY()][toTile.getX()].addAgent(agent);
            setAgentPosition(toTile);
        }
        else{
            throw new RuntimeException("Can not move to tile " + toTile.getX() + ", " + toTile.getY());
        }
    }
    private Tile getTileFromDirection(Tile agentPosition, String direction) {
        int x = agentPosition.getX();
        int y = agentPosition.getY();
        if(direction.equals(DirectionEnum.RIGHT.getDirection())){
            x++;
        }
        else if(direction.equals(DirectionEnum.LEFT.getDirection())){
            x--;
        }
        else if(direction.equals(DirectionEnum.UP.getDirection())){
            y--;
        }
        else if(direction.equals(DirectionEnum.DOWN.getDirection())){
            y++;
        }
        return tiles[y][x];
    }


    public boolean isExplored() {
        for(Tile[] tiles : tiles){
            for(Tile tile : tiles){
                if(!tile.isExploredByDefault()){
                    return false;
                }
            }
        }
        return true;
    }

    public double explored(){
        double notExplored = 0;
        double explored = 0;
        for(Tile[] tiles : tiles){
            for(Tile tile : tiles){
                if(!tile.isExploredByDefault()) {
                    if (!tile.isExploredByDefault()) {
                        notExplored++;
                    }
                    else{
                        explored++;
                    }
                }
            }
        }
        return explored/(notExplored+explored);
    }

    public Tile[][] getTiles() {
        return tiles;
    }

    public Tile getAgentPosition(Agent agent){
        return agentPosition;
    }

    private void setAgentPosition(Tile agentPosition) {
        this.agentPosition = agentPosition;
    }


    /////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////
    ///////////////////---------INITIALIZATION----------/////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////


    public void loadMap(Scenario scenario){

        initializeEmptyMap();
        ArrayList<Area> walls = scenario.getWalls();
        ArrayList<TelePortal> teleportals = scenario.getTeleportals();
        for (Area wall : walls) {
//            System.out.println("aaa");
            loadWall(wall);
        }
        for(TelePortal telePortal : teleportals){
            loadTeleportal(telePortal);
        }
        printLayout();
    }

    private void initializeEmptyMap() {
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                tiles[i][j] = new Floor(j * 10, i * 10);
            }
        }
    }


    public void loadWall(Area wall){
        for (int i = 0; i < tiles.length; i++) {

            for (int j = 0; j < tiles[0].length; j++) {
                //needed as top left = 0,0
                int oppIndex = tiles.length - i - 1;
                fallsWithinWall(wall, i, j, oppIndex);
                test[i][j] = "i: " + oppIndex + " j: " + j;

            }
        }
    }

    public void loadTeleportal(TelePortal teleportal){
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                if(teleportal.containP(tiles[i][j].getX(), tiles[i][j].getY())){
                    tiles[i][j].setType(new TeleportalTile(teleportal));
                }
            }
        }
    }

    private void fallsWithinWall(Area wall, int i, int j, int oppIndex) {
        if (j >= wall.getLeftBoundary() && j <= wall.getRightBoundary() && oppIndex <= wall.getTopBoundary() && oppIndex >= wall.getBottomBoundary()) {
            //could do with a factory here
            // tiles[oppIndex][j] = new Wall(); //TODO what is tried to do here? Shouldn't be only boolean?
        }
//        if (j >= wall.getLeftBoundary() && j <= wall.getRightBoundary() && i - 1 <= wall.getTopBoundary() && i-1 >= wall.getBottomBoundary()) {
//            //could do with a factory here
//            map[i][j] = new Tile(new Wall());
//            System.out.print(i + ", " + j);
//        }
//        if(wall.containP(i, j)){
//            map[i][j] = new Tile(new Wall());
//        }
    }

    public void printMap(){
        System.out.println(Arrays.deepToString(tiles).replace("], ", "]\n"));
    }
    public void printLayout(){
        System.out.println(Arrays.deepToString(test).replace("], ", "]\n"));
    }
}
