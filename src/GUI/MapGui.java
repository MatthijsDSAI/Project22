package nl.maastrichtuniversity.dke.explorer;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class MapGui extends JPanel{

    private Scenario scenario;
    private int mapHeight;
    private int mapWidth;
    private double scaling;

    public MapGui(){
        setting();
    }

    public MapGui(Scenario scenario){
        this.scenario = scenario;
        mapHeight = scenario.mapHeight;
        mapWidth = scenario.mapWidth;
        scaling = scenario.getScaling();
        setting();
    }

    public void setting(){
        JFrame jf = new JFrame("Map");
        jf.add(this, BorderLayout.CENTER);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setLocation(new Point(400, 150));
        jf.setSize(800, 900);
        jf.setResizable(false);
        jf.setVisible(true);
    }

    @Override
    public void paint(Graphics g){
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        drawArea(g2);
        drawGuards(g2);
        drawIntruders(g2);
    }

    public void drawArea(Graphics2D g2){
//        Area wall = new Area(50,0,51,20);
//        wall.drawArea(g2, c);
        Color c = Color.PINK;
        ArrayList<Area> walls = scenario.getWalls();
        for(Area wall : walls){
            wall.draw(g2, c);
        }
//      c = Color.PINK;
        ArrayList<TelePortal> teleports = scenario.getTeleportals();
        for(Area teleport : teleports){
            teleport.draw(g2, c);
        }
        //      c = Color.PINK;
        ArrayList<Area> shaded = scenario.getShaded();
        for(Area s : shaded){
            s.draw(g2, c);
        }

        scenario.spawnAreaGuards.draw(g2, c);
        scenario.spawnAreaIntruders.draw(g2, c);
        scenario.getTargetArea().draw(g2, c);
    }

    public void drawGuards(Graphics2D g2){
        double[][] spawnGuards = scenario.spawnGuards();
        for(int i=0; i< scenario.getNumGuards(); i++){
            g2.drawOval((int) spawnGuards[i][0], (int)spawnGuards[i][1], (int) spawnGuards[i][2], (int) spawnGuards[i][2]);
        }
    }

    public void drawIntruders(Graphics2D g2){
        double[][] spawnIntruders = scenario.spawnIntruders();
        for(int i=0; i< scenario.numIntruders; i++){
            g2.drawOval((int) spawnIntruders[i][0], (int)spawnIntruders[i][1], (int) spawnIntruders[i][2], (int) spawnIntruders[i][2]);
        }
    }

    public static void main(String[] args){
        MapGui map = new MapGui();
    }
}
