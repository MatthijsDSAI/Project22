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
        for(Tile tile : visibleTiles) {
            if(tile.toString().equals("TargetArea")) {
                Path path = findPath(agent, tile);
                return findNextMoveDirection(agent, path.get(1));
            }

            Agent agentFound = tile.getAgent();
            if(agentFound != null && agentFound.getType().equals("Intruder")) {
                Tile goalTile = agentFound.getAgentPosition();
                Path path = findPath(agent, goalTile);
                return findNextMoveDirection(agent, path.get(1));
            }
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
