package controller.Map.tiles;

public class Floor extends TileType {

    private int speedPercentage = 5;
    public Floor(){
        setWalkable(true);
        setSeeThrough(true);
        setExplored(false);
        setSpeedPercentage(speedPercentage);
    }

    public String toString(){
        return "Floor";
    }

}
