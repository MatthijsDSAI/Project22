package controller.Map.tiles;

import java.awt.*;

public class Shaded extends TileType{
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
