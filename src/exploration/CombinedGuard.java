package exploration;

import agents.Agent;
import agents.Guard;
import agents.Intruder;
import controller.Map.Map;
import controller.Map.tiles.Tile;
import controller.Scenario;
import utils.DirectionEnum;
import utils.Path;
import java.util.*;
import static java.lang.Math.abs;

public class CombinedGuard extends FrontierBasedExploration {

    public Map map;

    public Agent agent;
    public FrontierBasedExploration frontierExploration;
    public QLGuard qlGuard;
    private boolean targetHasBeenReached = false;
    private boolean invaderSeen = false;
    private boolean patrolling = false;

    private final Queue<Tile> cornersOfStandardized = new LinkedList<Tile>();

    private boolean useQL = false;
    private boolean isChasing = false;
    private int timePassedAfterSeeingInvader = 0;
    private int situationStageOf3 = 1;
    private int situationStageOf4 = 1;
    private Random r = new Random();

    private final int northBoundaryOfStandardized;
    private final int southBoundaryOfStandardized;
    private final int westBoundaryOfStandardized;
    private final int eastBoundaryOfStandardized;

    private final Tile northEastCorner;
    private final Tile northWestCorner;
    private final Tile southWestCorner;
    private final Tile southEastCorner;

    private int previousDistToW = 0;
    private int previousDistToN = 0;
    private int previousDistToE = 0;
    private int previousDistToS = 0;

    private int distanceToOtherGuards = 0;


    // assuming we know the boundaries of standardized area
    public CombinedGuard(Agent agent, Map map, int northBoundaryOfStandardized, int southBoundaryOfStandardized, int westBoundaryOfStandardized, int eastBoundaryOfStandardized) {
        super(agent, map);

        this.map = map;
        this.agent = agent;

        this.distanceToOtherGuards = Math.floorDiv(((eastBoundaryOfStandardized - westBoundaryOfStandardized) * 2 + (southBoundaryOfStandardized - northBoundaryOfStandardized) * 2), Scenario.config.getNumOfGuards());

        this.northBoundaryOfStandardized = northBoundaryOfStandardized;
        this.southBoundaryOfStandardized = southBoundaryOfStandardized;
        this.westBoundaryOfStandardized = westBoundaryOfStandardized;
        this.eastBoundaryOfStandardized = eastBoundaryOfStandardized;
        // getting the corner tiles
        this.northEastCorner = map.getTile(eastBoundaryOfStandardized, northBoundaryOfStandardized);
        this.northWestCorner = map.getTile(westBoundaryOfStandardized, northBoundaryOfStandardized);
        this.southWestCorner = map.getTile(westBoundaryOfStandardized, southBoundaryOfStandardized);
        this.southEastCorner = map.getTile(eastBoundaryOfStandardized, southBoundaryOfStandardized);

        cornersOfStandardized.add(northEastCorner);
        cornersOfStandardized.add(northWestCorner);
        cornersOfStandardized.add(southWestCorner);
        cornersOfStandardized.add(southEastCorner);


        this.frontierExploration = new FrontierBasedExploration(agent, map);
        this.qlGuard = new QLGuard(agent, map);
    }

