package exploration;

import agents.Agent;
import controller.Map.Map;
import controller.Map.tiles.Tile;
import utils.DirectionEnum;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QLGuard extends FrontierBasedExploration{

    private int taLength = 5;
    private int beltLength = 5;
    private int numberOfActions = 4;
    private int areaLength = taLength + 2*beltLength;
    private int totalStates = areaLength * areaLength;

    private double[][] qTable;
    private boolean placedMiddleMarker = false;

    private int areaAllocated = 0; //0 = whole area, 1 = south, 2 = north

    public QLGuard(Agent agent, Map map) {
        super(agent, map);
        //get qtable from file
        //int[] movesQTable = getMovesQTable();
        qTable = getQTableFromFile();
    }

    @Override
    public DirectionEnum makeMove(Agent agent) {
        Tile curTile = agent.getAgentPosition();
        int currentState = getStateFromCoord(curTile.getX(),curTile.getY());
        visibleTiles = agent.getVisibleTiles();

        int action = decideAction(currentState);

        DirectionEnum dir = actionToDirection(action);
        return dir;
    }

    public DirectionEnum makeMoveToCenter(Agent agent){
        Tile curTile = agent.getAgentPosition();
        int centerTile = totalStates/2;
        centerTile += totalStates%2;
        return getCalculatePathFromState(getStateFromCoord(curTile.getX(),curTile.getY()),centerTile);
    }

    public boolean checkCurrentTileForMarker(Agent agent) {
        //if(agent.getAgentPosition().getColor().toString().equals("ORANGE"))
        if(agent.getAgentPosition().getHasMarker())
            return true;
        return false;
    }

    public boolean checkSecondMarker(ArrayList<Tile> visibleTiles) {
        int centerTile = totalStates/2;
        centerTile += totalStates%2;
        int secondTile = centerTile +1;
        for (Tile t:visibleTiles) {
            if(getStateFromCoord(t.getX(),t.getY())==secondTile){
                //if(t.getColor().toString().equals("ORANGE")){
                if(t.getHasMarker()){
                    return true;
                }
                return false;
            }
        }
        return false;
    }


    public double[] getActions(int currentState){
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

    private int decideAction(int currentState) {
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

    private double[][] getQTableFromFile() {
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

    public void makeSouthQTable(){
        for (int i = 0; i < qTable.length/2 + 1; i++) {
            qTable[i][3] = 0.0; //remove possibility to go up -> agent will walk downwards
        }
    }

    public void makeNorthQTable(){
        for (int i = qTable.length/2; i < qTable.length; i++) {
            qTable[i][1] = 0.0; //remove possibility to go down -> agent will walk upwards
        }
    }

    public DirectionEnum actionToDirection(int action) {
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
        return y * areaLength + x;
    }

    public DirectionEnum getCalculatePathFromState(int currentState, int goalState){
        int currentY = currentState/areaLength;
        int goalY = goalState/areaLength;
        if(goalY<currentY) return actionToDirection(3);
        else if(goalY>currentY) return actionToDirection(1);
        int currentX = currentState%areaLength;
        int goalX = goalState%areaLength;
        if(goalX<currentX) return actionToDirection(0);
        else if(goalX>currentX) return actionToDirection(2);
        return null;
    }
}
