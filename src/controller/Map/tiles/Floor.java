package controller.Map.tiles;

import java.awt.*;

public class Floor extends TileType {


    public Floor(){
        walkable = true;
        seeThrough = true;
        exploredByDefault = false;
        c = Color.white;
    }

    @Override
    public boolean isWalkable() {
        return walkable;
    }

    @Override
    public boolean isSeeThrough() {
        return seeThrough;
    }



    public String toString(){
        return "floor";
    }

    @Override
    public boolean isExploredByDefault() {
        return exploredByDefault;
    }
}
