package controller.Map.tiles;

import javafx.scene.paint.Color;

public class Floor extends Tile {

    public Floor(int x, int y){
        this.setWalkable(true);
        this.setSeeThrough(true);
        this.setExploredByDefault(false);
        this.setExploredByDefaultForIntruders(false);
        this.setColor(Color.LIGHTGOLDENRODYELLOW);
        this.setX(x);
        this.setY(y);
    }

    @Override
    public String toString(){
        return "Floor";
    }

    public Floor clone(){
        return new Floor(this.getX(), this.getY());
    }

}
