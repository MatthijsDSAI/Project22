package controller.Map.tiles;

import java.awt.*;

public class TeleportalTile extends TileType {

        //maybe store target coordinates in here
        //and angle
        public TeleportalTile() {
            this.setWalkable(true);
            this.setSeeThrough(true);
            this.setExploredByDefault(false);
            this.setC(Color.MAGENTA);
        }

        public TeleportalTile(int x, int y){
            this.setWalkable(true);
            this.setSeeThrough(true);
            this.setExploredByDefault(false);
            this.setC(Color.MAGENTA);
            this.setX(x);
            this.setY(y);
        }

        @Override
        public String toString(){
            return "TelePortal";
        }


}
