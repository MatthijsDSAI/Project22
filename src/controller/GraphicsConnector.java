package controller;

import GUI.MapGui;
import agents.Guard;
import agents.Intruder;
import controller.Map.Map;
import controller.Map.tiles.Tile;
import javafx.scene.paint.Color;

import java.awt.*;
import java.util.ArrayList;

/*
* Forms the bridge between the backend and the GUI.
* Not much going on now, but with the advanced GUI in phase 2 we will have a lot more here.
 */
public class GraphicsConnector {
    private Map map;
    private Tile[][] tiles;
    private GameRunner gameRunner;
    private ArrayList<Guard> guards;
    private ArrayList<Intruder> intruders;
    private MapGui gui;

    public GraphicsConnector(GameRunner gameRunner) {
        this.gameRunner = gameRunner;
        map = gameRunner.getMap();
        tiles = map.getTiles();
        guards = gameRunner.getGuards();
        if (gameRunner.getGameMode()==1) {
            intruders = gameRunner.getIntruders();
        }
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

    public void run(){
        if(Scenario.config.getTraining()){
            gameRunner.trainLoop(Scenario.config.getGameMode());
        }
        else{
            gameRunner.run(Scenario.config.getGameMode());
        }
    }

    public void initGameRunner(String guard, String intruder){
        gameRunner.init(guard, intruder);
    }
    public void setGui(MapGui gui) {
        this.gui = gui;
    }
}