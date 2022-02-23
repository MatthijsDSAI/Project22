package controller.Map;

import agents.HumanPlayer;
import agents.Player;
import controller.Area;
import controller.Map.tiles.Floor;
import controller.Map.tiles.Tile;
import controller.Map.tiles.Wall;
import controller.Scenario;

import java.util.ArrayList;
import java.util.Arrays;

public class Map {
    private Tile[][] map;
    private String[][] test;
    public Map(int horizontalSize, int verticalSize){
        map = new Tile[horizontalSize][verticalSize];
        test = new String[horizontalSize][verticalSize];
    }



    public void loadMap(Scenario scenario){

        initializeEmptyMap();
        ArrayList<Area> walls = scenario.getWalls();
        for (Area wall : walls) {
            System.out.println("aaa");
            loadWall(wall);
        }
        printLayout();
    }

    private void initializeEmptyMap() {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                map[i][j] = new Tile(new Floor());

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

    private void fallsWithinWall(Area wall, int i, int j, int oppIndex) {
        if (j >= wall.getLeftBoundary() && j <= wall.getRightBoundary() && oppIndex <= wall.getTopBoundary() && oppIndex >= wall.getBottomBoundary()) {
            //could do with a factory here
            map[i][j] = new Tile(new Wall());
        }
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

    public void movePlayer(Player player, int xFrom, int yFrom, int xTo, int yTo){
        map[yFrom][xFrom].removePlayer();
        map[yTo][xTo].addPlayer(player);
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

}
