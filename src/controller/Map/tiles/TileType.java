package controller.Map.tiles;

public abstract class TileType {
    protected boolean exploredByDefault;
    protected boolean walkable;
    protected boolean seeThrough;
    public TileType(){
        
    }

    public abstract boolean isWalkable();

    public abstract boolean isSeeThrough();

    public abstract boolean isExploredByDefault();
}
