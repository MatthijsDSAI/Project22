package exploration;

import agents.Agent;
import agents.Guard;
import agents.Intruder;
import controller.Map.Map;
import controller.Map.tiles.Tile;
import javafx.scene.paint.Color;
import utils.DirectionEnum;
import utils.Path;
import utils.Utils;

import java.util.ArrayList;
import java.util.Queue;
import java.util.Random;

public class CombinedIntruder extends FrontierBasedExploration{

    public Map map;
    public Agent agent;
    public BaseLineIntruder baseLineIntruder;
    private Random r = new Random();
    public FrontierBasedExploration frontierExploration;
    public DirectionEnum prevDir;

    public CombinedIntruder(Agent agent, Map map) {
        super(agent, map);
        this.map = map;
        this.agent = agent;
        this.baseLineIntruder = new BaseLineIntruder(agent, map);
        this.frontierExploration = new FrontierBasedExploration(agent, map);
    }
    /**
     * This class handles 4 situations: (new situations can be added further)
     * <p>
     * Situation 1: Intruder didn't see the guards, TA and marker, so it will keep exploration
     * <p>
     * Situation 2: Intruder see the TA without seeing the TA and marker, it will go straight to the TA
     * <p>
     * Situation 3: Intruder see the guards, it will escape to the tile which is furthest to the guards
     * <p>
     * Situation 4.1: Intruder see the marker, without seeing the TA and guards, it will move to the marker
     * Situation 4.2: Intruder see the marker and see the TA , it will move to the TA
     */

    @Override
    public DirectionEnum makeMove(Agent agent) {
        int x = agent.getX_position();
        int y = agent.getY_position();

        ArrayList<Tile> visibleTiles = agent.getVisibleTiles();
        updateKnowledge(agent, visibleTiles);

        boolean foundTarget = checkTargetArea(visibleTiles);
        boolean hasMarker = checkMarker(visibleTiles);
        boolean hasGuards = checkGuards(visibleTiles);

        if(hasMarker){
//            MarkerInterpretation(agent);
        }

        // Situation 1: Intruder didn't see the guards, TA and marker, so it will keep exploration
        if(!hasGuards && !foundTarget && !hasMarker){
//            return baseLineIntruder.makeMove(agent);
            Tile tile = null;
            if(agent.getAgentPosition().toString().equals("TargetArea")){
                return prevDir;
            }
            Tile curTile = agent.getAgentPosition();
            visibleTiles = agent.getVisibleTiles();
            updateKnowledge(agent, visibleTiles);
            updateFrontiers(agent);
            if (frontierQueue.isEmpty()) {
                return null;
            }
            tile = findBestFrontier(frontierQueue, (Intruder) agent);
            Path path = findPath(agent, tile);
            if(path.size()==1){
                return findNextMoveDirection(agent, path.get(0));
            }
            DirectionEnum dir = findNextMoveDirection(agent, path.get(1));
            prevDir = dir;
            return dir;
//            return frontierExploration.makeMove(agent);
        }

        //Situation 2: Intruder see the TA without seeing the TA and marker, it will go straight to the TA
        if(!hasGuards && foundTarget && !hasMarker){
            return move(agent, getTargetArea(visibleTiles));
        }

        // Situation 3: Intruder see the guards, it will escape to the tile which is furthest to the guards
        if(hasGuards && !foundTarget){
            Guard guard = getGuards(visibleTiles);
            int guardX = guard.getX_position();
            int guardY = guard.getY_position();
            double maxDistance = 0;
            Tile bestTile = null;
            for(Tile tile : visibleTiles){
                double distance = Math.sqrt(Math.abs((tile.getX() - guardX) * (tile.getX() - guardX) + (tile.getY() - guardY)* (tile.getY() - guardY)));
                if(distance > maxDistance && tile.isWalkable()){
                    maxDistance = distance;
                    bestTile = tile;
                }
            }
            return move(agent, bestTile);
        }

        if(checkGuards(visibleTiles) && foundTarget){
            Tile target = getTargetArea(visibleTiles);
            Guard guard = getGuards(visibleTiles);

            double distanceTarget = Math.sqrt(Math.abs((x - target.getX()) * (x - target.getX()) + (y - target.getY())* (y - target.getY())));
            double distanceGuard = Math.sqrt(Math.abs((x - guard.getX_position()) * (x - guard.getX_position()) + (y - guard.getY_position())* (y - guard.getY_position())));
            
            if(distanceTarget > distanceGuard){
                double maxDistance = 0;
                Tile bestTile = null;
                for(Tile tile : visibleTiles){
                    double distance = Math.sqrt(Math.abs((tile.getX() - guard.getX_position()) * (tile.getX() - guard.getX_position()) + (tile.getY() - guard.getY_position())* (tile.getY() - guard.getY_position())));
                    if(distance > maxDistance && tile.isWalkable()){
                        maxDistance = distance;
                        bestTile = tile;
                    }
                }
                return move(agent, bestTile);
            }else {
                return move(agent, getTargetArea(visibleTiles));
            }
        }

//        //Situation 4.1: Intruder see the marker, without seeing the TA and guards, it will move to the marker
//        if(hasMarker && !hasGuards && !foundTarget){
//            Tile marker = getMarkerTile(visibleTiles);
//            return move(agent, marker);
//        }
//
//        //Situation 4.2: Intruder see the marker and see the TA , it will move to the TA
//        if(hasMarker && !hasGuards && foundTarget){
//            return move(agent, getTargetArea(visibleTiles));
//        }

        return null;
    }

//    public void MarkerInterpretation(Agent agent){
//        Tile f = agent.findMarker();
//        if(f!=null)
//        {
//            Color c = agent.ownMap.getTile(f.getX(),f.getY()).getColor();
//            if(c==Color.RED){
//                //TO ADD % explored >= 50% don't follow this rule anymore
//                // Actually check frontiers
//                if(agent.getY_position()==agent.ownMap.getVerticalSize()/2 && ok ==0)
//                    agent.setAngle((int) (agent.getAngle()+90));
//                //if(agent.getExploration())
//                System.out.println("Reached the half of the map.");
//            }
//        }
//    }

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

