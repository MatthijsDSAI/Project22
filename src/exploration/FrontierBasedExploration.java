package exploration;
import agents.Guard;
import controller.Map.Map;
import controller.Map.tiles.Tile;
import controller.Scenario;
import utils.AdjacencyList;
import utils.DirectionEnum;
import utils.Path;
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
    private boolean DEBUG = Scenario.config.DEBUG;

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

    private String tilesLLtoString(LinkedList<Tile> tiles) {
        String result = "";
        for(Tile tile : tiles) {
            result += adjacencyList.getTileIndex(tile) + ", ";
        }
        return result;
    }

    public DirectionEnum step(Guard guard) {
        Tile curTile = guard.getAgentPosition();
        int curX = curTile.getX();
        int curY = curTile.getY();
        visibleTiles = guard.getVisibleTiles();
        updateExploredTiles(visibleTiles);
        adjacencyList.addNodes(visibleTiles);
        updateFrontiers(visibleTiles);
        Tile tile = findFrontiers(guard).get(1);
        DirectionEnum dir = findNextMoveDirection(guard, tile);
        return dir;
    }

    public void updateExploredTiles(ArrayList<Tile> visibleTiles) {
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

    public Path findFrontiers(Guard guard) {
        if(DEBUG)
            System.out.println("!Searching for frontier!");
        Tile curTile = guard.getAgentPosition();
        if(DEBUG)
            System.out.println("Guard pos at start of Frontier: " + curTile.getX() + ", " + curTile.getY() + " --- " + adjacencyList.getTileIndex(curTile));


        BFSQueue = new LinkedList<>();

        BFSQueue.add(curTile);
        LinkedList<Tile> tilesSeen = new LinkedList<>();
        while(!BFSQueue.isEmpty()) {
            curTile = BFSQueue.remove();
            tilesSeen.add(curTile);
            LinkedList<Tile> curAdjacencyList = adjacencyList.get(curTile);
            for(Tile tile : curAdjacencyList) {
                if(BFSQueue.contains(tile) || tilesSeen.contains(tile)) {
                    continue;
                }
                BFSQueue.add(tile);
                int tileIndex = adjacencyList.getTileIndex(tile);
                if(tile.isWalkable() && isFrontier(adjacencyList.get(tile)) && !frontierQueue.contains(tile) && !exploredTiles.contains(tile)) {
                    frontierQueue.add(tile);
                }
            }
        }
        if(DEBUG)
            System.out.println("Starting second frontier search");
        printQueue(frontierQueue);

        curTile = guard.getAgentPosition();
        LinkedList<Path> queue = new LinkedList<>();
        LinkedList<Integer> distances = new LinkedList<>();
        Path path = new Path();
        path.add(curTile);
        queue.add(path);
        tilesSeen = new LinkedList<>();
        while(!queue.isEmpty()) {
            path = queue.remove(findShortestPath(queue, frontierQueue));
            //System.out.println("Current path: " + tilesLLtoString(path));
            Tile lastTile = path.getLast();
            tilesSeen.add(lastTile);
            //System.out.println("Looking at tile: " + lastTile.getX() + ", " + lastTile.getY() + "--- tile: " + adjacencyList.getTileIndex(lastTile));
            if (lastTile.isWalkable() && isFrontier(adjacencyList.get(lastTile)) && frontierQueue.contains(lastTile)) {
                //System.out.println("Cur guard pos: " + guard.getAgentPosition().getX() + ", " + guard.getAgentPosition().getY() + " --- tile: " + adjacencyList.getTileIndex(lastTile));
                //System.out.println("Tile found pos: " + path.get(1).getX() + ", " + path.get(1).getY() + "--- tile: " + adjacencyList.getTileIndex(path.get(1)));
                //System.out.println("Found path: " + tilesLLtoString(path));
                return path;
            }

            LinkedList<Tile> curAdjacencyList = adjacencyList.get(lastTile);
            for (Tile tile : curAdjacencyList) {
                if(path.contains(tile) || tilesSeen.contains(tile)) {
                    continue;
                }
                if (!path.contains(tile) && tile.isWalkable()) {
                    Path newPath = new Path(path);
                    newPath.add(tile);
                    queue.offer(newPath);
                }
            }
        }
        return null;
    }

    private int findShortestPath(LinkedList<Path> paths, Queue<Tile> frontiers) {
        int shortestDist = Integer.MAX_VALUE;
        int bestPathIndex = -1;
        for(int i = 0; i < paths.size(); i++) {
            Path path = paths.get(i);
            int curDist = path.updateDist(frontiers);
            if(curDist < shortestDist) {
                shortestDist = curDist;
                bestPathIndex = i;
            }
        }
        if(bestPathIndex == -1) {
            throw new RuntimeException("Game is done");
        }
        return bestPathIndex;
    }

    public DirectionEnum findNextMoveDirection(Guard guard, Tile nextTile) {
        Tile curTile = guard.getAgentPosition();
        int curX = curTile.getX();
        int curY = curTile.getY();
        int goalX = nextTile.getX();
        int goalY = nextTile.getY();
        if(DEBUG)
            System.out.println("Found frontier: " + goalX + ", " + goalY + " --- " + adjacencyList.getTileIndex(curTile));
        if(DEBUG)
            System.out.print("Direction for that tile is: ");
        if(adjacencyList.get(nextTile).size() == 4) {
            frontierQueue.remove(nextTile);
        }

        ArrayList<DirectionEnum> dirs = new ArrayList<>();

        if(curX < goalX) {
            if(DEBUG)
                System.out.println("East");
            dirs.add(DirectionEnum.EAST);
        }
        else if(curX > goalX) {
            if(DEBUG)    
                System.out.println("West");
            dirs.add(DirectionEnum.WEST);
        }
        else if (curY < goalY) {
            if(DEBUG)
                System.out.println("South");
            dirs.add(DirectionEnum.SOUTH);
        }
        else if (curY > goalY) {
            if(DEBUG)
                System.out.println("North");
            dirs.add(DirectionEnum.NORTH);
        }
        for(DirectionEnum dir : dirs) {
            if(guard.getAngle() == dir.getAngle()) {
                return dir;
            }
        }

        if(!dirs.isEmpty()) {
            return dirs.get(0);
        }
        else {
            return null;
        }
    }

    private void printQueue(Queue<Tile> queue) {
        String result = "frontierQueue: ";
        for(Tile tile : queue) {
            result += adjacencyList.getTileIndex(tile) + ", ";
        }
        if(DEBUG)
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
