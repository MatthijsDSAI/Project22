package controller.Map.tiles;

public class Shaded extends Tile {
    public Shaded(int x, int y) {
        this.setWalkable(true);
        this.setSeeThrough(false);
        this.setExploredByDefault(false);
        // this.setC(Color.WHITE);
        this.setX(x);
        this.setY(y);
    }

    @Override
    public String toString() {
        return "Shaded";
    }
}
