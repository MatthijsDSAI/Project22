package utils;

import agents.Agent;
import controller.Map.Map;
import controller.Map.tiles.Tile;
import controller.Scenario;

import java.util.ArrayList;

public class Utils {
    public static int TransFormIntoValidAngle(int angle){
        while(angle<0){
            angle+=360;
        }
        while(angle>=360){
            angle-=360;
        }
        return angle;
    }


    public static Tile[][] transpose(Tile[][] map) {
        Tile[][] newMap = new Tile[map[0].length][map.length];
        for(int i=0; i<map.length; i++){
            for(int j=0; j<map[0].length; j++){
                newMap[j][i] = map[i][j];
            }
        }
        return newMap;
    }

    public static DirectionEnum getRandomDirection(){
        return DirectionEnum.getDirection(getRandomNumber(0,4)*90);
    }

    public static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public static void sleep(long sleep) {
        try {
            Thread.sleep(sleep);
        }
        catch(InterruptedException e){
            System.out.println("Threading issue");
        }
    }

    public static double findAngleToTargetArea(int x1, int y1) {
        int x2 = Scenario.config.getCenterOfTargetArea()[0];
        int y2 = Scenario.config.getCenterOfTargetArea()[1];
        return findAngleFromTileToTile(x2, y2, x1, y1);
    }

    public static double findAngleFromTileToTile(int x1, int y1, int x2, int y2){
        double angle = Math.toDegrees(Math.atan2(y2-y1, x2-x1))+90;
        if(angle<0){
            angle+=360;
        }
        return 360-angle;
    }

    public static double findAngleFromStartingPosition(Agent agent){
        Tile tile1 = agent.getAgentPosition();
        Tile tile2 = agent.getStartingTile();
        int x1 = tile1.getX();
        int y1 = tile1.getY();
        int x2 = tile2.getX();
        int y2 = tile2.getY();
        return findAngleFromTileToTile(x1,y1,x2,y2);
    }
    public static double findAngleFromStartingPosition(Agent agent, Tile potentialTile){
        Tile tile1 = potentialTile;
        Tile tile2 = agent.getStartingTile();
        int x1 = tile1.getX();
        int y1 = tile1.getY();
        int x2 = tile2.getX();
        int y2 = tile2.getY();
        return findAngleFromTileToTile(x1,y1,x2,y2);
    }
    public static int gcd(int a, int b){
        return b == 0 ? a:gcd(b, a%b);
    }

    public static int LcmArray(int[] list, int n){
        if(n==list.length-1){
            return list[n];
        }
        return (list[n]*LcmArray(list, n+1)/gcd(list[n], LcmArray(list,n+1)));
    }

    public static DirectionEnum findClosestDirection(double angle){
        if((angle>=0 && angle<=45) || (angle<= 360 && angle>=315)){
            return DirectionEnum.NORTH;
        }
        if((angle>=90 && angle<=135) || (angle<= 90 && angle>=45)){
            return DirectionEnum.WEST;
        }
        if((angle>=180 && angle<=225) || (angle<= 180 && angle>=135)){
            return DirectionEnum.SOUTH;
        }
        if((angle>=270 && angle<=315) || (angle<= 270 && angle>=225)){
            return DirectionEnum.EAST;
        }
        return null;
    }

    public static double differenceBetweenAngles(double alpha, double beta){
        if(alpha<beta){
            double temp = alpha;
            alpha = beta;
            beta = temp;
        }
        if(Math.abs(alpha-beta)<180){
            return Math.abs(alpha-beta);
        }
        return beta + 360-alpha;
    }

    public static double distanceBetweenTiles(Tile tile1, Tile tile2){
        return Math.sqrt(Math.pow(tile1.getX()-tile2.getX(), 2) + Math.pow(tile1.getY()-tile2.getY(), 2));
    }

    public static ArrayList<Tile> getSurroundingTiles(Map map, Tile tile) {
        ArrayList<Tile> surroundingTiles = new ArrayList<>();
        surroundingTiles.add(map.getTile(tile.getX()-1, tile.getY()));
        surroundingTiles.add(map.getTile(tile.getX()-1, tile.getY()+1));
        surroundingTiles.add(map.getTile(tile.getX(), tile.getY()+1));
        surroundingTiles.add(map.getTile(tile.getX()+1, tile.getY()+1));
        surroundingTiles.add(map.getTile(tile.getX()+1, tile.getY()));
        surroundingTiles.add(map.getTile(tile.getX()+1, tile.getY()-1));
        surroundingTiles.add(map.getTile(tile.getX(), tile.getY()-1));
        surroundingTiles.add(map.getTile(tile.getX()-1, tile.getY()-1));
        return surroundingTiles;

    }
}
