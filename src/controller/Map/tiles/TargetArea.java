package controller.Map.tiles;

import javafx.scene.paint.Color;

public class TargetArea extends Tile {
    public TargetArea(int x, int y) {
        this.setWalkable(true);
        this.setSeeThrough(true);
        this.setExploredByDefault(false);
        this.setColor(Color.LIMEGREEN);
        this.setX(x);
        this.setY(y);
    }

    @Override
    public String toString() {
        return "TargetArea";
    }

    public TargetArea clone(){
        return new TargetArea(this.getX(), this.getY());
    }
}
