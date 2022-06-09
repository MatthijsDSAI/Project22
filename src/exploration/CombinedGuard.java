package exploration;

import agents.Agent;
import agents.Guard;
import agents.Intruder;
import controller.Map.Map;
import controller.Map.tiles.Tile;
import utils.DirectionEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static java.lang.Math.abs;

public class CombinedGuard extends Exploration {

    public Map map;
    public Agent agent;
    public int sizeOfStandardized;
    public int sizeOfTA;
    public FrontierBasedExploration frontierExploration;

    private boolean targetHasBeenReached = false;
    private boolean doWeKnowTAYet = false;
    private int upperBoundaryOfStandardized;

    private boolean invaderSeen = false;
    private boolean patrolling = false;

    private int situationStage = 1;
    private boolean wholeVisionInsideTA = false;
    private Random r;

    private int northBoundaryOfStandardized;
    private int southBoundaryOfStandardized;
    private int westBoundaryOfStandardized;
    private int eastBoundaryOfStandardized;

    // assuming we know the location of standardized area
    public CombinedGuard(Agent agent, Map map, int northBoundaryOfStandardized, int southBoundaryOfStandardized, int westBoundaryOfStandardized, int eastBoundaryOfStandardized) {
        this.map = map;
        this.agent = agent;
        this.northBoundaryOfStandardized = northBoundaryOfStandardized;
        this.southBoundaryOfStandardized = southBoundaryOfStandardized;
        this.westBoundaryOfStandardized = westBoundaryOfStandardized;
        this.eastBoundaryOfStandardized = eastBoundaryOfStandardized;

        this.frontierExploration = new FrontierBasedExploration(agent, map);
    }

    // assuming we only know the size of standardized and TA area
    public CombinedGuard(Agent agent, Map map, int sizeOfStandardized, int sizeOfTA) {
        this.agent = agent;
        this.map = map;
        this.sizeOfStandardized = sizeOfStandardized;
        this.sizeOfTA = sizeOfTA;

        // creating algos:
        this.frontierExploration = new FrontierBasedExploration(agent, map);
    }


    /**
     * 4 things can happen: ( can be added further )
     * 1. Agent has never seen TA (target area) or intruder, so keep exploring
     * 2. Agent has seen intruder but not the TA, what TODO?
     * 3. Agent has seen target area but not the intruder, start patrolling around the TA via QL
     * 4. Agent has seen intruder whilst previously seeing TA;
     *      - either shout (check if there exists another guard) and pursue invader
     *      - pursue invader solo
     *      - disregard
     *
     * 5. Agent sees intruder and TA at the same time
     *
    */
    @Override
    public DirectionEnum makeMove(Agent agent) {
        Guard guard = (Guard) agent;
        ArrayList<Tile> visibleTiles = guard.getVisibleTiles();

        // first checking if agent sees TA, if sees TA set "targetHasBeenReached" true and never change
        if (!targetHasBeenReached) { // agent sees the target area first time
            targetHasBeenReached = checkTargetArea(visibleTiles);
        }

        invaderSeen = checkInvader(visibleTiles);

        // situation 1: guard has not seen TA or Invader yet, so keep exploring
        if (!targetHasBeenReached && !invaderSeen) {
            return frontierExploration.makeMove(agent);
        }

        // situation 2: guard has seen invader whilst not seeing TA, chase anyway
        if (!targetHasBeenReached && invaderSeen) { // agent sees invader for the first time
            return chasing(guard, getInvader(visibleTiles).getAgentPosition());
        }

        // situation 3: (where QL will be added)
        // situation 3.1: agent sees the target area first time, take to the border
        if (targetHasBeenReached && (situationStage == 1)) {
            int x = agent.getX_position();
            int y = agent.getY_position();

            HashMap<Integer, DirectionEnum> distanceToBoundaries = new HashMap<>();
            distanceToBoundaries.put(abs(x - northBoundaryOfStandardized), DirectionEnum.NORTH);
            distanceToBoundaries.put(abs(x - southBoundaryOfStandardized), DirectionEnum.SOUTH);
            distanceToBoundaries.put(abs(y - eastBoundaryOfStandardized), DirectionEnum.EAST);
            distanceToBoundaries.put(abs(y - westBoundaryOfStandardized), DirectionEnum.WEST);

            DirectionEnum answer = DirectionEnum.getDirection(agent.getAngle());
            int closestDistance = map.getVerticalSize();
            for (java.util.Map.Entry<Integer, DirectionEnum> entry : distanceToBoundaries.entrySet()) {
                if (entry.getKey() <= closestDistance) {
                    answer = entry.getValue();
                }
            }
            situationStage = 2;
            return answer;
        }
//            // checking if we have the corner of TA in vision
//            for (int i = 0; i < visibleTiles.size(); i = i + 3) {
//                if (visibleTiles.get(i).toString().equals("TargetArea") && visibleTiles.get(i + 1).toString().equals("TargetArea") && visibleTiles.get(i + 3).toString().equals("TargetArea")) {
//                    wholeVisionInsideTA = true;
//                    situationStage = 2;
//                    return DirectionEnum.getDirection(agent.getAngle()); // agent just needs to keep going straight
//                }
//            }
//            if (!wholeVisionInsideTA) { // means we have vision of the edge, width of vision is 3 (1', 2', 3' tiles of each row)
//                int xOfCorner;
//                int yOfCorner;
//                for (int j = 0; j < visibleTiles.size(); j = j + 3) {
//                    if (!visibleTiles.get(j).toString().equals("TargetArea")) { // 1' not inside TA
//                        if (!visibleTiles.get(j + 2).toString().equals("TargetArea")) { // 2' not inside TA
//                            Tile cornerTile = visibleTiles.get(j + 1);
//                            xOfCorner = cornerTile.getX();
//                            yOfCorner = cornerTile.getY();
//                        } else {
//                            Tile cornerTile = visibleTiles.get(j + 2); // 2' inside TA, since part vision 3' definitely not inside TA
//                            xOfCorner = cornerTile.getX();
//                            yOfCorner = cornerTile.getY();
//                        }
//
//                    } else { // 1' inside TA
//                        if (!visibleTiles.get(j + 2).toString().equals("TargetArea")) { // 3' inside TA, 3' is corner
//                            Tile cornerTile = visibleTiles.get(j + 2);
//                            xOfCorner = cornerTile.getX();
//                            yOfCorner = cornerTile.getY();
//                        } else {
//                            Tile cornerTile = visibleTiles.get(j); // 3' not inside TA, 1' is corner
//                            xOfCorner = cornerTile.getX();
//                            yOfCorner = cornerTile.getY();
//                        }
//                    }
//                }


        // situation 3.2: Guard is going to the border of standardized area in a straight line
        if (targetHasBeenReached && (situationStage == 2)) {
            int x = agent.getX_position();
            int y = agent.getY_position();

            HashMap<Integer, DirectionEnum> distanceToBoundaries = new HashMap<>();
            distanceToBoundaries.put((x - northBoundaryOfStandardized), DirectionEnum.NORTH);
            distanceToBoundaries.put((x - southBoundaryOfStandardized), DirectionEnum.SOUTH);
            distanceToBoundaries.put((y - eastBoundaryOfStandardized), DirectionEnum.EAST);
            distanceToBoundaries.put((y - westBoundaryOfStandardized), DirectionEnum.WEST);

            for (java.util.Map.Entry<Integer, DirectionEnum> entry : distanceToBoundaries.entrySet()) {
                if (entry.getKey() == 0) {
                    situationStage = 3;
                    return DirectionEnum.getDirection(agent.getAngle() + 90); // turning it to right
                }
            }
            return DirectionEnum.getDirection(agent.getAngle()); // going straight
        }

        // situation 3.3: Guard is patrolling along the border of standardized area
        if (targetHasBeenReached && (situationStage == 3)) {
            int x = agent.getX_position();
            int y = agent.getY_position();

            HashMap<Integer, DirectionEnum> distanceToBoundaries = new HashMap<>();
            distanceToBoundaries.put((x - northBoundaryOfStandardized), DirectionEnum.NORTH);
            distanceToBoundaries.put((x - southBoundaryOfStandardized), DirectionEnum.SOUTH);
            distanceToBoundaries.put((y - eastBoundaryOfStandardized), DirectionEnum.EAST);
            distanceToBoundaries.put((y - westBoundaryOfStandardized), DirectionEnum.WEST);

            int numberOfZeroDistance = 0;

            for (java.util.Map.Entry<Integer, DirectionEnum> entry : distanceToBoundaries.entrySet()) {
                if (entry.getKey() == 0) {
                    numberOfZeroDistance++;
                }
            }

            if (numberOfZeroDistance > 1) {
                System.out.println(numberOfZeroDistance);
                return DirectionEnum.getDirection(agent.getAngle() + 90); // turning right
            }
            return DirectionEnum.getDirection(agent.getAngle());
        }

        // situation 4: Guard sees invader while patrolling
        if (patrolling && invaderSeen) {
            // TODO guard.shout()
            return chasing(guard, getInvader(visibleTiles).getAgentPosition());
        }

        return null;
    }

