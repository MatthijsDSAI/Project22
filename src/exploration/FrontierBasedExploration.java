package exploration;
import controller.Map.Map;
import controller.Map.tiles.Tile;

public class FrontierBasedExploration {
    //no prior information about the map
    Tile[][] map;
    int x, y, angle;

    //Constructor -> tells which is the position of the robot and the angle
    public FrontierBasedExploration(int x, int y, int angle, int horizontalSize, int verticalSize) {
        this.x=x;
        this.y=y;
        this.angle=angle;
        map = new Tile[horizontalSize][verticalSize];
        explore();
    }

    public void explore(){
        map[x][y]=null;
        //check vision field
        //update map
        //decide which fronteier to explore
    }

    public void setLocation(int x, int y) {
        this.x=x;
        this.y=y;
    }

    public void setX(int x) {
        this.x=x;
    }

    public void setY(int y) {
        this.y=y;
    }

    public void setAngle(int angle){
        this.angle=angle;
    }

    public int getAngle(){
        return angle;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int[][] getMap(){
        return Map;
    }
}
