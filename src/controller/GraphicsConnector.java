package controller;

import GUI.MapGui;
import agents.Agent;
import agents.Guard;
import agents.Intruder;
import controller.Map.Map;
import controller.Map.tiles.Tile;
import javafx.scene.paint.Color;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

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
        if (gameRunner.isGameMode1()) {
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
        gameRunner.run();
    }
    public void setGui(MapGui gui) {
        this.gui = gui;
    }
}