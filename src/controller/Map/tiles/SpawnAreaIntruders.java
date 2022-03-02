package controller.Map.tiles;

import controller.Map.tiles.Tile;

public class SpawnAreaIntruders extends Tile {
    public SpawnAreaIntruders(int x, int y) {
        this.setWalkable(true);
        this.setSeeThrough(true);
        this.setExploredByDefault(true);
        // this.setC(Color.WHITE);
        this.setX(x);
        this.setY(y);
    }

    @Override
    public String toString() {
        return "SpawnAreaIntruders";
    }
}