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

public class QLGuard extends FrontierBasedExploration{

    private int taLength = 5;
    private int beltLength = 5;
    private int numberOfActions = 4;
    private int areaLength = taLength + 2*beltLength;
    private int totalStates = areaLength * areaLength;

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
        return y * areaLength + x;
    }
}
