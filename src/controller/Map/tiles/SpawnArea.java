package controller.Map.tiles;

import javafx.scene.paint.Color;

public class SpawnArea extends TileType{
    public SpawnArea(int x, int y) {
        this.setWalkable(true);
        this.setSeeThrough(true);
        this.setExploredByDefault(true);
        // this.setC(Color.WHITE);
        this.setX(x);
        this.setY(y);
    }

    @Override
    public String toString() {
        return "SpawnArea";
    }
}
