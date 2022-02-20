package controller.Map;

import agents.HumanPlayer;
import agents.Player;
import controller.Area;
import controller.GameRunner;
import controller.Map.tiles.Floor;
import controller.Map.tiles.Tile;
import controller.Map.tiles.Wall;
import controller.Scenario;
import utils.DirectionEnum;
import controller.TelePortal;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;

public class Map {
    private Tile[][] map;
    private String[][] test;
    private Player player;
    private Point2D playerPosition;
    public Map(int horizontalSize, int verticalSize, Player player){
        this.player = player;
        this.playerPosition = new Point2D.Double(0,0);
        map = new Tile[horizontalSize][verticalSize];
        test = new String[horizontalSize][verticalSize];
    }


    public Map(int row, int col){
        map = new Tile[row][col];
    }

    public void loadMap(Scenario scenario){

        initializeEmptyMap();
        ArrayList<Area> walls = scenario.getWalls();
        for (Area wall : walls) {
//            System.out.println("aaa");
            loadWall(wall);
        }
        printLayout();
    }

    private void initializeEmptyMap() {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                map[i][j] = new Tile(new Floor(), j * 10, i * 10);
            }
        }
    }


    public void loadWall(Area wall){
        for (int i = 0; i < map.length; i++) {

            for (int j = 0; j < map[0].length; j++) {
                //needed as top left = 0,0
                int oppIndex = map.length - i - 1;
                fallsWithinWall(wall, i, j, oppIndex);
                test[i][j] = "i: " + oppIndex + " j: " + j;

            }
        }
    }

//    public void loadTeleportal(Area teleportal){
//        for (int i = 0; i < map.length; i++) {
//            for (int j = 0; j < map[0].length; j++) {
//                if(teleportal.containP(map[i][j].getX(), map[i][j].getY())){
//                    map[i][j].setType(new Wall());
//                }
//            }
//        }
//    }

    private void fallsWithinWall(Area wall, int i, int j, int oppIndex) {
        if (j >= wall.getLeftBoundary() && j <= wall.getRightBoundary() && oppIndex <= wall.getTopBoundary() && oppIndex >= wall.getBottomBoundary()) {
            //could do with a factory here
            map[oppIndex][j] = new Tile(new Wall());
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
        System.out.println(Arrays.deepToString(map).replace("], ", "]\n"));
    }
    public void printLayout(){
        System.out.println(Arrays.deepToString(test).replace("], ", "]\n"));
    }

    public void addPlayer(Player player, int x, int y) {
        map[y][x].addPlayer(player);
    }

    public void movePlayerFromTo(Player player, int xFrom, int yFrom, int xTo, int yTo){
        Tile tile = map[yTo][xTo];
        if(tile.isWalkable()) {
            map[yFrom][xFrom].removePlayer();
            map[yTo][xTo].addPlayer(player);
            setPlayerPosition(new Point2D.Double(xTo, yTo));
        }
        else{
            throw new RuntimeException("Can not move to tile " + xTo + ", " + yTo);
        }
    }

    public void movePlayer(Player player, String direction){
        Point2D playerPosition = getPlayerPosition(player);
        Point2D toTile = getTileFromDirection(playerPosition, direction);
        int xFrom = (int) playerPosition.getX();
        int yFrom = (int) playerPosition.getY();
        int xTo = (int) toTile.getX();
        int yTo = (int) toTile.getY();
        Tile tile = map[yTo][xTo];
        if(tile.isWalkable()) {
            map[yFrom][xFrom].removePlayer();
            map[yTo][xTo].addPlayer(player);
            setPlayerPosition(toTile);
        }
        else{
            throw new RuntimeException("Can not move to tile " + xTo + ", " + yTo);
        }
    }

    private Point2D getTileFromDirection(Point2D playerPosition, String direction) {
        int x = (int) playerPosition.getX();
        int y = (int) playerPosition.getY();
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
        for(Tile[] tiles : map){
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
        for(Tile[] tiles : map){
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

    public Tile[][] getMap() {
        return map;
    }

    public Point2D getPlayerPosition(Player player){
        return playerPosition;
    }

    private void setPlayerPosition(Point2D playerPosition) {
        this.playerPosition = playerPosition;
    }
}
