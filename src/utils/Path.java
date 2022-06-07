package utils;

import controller.Map.tiles.Tile;
import controller.Scenario;

import java.util.LinkedList;
import java.util.Queue;

public class Path extends LinkedList<Tile>{

    public int dist;
    public int cost;

    public Path() {
        super();
        this.cost = 0;
    }

    public Path(Path other) {
        super(other);
    }

    @Override
    public boolean add(Tile tile) {
        cost++;
        if(Scenario.config.DEBUG)
            System.out.println("Cur cost: " + cost);
        return super.add(tile);
    }

    public int updateDist(Queue<Tile> frontiers) {
        if(this.size() > 0) {
            int smallestDist = Integer.MAX_VALUE;
            for(Tile tile : frontiers) {
                int curDist = cost + this.getLast().manhattanDist(tile);
                if(curDist < smallestDist) smallestDist = curDist;
            }
            this.dist = smallestDist;
            return smallestDist;
        }
        return Integer.MAX_VALUE;
    }

    @Override
    public String toString() {
        String result = "";
        for(Tile tile : this) {
            result += tile.getCoordinates() + ", ";
        }
        return result;
    }
}
