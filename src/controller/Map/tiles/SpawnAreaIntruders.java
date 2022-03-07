package controller.Map.tiles;

import controller.Map.tiles.Tile;
import javafx.scene.paint.Color;

public class SpawnAreaIntruders extends Tile {
    public SpawnAreaIntruders(int x, int y) {
        this.setWalkable(true);
        this.setSeeThrough(true);
        this.setExploredByDefault(true);
        this.setColor(Color.RED);
        this.setX(x);
        this.setY(y);
    }

    @Override
    public String toString() {
        return "SpawnAreaIntruders";
    }

    public SpawnAreaIntruders clone(){
        return new SpawnAreaIntruders(this.getX(), this.getY());
    }
}