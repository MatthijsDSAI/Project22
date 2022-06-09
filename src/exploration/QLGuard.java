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
        int[] movesQTable = new int[totalStates];

        String[] attributes = new String[totalStates];
        Path pathToFile = Paths.get("movesQTable.csv");

        // create an instance of BufferedReader
        // using try with resource, Java 7 feature to close resources
        try (BufferedReader br = Files.newBufferedReader(pathToFile,
                StandardCharsets.US_ASCII)) {
            // read the first line from the text file
            String line = br.readLine();
            attributes = line.split(",");
            } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < totalStates; i++) {
            movesQTable[i] = Integer.parseInt(attributes[i]);
        }

        DirectionEnum dir = actionToDirection(movesQTable[currentState]);
        return dir;
    }

    private DirectionEnum actionToDirection(int action) {
        if(action == 0) return DirectionEnum.WEST;
        else if(action == 1) return DirectionEnum.SOUTH;
        else if(action == 2) return DirectionEnum.EAST;
        else return DirectionEnum.NORTH;
    }

    public int getStateFromCoord(int x, int y){
        return y * areaLength + x;
    }
}
