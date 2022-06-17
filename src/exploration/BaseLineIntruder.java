package exploration;

import agents.Agent;
import agents.Guard;
import agents.Intruder;
import controller.Map.Map;
import controller.Map.tiles.Tile;
import javafx.scene.paint.Color;
import utils.AdjacencyList;
import utils.DirectionEnum;
import utils.Path;
import utils.Utils;


import java.util.*;

public class BaseLineIntruder extends FrontierBasedExploration {
    Color[] c = {Color.LAVENDER, Color.BROWN, Color.YELLOW, Color.PINK, null};
    Map map;

    public BaseLineIntruder(Agent agent, Map map) {
        super(agent, map);
        this.map=map;
        agent.createMarkers(5, 7, c);
    }
    public DirectionEnum prevDir;

    @Override
    public DirectionEnum makeMove(Agent agent) {
        Intruder intruder = (Intruder) agent;
        Tile tile = null;
        if(intruder.getAgentPosition().toString().equals("TargetArea")){
            agent.addMarkers(4,map);
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
        if(intruder.findMarker()!=null) {
            Tile z = MarkerInterpretation(intruder);
            if (!z.equals(intruder.getAgentPosition())) {
                double angleFromStart = Utils.findAngleFromStartingPosition(intruder, z);
                double differenceToOptimalAngle = Utils.differenceBetweenAngles(angleFromStart, intruder.angleOfTarget);
                if (differenceToOptimalAngle < optimalFrontier) {
                    bestFrontier = z;
                    optimalFrontier = differenceToOptimalAngle;
                }
            }
        }
        return bestFrontier;
    }

    public Tile MarkerInterpretation(Agent agent){
        Tile f = agent.findMarker();
        if(f!=null)
        {
            Color c = agent.ownMap.getTile(f.getX(),f.getY()).getColor();
            if(c==Color.LAVENDER){ // turn south
                if(agent.ownMap.getTile(agent.getX_position(),agent.getY_position()+1)!=null && agent.ownMap.getTile(agent.getX_position(),agent.getY_position()+1).isWalkable())
                    return agent.ownMap.getTile(agent.getX_position(),agent.getY_position()+1);
            }
            else if(c==Color.BROWN){ //turn east
                if(agent.ownMap.getTile(agent.getX_position()+1,agent.getY_position())!=null && agent.ownMap.getTile(agent.getX_position()+1,agent.getY_position()).isWalkable())
                    return agent.ownMap.getTile(agent.getX_position()+1,agent.getY_position());
            }
            else if(c==Color.YELLOW){ //turn north
                if(agent.ownMap.getTile(agent.getX_position(),agent.getY_position()-1)!=null && agent.ownMap.getTile(agent.getX_position(),agent.getY_position()-1).isWalkable())
                    return agent.ownMap.getTile(agent.getX_position(),agent.getY_position()-1);
            }
            else if(c==Color.PINK){ //turn west
                if(agent.ownMap.getTile(agent.getX_position()-1,agent.getY_position())!=null && agent.ownMap.getTile(agent.getX_position()-1,agent.getY_position()).isWalkable())
                    return agent.ownMap.getTile(agent.getX_position()-1,agent.getY_position());
            }
            else if(f.getIsPheromone()==true)
            {
                return f;
            }
        }
        return null;
    }
}