    /**
     * This class handles 4 situations: (new situations can be added further)
     * <p>
     * Situation 1: Guard has not seen TA or Invader yet, so keep exploring. Return frontierExploration move
     * <p>
     * Situation 2: Guard has seen invader whilst not seeing TA, start to chase
     * - Situation 2.1: Guard is chasing invader without the knowledge of TA, return chasing method
     * <p>
     * Situation 3: Guard has seen target area but not the intruder, start patrolling (either with baseline patrolling or QL)
     * The situations mentioned below is for baseline patrolling, if QL is to be used then these situations can be skipped
     * - Situation 3.1: Turn guard towards to the closest border
     * - Situation 3.2: Make guard reach the corner of a boundary by going in a straight line
     * - Situation 3.3: Make guard go to the corner of standardized area in a straight line
     * - Situation 3.4: Make guard patrol along the border of standardized area
     * <p>
     * Situation 4: Guard sees invader while patrolling
     * - Situation 4.1: Chase intruder, if distance to the closest corner becomes over 10 then give up and return to TA
     * (?further situations could be added here?)
     * (shout (check if there exists another guard) and pursue invader)
     * (disregard)
     * ...
     */
    @Override
    public DirectionEnum makeMove(Agent agent) {
        boolean DEBUG = true;
        int x = agent.getX_position();
        int y = agent.getY_position();

        ArrayList<Tile> visibleTiles = agent.getVisibleTiles();
        updateKnowledge(agent, visibleTiles);

        if (DEBUG) {
            System.out.println();
            System.out.println("Location of guard: at x:" + x + ", y:" + y);
        }

        // if guard sees TA set "targetHasBeenReached" true and never change back to false
        if (!targetHasBeenReached) {
            targetHasBeenReached = checkTargetArea(visibleTiles);
        }

        // this means we were chasing an intruder and have captured it, so go back to TA (situation 3.1)
        if (invaderSeen && !checkInvader(visibleTiles)) {
            situationStageOf3 = 1;
        }
        // checking if agent sees intruder
        invaderSeen = checkInvader(visibleTiles);

        // situation 1: guard has not seen TA or Invader yet, so keep exploring
        if (!targetHasBeenReached && !invaderSeen && !isChasing) {
            System.out.println("1");
            if (DEBUG) {
                System.out.println(frontierExploration.makeMove(this.agent).getDirection());
            }
            return frontierExploration.makeMove(this.agent);
        }

        // situation 2: guard has seen invader whilst not seeing TA, start to chase
        if (!targetHasBeenReached && invaderSeen && !isChasing) {
            System.out.println("2");
            isChasing = true;
            if (DEBUG) {
                System.out.println(chasing(agent, getInvader(visibleTiles).getAgentPosition()).getDirection());
            }
            return chasing(agent, getInvader(visibleTiles).getAgentPosition());
        }

        // situation 2.1: guard is chasing invader without the knowledge of TA
        if (!targetHasBeenReached && isChasing) {
            System.out.println("2.1");
            if (DEBUG) {
                // System.out.println(chasing(guard, getInvader(visibleTiles).getAgentPosition()).getDirection());
            }
            if (checkInvader(visibleTiles)) { // meaning again saw the intruder, set counter to 0
                timePassedAfterSeeingInvader = 0;
            } else {
                timePassedAfterSeeingInvader++;
            }
            // meaning 15 timesteps has passed since last time of seeing invader, then give up and start exploring
            if (timePassedAfterSeeingInvader > 15) {
                isChasing = false;
                return makeMove(agent);
            } else {
                if (!checkInvader((visibleTiles))) {
                    return frontierExploration.makeMove(this.agent);
                } else {
                    return chasing(agent, getInvader(visibleTiles).getAgentPosition());
                }
            }
        }

        // situation 3:

        // situation 3.1: Guard sees the target area first time
        if (targetHasBeenReached && (situationStageOf3 == 1)) {
            System.out.println("3.1");

            Path path = findPath(agent, cornersOfStandardized, false);
            if (path.size() == 2) { // setting to 1 so that guard is turned to right direction in situation 3.2 (so 1 move away from goal)
                situationStageOf3 = 2;
                previousDistToW = (x - westBoundaryOfStandardized);
                previousDistToE = (x - eastBoundaryOfStandardized);
                previousDistToN = (y - northBoundaryOfStandardized);
                previousDistToS = (y - southBoundaryOfStandardized);
            }
            if (DEBUG) System.out.println(findNextMoveDirection(agent, path.get(1)));
            return findNextMoveDirection(agent, path.get(1));
        }

        // situation 3.3: Guard is going to the corner of standardized area
        if (targetHasBeenReached && (situationStageOf3 == 2)) {
            System.out.println("3.2");

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

            // checking if reached any of 4 corners (meaning distance to any 2 borders being 0)
            if ((distToW == 0 && distToN == 0) || (distToN == 0 && distToE == 0) || (distToE == 0 && distToS == 0) || (distToS == 0 && distToW == 0)) {
                patrolling = true; // start patrolling after reaching corner
                situationStageOf3 = -1;

                // for northwest corner
                if ((distToW == 0 && distToN == 0)) {
                    if (DEBUG) System.out.println("reached northwest corner");
                    if (previousDistToN < 0) { // coming from north
                        return DirectionEnum.getDirection(agent.getAngle() + 90); // turning left
                    } else if (previousDistToN > 0) { // coming from south
                        return DirectionEnum.getDirection(agent.getAngle() - 90); // turning right
                    } else if (previousDistToW < 0) { // coming from west
                        return DirectionEnum.getDirection(agent.getAngle()); // go straight
                    } else if (previousDistToW > 0) { // coming from east
                        return DirectionEnum.getDirection(agent.getAngle() + 180); // u-turn
                    }
                }

                // for northeast corner
                if ((distToE == 0 && distToN == 0)) {
                    if (DEBUG) System.out.println("reached northeast corner");
                    if (previousDistToN < 0) { // coming from north
                        return DirectionEnum.getDirection(agent.getAngle());
                    } else if (previousDistToN > 0) { // coming from south
                        return DirectionEnum.getDirection(agent.getAngle() + 180);
                    } else if (previousDistToE < 0) { // coming from west
                        return DirectionEnum.getDirection(agent.getAngle() - 90);
                    } else if (previousDistToE > 0) { // coming from east
                        return DirectionEnum.getDirection(agent.getAngle() + 90);
                    }
                }

                // for southeast corner
                if ((distToS == 0 && distToE == 0)) {
                    if (DEBUG) System.out.println("reached southeast corner");
                    if (previousDistToS < 0) { // coming from north
                        return DirectionEnum.getDirection(agent.getAngle() - 90);
                    } else if (previousDistToS > 0) { // coming from south
                        return DirectionEnum.getDirection(agent.getAngle() + 90);
                    } else if (previousDistToE < 0) { // coming from west
                        return DirectionEnum.getDirection(agent.getAngle() + 180);
                    } else if (previousDistToE > 0) { // coming from east
                        return DirectionEnum.getDirection(agent.getAngle());
                    }
                }

                // for southwest corner
                if ((distToS == 0 && distToW == 0)) {
                    if (DEBUG) System.out.println("reached southwest corner");
                    if (previousDistToS < 0) { // coming from north
                        return DirectionEnum.getDirection(agent.getAngle() + 180);
                    } else if (previousDistToS > 0) { // coming from south
                        return DirectionEnum.getDirection(agent.getAngle());
                    } else if (previousDistToW < 0) { // coming from west
                        return DirectionEnum.getDirection(agent.getAngle() + 90);
                    } else if (previousDistToW > 0) { // coming from east
                        return DirectionEnum.getDirection(agent.getAngle() - 90);
                    }
                }
            }


            previousDistToW = distToW;
            previousDistToE = distToE;
            previousDistToN = distToN;
            previousDistToS = distToS;

            if (DEBUG) System.out.println(DirectionEnum.getDirection(agent.getAngle()).getDirection());
            return DirectionEnum.getDirection(agent.getAngle());
        }

        if (!useQL) { // do baseline patrolling TODO, handle multiple guards
            // situation 3.3: Guard is patrolling along the border of standardized area
            if (patrolling && !invaderSeen) {

                System.out.println("3.3: Baseline Patrolling");

                int distToW = (x - westBoundaryOfStandardized);
                int distToE = (x - eastBoundaryOfStandardized);
                int distToN = (y - northBoundaryOfStandardized);
                int distToS = (y - southBoundaryOfStandardized);

                // if reaches corner then turn right
                if ((distToW == 0 && distToN == 0) || (distToN == 0 && distToE == 0) || (distToE == 0 && distToS == 0) || (distToS == 0 && distToW == 0)) {
                    if (DEBUG) System.out.println(DirectionEnum.getDirection(agent.getAngle() - 90));
                    return DirectionEnum.getDirection(agent.getAngle() - 90); // turning right
                }
                if (DEBUG) System.out.println(DirectionEnum.getDirection(agent.getAngle()));
                return DirectionEnum.getDirection(agent.getAngle());
            }

        } else {
            // situation 3.3: Guard is patrolling along the border of standardized area by using QL
            if (patrolling && !invaderSeen) {
                System.out.println("3.3: QL patrolling");

                return qlGuard.makeMove(agent);
            }
        }


        // situation 4: Guard sees invader while patrolling

        // situation 4.1: Shout then follow the intruder
        if (patrolling && invaderSeen && (situationStageOf4 == 1)) {
            System.out.println("4.1");
            // TODO guard.shout()
            patrolling = false;
            isChasing = true;
            situationStageOf4 = 2;
            return chasing(agent, getInvader(visibleTiles).getAgentPosition());
        }

        // situation 4.2: Guard chasing invader while knowing where TA is
        if (!patrolling && targetHasBeenReached && isChasing && (situationStageOf4 == 2)) {
            System.out.println("4.2");

            int temp = getDistanceToClosestCorner(map.getTile(x, y));
            if (temp > 15) { // if guard goes too far from TA return
                // reseting params
                invaderSeen = false;
                isChasing = false;
                situationStageOf3 = 1;
                situationStageOf4 = 1;
                // for now let's just return a random move before going back to our standardized area
                Path path = findPath(agent, cornersOfStandardized, false);
                return findNextMoveDirection(agent, path.get(1));
            } else {
                if (!checkInvader((visibleTiles))) {
                    return frontierExploration.makeMove(this.agent);
                } else {
                    return chasing(agent, getInvader(visibleTiles).getAgentPosition());
                }
            }
        }

        System.out.println("returning null, targetHasBeenReached: "+targetHasBeenReached+ "invaderSeen: "+isChasing);
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

    public DirectionEnum randomMove(Tile agentTile) {
        List<DirectionEnum> validMoves = new ArrayList<>();

        if (map.getTile(agentTile.getX() + 1, agentTile.getY()).isWalkable()) {
            validMoves.add(DirectionEnum.EAST);
        }
        if (map.getTile(agentTile.getX() - 1, agentTile.getY()).isWalkable()) {
            validMoves.add(DirectionEnum.WEST);
        }
        if (map.getTile(agentTile.getX(), agentTile.getY() - 1).isWalkable()) {
            validMoves.add(DirectionEnum.NORTH);
        }
        if (map.getTile(agentTile.getX(), agentTile.getY() + 1).isWalkable()) {
            validMoves.add(DirectionEnum.SOUTH);
        }
        return validMoves.get(r.nextInt(validMoves.size()));
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
            Agent foundAgent = tile.getAgent();
            if ((foundAgent != null) && foundAgent.getType().equals("Intruder")) {
                return (Intruder) foundAgent;
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

        if (guardsX < intruderX) {
            System.out.println("East");
            if (map.getTile(agent.getX_position() + 1, agent.getY_position()).isWalkable()) {
                dirs.add(DirectionEnum.EAST);
            }
        } else if (guardsX > intruderX) {
            System.out.println("West");
            if (map.getTile(agent.getX_position() - 1, agent.getY_position()).isWalkable()) {
                dirs.add(DirectionEnum.WEST);
            }
        }

        if (guardsY < intruderY) {
            System.out.println("South");
            if (map.getTile(agent.getX_position(), agent.getY_position() + 1).isWalkable()) {
                dirs.add(DirectionEnum.SOUTH);
            }
        } else if (guardsY > intruderY) {
            System.out.println("North");
            if (map.getTile(agent.getX_position(), agent.getY_position() - 1).isWalkable()) {
                dirs.add(DirectionEnum.NORTH);
            }
        }

        if (!dirs.isEmpty()) {
            System.out.println(dirs.size());
            return dirs.get(r.nextInt(dirs.size()));
        } else {
            return null;
        }
    }
}

//    // first add actual dir if walkable, then right if walkable, then left if walkable
//    public DirectionEnum checkIfWall(DirectionEnum givenDir, Tile agentTile) {
//        List<DirectionEnum> validMoves = new ArrayList<>();
//
//        if (givenDir == DirectionEnum.NORTH) {
//            if (map.getTile(agentTile.getX(), agentTile.getY() - 1).isWalkable()){
//                validMoves.add(DirectionEnum.NORTH);
//            } else if (map.getTile(agentTile.getX() + 1, agentTile.getY()).isWalkable()){
//                validMoves.add(DirectionEnum.EAST);
//            } else if (map.getTile(agentTile.getX() - 1, agentTile.getY()).isWalkable()){
//                validMoves.add(DirectionEnum.WEST);
//            }
//        }
//
//        if (givenDir == DirectionEnum.EAST) {
//            if (map.getTile(agentTile.getX(), agentTile.getY() - 1).isWalkable()){
//                validMoves.add(DirectionEnum.EAST);
//            } else if (map.getTile(agentTile.getX() + 1, agentTile.getY()).isWalkable()){
//                validMoves.add(DirectionEnum.SOUTH);
//            } else if (map.getTile(agentTile.getX() - 1, agentTile.getY()).isWalkable()){
//                validMoves.add(DirectionEnum.NORTH);
//            }
//        }
//
//        if (givenDir == DirectionEnum.SOUTH) {
//            if (map.getTile(agentTile.getX(), agentTile.getY() - 1).isWalkable()){
//                validMoves.add(DirectionEnum.SOUTH);
//            } else if (map.getTile(agentTile.getX() + 1, agentTile.getY()).isWalkable()){
//                validMoves.add(DirectionEnum.WEST);
//            } else if (map.getTile(agentTile.getX() - 1, agentTile.getY()).isWalkable()){
//                validMoves.add(DirectionEnum.EAST);
//            }
//        }
//
//        if (givenDir == DirectionEnum.WEST) {
//            if (map.getTile(agentTile.getX(), agentTile.getY() - 1).isWalkable()){
//                validMoves.add(DirectionEnum.WEST);
//            } else if (map.getTile(agentTile.getX() + 1, agentTile.getY()).isWalkable()){
//                validMoves.add(DirectionEnum.NORTH);
//            } else if (map.getTile(agentTile.getX() - 1, agentTile.getY()).isWalkable()){
//                validMoves.add(DirectionEnum.SOUTH);
//            }
//        }
//
//        return validMoves.get(0);
//    }
//
//    public List<DirectionEnum> getDirCloserToCorner(Tile agentTile, Tile cornerTile) {
//        List<DirectionEnum> validMoves = new ArrayList<>();
//
//        int currentManDistance = agentTile.manhattanDist(cornerTile);
//
//        if (map.getTile(agentTile.getX(), agentTile.getY() - 1).manhattanDist(cornerTile) < currentManDistance){ // north
//            validMoves.add(DirectionEnum.NORTH);
//        } else if (map.getTile(agentTile.getX() + 1, agentTile.getY()).manhattanDist(cornerTile) < currentManDistance){ // north
//            validMoves.add(DirectionEnum.EAST);
//        } else if (map.getTile(agentTile.getX(), agentTile.getY() + 1).manhattanDist(cornerTile) < currentManDistance){ // north
//            validMoves.add(DirectionEnum.SOUTH);
//        } else if (map.getTile(agentTile.getX() - 1, agentTile.getY()).manhattanDist(cornerTile) < currentManDistance){ // north
//            validMoves.add(DirectionEnum.WEST);
//        }
//        return validMoves;
//    }
//
//    public Tile applyDir(Tile currentTile, DirectionEnum givenDir) {
//        if (givenDir == DirectionEnum.NORTH) {
//            return map.getTile(currentTile.getX(), currentTile.getY() - 1);
//        } else if (givenDir == DirectionEnum.EAST) {
//            return map.getTile(currentTile.getX() + 1, currentTile.getY());
//        } else if (givenDir == DirectionEnum.SOUTH) {
//            return map.getTile(currentTile.getX(), currentTile.getY() + 1);
//        } else if (givenDir == DirectionEnum.WEST) {
//            return map.getTile(currentTile.getX() - 1, currentTile.getY());
//        }
//        return null;
//    }
//
//}


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
// situation 3:

//        // situation 3.1: Guard sees the target area first time, turn towards to closest border
//        if (targetHasBeenReached && (situationStageOf3 == 1)) {
//            // checking if guard already on a border
//            if (!(((x - eastBoundaryOfStandardized) == 0) || ((x - westBoundaryOfStandardized) == 0) || ((y - northBoundaryOfStandardized) == 0) || ((y - southBoundaryOfStandardized) == 0))) {
//                if (DEBUG) System.out.println("3.1");
//
//                if (DEBUG) {
//                    System.out.println("Distance to east boundary: " + (x - eastBoundaryOfStandardized));
//                    System.out.println("Distance to west boundary: " + (x - westBoundaryOfStandardized));
//                    System.out.println("Distance to north boundary: " + (y - northBoundaryOfStandardized));
//                    System.out.println("Distance to south boundary: " + (y - southBoundaryOfStandardized));
//                }
//
//
//                DirectionEnum answer = DirectionEnum.EAST;
//                int smallestVal = (x - eastBoundaryOfStandardized);
//
//                if (abs((x - westBoundaryOfStandardized)) < abs((x - eastBoundaryOfStandardized))) {
//                    answer = DirectionEnum.WEST;
//                    smallestVal = (x - westBoundaryOfStandardized);
//                }
//                if (abs((y - northBoundaryOfStandardized)) < abs(smallestVal)) {
//                    answer = DirectionEnum.NORTH;
//                    smallestVal = (y - northBoundaryOfStandardized);
//                }
//                if (abs((y - southBoundaryOfStandardized)) < abs(smallestVal)) {
//                    answer = DirectionEnum.SOUTH;
//                    smallestVal = (y - southBoundaryOfStandardized);
//                }
//
//                situationStageOf3 = 2;
//
//                if (smallestVal < 0 && answer.getDirection().equals("west")) {
//                    if (DEBUG) System.out.println(DirectionEnum.getDirection(answer.getAngle() + 180));
//                    answer = DirectionEnum.getDirection(answer.getAngle() + 180);
//                } else if (smallestVal < 0 && answer.getDirection().equals("north")) {
//                    if (DEBUG) System.out.println(DirectionEnum.getDirection(answer.getAngle() + 180));
//                    answer = DirectionEnum.getDirection(answer.getAngle() + 180);
//                } else if (smallestVal > 0 && answer.getDirection().equals("east")) {
//                    if (DEBUG) System.out.println(DirectionEnum.getDirection(answer.getAngle() + 180));
//                    answer = DirectionEnum.getDirection(answer.getAngle() + 180);
//                } else if (smallestVal > 0 && answer.getDirection().equals("south")) {
//                    if (DEBUG) System.out.println(DirectionEnum.getDirection(answer.getAngle() + 180));
//                    answer =  DirectionEnum.getDirection(answer.getAngle() + 180);
//                }
//                if (DEBUG) System.out.println(answer);
//                return checkIfWall(answer, map.getTile(x, y));
//            } else { // meaning guard is already on a border skip situation 3.1
//                situationStageOf3 = 2;
//
//                // fixes the problem of missing previous data if it goes directly go to situation 3.3
//                previousDistToW = (x - westBoundaryOfStandardized);
//                previousDistToE = (x - eastBoundaryOfStandardized);
//                previousDistToN = (y - northBoundaryOfStandardized);
//                previousDistToS = (y - southBoundaryOfStandardized);
//            }
//        }
//
//        // situation 3.2: Guard is going to reach the corner of a boundary in a straight line
//        if (targetHasBeenReached && (situationStageOf3 == 2)) {
//            System.out.println("3.2");
//            System.out.println("situation stage is" + situationStageOf3);
//
//            DirectionEnum answer = DirectionEnum.getDirection(agent.getAngle());
//
//            int distToW = (x - westBoundaryOfStandardized);
//            int distToE = (x - eastBoundaryOfStandardized);
//            int distToN = (y - northBoundaryOfStandardized);
//            int distToS = (y - southBoundaryOfStandardized);
//
//            // fixes the problem of missing previous data if it goes directly go to situation 3.3
//            previousDistToW = distToW;
//            previousDistToE = distToE;
//            previousDistToN = distToN;
//            previousDistToS = distToS;
//
//            if (DEBUG) {
//                System.out.println("Distance to east boundary: " + (x - eastBoundaryOfStandardized));
//                System.out.println("Distance to west boundary: " + (x - westBoundaryOfStandardized));
//                System.out.println("Distance to north boundary: " + (y - northBoundaryOfStandardized));
//                System.out.println("Distance to south boundary: " + (y - southBoundaryOfStandardized));
//            }
//
//            // has reached a border
//            if ((distToW == 0) || (distToE == 0) || (distToN == 0) || (distToS == 0)) {
//                situationStageOf3 = 3;
//                if (DEBUG) {
//                    System.out.println("previous dir of agent: " + DirectionEnum.getDirection(agent.getAngle()));
//                    System.out.println("new dir: " + DirectionEnum.getDirection(agent.getAngle()).getDirection());
//                }
//                if (distToW == 0) { // on west border
//                    if (abs(distToS) < abs(distToN)) { // closer to south
//                        if (distToS > 0) {
//                            answer = DirectionEnum.NORTH;
//                        } else {
//                            answer = DirectionEnum.SOUTH;
//                        }
//                    } else { // closer to north
//                        if (distToN > 0) {
//                            answer = DirectionEnum.NORTH;
//                        } else {
//                            answer = DirectionEnum.SOUTH;
//                        }
//                    }
//                } else if (distToN == 0) { // on north border
//                    if (abs(distToW) < abs(distToE)) { // closer to west
//                        if (distToW > 0) {
//                            answer = DirectionEnum.WEST;
//                        } else {
//                            answer = DirectionEnum.EAST;
//                        }
//                    } else { // closer to east
//                        if (distToE > 0) {
//                            answer = DirectionEnum.WEST;
//                        } else {
//                            answer = DirectionEnum.EAST;
//                        }
//                    }
//                } else if (distToE == 0) { // on east border
//                    if (abs(distToS) < abs(distToN)) { // closer to south
//                        if (distToS > 0) {
//                            answer = DirectionEnum.NORTH;
//                        } else {
//                            answer = DirectionEnum.SOUTH;
//                        }
//                    } else { // closer to north
//                        if (distToN > 0) {
//                            answer = DirectionEnum.NORTH;
//                        } else {
//                            answer = DirectionEnum.SOUTH;
//                        }
//                    }
//                } else { // on south border
//                    if (abs(distToW) < abs(distToE)) { // closer to west
//                        if (distToW > 0) {
//                            answer = DirectionEnum.WEST;
//                        } else {
//                            answer = DirectionEnum.EAST;
//                        }
//                    } else { // closer to east
//                        if (distToE > 0) {
//                            answer = DirectionEnum.WEST;
//                        } else {
//                            answer = DirectionEnum.EAST;
//                        }
//                    }
//                }
//            }
//            if (DEBUG) System.out.println(DirectionEnum.getDirection(agent.getAngle()).getDirection());
//            return checkIfWall(answer, map.getTile(x, y)); // going straight
//        }
