package mapgenerator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class MapDrawer extends JFrame implements MouseListener {
    private int amountOfClicks = 0;
    StringBuilder wall;
    private double[] tempLine;
    private static final double HORIZONTAL_BORDER = 80;
    private static final double VERTICAL_BORDER = 80;
    private static final double frameWidth = 800;
    private static final double frameHeight = 800;
    private final ArrayList<Rectangle2D> walls;
    private final ArrayList<Line2D> borders;
    private double[] print;

    public MapDrawer(ArrayList<Rectangle2D> walls, ArrayList<Line2D> borders){
        setLayout(new BorderLayout());
        JPanel panel = new JPanel();
        add(panel, BorderLayout.CENTER);
        addMouseListener(this);
        setTitle("Window");
        setSize((int)(frameWidth+HORIZONTAL_BORDER), (int)(frameHeight+VERTICAL_BORDER));

        this.borders = borders;
        this.walls = walls;




    }

    private static void scaleSize(ArrayList<Rectangle2D> walls, double mapWidth, double mapHeight) {
        double widthMultiplier = frameWidth/mapWidth;
        double heightMultiplier = frameHeight/mapHeight;
        for (Rectangle2D line : walls) {
            //line.setLine((line.getX1() * widthMultiplier) +  (HORIZONTAL_BORDER/2), (line.getY1() * heightMultiplier) + (VERTICAL_BORDER/2), (line.getX2() * widthMultiplier) +  (HORIZONTAL_BORDER/2), (line.getY2() * heightMultiplier) + (VERTICAL_BORDER/2));

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
        for (Rectangle2D rect : walls) {
            g2.draw(rect);
            g2.fill(rect);
        }

    }

    public static void main(String[] args){
        ArrayList<Rectangle2D> list = SimpleMapReader.readInCoordinatesOfWall();
        double mapWidth = 80;
        double mapHeight = 80;
        ArrayList<Rectangle2D> newList = new ArrayList<>();
        for(Rectangle2D rec : list){
            newList.add(new Rectangle2D.Double(rec.getX()+HORIZONTAL_BORDER/2, rec.getY() +VERTICAL_BORDER/2, rec.getWidth(), rec.getHeight()));
        }
        scaleSize(list, mapWidth, mapHeight);

        ArrayList<Line2D> borders = new ArrayList<>();
        borders.add(new Line2D.Double( (HORIZONTAL_BORDER/2), (VERTICAL_BORDER/2),frameWidth+ (HORIZONTAL_BORDER/2), (VERTICAL_BORDER/2)));
        borders.add(new Line2D.Double( (HORIZONTAL_BORDER/2), (VERTICAL_BORDER/2), (HORIZONTAL_BORDER/2),frameHeight+ (VERTICAL_BORDER/2)));
        borders.add(new Line2D.Double(frameWidth+ (HORIZONTAL_BORDER/2), (VERTICAL_BORDER/2),frameWidth+ (HORIZONTAL_BORDER/2),frameHeight+ (VERTICAL_BORDER/2)));
        borders.add(new Line2D.Double( (HORIZONTAL_BORDER/2),frameHeight+ (VERTICAL_BORDER/2),frameWidth+ (HORIZONTAL_BORDER/2),frameHeight+ (VERTICAL_BORDER/2)));
        MapDrawer s = new MapDrawer(newList, borders);
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
        print = new double[4];
        wall = new StringBuilder("wall = ");
        double x = transformX(e.getX());
        double y = transformY(e.getY());

        print[0] = x;
        print[1] = y;
        tempLine[0] = e.getX();
        tempLine[1] = e.getY();

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        double x = transformX(e.getX());
        double y = transformY(e.getY());

        print[2] = x;
        print[3] = y;
        tempLine[2] = e.getX();
        tempLine[3] = e.getY();

        double xRect = Math.min(tempLine[0], tempLine[2]);
        double wRect = Math.max(tempLine[0], tempLine[2]) - xRect;
        double yRect = Math.min(tempLine[1], tempLine[3]);
        double hRect = Math.max(tempLine[1], tempLine[3]) - yRect;
        walls.add(new Rectangle2D.Double(xRect, yRect, wRect, hRect));

        xRect = Math.min(print[0], print[2]);
        wRect = Math.max(print[0], print[2]) - xRect;
        yRect = Math.min(print[1], print[3]);
        hRect = Math.max(print[1], print[3]) - yRect;
        System.out.println("wall = " + (int)xRect/10 + " " + (int)Math.max(print[1], print[3])/10 + " " + (int)Math.max(print[0], print[2])/10 + " " + (int)yRect/10);
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
