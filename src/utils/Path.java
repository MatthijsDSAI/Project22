package utils;

import controller.Map.tiles.Tile;

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
        System.out.println("Cur cost: " + cost);
        return super.add(tile);
    }

    public int updateDist(Queue<Tile> frontiers) {
        if(this.size() > 0) {
            int smallestDist = Integer.MAX_VALUE;
            for(Tile tile : frontiers) {
                int curDist = this.getLast().manhattanDist(tile);
                if(curDist < smallestDist) smallestDist = curDist;
            }
            this.dist = smallestDist;
            return cost + smallestDist;
        }
        return Integer.MAX_VALUE;
    }
}
