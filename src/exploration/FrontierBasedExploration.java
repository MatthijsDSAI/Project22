package exploration;
import agents.Agent;
import agents.Agent;
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

public class FrontierBasedExploration extends Exploration{
    //no prior information about the map
    public Map map;
    public ArrayList<Tile> visibleTiles;
    public AdjacencyList adjacencyList;
    public LinkedList<Tile> exploredTiles;
    public Queue<Tile> frontierQueue;
    public Queue<Tile> BFSQueue;
    public Path curPath;
    public boolean DEBUG = Scenario.config.DEBUG;

    //Constructor -> tells which is the position of the robot and the angle
    public FrontierBasedExploration(Agent agent, Map map) {
        this.map = map;
        visibleTiles = agent.getVisibleTiles();
        adjacencyList = new AdjacencyList(map.getTiles(), visibleTiles);
        frontierQueue = new LinkedList<>();
        BFSQueue = new LinkedList<>();
        exploredTiles = new LinkedList<>();
    }

    @Override
    /**
     * Determines the next move for the agent based on its current information.
     *
     * @param agent the agent for which to determine the next move
     * @return the DirectionEnum to move towards
     */
    public DirectionEnum makeMove(Agent agent) {
        visibleTiles = agent.getVisibleTiles();     // update the currently visible tiles
        updateKnowledge(visibleTiles);              // update the knowledge base of the agent
        boolean updated = updateFrontiers(agent);   // update the frontiers and set boolean value to whether or not there was a new frontier found
        Tile goalTile = null;
        if (updated || this.curPath.size() <= 1) {
            this.curPath = findPath(agent, frontierQueue);
        }
        goalTile = this.curPath.remove(1);
        if(frontierQueue.isEmpty()){
            return null;
        }
        DirectionEnum dir = findNextMoveDirection(agent, goalTile);
        return dir;
    }

    /**
     * Updates the knowledge base of the agent based on the currently visible tiles.
     * @param visibleTiles ArrayList containing the currently visible tiles
     */
    public void updateKnowledge(ArrayList<Tile> visibleTiles) {
        adjacencyList.addNodes(visibleTiles);           // add the currently visible tiles to the adjacency list
        for(Tile tile : visibleTiles) {                 // loop over the visible tiles
            if(adjacencyList.get(tile).size() == 4) {
                frontierQueue.remove(tile);             // if 4 adjacent tiles are known, remove from the frontier queue
                if(!exploredTiles.contains(tile)) exploredTiles.add(tile); // if tile is not in explored tiles yet, add it
            }
        }
    }

    /**
     * Updates the frontierQueue based on a Bread First Search performed from the current agent position.
     * @param agent the agent from which the starting position is decided
     */
    public boolean updateFrontiers(Agent agent) {
        boolean frontierFound = false;
        Tile startTile = agent.getAgentPosition();          // Determine the starting tile
        // Create the queue for the BFS search and add the starting tile to it
        BFSQueue = new LinkedList<>();
        BFSQueue.add(startTile);
        LinkedList<Tile> tilesSeen = new LinkedList<>();    // List containing the tiles which have already been seen in search

        // Loop over BFSQueue until it is empty
        while(!BFSQueue.isEmpty()) {
            // Get tile from queue to perform BFS search from and add it to the list of tiles seen
            Tile curTile = BFSQueue.remove();
            tilesSeen.add(curTile);
            // Get the tiles adjacent to the current tile and loop over them
            LinkedList<Tile> curAdjacencyList = adjacencyList.get(curTile);
            for(Tile tile : curAdjacencyList) {
                // if the adjacent tile is already in the queue or was already checked skip this loop
                if(BFSQueue.contains(tile) || tilesSeen.contains(tile)) {
                    continue;
                }
                // else add it to the BFS queue and if it is a frontier add it to the frontier queue
                BFSQueue.add(tile);
                if(tile.isWalkable() && isFrontier(tile) && !frontierQueue.contains(tile) && !exploredTiles.contains(tile)) {
                    frontierQueue.add(tile);
                    frontierFound = true;
                }
            }
        }
        return frontierFound;
    }

