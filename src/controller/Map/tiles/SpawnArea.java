package controller.Map.tiles;

public class SpawnArea extends Tile {
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
