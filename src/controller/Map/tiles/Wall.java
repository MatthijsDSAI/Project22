package controller.Map.tiles;

import java.awt.*;

public class Wall extends TileType{
    public Wall(){
        walkable = false;
        seeThrough = false;
        exploredByDefault = true;
        c = Color.BLACK;
    }

    @Override
    public boolean isWalkable() {
        return walkable;
    }

    @Override
    public boolean isSeeThrough() {
        return seeThrough;
    }

    @Override
    public boolean isExploredByDefault() {
        return exploredByDefault;
    }

    public String toString(){
        return "wall";
    }
}
