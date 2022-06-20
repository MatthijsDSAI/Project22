package exploration;

import agents.Agent;
import agents.Intruder;
import controller.Map.Map;
import controller.Map.tiles.Tile;
import controller.Scenario;
import javafx.scene.paint.Color;
import utils.DirectionEnum;

import java.util.*;
import static java.lang.Math.abs;

public class CombinedGuard extends FrontierBasedExploration {

    public Map map;
    Color[] c = {Color.RED, Color.ORANGE, Color.GREEN, Color.WHITE, null};

    public Agent agent;
    public FrontierBasedExploration frontierExploration;
    public QLGuard qlGuard;
    private boolean targetHasBeenReached = false;
    private boolean invaderSeen = false;
    private boolean patrolling = false;
    private boolean hasPlacedTAMarker = false;
    private boolean needToPlaceSecondMarker = false;
    private boolean qlGuardHasHalve = false;
    private boolean freeGuard = false;

    private final Queue<Tile> cornersOfStandardized = new LinkedList<>();

    private boolean useQL = true;
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
    private DirectionEnum lastDirIntruderHasBeenSeen;

    private int distanceToOtherGuards = 0;


    // assuming we know the boundaries of standardized area
    public CombinedGuard(Agent agent, Map map, int northBoundaryOfStandardized, int southBoundaryOfStandardized, int westBoundaryOfStandardized, int eastBoundaryOfStandardized) {
        super(agent, map);
        agent.createMarkers(5,6,c);
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
        boolean DEBUG = false;
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
            if(targetHasBeenReached) {
                agent.updateTargetArea();
                isChasing = false;
            }
        }

        // this means we were chasing an intruder and have captured it, so go back to TA (situation 3.1)
        if (invaderSeen && !checkInvader(visibleTiles)) {
            situationStageOf3 = 1;
        }
        // checking if agent sees intruder
        invaderSeen = checkInvader(visibleTiles);
        if (invaderSeen) {
            lastDirIntruderHasBeenSeen = DirectionEnum.getDirection(agent.getAngle());
        }

