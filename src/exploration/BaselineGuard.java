package exploration;

import agents.Agent;
import agents.Intruder;
import controller.Map.Map;
import controller.Map.tiles.Tile;
import utils.DirectionEnum;
import utils.Path;

import java.util.LinkedList;

public class BaselineGuard extends FrontierBasedExploration{

    LinkedList<Tile> targetAreaTiles = new LinkedList<>();

    public BaselineGuard(Agent agent, Map map) {
        super(agent, map);
    }

    @Override
    public DirectionEnum makeMove(Agent agent) {
        Tile curTile = agent.getAgentPosition();
        visibleTiles = agent.getVisibleTiles();
        boolean updated = updateKnowledge(agent, visibleTiles);
        Tile goalTile = null;
        for(Tile tile : visibleTiles) {
            Agent agentFound = tile.getAgent();
            if(agentFound != null && agentFound.getType().equals("Intruder")) {
                goalTile = agentFound.getAgentPosition();
                Path path = findPath(agent, goalTile);
                if(path!= null && path.size() > 1) {
                    goalTile = path.get(1);
                }
                return findNextMoveDirection(agent, goalTile);
            }
            if(tile.toString().equals("TargetArea") && !targetAreaTiles.contains(tile)) {
                targetAreaTiles.add(tile);
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
        else if(!targetAreaTiles.isEmpty()) {
            Path path =  findPath(agent, targetAreaTiles, false);
            if(path == null) {
                return findNextMoveDirection(agent, null);
            }
            return findNextMoveDirection(agent, path.get(1));
        }

        if(frontierQueue.isEmpty()){
            return null;
        }
        goalTile = updateGoal(agent, updated); // update the goal tile for the agent
        DirectionEnum dir = findNextMoveDirection(agent, goalTile);
        return dir;
    }
}
