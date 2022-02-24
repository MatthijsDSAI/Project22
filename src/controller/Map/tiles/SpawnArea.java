package controller.Map.tiles;

public class SpawnArea extends TileType{
    public SpawnArea() {
        walkable = true;
        seeThrough = true;
        exploredByDefault = true;
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
        return "spawnArea";
    }
}
