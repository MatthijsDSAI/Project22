package controller.Map.tiles;


import javafx.scene.paint.Color;

public class Shaded extends Tile {
    public Shaded(int x, int y) {
        this.setWalkable(true);
        this.setSeeThrough(false);
        this.setExploredByDefault(false);
        this.setColor(Color.GRAY);
        this.setX(x);
        this.setY(y);
    }

    @Override
    public String toString() {
        return "Shaded";
    }

    public Shaded clone(){
        return new Shaded(this.getX(), this.getY());
    }
}
