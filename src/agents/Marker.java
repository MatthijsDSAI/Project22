package agents;
import controller.Map.Map;
import controller.Map.tiles.Tile;
import javafx.scene.paint.Color;
import utils.DirectionEnum;

import java.util.ArrayList;

public class Marker{

    private int s=0;
    private Color color;
    private int number_markers, distance = -1;
    boolean isVisual = true;

    Marker(Color d, int number_markers)
    {
        this.s++;
        this.color =d;
        this.number_markers=number_markers;
    }

    public void setNumber_markers(int n){this.number_markers=n;}
    public Color getColor(){return color;}
    public int getNumber_markers(){return number_markers;}
    public int getDistance() {return distance;}
    public void setDistance(int d){ this.distance=d;}
}