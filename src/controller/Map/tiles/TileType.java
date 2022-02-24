package controller.Map.tiles;

import java.awt.*;

public abstract class TileType {
    protected boolean exploredByDefault;
    protected boolean walkable;
    protected boolean seeThrough;
    protected Color c;
    public TileType(){
        
    }

    public abstract boolean isWalkable();

    public abstract boolean isSeeThrough();

    public abstract boolean isExploredByDefault();
}
