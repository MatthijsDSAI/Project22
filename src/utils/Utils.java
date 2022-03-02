package utils;

import controller.Map.tiles.Tile;

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
}
