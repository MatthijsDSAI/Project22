package exploration;
import agents.Guard;
import controller.Map.Map;
import controller.Map.tiles.Tile;
import utils.AdjacencyList;
import utils.DirectionEnum;
import utils.Position;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class FrontierBasedExploration {
    //no prior information about the map
    Tile[][] map;
    private ArrayList<Tile> visibleTiles;
    private AdjacencyList adjacencyList;
    private LinkedList<Tile> exploredTiles;
    private Queue<Tile> frontierQueue;
    private Queue<Tile> BFSQueue;
    private Tile nextTile;

    //Constructor -> tells which is the position of the robot and the angle
    public FrontierBasedExploration(Guard guard, Tile[][] map) {
        this.map = map;
        visibleTiles = guard.getVisibleTiles();
        adjacencyList = new AdjacencyList(map, visibleTiles);
        frontierQueue = new LinkedList<>();
        BFSQueue = new LinkedList<>();
        exploredTiles = new LinkedList<>();
    }

    private void printTiles(ArrayList<Tile> tiles) {
        String result = "";
        for(Tile tile : tiles) {
            result += adjacencyList.getTileIndex(tile) + ", ";
        }
        System.out.println(result);
    }

    private void printTilesLL(LinkedList<Tile> tiles) {
        String result = "";
        for(Tile tile : tiles) {
            result += adjacencyList.getTileIndex(tile) + ", ";
        }
        System.out.println(result);
    }

    public DirectionEnum step(Guard guard) {
        Tile curTile = guard.getAgentPosition();
        int curX = curTile.getX();
        int curY = curTile.getY();
        visibleTiles = guard.getVisibleTiles();
        updateExploredTiles(visibleTiles);
        adjacencyList.addNodes(visibleTiles);
        updateFrontiers(visibleTiles);
        Tile tile = findFrontiers(guard);
        DirectionEnum dir = findNextMoveDirection(guard, tile);
        return dir;
    }

    private void updateExploredTiles(ArrayList<Tile> visibleTiles) {
        for(Tile tile : visibleTiles) {
            if(!exploredTiles.contains(tile) && adjacencyList.get(tile).size() == 4) {
                exploredTiles.add(tile);
            }
        }
    }

    public Tile getNextTile() {
        return nextTile;
    }

    public Queue<Tile> updateFrontiers(ArrayList<Tile> visibleTiles) {
        for(Tile tile : visibleTiles) {
            if(adjacencyList.get(tile).size() == 4 && frontierQueue.contains(tile)) {
                frontierQueue.remove(tile);
            }
        }
        return frontierQueue;
    }

    public Tile findFrontiers(Guard guard) {
        Tile curTile = guard.getAgentPosition();
        System.out.println(curTile.getX() + ", " + curTile.getY() + " --- " + adjacencyList.getTileIndex(curTile));
        //System.out.println("Starting frontier search, looking at tile " + curTileIndex);
        BFSQueue = new LinkedList<>();
        //System.out.println("Adding tile " + curTileIndex + " to BFSQueue.");
        BFSQueue.add(curTile);
        LinkedList<Tile> tilesSeen = new LinkedList<>();
        while(!BFSQueue.isEmpty()) {
            curTile = BFSQueue.remove();
            tilesSeen.add(curTile);
            //System.out.println("Removing " + curTileIndex + " from BFSQueue and looping through adjacent tiles.");
            LinkedList<Tile> curAdjacencyList = adjacencyList.get(curTile);
            //System.out.print("Tiles adjacent to tile " + curTileIndex + " ");
            for(Tile tile : curAdjacencyList) {
                //System.out.println("Looking at adjacent tile " + tileIndex);
                if(BFSQueue.contains(tile) || tilesSeen.contains(tile)) {
                    //System.out.println("Tile " + tileIndex + " skipped because it was already explored.");
                    continue;
                }
                //System.out.println("Adding adjacent tile " + tileIndex + " toBFSQueue.");
                BFSQueue.add(tile);
                int tileIndex = adjacencyList.getTileIndex(tile);
                if(tile.isWalkable() && isFrontier(adjacencyList.get(tile)) && !frontierQueue.contains(tile) && !exploredTiles.contains(tile)) {
                System.out.println("Adding adjacent tile " + tileIndex + " to frontierQueue.");
                    frontierQueue.add(tile);
                }
            }
        }

        curTile = guard.getAgentPosition();
        BFSQueue = new LinkedList<>();
        BFSQueue.add(curTile);
        tilesSeen = new LinkedList<>();
        while(!BFSQueue.isEmpty()) {
            curTile = BFSQueue.remove();
            tilesSeen.add(curTile);
            int curTileIndex = adjacencyList.getTileIndex(curTile);
            System.out.println("Removing " + curTileIndex + " from BFSQueue and looping through adjacent tiles.");
            LinkedList<Tile> curAdjacencyList = adjacencyList.get(curTile);
            System.out.print("Tiles adjacent to tile " + curTileIndex + " ");
            for(Tile tile : curAdjacencyList) {
                int tileIndex = adjacencyList.getTileIndex(tile);
                System.out.println("Looking at adjacent tile " + tileIndex);
                if(BFSQueue.contains(tile) || tilesSeen.contains(tile)) {
                    System.out.println("Tile " + tileIndex + " skipped because it was already explored.");
                    continue;
                }
                System.out.println("Adding adjacent tile " + tileIndex + " toBFSQueue.");
                BFSQueue.add(tile);
                if(tile.isWalkable() && isFrontier(adjacencyList.get(tile)) && frontierQueue.contains(tile)) {
                    System.out.println("Frontier detection returning tile: " + adjacencyList.getTileIndex(tile));
                    //exploredTiles.add(tile);
                    //frontierQueue.remove(tile);
                    return tile;
                }
            }
        }
        printQueue(frontierQueue);
        return null;
    }

    /*public Tile findNextMoveTile(Queue<Tile> frontierQueue) {
        Tile nextTile = frontierQueue.remove();
        System.out.println("Want to move to tile " + adjacencyList.getTileIndex(nextTile));
        int curX = this.guard.getX_position();
        int curY = this.guard.getY_position();
        int goalX = nextTile.getX();
        int goalY = nextTile.getY();

        System.out.println("Current x: " + curX);
        System.out.println("Current y: " + curX);
        System.out.println("Goal x: " + goalX);
        System.out.println("Goal y: " + goalY);

        if(curX < goalX) {
            System.out.println("Return tile at (" + (curX+1) + ", " + curY + ")");
            return map[curX+1][curY];
        }
        else if(curX > goalX) {
            System.out.println("Return tile at (" + (curX-1) + ", " + curY + ")");
            return map[curX-1][curY];
        }
        else if (curY < goalY) {
            System.out.println("Return tile at (" + curX + ", " + (curY+1) + ")");
            return map[curX][curY+1];
        }
        else if (curY > goalY) {
            System.out.println("Return tile at (" + curX + ", " + (curY-1) + ")");
            return map[curX][curY-1];
        }
        return null;
    }*/

    public DirectionEnum findNextMoveDirection(Guard guard, Tile nextTile) {
        Tile curTile = guard.getAgentPosition();
        int curX = curTile.getX();
        int curY = curTile.getY();
        int goalX = nextTile.getX();
        int goalY = nextTile.getY();
        System.out.print("Direction for that tile is: ");
        if(Math.abs((curX-goalX) + (curY-goalY)) == 1 ) {
            frontierQueue.remove();
        }

        if(curX < goalX) {
            System.out.println("East");
            return DirectionEnum.EAST;
        }
        else if(curX > goalX) {
            System.out.println("West");
            return DirectionEnum.WEST;
        }
        else if (curY < goalY) {
            System.out.println("South");
            return DirectionEnum.SOUTH;
        }
        else if (curY > goalY) {
            System.out.println("North");
            return DirectionEnum.NORTH;
        }
        return null;
    }

    private void printQueue(Queue<Tile> queue) {
        String result = "frontierQueue: ";
        for(Tile tile : queue) {
            result += adjacencyList.getTileIndex(tile) + ", ";
        }
        System.out.println(result);
    }

    private boolean isFrontier(LinkedList<Tile> adjacencyList) {
        if(adjacencyList.size() < 4) return true;
        return false;
    }

    public void explore(){
        //check vision field
        //update map
        //decide which frontier to explore
    }

    public Tile[][] getMap(){
        return map;
    }
}
