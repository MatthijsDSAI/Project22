package controller.Map.tiles;

import javafx.scene.paint.Color;

public class SpawnArea extends Tile {
    public SpawnArea(int x, int y) {
        this.setWalkable(true);
        this.setSeeThrough(true);
        this.setExploredByDefault(true);
        this.setColor(Color.BLANCHEDALMOND);
        this.setX(x);
        this.setY(y);
    }

    @Override
    public String toString() {
        return "SpawnArea";
    }
}
