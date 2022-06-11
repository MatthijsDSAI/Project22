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
    private boolean invaderSeen = false;
    private boolean patrolling = false;

    private int situationStage = 1;
    private Random r;

    private int northBoundaryOfStandardized;
    private int southBoundaryOfStandardized;
    private int westBoundaryOfStandardized;
    private int eastBoundaryOfStandardized;

    private boolean guardInLoop = false;
    private boolean isChasing = false;
    private int timePassedAfterSeeingInvader = 0;

    private int previousDistToW = 0;
    private int previousDistToN = 0;
    private int previousDistToE = 0;
    private int previousDistToS = 0;


    // assuming we know the boundaries of standardized area
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
        boolean DEBUG = false;
        Guard guard = (Guard) agent;
        ArrayList<Tile> visibleTiles = guard.getVisibleTiles();

        if (DEBUG) {
            System.out.println();
            System.out.println("Location of guard: at x:" + guard.getY_position() + ", y:" + guard.getX_position());
        }

        // first checking if agent sees TA, if sees TA set "targetHasBeenReached" true and never change
        if (!targetHasBeenReached) {
            targetHasBeenReached = checkTargetArea(visibleTiles);
        }
        // checking if agent sees intruder
        invaderSeen = checkInvader(visibleTiles);

        // situation 1: guard has not seen TA or Invader yet, so keep exploring
        if (!targetHasBeenReached && !invaderSeen) {
            if (DEBUG) {
                System.out.println("1");
                System.out.println(frontierExploration.makeMove(this.agent).getDirection());
            }
            return frontierExploration.makeMove(this.agent);
        }

        // situation 2: guard has seen invader whilst not seeing TA, start to chase
        if (!targetHasBeenReached && invaderSeen) { // agent sees invader for the first time
            isChasing = true;
            if (DEBUG) {
                System.out.println("2");
                System.out.println(chasing(guard, getInvader(visibleTiles).getAgentPosition()).getDirection());
            }
            return chasing(guard, getInvader(visibleTiles).getAgentPosition());
        }

        // situation 2.1: guard is chasing invader without the knowledge of TA
        if (!targetHasBeenReached && isChasing && invaderSeen) {
            if (DEBUG) {
                System.out.println("2.1");
                // System.out.println(chasing(guard, getInvader(visibleTiles).getAgentPosition()).getDirection());
            }
            if (checkInvader(visibleTiles)) {
                timePassedAfterSeeingInvader = 0;
            } else {
                timePassedAfterSeeingInvader++;
            }

            if (timePassedAfterSeeingInvader > 5) {
                invaderSeen = false;
                return frontierExploration.makeMove(this.agent);
            } else {
                return chasing(guard, getInvader(visibleTiles).getAgentPosition());
            }
        }

        // situation 3: (where QL will be added)
        // situation 3.1: agent sees the target area first time, turn towards to closest border
        if (targetHasBeenReached && (situationStage == 1)) {
            if (DEBUG) System.out.println("3.1");
            int x = agent.getX_position();
            int y = agent.getY_position();

            if (DEBUG) {
                System.out.println("Distance to east boundary: " + (x - eastBoundaryOfStandardized));
                System.out.println("Distance to west boundary: " + (x - westBoundaryOfStandardized));
                System.out.println("Distance to north boundary: " + (y - northBoundaryOfStandardized));
                System.out.println("Distance to south boundary: " + (y - southBoundaryOfStandardized));
            }


            DirectionEnum answer = DirectionEnum.EAST;
            int smallestVal = (x - eastBoundaryOfStandardized);
            if (abs((x - westBoundaryOfStandardized)) < abs(x - eastBoundaryOfStandardized)) {
                answer = DirectionEnum.WEST;
                smallestVal = (x - westBoundaryOfStandardized);
            }
            if (abs((x - northBoundaryOfStandardized)) < abs(smallestVal)) {
                answer = DirectionEnum.NORTH;
                smallestVal = (x - northBoundaryOfStandardized);
            }
            if (abs((x - southBoundaryOfStandardized)) < abs(smallestVal)) {
                answer = DirectionEnum.SOUTH;
                smallestVal = (x - southBoundaryOfStandardized);
            }

            situationStage = 2;

            if (smallestVal < 0 && answer.getDirection().equals("west")) {
                if (DEBUG) System.out.println(DirectionEnum.getDirection(answer.getAngle() + 180));
                return DirectionEnum.getDirection(answer.getAngle() + 180);
            } else if (smallestVal < 0 && answer.getDirection().equals("north")) {
                if (DEBUG) System.out.println(DirectionEnum.getDirection(answer.getAngle() + 180));
                return DirectionEnum.getDirection(answer.getAngle() + 180);
            } else if (smallestVal > 0 && answer.getDirection().equals("east")) {
                if (DEBUG) System.out.println(DirectionEnum.getDirection(answer.getAngle() + 180));
                return DirectionEnum.getDirection(answer.getAngle() + 180);
            } else if (smallestVal > 0 && answer.getDirection().equals("south")) {
                if (DEBUG) System.out.println(DirectionEnum.getDirection(answer.getAngle() + 180));
                return DirectionEnum.getDirection(answer.getAngle() + 180);
            }
            // add handling of 0
            if (DEBUG) System.out.println(DirectionEnum.getDirection(answer.getAngle()));
            return answer;
        }

        // situation 3.2: Guard is going to reach the corner of a boundary in a straight line
        if (targetHasBeenReached && (situationStage == 2)) {
            System.out.println("3.2");
            int x = agent.getX_position();
            int y = agent.getY_position();

            HashMap<Integer, DirectionEnum> distanceToBoundaries = new HashMap<>();
            distanceToBoundaries.put((x - eastBoundaryOfStandardized), DirectionEnum.EAST);
            distanceToBoundaries.put((x - westBoundaryOfStandardized), DirectionEnum.WEST);
            distanceToBoundaries.put((y - northBoundaryOfStandardized), DirectionEnum.NORTH);
            distanceToBoundaries.put((y - southBoundaryOfStandardized), DirectionEnum.SOUTH);

            int distToW = (x - westBoundaryOfStandardized);
            int distToE = (x - eastBoundaryOfStandardized);
            int distToN = (y - northBoundaryOfStandardized);
            int distToS = (y - southBoundaryOfStandardized);

            if (DEBUG) {
                System.out.println("Distance to east boundary: " + (x - eastBoundaryOfStandardized));
                System.out.println("Distance to west boundary: " + (x - westBoundaryOfStandardized));
                System.out.println("Distance to north boundary: " + (y - northBoundaryOfStandardized));
                System.out.println("Distance to south boundary: " + (y - southBoundaryOfStandardized));
            }

            for (java.util.Map.Entry<Integer, DirectionEnum> entry : distanceToBoundaries.entrySet()) {
                if (entry.getKey() == 0) {
                    situationStage = 3;
                    if (DEBUG) {
                        System.out.println("previous dir of agent: " + DirectionEnum.getDirection(agent.getAngle()));
                        System.out.println("new dir: " + DirectionEnum.getDirection(agent.getAngle() + addDegreeFor3_2).getDirection());
                    }
                    if (distToW == 0) { // on west border
                        if (abs(distToS) < abs(distToN)) { // closer to south
                            if (distToS > 0) {
                                return DirectionEnum.NORTH;
                            } else {
                                return DirectionEnum.SOUTH;
                            }
                        } else { // closer to north
                            if (distToN > 0) {
                                return DirectionEnum.NORTH;
                            } else {
                                return DirectionEnum.SOUTH;
                            }
                        }
                    } else if (distToN == 0) { // on north border
                        if (abs(distToW) < abs(distToE)) { // closer to west
                            if (distToW > 0) {
                                return DirectionEnum.WEST;
                            } else {
                                return DirectionEnum.EAST;
                            }
                        } else { // closer to east
                            if (distToW > 0) {
                                return DirectionEnum.WEST;
                            } else {
                                return DirectionEnum.EAST;
                            }
                        }
                    } else if (distToE == 0) { // on east border
                        if (abs(distToS) < abs(distToN)) { // closer to south
                            if (distToS > 0) {
                                return DirectionEnum.NORTH;
                            } else {
                                return DirectionEnum.SOUTH;
                            }
                        } else { // closer to north
                            if (distToN > 0) {
                                return DirectionEnum.NORTH;
                            } else {
                                return DirectionEnum.SOUTH;
                            }
                        }
                    } else if (distToS == 0) { // on north border
                        if (abs(distToW) < abs(distToE)) { // closer to west
                            if (distToW > 0) {
                                return DirectionEnum.WEST;
                            } else {
                                return DirectionEnum.EAST;
                            }
                        } else { // closer to east
                            if (distToW > 0) {
                                return DirectionEnum.WEST;
                            } else {
                                return DirectionEnum.EAST;
                            }
                        }
                    }
                }
            }
            if (DEBUG) System.out.println(DirectionEnum.getDirection(agent.getAngle()).getDirection());
            return DirectionEnum.getDirection(agent.getAngle()); // going straight
        }

        // situation 3.3: Guard is going to the corner of standardized area in a straight line
        if (targetHasBeenReached && (situationStage == 3)) {
            System.out.println("3.3");
            int x = agent.getX_position();
            int y = agent.getY_position();

            int distToW = (x - westBoundaryOfStandardized);
            int distToE = (x - eastBoundaryOfStandardized);
            int distToN = (y - northBoundaryOfStandardized);
            int distToS = (y - southBoundaryOfStandardized);

            if (DEBUG) {
                System.out.println("Distance to east boundary: " + (x - eastBoundaryOfStandardized));
                System.out.println("Distance to west boundary: " + (x - westBoundaryOfStandardized));
                System.out.println("Distance to north boundary: " + (y - northBoundaryOfStandardized));
                System.out.println("Distance to south boundary: " + (y - southBoundaryOfStandardized));
            }

            Tile closestTile = getClosestCorner(map.getTile(x, y));

            // checking if reached any of 4 corners
            if((distToW == 0 && distToN == 0) || (distToN == 0 && distToE == 0) || (distToE == 0 && distToS == 0) || (distToS == 0 && distToW == 0)) {
                if (!guardInLoop) {
                    guardInLoop = true;
                    patrolling = true;
                    situationStage = 4;
                    if (DirectionEnum.getDirection(agent.getAngle()) == DirectionEnum.WEST && previousDistToW < 0) {
                        return DirectionEnum.getDirection(agent.getAngle() - 90); // turning left
                    } else if (DirectionEnum.getDirection(agent.getAngle()) == DirectionEnum.WEST && previousDistToW > 0) {
                        return DirectionEnum.getDirection(agent.getAngle() + 90); // turning right
                    }

                    if (DirectionEnum.getDirection(agent.getAngle()) == DirectionEnum.NORTH && previousDistToN < 0) {
                        return DirectionEnum.getDirection(agent.getAngle() - 90);
                    } else if (DirectionEnum.getDirection(agent.getAngle()) == DirectionEnum.NORTH && previousDistToN > 0) {
                        return DirectionEnum.getDirection(agent.getAngle() + 90);
                    }

                    if (DirectionEnum.getDirection(agent.getAngle()) == DirectionEnum.EAST && previousDistToE < 0) {
                        return DirectionEnum.getDirection(agent.getAngle() + 90);
                    } else if (DirectionEnum.getDirection(agent.getAngle()) == DirectionEnum.EAST && previousDistToE > 0) {
                        return DirectionEnum.getDirection(agent.getAngle() - 90);
                    }

                    if (DirectionEnum.getDirection(agent.getAngle()) == DirectionEnum.SOUTH && previousDistToS < 0) {
                        return DirectionEnum.getDirection(agent.getAngle() + 90);
                    } else if (DirectionEnum.getDirection(agent.getAngle()) == DirectionEnum.SOUTH && previousDistToS > 0) {
                        return DirectionEnum.getDirection(agent.getAngle() - 90);
                    }
                }
            }

            if (!guardInLoop) {
                this.previousDistToW = distToW;
                this.previousDistToE = distToE;
                this.previousDistToN = distToN;
                this.previousDistToS = distToS;
            }

            System.out.println("aaa");
            if (DEBUG) System.out.println(DirectionEnum.getDirection(agent.getAngle()).getDirection());
            return DirectionEnum.getDirection(agent.getAngle());
        }

        // situation 3.4: Guard is patrolling along the border of standardized area
        if (patrolling && !invaderSeen) {
            int x = agent.getX_position();
            int y = agent.getY_position();

            int distToW = (x - westBoundaryOfStandardized);
            int distToE = (x - eastBoundaryOfStandardized);
            int distToN = (y - northBoundaryOfStandardized);
            int distToS = (y - southBoundaryOfStandardized);

            if((distToW == 0 && distToN == 0) || (distToN == 0 && distToE == 0) || (distToE == 0 && distToS == 0) || (distToS == 0 && distToW == 0)) {
                return DirectionEnum.getDirection(agent.getAngle() - 90); // turning right
            }
            return DirectionEnum.getDirection(agent.getAngle());
        }

        // situation 4: Guard sees invader while patrolling
        if (patrolling && invaderSeen) {
            // TODO guard.shout()
            return chasing(guard, getInvader(visibleTiles).getAgentPosition());
        }

        // situation 4.1: Guard chasing invader while knowing where TA is
        if (targetHasBeenReached && invaderSeen) {
            // TODO guard.shout()

            Tile guardPosition = map.getTile(guard.getX_position(), guard.getY_position());
            int temp = getDistanceToClosestCorner(guardPosition);
            if (temp > 10) {
                invaderSeen = false;
                situationStage = 1;
                // for now let's just return a random move before going back to our standardized area
                return randomMove(guardPosition);
            } else {
                return chasing(guard, getInvader(visibleTiles).getAgentPosition());
            }
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

    public DirectionEnum randomMove(Tile agentTile){
        List<DirectionEnum> validMoves = new ArrayList<>();

        if (map.getTile(agentTile.getX() + 1, agentTile.getY()).isWalkable()){
            validMoves.add(DirectionEnum.EAST);
        }
        if (map.getTile(agentTile.getX() - 1, agentTile.getY()).isWalkable()){
            validMoves.add(DirectionEnum.WEST);
        }
        if (map.getTile(agentTile.getX(), agentTile.getY() - 1).isWalkable()){
            validMoves.add(DirectionEnum.NORTH);
        }
        if (map.getTile(agentTile.getX(), agentTile.getY() + 1).isWalkable()){
            validMoves.add(DirectionEnum.SOUTH);
        }
        return validMoves.get(r.nextInt(validMoves.size()));
    }

    private Tile getClosestCorner(Tile agentTile) {
        Tile closestTile;
        int smallestVal = agentTile.manhattanDist(northEastCorner);
        closestTile = northEastCorner;

        if (abs(agentTile.manhattanDist(northWestCorner)) < abs(smallestVal)) {
            smallestVal = agentTile.manhattanDist(northWestCorner);
            closestTile = northWestCorner;

        }
        if (abs(agentTile.manhattanDist(southWestCorner)) < abs(smallestVal)) {
            smallestVal = agentTile.manhattanDist(southWestCorner);
            closestTile = southWestCorner;
        }
        if (abs(agentTile.manhattanDist(southEastCorner)) < abs(smallestVal)) {
            smallestVal = agentTile.manhattanDist(southEastCorner);
            closestTile = southEastCorner;
        }
        return closestTile;
    }

    private int getDistanceToClosestCorner(Tile agentTile) {
        int smallestVal = agentTile.manhattanDist(northEastCorner);

        if (abs(agentTile.manhattanDist(northWestCorner)) < abs(smallestVal)) {
            smallestVal = agentTile.manhattanDist(northWestCorner);

        }
        if (abs(agentTile.manhattanDist(southWestCorner)) < abs(smallestVal)) {
            smallestVal = agentTile.manhattanDist(southWestCorner);
        }
        if (abs(agentTile.manhattanDist(southEastCorner)) < abs(smallestVal)) {
            smallestVal = agentTile.manhattanDist(southEastCorner);
        }
        return smallestVal;
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

        if(guardsX < intruderX) {
            System.out.println("East");
            if(map.getTile(agent.getX_position()+1, agent.getY_position()).isWalkable()) {
                dirs.add(DirectionEnum.EAST);
            }
        }
        else if(guardsX > intruderX) {
            System.out.println("West");
            if(map.getTile(agent.getX_position()-1, agent.getY_position()).isWalkable()) {
                dirs.add(DirectionEnum.WEST);
            }
        }

        if (guardsY < intruderY) {
            System.out.println("South");
            if(map.getTile(agent.getX_position(), agent.getY_position()+1).isWalkable()) {
                dirs.add(DirectionEnum.SOUTH);
            }
        }
        else if (guardsY > intruderY) {
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

}


// thrash of 3.1:
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


// second 3 trash:
// situation 3: (where QL will be added)
// situation 3.1: agent sees the target area first time, go towards to closest border
//        if (targetHasBeenReached && (situationStage == 1)) {
//                if (DEBUG) System.out.println("3.1");
//                int x = agent.getX_position();
//                int y = agent.getY_position();
//
//                if (DEBUG) {
//                System.out.println("Distance to east boundary: " + (x - eastBoundaryOfStandardized));
//                System.out.println("Distance to west boundary: " + (x - westBoundaryOfStandardized));
//                System.out.println("Distance to north boundary: " + (y - northBoundaryOfStandardized));
//                System.out.println("Distance to south boundary: " + (y - southBoundaryOfStandardized));
//                }
//
//                DirectionEnum answer = DirectionEnum.EAST;
//                int smallestVal = (x - eastBoundaryOfStandardized);
//                if (abs((x - westBoundaryOfStandardized)) < abs(x - eastBoundaryOfStandardized)) {
//        answer = DirectionEnum.WEST;
//        smallestVal = (x - westBoundaryOfStandardized);
//        }
//        if (abs((x - northBoundaryOfStandardized)) < abs(smallestVal)) {
//        answer = DirectionEnum.NORTH;
//        smallestVal = (x - northBoundaryOfStandardized);
//        }
//        if (abs((x - southBoundaryOfStandardized)) < abs(smallestVal)) {
//        answer = DirectionEnum.SOUTH;
//        smallestVal = (x - southBoundaryOfStandardized);
//        }
//
//        situationStage = 2;
//        if (DEBUG) System.out.println(answer.getDirection());
//
//        if (smallestVal < 0) {
//        return DirectionEnum.getDirection(answer.getAngle() + 180);
//        } // add handling of 0
//        return answer;
//        }
//
//        // situation 3.2: Guard is going to the border of standardized area in a straight line
//        if (targetHasBeenReached && (situationStage == 2)) {
//        System.out.println("3.2");
//        int x = agent.getX_position();
//        int y = agent.getY_position();
//
//        HashMap<Integer, DirectionEnum> distanceToBoundaries = new HashMap<>();
//        distanceToBoundaries.put((x - eastBoundaryOfStandardized), DirectionEnum.EAST);
//        distanceToBoundaries.put((x - westBoundaryOfStandardized), DirectionEnum.WEST);
//        distanceToBoundaries.put((y - northBoundaryOfStandardized), DirectionEnum.NORTH);
//        distanceToBoundaries.put((y - southBoundaryOfStandardized), DirectionEnum.SOUTH);
//
//        if (DEBUG) {
//        System.out.println("Distance to east boundary: " + (x - eastBoundaryOfStandardized));
//        System.out.println("Distance to west boundary: " + (x - westBoundaryOfStandardized));
//        System.out.println("Distance to north boundary: " + (y - northBoundaryOfStandardized));
//        System.out.println("Distance to south boundary: " + (y - southBoundaryOfStandardized));
//        }
//
//        for (java.util.Map.Entry<Integer, DirectionEnum> entry : distanceToBoundaries.entrySet()) {
//        if (entry.getKey() == 0) {
//        situationStage = 3;
//        if (DEBUG) {
//        System.out.println("previous dir of agent: " + DirectionEnum.getDirection(agent.getAngle()));
//        System.out.println("new dir: " + DirectionEnum.getDirection(agent.getAngle() + addDegreeFor3_2).getDirection());
//        }
//        return DirectionEnum.getDirection(agent.getAngle() + addDegreeFor3_2); // turning it to right
//        }
//        }
//        if (DEBUG) System.out.println(DirectionEnum.getDirection(agent.getAngle()).getDirection());
//        return DirectionEnum.getDirection(agent.getAngle()); // going straight
//        }
//
//        // situation 3.3: Guard is patrolling along the border of standardized area
//        if (targetHasBeenReached && (situationStage == 3)) {
//        System.out.println("3.3");
//        int x = agent.getX_position();
//        int y = agent.getY_position();
//
//        int[] list = new int[4];
//        list[0] = abs(x - eastBoundaryOfStandardized);
//        list[1] = abs(x - westBoundaryOfStandardized);
//        list[2] = abs(y - northBoundaryOfStandardized);
//        list[3] = abs(y - southBoundaryOfStandardized);
//
//        if (DEBUG) {
//        System.out.println("Distance to east boundary: " + (x - eastBoundaryOfStandardized));
//        System.out.println("Distance to west boundary: " + (x - westBoundaryOfStandardized));
//        System.out.println("Distance to north boundary: " + (y - northBoundaryOfStandardized));
//        System.out.println("Distance to south boundary: " + (y - southBoundaryOfStandardized));
//        }
//
//        int numberOfZeroDistance = 0;
//
//        for (int temp: list) {
//        if (temp == 0) {
//        numberOfZeroDistance++;
//        }
//        }
//
//        if (numberOfZeroDistance > 1) {
//        if (DEBUG) {
//        System.out.println("previous dir of agent: " + DirectionEnum.getDirection(agent.getAngle()));
//        System.out.println("new dir: " + DirectionEnum.getDirection(agent.getAngle() - 90).getDirection());
//        }
//        return DirectionEnum.getDirection(agent.getAngle() - 90); // turning right
//        }
//        if (DEBUG) System.out.println(DirectionEnum.getDirection(agent.getAngle()).getDirection());
//        return DirectionEnum.getDirection(agent.getAngle());
//        }
