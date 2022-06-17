package exploration;

import agents.Agent;
import controller.Map.Map;
import controller.Map.tiles.Tile;
import utils.DirectionEnum;

import java.awt.print.Book;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Math.abs;

public class QLGuard extends FrontierBasedExploration{

    private int taLength = 5;
    private int beltLength = 5;
    private int numberOfActions = 4;
    private int areaLength = taLength + 2*beltLength;
    private int totalStates = areaLength * areaLength;

    private int errorCount = 0;

    private Random r = new Random();

    public QLGuard(Agent agent, Map map) {
        super(agent, map);
    }

    @Override
    public DirectionEnum makeMove(Agent agent) {
        Tile curTile = agent.getAgentPosition();
        int currentState = getStateFromCoord(curTile.getX(),curTile.getY());

        visibleTiles = agent.getVisibleTiles();

        //get qtable from file
        int[] movesQTable = getMovesQTable();
        double[][] qTable = getQTable();

        int action = decideAction(qTable, currentState);

        DirectionEnum dir = actionToDirection(action);
        return dir;
    }

    public DirectionEnum makeMove(Agent agent, int distToW, int distToE, int distToN, int distToS) {
        Tile curTile = agent.getAgentPosition();
        int currentState = getStateFromCoord(curTile.getX(),curTile.getY());

        visibleTiles = agent.getVisibleTiles();

        //get qtable from file
        int[] movesQTable = getMovesQTable();
        double[][] qTable = getQTable();

        if (currentState < 0 || currentState > 224) {
            System.out.println("jeez");
            ArrayList<DirectionEnum> possibleMoves = new ArrayList<>();

            if (abs(distToW) < 7 && abs(distToN) < 7) {
                if (checkIfWalkable(agent.getAgentPosition(), DirectionEnum.EAST)) {
                    possibleMoves.add(DirectionEnum.EAST);
                }
                if (checkIfWalkable(agent.getAgentPosition(), DirectionEnum.SOUTH)) {
                    possibleMoves.add(DirectionEnum.SOUTH);
                }
            } else if (abs(distToE) < 7 && abs(distToN) < 7) {
                if (checkIfWalkable(agent.getAgentPosition(), DirectionEnum.WEST)) {
                    possibleMoves.add(DirectionEnum.WEST);
                }
                if (checkIfWalkable(agent.getAgentPosition(), DirectionEnum.SOUTH)) {
                    possibleMoves.add(DirectionEnum.SOUTH);
                }
            } else if (abs(distToE) < 7 && abs(distToS) < 7) {
                if (checkIfWalkable(agent.getAgentPosition(), DirectionEnum.WEST)) {
                    possibleMoves.add(DirectionEnum.WEST);
                }
                if (checkIfWalkable(agent.getAgentPosition(), DirectionEnum.NORTH)) {
                    possibleMoves.add(DirectionEnum.NORTH);
                }
            } else if (abs(distToW) < 7 && abs(distToS) < 7) {
                if (checkIfWalkable(agent.getAgentPosition(), DirectionEnum.EAST)) {
                    possibleMoves.add(DirectionEnum.EAST);
                }
                if (checkIfWalkable(agent.getAgentPosition(), DirectionEnum.NORTH)) {
                    possibleMoves.add(DirectionEnum.NORTH);
                }
            }
            errorCount++;
            if (possibleMoves.size() == 0) {
                if (checkIfWalkable(agent.getAgentPosition(), DirectionEnum.EAST)) {
                    possibleMoves.add(DirectionEnum.EAST);
                }
                if (checkIfWalkable(agent.getAgentPosition(), DirectionEnum.SOUTH)) {
                    possibleMoves.add(DirectionEnum.SOUTH);
                }
                if (checkIfWalkable(agent.getAgentPosition(), DirectionEnum.WEST)) {
                    possibleMoves.add(DirectionEnum.WEST);
                }
                if (checkIfWalkable(agent.getAgentPosition(), DirectionEnum.NORTH)) {
                    possibleMoves.add(DirectionEnum.NORTH);
                }
            }
            return possibleMoves.get(r.nextInt(possibleMoves.size()));
        }

        int action = decideAction(qTable, currentState);

        DirectionEnum dir = actionToDirection(action);
        return dir;
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

    public DirectionEnum makeMove(Agent agent, List<Integer> invalidMoves) {
        Tile curTile = agent.getAgentPosition();
        int currentState = getStateFromCoord(curTile.getX(),curTile.getY());

        visibleTiles = agent.getVisibleTiles();

        //get qtable from file
        int[] movesQTable = getMovesQTable();
        double[][] qTable = getQTable();

        int action = decideSingleAction(getActions(qTable, currentState),invalidMoves);

        DirectionEnum dir = actionToDirection(action);
        return dir;
    }

    public double[] getActions(double[][] qTable, int currentState){
        return qTable[currentState];
    }

    public int decideSingleAction(double[] moveValues, List<Integer> invalidMoves){
        //remove invalid moves
        for (int i = 0; i < invalidMoves.size(); i++) {
            moveValues[invalidMoves.get(i)] = 0.0;
        }
        Random rand = new Random();
        double total = 0.0;
        for (int i = 0; i < numberOfActions; i++) {
            total += moveValues[i];
        }
        double r = rand.nextDouble()*total;
        double sum = 0.0;
        for (int i = 0; i < numberOfActions; i++) {
            sum += moveValues[i];
            if(r<=sum) return i;
        }
        //if all moves are invalid
        return -1;
    }

    private int decideAction(double[][] qTable, int currentState) {
        Random rand = new Random();
        double total = 0.0;
        for (int i = 0; i < numberOfActions; i++) {
            total += qTable[currentState][i];
        }
        double r = rand.nextDouble()*total;
        double sum = 0.0;
        for (int i = 0; i < numberOfActions; i++) {
            sum += qTable[currentState][i];
            if(r<=sum) return i;
        }
        return 0;
    }

    private double[][] getQTable() {
        double[][] qTable = new double[totalStates][numberOfActions];
        String[][] values = new String[totalStates][numberOfActions];
        Path pathToFile = Paths.get("qTable.csv");


        try (BufferedReader br = Files.newBufferedReader(pathToFile,
                StandardCharsets.US_ASCII)) {
            // read the first line from the text file
            for (int i = 0; i < totalStates; i++) {
                String line = br.readLine();
                if(!line.equals("")){
                    values[i] = line.split(",");
                } else i--;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < totalStates; i++) {
            for (int j = 0; j < numberOfActions; j++) {
                qTable[i][j] = Double.parseDouble(values[i][j]);
            }
            // System.out.println(i);
        }
        return qTable;
    }

    private int[] getMovesQTable() {
        int[] movesQTable = new int[totalStates];
        String[] moves = new String[totalStates];
        Path pathToFile = Paths.get("movesQTable.csv");

        // create an instance of BufferedReader
        // using try with resource, Java 7 feature to close resources
        try (BufferedReader br = Files.newBufferedReader(pathToFile,
                StandardCharsets.US_ASCII)) {
            // read the first line from the text file
            String line = br.readLine();
            moves = line.split(",");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < totalStates; i++) {
            movesQTable[i] = Integer.parseInt(moves[i]);
        }
        return movesQTable;
    }

    private DirectionEnum actionToDirection(int action) {
        if(action == 0) return DirectionEnum.WEST;
        else if(action == 1) return DirectionEnum.SOUTH;
        else if(action == 2) return DirectionEnum.EAST;
        else return DirectionEnum.NORTH;
    }

    public int getStateFromCoord(int x, int y){
        int difference = map.getHorizontalSize() - areaLength;
        difference /= 2;
        x -= difference;
        y -= difference;
        if(x < 0 || x>areaLength-1 || y < 0 || y > areaLength-1) return -1; //if coordinate is not in standardized area
        int state = y * areaLength + x;
        return state;
    }
}
