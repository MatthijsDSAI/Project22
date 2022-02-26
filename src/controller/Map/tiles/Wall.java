package controller.Map.tiles;

import java.awt.*;

public class Wall extends TileType{
    public Wall(int x, int y){
        this.setWalkable(false);
        this.setSeeThrough(false);
        this.setExploredByDefault(true);
        this.setC(Color.BLACK);
        this.setX(x);
        this.setY(y);
    }

    @Override
    public String toString(){
        return "Wall";
    }
}