    /**
     * Find the shortest path to a list of potential goal tiles using A-star search
     * @param agent     the agent from which the starting position is decided
     * @param goalTiles the list of potential goal tiles
     * @return the shortest path found
     */
    public Path findPath(Agent agent, Queue<Tile> goalTiles) {
        Tile startTile = agent.getAgentPosition();          // Determine the starting tile
        // Create the path queue and add the first path containing the starting tile
        LinkedList<Path> pathQueue = new LinkedList<>();
        Path path = new Path();
        path.add(startTile);
        pathQueue.add(path);
        LinkedList<Tile> tilesSeen = new LinkedList<>(); // List containing the tiles which have already been seen in search

        // loop over pathQueue until it is empty
        while(!pathQueue.isEmpty()) {
            // remove the shortest path from the current pathQueue
            path = pathQueue.remove(findShortestPath(pathQueue, goalTiles));
            // get the last tile from the shortest path, add it to the tiles seen and check if it is a frontier
            Tile lastTile = path.getLast();
            tilesSeen.add(lastTile);
            if (lastTile.isWalkable() && isFrontier(lastTile) && goalTiles.contains(lastTile) && startTile != lastTile) {
                return path; // if current path is a path to a frontier return that path
            }
            // get the tiles adjacent to the current tile and loop over them
            LinkedList<Tile> curAdjacencyList = adjacencyList.get(lastTile);
            for (Tile tile : curAdjacencyList) {
                if(path.contains(tile) || tilesSeen.contains(tile)) {
                    continue; // if the tile has already been seen or is already in the path continue
                }
                if (!path.contains(tile) && tile.isWalkable()) {
                    // if the tile is has not been seen yet and is walkable create a new path and add it to the queue
                    Path newPath = new Path(path);
                    newPath.add(tile);
                    pathQueue.offer(newPath);
                }
            }
        }
        return null;
    }

    /**
     * Find the shortest path to a goal tiles using A-star search (refer to the other findPath implementation for more
     *                                                             detailed comments on how this works)
     * @param agent     the agent from which the starting position is decided
     * @param goalTile  the goal tile
     * @return the shortest path found
     */
    public Path findPath(Agent agent, Tile goalTile) {
        Tile curTile = agent.getAgentPosition();
        LinkedList<Tile> goal = new LinkedList<>();
        goal.add(goalTile);
        LinkedList<Path> queue = new LinkedList<>();
        Path path = new Path();
        path.add(curTile);
        queue.add(path);
        LinkedList<Tile> tilesSeen = new LinkedList<>();
        while(!queue.isEmpty()) {
            path = queue.remove(findShortestPath(queue, goal));
            Tile lastTile = path.getLast();
            tilesSeen.add(lastTile);
            if (lastTile.isWalkable() && goal.contains(lastTile)) {
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

    /**
     * Perform an iteration of A-star search on a list of paths and goal tiles.
     * @param paths         the list of paths to continue search from
     * @param frontiers     the list of potential goal tiles
     * @return the index of the shortest path to continue searching from
     */
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

    /**
     * Return the direction for the agent to move to when attempting to walk towards a specific tile.
     * @param agent     the agent from which the starting position is decided
     * @param nextTile  the tile the agent should move towards
     * @return the DirectionEnum for the agent to move towards the nextTile tile
     */
    public DirectionEnum findNextMoveDirection(Agent agent, Tile nextTile) {
        Tile curTile = agent.getAgentPosition();
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
            if(agent.getAngle() == dir.getAngle()) {
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

    /**
     * Checks if a provided tile is a frontier based on the amount of known adjacent tiles
     * @param tile  the tile for which to check if it is a frontier
     * @return boolean result
     */
    boolean isFrontier(Tile tile) {
        if(adjacencyList.get(tile).size() < 4) return true;
        return false;
    }

    public Map getMap(){
        return map;
    }
}
