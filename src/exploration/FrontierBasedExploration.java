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
    Guard guard;
    private ArrayList<Tile> visibleTiles;
    private AdjacencyList adjacencyList;
    private LinkedList<Tile> exploredTiles;
    private Queue<Tile> frontierQueue;
    private Queue<Tile> BFSQueue;
    private Tile nextTile;

    //Constructor -> tells which is the position of the robot and the angle
    public FrontierBasedExploration(Guard guard, Tile[][] map) {
        this.guard = guard;
        this.map = map;
        visibleTiles = guard.getVisibleTiles();
        adjacencyList = new AdjacencyList(map, visibleTiles);
        frontierQueue = new LinkedList<>();
        BFSQueue = new LinkedList<>();
        exploredTiles = new LinkedList<>();
    }

    public void updateAdjacencyList() {
        visibleTiles = guard.getVisibleTiles();
        adjacencyList.addNodes(visibleTiles);
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

    public DirectionEnum step(Guard guardPassed) {
        visibleTiles = guard.getVisibleTiles();
        adjacencyList.addNodes(visibleTiles);
        frontierQueue = findFrontiers();
        DirectionEnum dir = findNextMoveDirection();
        return dir;
    }

    public void moveAgent(Guard guardPassed) {
        this.guard.setAgentPosition(guardPassed.getAgentPosition());
        System.out.println("Guard pos updated to (" + guardPassed.getX_position() + ", " + guardPassed.getY_position() + ")");
    }

    public Tile getNextTile() {
        return nextTile;
    }

    public Queue<Tile> findFrontiers() {
        Tile curTile = guard.getAgentPosition();
        int curTileIndex = adjacencyList.getTileIndex(curTile);
        //System.out.println("Starting frontier search, looking at tile " + curTileIndex);
        BFSQueue = new LinkedList<>();
        //System.out.println("Adding tile " + curTileIndex + " to BFSQueue.");
        BFSQueue.add(curTile);
        while(!BFSQueue.isEmpty()) {
            curTile = BFSQueue.remove();
            exploredTiles.add(curTile);
            curTileIndex = adjacencyList.getTileIndex(curTile);
            //System.out.println("Removing " + curTileIndex + " from BFSQueue and looping through adjacent tiles.");
            LinkedList<Tile> curAdjacencyList = adjacencyList.get(curTile);
            //System.out.print("Tiles adjacent to tile " + curTileIndex + " ");
            for(Tile tile : curAdjacencyList) {
                int tileIndex = adjacencyList.getTileIndex(tile);
                //System.out.println("Looking at adjacent tile " + tileIndex);
                if(BFSQueue.contains(tile) || exploredTiles.contains(tile)) {
                    //System.out.println("Tile " + tileIndex + " skipped because it was already explored.");
                    continue;
                }
                //System.out.println("Adding adjacent tile " + tileIndex + " toBFSQueue.");
                BFSQueue.add(tile);
                if(tile.isWalkable() && isFrontier(adjacencyList.get(tile)) && !frontierQueue.contains(tile)) {
                //System.out.println("Adding adjacent tile " + tileIndex + " to frontierQueue.");
                    frontierQueue.add(tile);
                }
            }
        }
        printQueue(frontierQueue);
        return frontierQueue;
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

    public DirectionEnum findNextMoveDirection() {
        nextTile = this.frontierQueue.remove();
        int curX = this.guard.getX_position();
        int curY = this.guard.getY_position();
        int goalX = nextTile.getX();
        int goalY = nextTile.getY();
        System.out.println("Current x: " + curX);
        System.out.println("Current y: " + curX);
        System.out.println("Goal x: " + goalX);
        System.out.println("Goal y: " + goalY);
        System.out.print("Direction for that tile is: ");

        if(curX < goalX) {
            System.out.println("East");
            return DirectionEnum.EAST;
        }
        else if(curX > goalX) {
            System.out.println("West");
            return DirectionEnum.WEST;
        }
        else if (curY < goalY) {
            System.out.println("North");
            return DirectionEnum.NORTH;
        }
        else if (curY > goalY) {
            System.out.println("South");
            return DirectionEnum.SOUTH;
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
