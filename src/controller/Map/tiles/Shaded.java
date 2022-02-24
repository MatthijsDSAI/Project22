package controller.Map.tiles;

public class Shaded extends TileType{
    public Shaded() {
        walkable = true;
        seeThrough = false;
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
        return "shaded";
    }
}
