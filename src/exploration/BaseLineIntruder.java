package exploration;

import agents.Agent;
import agents.Guard;
import agents.Intruder;
import controller.Map.Map;
import controller.Map.tiles.Tile;
import utils.AdjacencyList;
import utils.DirectionEnum;
import utils.Path;
import utils.Utils;


import java.util.*;

public class BaseLineIntruder extends FrontierBasedExploration {

    public BaseLineIntruder(Agent agent, Map map) {
        super(agent, map);
    }
    public DirectionEnum prevDir;

    @Override
    public DirectionEnum makeMove(Agent agent) {
        Intruder intruder = (Intruder) agent;
        Tile tile = null;
        if(intruder.getAgentPosition().toString().equals("TargetArea")){
            return prevDir;
        }
        Tile curTile = intruder.getAgentPosition();
        visibleTiles = intruder.getVisibleTiles();
        updateKnowledge(agent, visibleTiles);
        updateFrontiers(intruder);
        if (frontierQueue.isEmpty()) {
            return null;
        }
        tile = findBestFrontier(frontierQueue, intruder);
        Path path = findPath(intruder, tile);
        if(path.size()==1){
            agent.addMarkers(1,map);
            return findNextMoveDirection(intruder, path.get(0));
        }
        DirectionEnum dir = findNextMoveDirection(intruder, path.get(1));
        prevDir = dir;
        return dir;
    }



    private Tile findBestFrontier(Queue<Tile> frontierQueue, Intruder intruder) {
        Tile bestFrontier = null;
        double optimalFrontier = 360;
        for(Tile tile : frontierQueue){
            if(!tile.equals(intruder.getAgentPosition())){
                double angleFromStart = Utils.findAngleFromStartingPosition(intruder, tile);
                double differenceToOptimalAngle = Utils.differenceBetweenAngles(angleFromStart, intruder.angleOfTarget);
                if(differenceToOptimalAngle<optimalFrontier){
                    bestFrontier = tile;
                    optimalFrontier = differenceToOptimalAngle;
                }
            }
        }
        return bestFrontier;
    }

}
