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
        visibleTiles = agent.getVisibleTiles();
        Tile goalTile = null;
        for(Tile tile : visibleTiles) {
            Agent agentFound = tile.getAgent();
            if(agentFound != null && agentFound.getType().equals("Intruder")) {
                goalTile = agentFound.getAgentPosition();
                Path path = findPath(agent, goalTile);
                return findNextMoveDirection(agent, path.get(1));
            }
            if(tile.toString().equals("TargetArea")) {
                goalTile = tile;
            }
        }
        if(goalTile != null) {
            Path path = findPath(agent, goalTile);
            return findNextMoveDirection(agent, path.get(1));
        }

        updateExploredTiles(visibleTiles);
        adjacencyList.addNodes(visibleTiles);
        updateFrontiers(visibleTiles);
        Tile tile = findFrontiers(agent).get(1);
        if(frontierQueue.isEmpty()){
            return null;
        }
        DirectionEnum dir = findNextMoveDirection(agent, tile);
        return dir;
    }
}
