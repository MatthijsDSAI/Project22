package controller.Map;

import agents.Agent;
import controller.GameRunner;
import controller.Map.tiles.Tile;
import javafx.scene.paint.Color;


import java.util.ArrayList;

public class GraphicsConnector {
    private Map map;
    private Tile[][] tiles;
    private GameRunner gameRunner;
    private ArrayList<Agent> agents;
    public GraphicsConnector(GameRunner gameRunner) {
        map = gameRunner.getMap();
        tiles = map.getTiles();
        agents = new ArrayList<>();
        agents.add(gameRunner.getAgent());
    }

    public Color[][] getMapOfColors(){
        Color[][] mapOfColors = new Color[map.getTiles().length][map.getTiles()[0].length];
        for(int i=0; i<tiles.length; i++){
            for(int j=0; j<tiles[0].length; j++){
                mapOfColors[i][j] = tiles[i][j].getColor();
            }
        }
        return mapOfColors;
    }


}
