package utils;

import controller.Map.tiles.Tile;

import java.util.LinkedList;
import java.util.Queue;

public class Path extends LinkedList<Tile>{

    public int dist;
    public Tile src;

    public Path() {
        super();
    }

    public Path(Path other) {
        super(other);
    }

    public int updateDist(Queue<Tile> frontiers) {
        if(this.size() > 0) {
            int smallestDist = Integer.MAX_VALUE;
            for(Tile tile : frontiers) {
                int curDist = this.getLast().manhattanDist(tile);
                if(curDist < smallestDist) smallestDist = curDist;
            }
            this.dist = smallestDist;
            return smallestDist;
        }
        return Integer.MAX_VALUE;
    }
}