    private boolean checkTargetArea(ArrayList<Tile> vision) {
        for (Tile tile : vision) {
            if (tile.toString().equals("TargetArea")) {
                return true;
            }
        }
        return false;
    }

    private boolean checkInvader(ArrayList<Tile> vision) {
        for (Tile tile : vision) {
            if (tile.hasAgent()) {
                if (tile.getAgent().getType().equals("Intruder")) {
                    return true;
                }
            }
        }
        return false;
    }

    private Intruder getInvader(ArrayList<Tile> vision) {
        for (Tile tile : vision) {
            if (tile.getAgent().getType().equals("Intruder")) {
                return (Intruder) tile.getAgent();
            }
        }
        return null;
    }

    public DirectionEnum chasing(Agent agent, Tile intruder) {
        int guardsX = agent.getX_position();
        int guardsY = agent.getY_position();
        int intruderX = intruder.getX();
        int intruderY = intruder.getY();

        ArrayList<DirectionEnum> dirs = new ArrayList<>();

        int xDiff = abs(guardsX - intruderX);
        int yDiff = abs(guardsY - intruderY);

        if(xDiff < yDiff) {
            if (guardsX < intruderX) {
                dirs.add(DirectionEnum.EAST);
            } else if (guardsX > intruderX) {
                dirs.add(DirectionEnum.WEST);
            }
        }
        else if(xDiff > yDiff) {
            if (guardsY < intruderY) {
                dirs.add(DirectionEnum.SOUTH);
            } else if (guardsY > intruderY) {
                dirs.add(DirectionEnum.NORTH);
            }
        }else {
            if (guardsX < intruderX) {
                dirs.add(DirectionEnum.EAST);
            } else if (guardsX > intruderX) {
                dirs.add(DirectionEnum.WEST);
            }
            if (guardsY < intruderY) {
                dirs.add(DirectionEnum.SOUTH);
            } else if (guardsY > intruderY) {
                dirs.add(DirectionEnum.NORTH);
            }
        }

        for(DirectionEnum dir : dirs) {
            if(agent.getAngle() == dir.getAngle()) {
                return dir;
            }
        }

        if(!dirs.isEmpty()) {
            r = new Random();
            return dirs.get(r.nextInt(dirs.size()));
        }
        else {
            return null;
        }
    }

}
