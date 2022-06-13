package exploration;

import agents.Agent;
import agents.Intruder;
import controller.Map.Map;
import controller.Map.tiles.Tile;
import javafx.scene.paint.Color;
import utils.DirectionEnum;
import utils.Path;

public class BaselineGuard extends FrontierBasedExploration{

    Color[] c = {Color.RED, Color.ORANGE, Color.GREEN, Color.WHITE, Color.LAVENDER};

    public BaselineGuard(Agent agent, Map map) {
        super(agent, map);
        //System.out.println("Creating markers...");
//        agent.createMarkers(5, c);
        //System.out.println("Markers created");
//        System.out.println("Adding one marker...");
//        agent.addMarkers(3, c[3], map);
//        System.out.println("Marker added");
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
                return findNextMoveDirection(agent, path.get(1));
            }
            if(tile.toString().equals("TargetArea")) {
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

        if(frontierQueue.isEmpty()){
            return null;
        }
        goalTile = updateGoal(agent, updated); // update the goal tile for the agent
        DirectionEnum dir = findNextMoveDirection(agent, goalTile);
        return dir;
    }
}
