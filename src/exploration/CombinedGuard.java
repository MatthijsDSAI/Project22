package exploration;

import agents.Agent;
import agents.Intruder;
import controller.Map.Map;
import controller.Map.tiles.Tile;
import utils.DirectionEnum;

import java.util.*;
import java.util.List;

import static java.lang.Math.abs;

public class CombinedGuard extends FrontierBasedExploration {

    public Map map;

    public Agent agent;
    public FrontierBasedExploration frontierExploration;
    public QLGuard qlGuard;
    private boolean targetHasBeenReached = false;
    private boolean invaderSeen = false;
    private boolean patrolling = false;

    private final Queue<Tile> cornersOfStandardized = new LinkedList<>();

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
    private DirectionEnum lastDirIntruderHasBeenSeen;

    // assuming we know the boundaries of standardized area
    public CombinedGuard(Agent agent, Map map, int northBoundaryOfStandardized, int southBoundaryOfStandardized, int westBoundaryOfStandardized, int eastBoundaryOfStandardized) {
        super(agent, map);

        this.map = map;
        this.agent = agent;

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
     * This class handles 3 situations:
     * <p>
     * Situation 1: Guard has not seen TA or Invader yet, so keep exploring. Return frontierExploration move
     * - Situation 1.1: Guard has seen invader whilst not seeing TA, start to chase. Return chasing method
     * <p>
     * Situation 2: Guard has seen target area but not the intruder, start patrolling (either with baseline patrolling or QL)
     * The situations mentioned below is for baseline patrolling, if QL is to be used then these situations can be skipped
     * - Situation 2.2: Make guard go to the closest border by using "findPath" method found at Frontier Exploration Class
     * - Situation 2.3: Make guard patrol along the border of standardized area
     * <p>
     * Situation 3: Guard sees invader while patrolling
     * - Situation 3.1: Chase intruder, if distance to the closest corner becomes over 10 then give up and return to TA
     */
    @Override
    public DirectionEnum makeMove(Agent agent) {
        int x = agent.getX_position();
        int y = agent.getY_position();

        ArrayList<Tile> visibleTiles = agent.getVisibleTiles();
        updateKnowledge(agent, visibleTiles);

        // if guard sees TA set "targetHasBeenReached" true and never change back to false
        if (!targetHasBeenReached) {
            targetHasBeenReached = checkTargetArea(visibleTiles);
            if (targetHasBeenReached && isChasing) { // this means guard saw TA while chasing, go to TA instead
                isChasing = false;
            } else if (targetHasBeenReached && checkInvader(visibleTiles)) { // guard saw TA and intruder at the same time
                isChasing = true;
                situationStageOf4 = 2;
            }
            if(targetHasBeenReached) {
                agent.updateTargetArea();
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

        // if sees intruder at situation 3.1 or situation 3.2, making it go to situation 4.1
        if (targetHasBeenReached && invaderSeen && !patrolling && !isChasing && ((situationStageOf3==1) || (situationStageOf3==2))) {
            patrolling = true;
        }

        // situation 1: guard has not seen TA or Invader yet, so keep exploring
        if (!targetHasBeenReached && !invaderSeen && !isChasing && !patrolling) {
            return frontierExploration.makeMove(this.agent);
        }

        // situation 1.1: guard has seen invader whilst never seen TA, start to chase
        if (!targetHasBeenReached && invaderSeen && !isChasing && !patrolling) {
            isChasing = true;
            return chasing(agent, visibleTiles, checkInvader(visibleTiles));
        }

        // situation 1.1: guard is chasing invader without the knowledge of TA
        if (!targetHasBeenReached && isChasing && !patrolling) {

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

        // situation 2:
        // situation 2.1: Guard sees the target area first time
        if (!invaderSeen && !isChasing && targetHasBeenReached && !patrolling && (situationStageOf3 == 1)) {

            this.curPath = findPathTargetArea(agent, cornersOfStandardized, false);
            if (this.curPath.size() == 2) { // setting to situation 3.2 (because we are 1 move away from goal)
                situationStageOf3 = 2;
                if (useQL) patrolling = true;
            }
            return findNextMoveDirection(agent, this.curPath.get(1));
        }

        // situation 2.2: Guard is going to the corner of standardized area
        if (!useQL) { // this part is baseline patrolling
            if (targetHasBeenReached && !invaderSeen && !isChasing && !patrolling && (situationStageOf3 == 2)) {

                int distToW = (x - westBoundaryOfStandardized);
                int distToE = (x - eastBoundaryOfStandardized);
                int distToN = (y - northBoundaryOfStandardized);
                int distToS = (y - southBoundaryOfStandardized);

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
                return null;
            }
            // do baseline patrolling
            // situation 2.3: Guard is patrolling along the border of standardized area
            if (patrolling && !invaderSeen && targetHasBeenReached && !isChasing) {

                int distToW = (x - westBoundaryOfStandardized);
                int distToE = (x - eastBoundaryOfStandardized);
                int distToN = (y - northBoundaryOfStandardized);
                int distToS = (y - southBoundaryOfStandardized);

                // if reaches corner then turn right
                if ((distToW == 0 && distToN == 0) || (distToN == 0 && distToE == 0) || (distToE == 0 && distToS == 0) || (distToS == 0 && distToW == 0)) {
                    return DirectionEnum.getDirection(agent.getAngle() - 90); // turning right
                }
                return DirectionEnum.getDirection(agent.getAngle());
            }

        } else {
            // situation 2.3: Guard is patrolling along the border of standardized area by using QL
            if (patrolling && !invaderSeen && targetHasBeenReached && !isChasing) {

                int distToW = (x - westBoundaryOfStandardized);
                int distToE = (x - eastBoundaryOfStandardized);
                int distToN = (y - northBoundaryOfStandardized);
                int distToS = (y - southBoundaryOfStandardized);

                return qlGuard.makeMove(agent, distToW, distToE, distToN, distToS);
            }
        }


        // situation 3: Guard sees invader while patrolling
        // situation 3.1: Shout then follow the intruder
        if (patrolling && invaderSeen && (situationStageOf4 == 1) && targetHasBeenReached && !isChasing) {
            patrolling = false;
            isChasing = true;
            situationStageOf4 = 2;
            situationStageOf3 = 1;
            return chasing(agent, visibleTiles, checkInvader(visibleTiles));
        }

        // situation 3.2: Guard chasing invader while knowing where TA is
        if (!patrolling && targetHasBeenReached && isChasing && (situationStageOf4 == 2)) {

            int temp = getDistanceToClosestCorner(map.getTile(x, y));
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

    public DirectionEnum chasing(Agent agent, ArrayList<Tile> vision, boolean thereIsIntruder) {
        int guardsX = agent.getX_position();
        int guardsY = agent.getY_position();

        ArrayList<DirectionEnum> dirs = new ArrayList<>();

        if (thereIsIntruder) {
            Tile intruderTile = getInvader(vision).getAgentPosition();
            int intruderX = intruderTile.getX();
            int intruderY = intruderTile.getY();

            if (guardsX < intruderX) {
                if (map.getTile(agent.getX_position() + 1, agent.getY_position()).isWalkable()) {
                    dirs.add(DirectionEnum.EAST);
                }
            } else if (guardsX > intruderX) {
                if (map.getTile(agent.getX_position() - 1, agent.getY_position()).isWalkable()) {
                    dirs.add(DirectionEnum.WEST);
                }
            }

            if (guardsY < intruderY) {
                if (map.getTile(agent.getX_position(), agent.getY_position() + 1).isWalkable()) {
                    dirs.add(DirectionEnum.SOUTH);
                }
            } else if (guardsY > intruderY) {
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
            return randomMove(agent.getAgentPosition());
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