package controller;

import agents.Agent;
import controller.Map.Map;
import controller.Map.tiles.Tile;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class TelePortal extends Area {
    protected int yTarget;
    protected int xTarget;
    protected double outOrientation;

    public TelePortal(int x1, int y1, int x2, int y2, int targetX, int targetY){
        super(x1,y1,x2,y2);
        yTarget=targetY;
        xTarget=targetX;
        outOrientation = 0.0;
    }

    public TelePortal(int x1, int y1, int x2, int y2, int targetX, int targetY, double orient){
        super(x1,y1,x2,y2);
        yTarget=targetY;
        xTarget=targetX;
        outOrientation = orient;
    }

    public int[] getNewLocation(){
        int[] target = new int[] {yTarget,xTarget};
        return target;
    }

    public double getNewOrientation(){
        return outOrientation;
    }

    public static class GraphicsConnector {
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
}
