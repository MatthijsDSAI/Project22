package controller.Map.tiles;

import javafx.scene.paint.Color;

public class Wall extends Tile {
    public Wall(int x, int y){
        this.setWalkable(false);
        this.setSeeThrough(false);
        this.setExploredByDefault(true);
        this.setColor(Color.BLACK);
        this.setX(x);
        this.setY(y);
    }

    @Override
    public String toString(){
        return "Wall";
    }

    public Wall clone(){
        return new Wall(this.getX(), this.getY());
    }
}
