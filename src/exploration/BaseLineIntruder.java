package exploration;

import agents.Agent;
import agents.Guard;
import agents.Intruder;
import controller.Map.Map;
import controller.Map.tiles.Tile;
import javafx.scene.paint.Color;
import utils.AdjacencyList;
import utils.DirectionEnum;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class BaseLineIntruder extends FrontierBasedExploration {
    private ArrayList<Tile> visibleTiles;
    private AdjacencyList adjacencyList;
    private LinkedList<Tile> exploredTiles;
    private Queue<Tile> frontierQueue;
    private Queue<Tile> BFSQueue;
    private Map testMap;
    public BaseLineIntruder(Agent agent, Tile[][] map) {
        super(agent, map);
        visibleTiles = agent.getVisibleTiles();
        adjacencyList = new AdjacencyList(map, visibleTiles);
        frontierQueue = new LinkedList<>();
        BFSQueue = new LinkedList<>();
        exploredTiles = new LinkedList<>();
    }

    @Override
    public DirectionEnum makeMove(Agent agent) {
        Intruder intruder = (Intruder) agent;
        Tile curTile = intruder.getAgentPosition();
        visibleTiles = intruder.getVisibleTiles();
        System.out.println(visibleTiles);
        updateExploredTiles(visibleTiles);
        adjacencyList.addNodes(visibleTiles);
        updateFrontiers(visibleTiles);
        colourFrontiers(intruder);
        Tile tile = findFrontiers(intruder).get(1);
        if(frontierQueue.isEmpty()){
            return null;
        }
        DirectionEnum dir = findNextMoveDirection(intruder, tile);
        return dir;
    }

    public void colourFrontiers(Intruder intruder){
        testMap = intruder.testmap;
        System.out.println(frontierQueue);
        for(Tile tile : frontierQueue){
            for(int i=0; i<testMap.getHorizontalSize(); i++){
                for(int j=0; j<testMap.getVerticalSize(); j++){
                    if(i==tile.getX() && j==tile.getY()){
                        testMap.getTile(i,j).setColor(Color.YELLOW);
                    }
                }
            }
        }
    }
}
