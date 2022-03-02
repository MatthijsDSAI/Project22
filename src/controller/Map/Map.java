package controller.Map;

import agents.Agent;
import agents.TestAgent;
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
    private Agent agent;

    private Tile tileVersion;
    private ArrayList<Agent> guards = new ArrayList<Agent>(); // TODO change to "Guard", "Intruder" later
    private ArrayList<Agent> intruders = new ArrayList<>();

    public Map(int horizontalSize, int verticalSize, Agent agent){
        this.agent = agent;
        tiles = new Tile[horizontalSize][verticalSize];
        agent.setAgentPosition(getTile(0,0));
    }



    public Map(int row, int col){
        tiles = new Tile[row][col];
    }

    public static Map createEmptyMap(Map map) {
        int horizontalSize = map.getTiles().length;
        int verticalSize = map.getTiles().length;
        return new Map(horizontalSize, verticalSize);
    }

    public void addAgent(Agent agent, int x, int y) {
        getTile(x,y).addAgent(agent);

    }

    public void moveAgentFromTo(Agent agent, int xFrom, int yFrom, int xTo, int yTo){
        Tile tile = getTile(xTo, yTo);
        if(tile.isWalkable()) {
            getTile(xFrom, yFrom).removeAgent();
            getTile(xTo, yTo).addAgent(agent);
            agent.setAgentPosition(getTile(xTo, yTo));
        }
        else{
            throw new RuntimeException("Can not move to tile " + xTo + ", " + yTo);
        }
    }

    public void moveAgent(Agent agent, String direction){
        Tile fromTile = agent.getAgentPosition();
        Tile toTile = getTileFromDirection(agent.getAgentPosition(), direction);


        changeTiles(fromTile, toTile);

        checkTeleport(fromTile, toTile);


    }

    public void checkTeleport(Tile fromTile, Tile toTile){
        if(toTile.toString().equals("TelePortal")){
            TeleportalTile teleportalTile = (TeleportalTile) tiles[toTile.getY()][toTile.getX()];
            int[] teleportTo = teleportalTile.teleport();
            fromTile = toTile;
            toTile = getTile(teleportTo[0], teleportTo[1]);
            changeTiles(fromTile, toTile);
        }
    }
    public void changeTiles(Tile fromTile, Tile toTile){
        if(fromTile.isWalkable()) {
            getTile(fromTile.getX(),fromTile.getY()).removeAgent();
            getTile(toTile.getX(),toTile.getY()).addAgent(agent);
            agent.setAgentPosition(toTile);
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
        int left;
        int right;
        int up;
        int down;
        initializeEmptyMap();

        for (Area area : scenario.getAreas()) {
            left = area.getLeftBoundary();
            right = area.getRightBoundary();
            up = area.getTopBoundary();
            down = area.getBottomBoundary();

            switch (area.getType()) {
                case "targetArea":
                    for (int i = left; i <= right; i++) {
                        for (int j = down; j <= up; j++) {
                            tiles[j][i] = new TargetArea(j, i);
                        }
                    }
                    break;
                case "spawnAreaIntruders":
                    for (int i = left; i <= right; i++) {
                        for (int j = down; j <= up; j++) {
                            tiles[j][i] = new SpawnAreaIntruders(j, i);
                        }
                    }
                    break;
                case "spawnAreaGuards":
                    for (int i = left; i <= right; i++) {
                        for (int j = down; j <= up; j++) {
                            tiles[j][i] = new SpawnAreaGuards(j, i);
                        }
                    }
                    break;
                case "wall":
                    for (int i = left; i <= right; i++) {
                        for (int j = down; j <= up; j++) {
                            tiles[j][i] = new Wall(j, i);
                        }
                    }
                    break;
                case "shaded":
                    for (int i = left; i <= right; i++) {
                        for (int j = down; j <= up; j++) {
                            tiles[j][i] = new Shaded(j, i);
                        }
                    }
                    break;
            }
        }

        for(TelePortal telePortal : scenario.getTeleportals()){ // teleportal here because it needs additional parameters
            left = telePortal.getLeftBoundary();
            right = telePortal.getRightBoundary();
            up = telePortal.getTopBoundary();
            down = telePortal.getBottomBoundary();
            for (int i = left; i <= right; i++) {
                for (int j = down; j <= up; j++) {
                    tiles[j][i] = new TeleportalTile(j, i, telePortal.getxTarget(), telePortal.getyTarget(), telePortal.getOutOrientation());
                }
            }
        }

        if (scenario.getGameMode() == 0) {
            for (int i = 0; i < scenario.getNumGuards(); i++) {
                spawnGuard(scenario.getSpawnAreaGuards());
            }
        } else if (scenario.getGameMode() == 1) {
            for (int i = 0; i < scenario.getNumGuards(); i++) {
                spawnGuard(scenario.getSpawnAreaGuards());
            }
            for (int i = 0; i < scenario.getNumIntruders(); i++) {
                spawnGuard(scenario.getSpawnAreaIntruders());
            }
        }
        printMap();
        System.out.println();
    }

    public void spawnGuard(Area givenArea){
        int rand1 = (int) (Math.random() * (givenArea.getRightBoundary() - givenArea.getLeftBoundary())) + givenArea.getLeftBoundary();
        int rand2 = (int) (Math.random() * (givenArea.getBottomBoundary() - givenArea.getTopBoundary())) + givenArea.getTopBoundary();
        Agent tempAgent = new TestAgent(rand1, rand2);
        guards.add(tempAgent);
        tiles[rand1][rand2].addAgent(tempAgent); // TODO replace with Guard agent later
    }

    public void spawnIntruder(Area givenArea){
        int rand1 = (int) (Math.random() * (givenArea.getRightBoundary() - givenArea.getLeftBoundary())) + givenArea.getLeftBoundary();
        int rand2 = (int) (Math.random() * (givenArea.getBottomBoundary() - givenArea.getTopBoundary())) + givenArea.getTopBoundary();
        // tiles[rand1][rand2].addAgent(new Intruder(rand1, rand2));
    }
//    public void loadMap(Scenario scenario){
//
//        initializeEmptyMap();
//        ArrayList<Area> walls = scenario.getWalls();
//        ArrayList<TelePortal> teleportals = scenario.getTeleportals();
//        for (Area wall : walls) {
////            System.out.println("aaa");
//            loadWall(wall);
//        }
//        for(TelePortal telePortal : teleportals){
//            loadTeleportal(telePortal);
//        }
//    }
//
    private void initializeEmptyMap() {
        for (int i = 0; i < tiles[0].length; i++) {
            for (int j = 0; j < tiles.length; j++) {
                setTile(new Floor(i,j));
            }
        }
    }
//
//
//    public void loadWall(Area wall){
//        for (int i = 0; i < tiles[0].length; i++) {
//            for (int j = 0; j < tiles.length; j++) {
//                //needed as top left = 0,0
//                int oppIndex = tiles.length - i - 1;
//                fallsWithinWall(wall, i, j, oppIndex);
//            }
//        }
//    }
//
//    private void fallsWithinWall(Area wall, int i, int j, int oppIndex) {
//        System.out.println(i + ", " + j);
//        if (oppIndex >= wall.getLeftBoundary() && oppIndex <= wall.getRightBoundary() && j <= wall.getTopBoundary() && j >= wall.getBottomBoundary()) {
//            setTile(new Wall(i,j));
//        }
//    }
//
//    public void loadTeleportal(TelePortal teleportal){
//        for (int i = 0; i < tiles[0].length; i++) {
//            for (int j = 0; j < tiles.length; j++) {
//                if(teleportal.containP(getTile(i,j).getX(), getTile(i,j).getY())){
//                    setTile(new TeleportalTile(i, j, teleportal));
//                }
//            }
//        }
//    }



    public void printMap(){
        System.out.println(Arrays.deepToString(tiles).replace("], ", "]\n"));
    }

    public ArrayList<Tile> computeVisibleTiles(Agent agent) {
        int d = (int)Scenario.config.getVISION();
        double angle = agent.getAngle();
        int agentX = agent.getAgentPosition().getX();
        int agentY = agent.getAgentPosition().getY();
        ArrayList<Tile> visibleTiles = new ArrayList<>();
        if(angle==0){
            int topLimit = Math.max(0, agentY-d);
            int leftLimit = Math.max(0,agentX-1);
            int rightLimit = Math.min(tiles.length-1, agentX+1);
            for(int i=leftLimit; i<=rightLimit; i++){
                for(int j=agentY; j<=topLimit; j++){
                    visibleTiles.add(getTile(i,j));
                }
            }
        }
        return visibleTiles;
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
}
