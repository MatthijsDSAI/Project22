package controller.Map.tiles;

public class TeleportalTile extends TileType{
    //maybe store target coordinates in here
    //and angle
    public TeleportalTile(){
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

    public String toString(){
        return "teleportal";
    }


}