    public DirectionEnum move(Agent agent, Tile goalTile) {
        int guardsX = agent.getX_position();
        int guardsY = agent.getY_position();
        int goalX = goalTile.getX();
        int goalY = goalTile.getY();

        ArrayList<DirectionEnum> dirs = new ArrayList<>();

        if(guardsX < goalX) {
            if(map.getTile(agent.getX_position()+1, agent.getY_position()).isWalkable()) {
                dirs.add(DirectionEnum.EAST);
            }
        }
        else if(guardsX > goalX) {
            if(map.getTile(agent.getX_position()-1, agent.getY_position()).isWalkable()) {
                dirs.add(DirectionEnum.WEST);
            }
        }

        if (guardsY < goalY) {
            if(map.getTile(agent.getX_position(), agent.getY_position()+1).isWalkable()) {
                dirs.add(DirectionEnum.SOUTH);
            }
        }
        else if (guardsY > goalY) {
            if(map.getTile(agent.getX_position(), agent.getY_position()-1).isWalkable()) {
                dirs.add(DirectionEnum.NORTH);
            }
        }

        if(!dirs.isEmpty()) {
            return dirs.get(r.nextInt(dirs.size()));
        }
        else {
            return null;
        }
    }

    private boolean checkTargetArea(ArrayList<Tile> vision) {
        for (Tile tile : vision) {
            if (tile.toString().equals("TargetArea")) {
                return true;
            }
        }
        return false;
    }

    private Tile getTargetArea(ArrayList<Tile> vision) {
        for (Tile tile : vision) {
            if (tile.toString().equals("TargetArea")) {
                return tile;
            }
        }
        return null;
    }

    private boolean checkGuards(ArrayList<Tile> vision) {
        for (Tile tile : vision) {
            if (tile.hasAgent()) {
                if (tile.getAgent().getType().equals("Guard")) {
                    return true;
                }
            }
        }
        return false;
    }

    private Guard getGuards(ArrayList<Tile> vision) {
        for (Tile tile : vision) {
            Agent foundAgent = tile.getAgent();
            if ((foundAgent != null) && foundAgent.getType().equals("Guard")) {
                return (Guard) foundAgent;
            }
        }
        return null;
    }

    private boolean checkMarker(ArrayList<Tile> vision) {
        for (Tile tile : vision) {
            if (tile.hasAgent()) {
                if (tile.getHasMarker()) {
                    return true;
                }
            }
        }
        return false;
    }

    private Tile getMarkerTile(ArrayList<Tile> vision) {
        for (Tile tile : vision) {
            if (tile.getHasMarker()) {
                return tile;
            }
        }
        return null;
    }
}
