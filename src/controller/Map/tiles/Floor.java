package controller.Map.tiles;

public class Floor extends TileType {

    private int speed = 5;
    public Floor(){
        setWalkable(true);
        setSeeThrough(true);
        setExplored(false);
        setSpeed(speed);
    }

    public String toString(){
        return "Floor";
    }

}
