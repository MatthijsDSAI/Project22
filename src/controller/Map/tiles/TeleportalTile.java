package controller.Map.tiles;

import controller.TelePortal;
import javafx.scene.paint.Color;

public class TeleportalTile extends Tile {

    private final TelePortal tp;

    //maybe store target coordinates in here
        //and angle
        public TeleportalTile(TelePortal tp) {
            this.tp = tp;
            this.setWalkable(true);
            this.setSeeThrough(true);
            this.setExploredByDefault(false);
            this.setC(Color.MAGENTA);
        }

        public TeleportalTile(int x, int y, TelePortal tp){
            this.tp = tp;
            this.setWalkable(true);
            this.setSeeThrough(true);
            this.setExploredByDefault(false);
            this.setC(Color.MAGENTA);
            this.setX(x);
            this.setY(y);
        }

    public TeleportalTile(int x, int y, int xTarget, int yTarget, double outOrientation) {

    }

    @Override
        public String toString(){
            return "TelePortal";
        }

        public int[] teleport(){
            if(hasAgent()){
                return tp.getNewLocation();
            }
            throw new RuntimeException("There is no agent to teleport on tile: " + getX() + ", " + getY());
        }
}
