package controller.Map;

import agents.Agent;
import controller.Area;
import controller.Map.tiles.Floor;
import controller.Map.tiles.TeleportalTile;
import controller.Map.tiles.Tile;
import controller.Map.tiles.Wall;
import controller.Scenario;
import controller.TelePortal;
import utils.DirectionEnum;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;

public class Map {
    private Tile[][] tiles;
    private String[][] test;
    private Agent agent;
    private Point2D agentPosition;

    public Map(int horizontalSize, int verticalSize, Agent agent){
        this.agent = agent;
        this.agentPosition = new Point2D.Double(0,0);
        tiles = new Tile[horizontalSize][verticalSize];
        test = new String[horizontalSize][verticalSize];
    }


    public Map(int row, int col){
        tiles = new Tile[row][col];
    }

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
                tiles[i][j] = new Tile(new Floor(), j * 10, i * 10);
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

    public void loadTeleportal(Area teleportal){
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                if(teleportal.containP(tiles[i][j].getX(), tiles[i][j].getY())){
                    tiles[i][j].setType(new TeleportalTile());
                }
            }
        }
    }

    private void fallsWithinWall(Area wall, int i, int j, int oppIndex) {
        if (j >= wall.getLeftBoundary() && j <= wall.getRightBoundary() && oppIndex <= wall.getTopBoundary() && oppIndex >= wall.getBottomBoundary()) {
            //could do with a factory here
            tiles[oppIndex][j] = new Tile(new Wall());
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

    public void addAgent(Agent agent, int x, int y) {
        tiles[y][x].addAgent(agent);
    }

    public void moveAgentFromTo(Agent agent, int xFrom, int yFrom, int xTo, int yTo){
        Tile tile = tiles[yTo][xTo];
        if(tile.isWalkable()) {
            tiles[yFrom][xFrom].removeAgent();
            tiles[yTo][xTo].addAgent(agent);
            setAgentPosition(new Point2D.Double(xTo, yTo));
        }
        else{
            throw new RuntimeException("Can not move to tile " + xTo + ", " + yTo);
        }
    }

    public void moveAgent(Agent agent, String direction){
        Point2D agentPosition = getAgentPosition(agent);
        Point2D toTile = getTileFromDirection(agentPosition, direction);
        int xFrom = (int) agentPosition.getX();
        int yFrom = (int) agentPosition.getY();
        int xTo = (int) toTile.getX();
        int yTo = (int) toTile.getY();
        Tile tile = tiles[yTo][xTo];
        if(tile.isWalkable()) {
            tiles[yFrom][xFrom].removeAgent();
            tiles[yTo][xTo].addAgent(agent);
            setAgentPosition(toTile);
        }
        else{
            throw new RuntimeException("Can not move to tile " + xTo + ", " + yTo);
        }
    }

    private Point2D getTileFromDirection(Point2D agentPosition, String direction) {
        int x = (int) agentPosition.getX();
        int y = (int) agentPosition.getY();
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
        return new Point2D.Double(x,y);
    }


    public boolean isExplored() {
        for(Tile[] tiles : tiles){
            for(Tile tile : tiles){
                if(!tile.isExplored()){
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
                    if (!tile.isExplored()) {
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

    public Point2D getAgentPosition(Agent agent){
        return agentPosition;
    }

    private void setAgentPosition(Point2D agentPosition) {
        this.agentPosition = agentPosition;
    }
}