        // situation 1: guard has not seen TA or Invader yet, so keep exploring
        if (!targetHasBeenReached && !invaderSeen && !isChasing) {
            System.out.println("1");
            if(agent.findMarker()!=null){
                MarkerInterpretation(agent);
            }
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
                System.out.println(chasing(agent, visibleTiles, checkInvader(visibleTiles)).getDirection());
            }
            return chasing(agent, visibleTiles, checkInvader(visibleTiles));
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
                timePassedAfterSeeingInvader = 0;
                isChasing = false;
                return frontierExploration.makeMove(this.agent);
            } else {
                return chasing(agent, visibleTiles, checkInvader(visibleTiles));
            }
        }

        // situation 3:

        // situation 3.1: Guard sees the target area first time
        if (!isChasing && targetHasBeenReached && (situationStageOf3 == 1)) {
            System.out.println("3.1");

            this.curPath = findPathTargetArea(agent, cornersOfStandardized, false);
            if (this.curPath.size() == 2) { // setting to 1 so that guard is turned to right direction in situation 3.2 (so 1 move away from goal)
                situationStageOf3 = 2;
                previousDistToW = (x - westBoundaryOfStandardized);
                previousDistToE = (x - eastBoundaryOfStandardized);
                previousDistToN = (y - northBoundaryOfStandardized);
                previousDistToS = (y - southBoundaryOfStandardized);
                if (useQL) patrolling = true;
            }
            if (DEBUG) System.out.println(findNextMoveDirection(agent, this.curPath.get(1)));
            return findNextMoveDirection(agent, this.curPath.get(1));
        }

        // situation 3.2: Guard is going to the corner of standardized area
        if (!useQL) { // TODO, handle multiple guards
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

                // if on both corners
                if (distToW == 0 && distToN == 0) {
                    return DirectionEnum.EAST;
                } else if (distToN == 0 && distToE == 0) {
                    return DirectionEnum.SOUTH;
                } else if (distToE == 0 && distToS == 0) {
                    return DirectionEnum.WEST;
                } else if (distToS == 0 && distToW == 0) {
                    return DirectionEnum.NORTH;
                }

                // if only on one corner
                if (distToN == 0) { // on east boundary
                    return DirectionEnum.EAST;
                } else if (distToE == 0) {
                    return DirectionEnum.SOUTH;
                } else if (distToS == 0) {
                    return DirectionEnum.WEST;
                } else if (distToW == 0) {
                    return DirectionEnum.NORTH;
                }

                if (DEBUG) System.out.println(DirectionEnum.getDirection(agent.getAngle()).getDirection());
                // return DirectionEnum.getDirection(agent.getAngle());
                System.out.println("problem at 3.2");
                return null;
            }
            // do baseline patrolling
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
                if(freeGuard)
                    return qlGuard.makeMove(agent);
                if(hasPlacedTAMarker) {
                    System.out.println("3.3.1: QL patrolling");
                    //if sees second marker, turn into north guard
                    if(!qlGuardHasHalve && qlGuard.checkSecondMarker(agent.getVisibleTiles())){
                        System.out.println("Saw second marker and made north table");
                        qlGuardHasHalve = true;
                        qlGuard.makeNorthQTable();
                    }
                    return qlGuard.makeMove(agent);
                } else {
                    System.out.println("3.3.2: QL patrolling: Placing marker");

                    if(!needToPlaceSecondMarker) {
                        // move to center
                        DirectionEnum dir = qlGuard.makeMoveToCenter(agent);
                        if (dir != null) return dir;
                        //if dir == null then we are at center
                        // check 1st marker
                        if (!qlGuard.checkCurrentTileForMarker(agent)) {
                            //place marker
                            agent.addMarkers(1,map);
                            hasPlacedTAMarker = true;
                            return qlGuard.makeMove(agent);
                        } else {
                            needToPlaceSecondMarker = true;
                            return qlGuard.actionToDirection(2);
                        }
                    } else{
                        //check if 2nd marker exists
                        if(qlGuard.checkCurrentTileForMarker(agent)){
                            freeGuard = true;
                        }
                        agent.addMarkers(2,map);
                        //place second marker
                        agent.addMarkers(1,map);
                        hasPlacedTAMarker = true;
                        needToPlaceSecondMarker = false;
                        //turn into south guard
                        qlGuard.makeSouthQTable();
                        qlGuardHasHalve = true;
                        return qlGuard.makeMove(agent);
                    }
                    // place 1st or 2nd marker
                }
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
            situationStageOf3 = 1;
            return chasing(agent, visibleTiles, checkInvader(visibleTiles));
        }

        // situation 4.2: Guard chasing invader while knowing where TA is
        if (!patrolling && targetHasBeenReached && isChasing && (situationStageOf4 == 2)) {
            System.out.println("4.2");

            int temp = getDistanceToClosestCorner(map.getTile(x, y));
            System.out.println("distance from closest corner: " + temp);
            if (temp > 15) { // if guard goes too far from TA return
                // reseting params
                invaderSeen = false;
                isChasing = false;
                situationStageOf3 = 1;
                situationStageOf4 = 1;
                // go back to our standardized area
                this.curPath = findPathTargetArea(agent, cornersOfStandardized, false);
                return findNextMoveDirection(agent, this.curPath.get(1));
            } else {
                return chasing(agent, visibleTiles, checkInvader(visibleTiles));
            }
        }

        System.out.println("reached the end");
        System.out.println(this);
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

    private Queue<Tile> getSTACornersInTheVision(ArrayList<Tile> vision) {
        Queue<Tile> list = new LinkedList<>();
        for (Tile oneTile: vision) {
            if (oneTile.isStandardizedTA()) list.add(oneTile);
        }
        return list;
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

    public Tile MarkerInterpretation(Agent agent){
        Tile f = agent.findMarker();
        if(f!=null)
        {
            Color c = agent.ownMap.getTile(f.getX(),f.getY()).getColor();
            if(c==Color.RED){
                if(agent.ownMap.getTile(agent.getX_position()+1,agent.getY_position())!=null && agent.ownMap.getTile(agent.getX_position()+1,agent.getY_position()).isWalkable())
                    return agent.ownMap.getTile(agent.getX_position()+1,agent.getY_position());
                else if(agent.ownMap.getTile(agent.getX_position()-1,agent.getY_position())!=null && agent.ownMap.getTile(agent.getX_position()-1,agent.getY_position()).isWalkable())
                    return agent.ownMap.getTile(agent.getX_position()-1,agent.getY_position());
            }
            if(c==this.c[1]){
                if(agent.ownMap.getTile(agent.getX_position(),agent.getY_position()+1)!=null && agent.ownMap.getTile(agent.getX_position(),agent.getY_position()+1).isWalkable())
                    return agent.ownMap.getTile(agent.getX_position(),agent.getY_position()+1);
                else if(agent.ownMap.getTile(agent.getX_position(),agent.getY_position()-1)!=null && agent.ownMap.getTile(agent.getX_position(),agent.getY_position()-1).isWalkable())
                    return agent.ownMap.getTile(agent.getX_position(),agent.getY_position()-1);
            }
            else if(c==Color.WHITE){
                System.out.println("An intruder was caught");
            }
            else if(f.getIsPheromone()==true)
            {
                if(agent.ownMap.getTile(agent.getX_position(),agent.getY_position()-1).toString().equals("TelePortal")){
                    if(agent.ownMap.getTile(agent.getX_position()+2,agent.getY_position()).isWalkable())
                        return agent.ownMap.getTile(agent.getX_position()+2,agent.getY_position());
                    else if(agent.ownMap.getTile(agent.getX_position()-2,agent.getY_position()).isWalkable())
                        return agent.ownMap.getTile(agent.getX_position()-2,agent.getY_position());
                }
                if(agent.ownMap.getTile(agent.getX_position(),agent.getY_position()+1).toString().equals("TelePortal")) {
                    if(agent.ownMap.getTile(agent.getX_position()+2,agent.getY_position()).isWalkable())
                        return agent.ownMap.getTile(agent.getX_position()+2,agent.getY_position());
                    else if(agent.ownMap.getTile(agent.getX_position()-2,agent.getY_position()).isWalkable())
                        return agent.ownMap.getTile(agent.getX_position()-2,agent.getY_position());
                }
                if(agent.ownMap.getTile(agent.getX_position()-1,agent.getY_position()).toString().equals("TelePortal"))
                {
                    if(agent.ownMap.getTile(agent.getX_position(),agent.getY_position()+2).isWalkable())
                        return agent.ownMap.getTile(agent.getX_position(),agent.getY_position()+2);
                    else if(agent.ownMap.getTile(agent.getX_position(),agent.getY_position()-2).isWalkable())
                        return agent.ownMap.getTile(agent.getX_position(),agent.getY_position()-2);
                }
                if(agent.ownMap.getTile(agent.getX_position()+1,agent.getY_position()).toString().equals("TelePortal"))
                {
                    if(agent.ownMap.getTile(agent.getX_position(),agent.getY_position()+2).isWalkable())
                        return agent.ownMap.getTile(agent.getX_position(),agent.getY_position()+2);
                    else if(agent.ownMap.getTile(agent.getX_position(),agent.getY_position()-2).isWalkable())
                        return agent.ownMap.getTile(agent.getX_position(),agent.getY_position()-2);
                }
                System.out.println("Agent already entered a teleportal.");
            }
            return f;
        }
        return null;
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

    public DirectionEnum chasing(Agent agent, ArrayList<Tile> vision, boolean thereIsIntruder) {
        int guardsX = agent.getX_position();
        int guardsY = agent.getY_position();

        ArrayList<DirectionEnum> dirs = new ArrayList<>();

        if (thereIsIntruder) {
            Tile intruderTile = getInvader(vision).getAgentPosition();
            int intruderX = intruderTile.getX();
            int intruderY = intruderTile.getY();

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
        } else { // meaning we lost vision of intruder, try to go in the way where last intruder was
            if (checkIfWalkable(agent.getAgentPosition(), lastDirIntruderHasBeenSeen)) {
                dirs.add(lastDirIntruderHasBeenSeen);
            }
            if (checkIfWalkable(agent.getAgentPosition(), DirectionEnum.getDirection(lastDirIntruderHasBeenSeen.getAngle() + 90))) {
                dirs.add(DirectionEnum.getDirection(lastDirIntruderHasBeenSeen.getAngle() + 90));
            }
            if (checkIfWalkable(agent.getAgentPosition(), DirectionEnum.getDirection(lastDirIntruderHasBeenSeen.getAngle() - 90))) {
                dirs.add(DirectionEnum.getDirection(lastDirIntruderHasBeenSeen.getAngle() - 90));
            }
        }

        if (!dirs.isEmpty()) {
            return dirs.get(r.nextInt(dirs.size()));
        } else {
            return null;
        }
    }

    public boolean checkIfWalkable(Tile agentTile, DirectionEnum dir) {
        int x = agentTile.getX();
        int y = agentTile.getY();

        if (dir == DirectionEnum.EAST) {
            return (map.getTile(x + 1, y).isWalkable());
        } else if (dir == DirectionEnum.WEST) {
            return (map.getTile(x - 1, y).isWalkable());
        } else if (dir == DirectionEnum.SOUTH) {
            return (map.getTile(x, y + 1).isWalkable());
        } else if (dir == DirectionEnum.NORTH) {
            return (map.getTile(x, y - 1).isWalkable());
        }
        return false;
    }

    @Override
    public String toString() {
        return "CombinedGuard{" +
                "targetHasBeenReached=" + targetHasBeenReached +
                ", invaderSeen=" + invaderSeen +
                ", patrolling=" + patrolling +
                ", useQL=" + useQL +
                ", isChasing=" + isChasing +
                ", timePassedAfterSeeingInvader=" + timePassedAfterSeeingInvader +
                ", situationStageOf3=" + situationStageOf3 +
                ", situationStageOf4=" + situationStageOf4 +
                '}';
    }
}