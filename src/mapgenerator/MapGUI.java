package mapgenerator;

import javax.sound.sampled.Line;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Line2D;
import java.util.ArrayList;

public class MapGUI extends JFrame implements MouseListener {
    private int amountOfClicks = 0;
    StringBuilder wall;
    private double[] tempLine;
    private static final double HORIZONTAL_BORDER = 60;
    private static final double VERTICAL_BORDER = 100;
    private static final double frameWidth = 600;
    private static final double frameHeight = 400;
    private final ArrayList<Line2D> walls;
    private final ArrayList<Line2D> borders;
    public MapGUI(ArrayList<Line2D> walls, ArrayList<Line2D> borders){
        setLayout(new BorderLayout());
        JPanel panel = new JPanel();
        add(panel, BorderLayout.CENTER);
        addMouseListener(this);
        setTitle("Window");
        setSize((int)(frameWidth+HORIZONTAL_BORDER), (int)(frameHeight+VERTICAL_BORDER));

        this.borders = borders;
        this.walls = walls;




    }

    private static void scaleSize(ArrayList<Line2D> walls, double mapWidth, double mapHeight) {
        double widthMultiplier = frameWidth/mapWidth;
        System.out.println(widthMultiplier);
        double heightMultiplier = frameHeight/mapHeight;
        for (Line2D line : walls) {
            line.setLine((line.getX1() * widthMultiplier) +  (HORIZONTAL_BORDER/2), (line.getY1() * heightMultiplier) + (VERTICAL_BORDER/2), (line.getX2() * widthMultiplier) +  (HORIZONTAL_BORDER/2), (line.getY2() * heightMultiplier) + (VERTICAL_BORDER/2));

        }
    }

    public void paint(Graphics g){
        super.paint(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(5.0F));
        g.setColor(Color.BLACK);
        for (Line2D lin : borders) {
            g2.draw(lin);
        }

        g.setColor(Color.RED);
        for (Line2D lin : walls) {
            g2.draw(lin);
        }

    }

    public static void main(String[] args){
        ArrayList<Line2D> list = SimpleMapReader.readInCoordinatesOfWall();
        double mapWidth = SimpleMapReader.readInConstant("width");
        double mapHeight = SimpleMapReader.readInConstant("height");

        scaleSize(list, mapWidth, mapHeight);

        ArrayList<Line2D> borders = new ArrayList<>();
        borders.add(new Line2D.Double( (HORIZONTAL_BORDER/2), (VERTICAL_BORDER/2),frameWidth+ (HORIZONTAL_BORDER/2), (VERTICAL_BORDER/2)));
        borders.add(new Line2D.Double( (HORIZONTAL_BORDER/2), (VERTICAL_BORDER/2), (HORIZONTAL_BORDER/2),frameHeight+ (VERTICAL_BORDER/2)));
        borders.add(new Line2D.Double(frameWidth+ (HORIZONTAL_BORDER/2), (VERTICAL_BORDER/2),frameWidth+ (HORIZONTAL_BORDER/2),frameHeight+ (VERTICAL_BORDER/2)));
        borders.add(new Line2D.Double( (HORIZONTAL_BORDER/2),frameHeight+ (VERTICAL_BORDER/2),frameWidth+ (HORIZONTAL_BORDER/2),frameHeight+ (VERTICAL_BORDER/2)));
        MapGUI s = new MapGUI(list, borders);
        s.setVisible(true);
    }

    public static String toString(Line2D line){
        return "[x1: "+line.getX1()+", y1: "+ line.getY1()+", x2: " + line.getX2() + " y2: " + line.getY2()+"]";
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        amountOfClicks++;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        tempLine = new double[4];
        wall = new StringBuilder("wall = ");
        double x = transformX(e.getX());
        double y = transformY(e.getY());

//        System.out.println("Line: " + amountOfClicks);
//        System.out.println("x1: " + x);
//        System.out.println("y1: " + y);
        wall.append((int)x);
        wall.append(" ");
        wall.append((int)y);
        wall.append(" ");
        tempLine[0] = e.getX();
        tempLine[1] = e.getY();

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        double x = transformX(e.getX());
        double y = transformY(e.getY());

//        System.out.println("x2: " + x);
//        System.out.println("y2: " + y);
        wall.append((int)x);
        wall.append(" ");
        wall.append((int)y);
        System.out.println(wall);
        wall = new StringBuilder();
        tempLine[2] = e.getX();
        tempLine[3] = e.getY();
        walls.add(new Line2D.Double(tempLine[0],tempLine[1],tempLine[2],tempLine[3]));
        repaint();

    }



    public double transformX(double x){
        x -= (HORIZONTAL_BORDER/2);
        if(x<0)
            return 0;
        return Math.min(x, frameWidth);
    }

    public double transformY(double y){
        y -= (VERTICAL_BORDER/2);
        if(y<0)
            return 0;
        return Math.min(y, frameHeight);
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
