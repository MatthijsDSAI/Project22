package controller.Map.tiles;

import controller.TelePortal;
import javafx.scene.paint.Color;

public class TeleportalTile extends Tile {

    protected int targetX;
    protected int targetY;
    protected double angle;

    public TeleportalTile(int x, int y, int targetX, int targetY, double angle){
        this.setWalkable(true);
        this.setSeeThrough(true);
        this.setExploredByDefault(false);
        this.setColor(Color.MAGENTA);
        this.setX(x);
        this.setY(y);
        this.targetX = targetX;
        this.targetY = targetY;
        this.angle = angle;
    }

    public int getTargetX() {return targetX;}

    public int getTargetY() {return targetY;}

    public double getAngle() {return angle;}

    @Override
    public String toString(){
        return "TelePortal";
    }

//        public int[] teleport(){
//            if(hasAgent()){
//                return tp.getNewLocation();
//            }
//            throw new RuntimeException("There is no agent to teleport on tile: " + getX() + ", " + getY());
//        }
    public TeleportalTile clone(){
        return new TeleportalTile(this.getX(), this.getY(), this.getTargetX(), this.getTargetY(), this.getAngle());
    }
}
