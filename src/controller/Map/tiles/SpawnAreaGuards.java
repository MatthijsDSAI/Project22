package controller.Map.tiles;

import javafx.scene.paint.Color;

public class SpawnAreaGuards extends Tile {
    public SpawnAreaGuards(int x, int y) {
        this.setWalkable(true);
        this.setSeeThrough(true);
        this.setExploredByDefault(false);
        this.setColor(Color.MIDNIGHTBLUE);
        this.setX(x);
        this.setY(y);
    }

    @Override
    public String toString() {
        return "SpawnAreaGuards";
    }

    public SpawnAreaGuards clone(){
        return new SpawnAreaGuards(this.getX(), this.getY());
    }
}
