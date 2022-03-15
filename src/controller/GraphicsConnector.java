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
    private int guiHeight;
    private int guiWidth;
    public GraphicsConnector(GameRunner gameRunner) {
        this.gameRunner = gameRunner;
        map = gameRunner.getMap();
        tiles = map.getTiles();
        guards = gameRunner.getGuards();
        if (gameRunner.isGameMode1()) {
            intruders = gameRunner.getIntruders();
        }
        // .add(gameRunner.getAgent());
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
//    public ArrayList<BufferedImage> getAgents()  {
//        //TODO: implement difference between guard and intruder
//        ArrayList<BufferedImage> list = new ArrayList<>();
//        for(Agent agent : agents){
//
//            try {
//                BufferedImage image = ImageIO.read(new File("guard.png"));
//                list.add(image);
//                AffineTransform transform = new AffineTransform();
//                transform.rotate(Math.toRadians(agent.getAngle()), image.getWidth()/2, image.getHeight()/2 );
//                AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR );
//                list.add(op.filter(image, null));
//
//            }
//            catch(Exception e){
//                System.out.println("Invalid image passed");
//            }
//
//        }
//        return list;
//    }
    public void updateGraphics(){
        gui.updateGraphics();
    }
    public void run(){
        gameRunner.run();
    }
    public void setGui(MapGui gui) {
        this.gui = gui;
    }

    public void setGuiHeight(int height) {
        guiHeight = height;
    }

    public int getGuiHeight() {
        return guiHeight;
    }

    public void setGuiWidth(int width) {
        guiWidth = width;
    }

    public int getGuiWidth() {
        return guiWidth;
    }


}