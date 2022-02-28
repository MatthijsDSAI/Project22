package controller.Map.tiles;

import javafx.scene.paint.Color;

public class TargetArea extends TileType{
    public TargetArea(int x, int y) {
        this.setWalkable(true);
        this.setSeeThrough(true);
        this.setExploredByDefault(false);
        // this.setC(Color.WHITE);
        this.setX(x);
        this.setY(y);
    }

    @Override
    public String toString() {
        return "TargetArea";
    }
}
