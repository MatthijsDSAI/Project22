package exploration;

import agents.Agent;
import agents.Intruder;
import controller.Map.Map;
import controller.Map.tiles.Tile;
import utils.DirectionEnum;
import utils.Path;

public class BaselineGuard extends FrontierBasedExploration{
    public BaselineGuard(Agent agent, Map map) {
        super(agent, map);
    }

    @Override
    public DirectionEnum makeMove(Agent agent) {
        Tile curTile = agent.getAgentPosition();
        visibleTiles = agent.getVisibleTiles();
        updateExploredTiles(visibleTiles);
        adjacencyList.addNodes(visibleTiles);
        updateFrontiers(visibleTiles);
//        System.out.println("Visible tiles: " + visibleTiles);
        Tile goalTile = null;
        for(Tile tile : visibleTiles) {
//            System.out.println("Now looking at: " + tile.toString());
            Agent agentFound = tile.getAgent();
            if(agentFound != null && agentFound.getType().equals("Intruder")) {
//                System.out.println("Found agent: ");
                goalTile = agentFound.getAgentPosition();
//                System.out.println("looking for path");
                Path path = findPath(agent, goalTile);
//                System.out.println("Path found");
//                System.out.println(path);
                return findNextMoveDirection(agent, path.get(1));
            }
            if(tile.toString().equals("TargetArea")) {
//                System.out.println("Trying to add target area as long as its not equal to guards current position");
//                System.out.println(tile.getX() + ", " + tile.getY());
//                System.out.println(curTile.getX() + ", " + curTile.getY());
            }
            if(tile.toString().equals("TargetArea") && !(tile.getX() == curTile.getX() && tile.getY() == curTile.getY())) {
                goalTile = tile;
            }
        }
        if(goalTile != null) {
            Path path = findPath(agent, goalTile);
            if(path == null) {
                return findNextMoveDirection(agent, goalTile);
            }
            return findNextMoveDirection(agent, path.get(1));
        }

        updateExploredTiles(visibleTiles);
        adjacencyList.addNodes(visibleTiles);
        updateFrontiers(visibleTiles);
        Path path = findFrontiers(agent);
        System.out.println(path);
        Tile tile = path.get(1);
        if(frontierQueue.isEmpty()){
            return null;
        }
        DirectionEnum dir = findNextMoveDirection(agent, tile);
        return dir;
    }
}
