package controller.Map.tiles;

public class TargetArea extends TileType{
    public TargetArea() {
        walkable = true;
        seeThrough = true;
        exploredByDefault = false;
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

    @Override
    public String toString() {
        return "targetArea";
    }
}
