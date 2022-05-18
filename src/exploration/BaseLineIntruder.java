package exploration;

import agents.Agent;
import agents.Guard;
import agents.Intruder;
import controller.Map.Map;
import controller.Map.tiles.Tile;
import javafx.scene.paint.Color;
import utils.AdjacencyList;
import utils.DirectionEnum;
import utils.Utils;


import java.util.*;

public class BaseLineIntruder extends FrontierBasedExploration {

    public BaseLineIntruder(Agent agent, Map map) {
        super(agent, map);
    }

    @Override
    public DirectionEnum makeMove(Agent agent) {
        Intruder intruder = (Intruder) agent;
        Tile tile = null;
        if(intruder.hasFoundTargetArea()){
            tile = intruder.getTargetArea();
        }
        else{
        Tile curTile = intruder.getAgentPosition();
        visibleTiles = intruder.getVisibleTiles();
        updateExploredTiles(visibleTiles);
        adjacencyList.addNodes(visibleTiles);
        updateFrontiers(visibleTiles);
        findFrontiers(intruder);
        if(frontierQueue.isEmpty()){
            return null;
        }
        tile = findBestFrontier(frontierQueue, intruder);
        }
        DirectionEnum dir = findNextMoveDirection(intruder, tile);
        return dir;
    }

    private Tile findBestFrontier(Queue<Tile> frontierQueue, Intruder intruder) {
        Tile bestFrontier = null;
        double optimalFrontier = 360;
        for(Tile tile : frontierQueue){
            double angleFromStart = Utils.findAngleFromStartingPosition(intruder, tile);
            double differenceToOptimalAngle = Utils.differenceBetweenAngles(angleFromStart, intruder.angleOfTarget);
            if(differenceToOptimalAngle<optimalFrontier){
                bestFrontier = tile;
                optimalFrontier = differenceToOptimalAngle;
            }
        }
        return bestFrontier;
    }

}
