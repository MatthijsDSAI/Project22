package controller.Map.tiles;

public class Wall extends TileType{

    public Wall(){
        setWalkable(true);
        setSeeThrough(true);

    }

    public String toString(){
        return "Wall";
    }
}
