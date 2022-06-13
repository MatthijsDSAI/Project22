package exploration;

import agents.Agent;
import agents.Guard;
import controller.Map.Map;
import controller.Map.tiles.Tile;
import utils.DirectionEnum;

import java.util.ArrayList;
import java.util.Random;

public class CombinedIntruder extends FrontierBasedExploration{

    public Map map;
    public Agent agent;
    public BaseLineIntruder baseLineIntruder;
    private Random r = new Random();

    public CombinedIntruder(Agent agent, Map map) {
        super(agent, map);
        this.map = map;
        this.agent = agent;
        this.baseLineIntruder = new BaseLineIntruder(agent, map);
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

        // Situation 1: Intruder didn't see the guards, TA and marker, so it will keep exploration
        if(!hasGuards && !foundTarget && !hasMarker){
            return baseLineIntruder.makeMove(agent);
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
                double distance = Math.sqrt(Math.abs((x - guardX) * (x - guardX) + (y - guardY)* (y - guardY)));
                if(distance > maxDistance && tile.isWalkable()){
                    maxDistance = distance;
                    bestTile = tile;
                }
            }
            return move(agent, bestTile);
        }

//        if(checkGuards(visibleTiles) && foundTarget){
//        }

        //Situation 4.1: Intruder see the marker, without seeing the TA and guards, it will move to the marker
        if(hasMarker && !hasGuards && !foundTarget){
            Tile marker = getMarkerTile(visibleTiles);
            return move(agent, marker);
        }

        //Situation 4.2: Intruder see the marker and see the TA , it will move to the TA
        if(hasMarker && !hasGuards && foundTarget){
            return move(agent, getTargetArea(visibleTiles));
        }
        return null;
    }

    public DirectionEnum move(Agent agent, Tile goalTile) {
        int guardsX = agent.getX_position();
        int guardsY = agent.getY_position();
        int goalX = goalTile.getX();
        int goalY = goalTile.getY();

        ArrayList<DirectionEnum> dirs = new ArrayList<>();

        if(guardsX < goalX) {
            System.out.println("East");
            if(map.getTile(agent.getX_position()+1, agent.getY_position()).isWalkable()) {
                dirs.add(DirectionEnum.EAST);
            }
        }
        else if(guardsX > goalX) {
            System.out.println("West");
            if(map.getTile(agent.getX_position()-1, agent.getY_position()).isWalkable()) {
                dirs.add(DirectionEnum.WEST);
            }
        }

        if (guardsY < goalY) {
            System.out.println("South");
            if(map.getTile(agent.getX_position(), agent.getY_position()+1).isWalkable()) {
                dirs.add(DirectionEnum.SOUTH);
            }
        }
        else if (guardsY > goalY) {
            System.out.println("North");
            if(map.getTile(agent.getX_position(), agent.getY_position()-1).isWalkable()) {
                dirs.add(DirectionEnum.NORTH);
            }
        }

        if(!dirs.isEmpty()) {
            System.out.println(dirs.size());
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
