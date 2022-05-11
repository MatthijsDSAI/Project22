package agents;
import controller.Map.tiles.Tile;
import javafx.scene.paint.Color;
import utils.DirectionEnum;

import java.util.ArrayList;

public enum Marker{
    Marker1(1, DirectionEnum.getDirection(270)),
    Marker2(2, DirectionEnum.getDirection(0)),
    Marker3(3, DirectionEnum.getDirection(180)),
    Marker4(4, DirectionEnum.getDirection(90)),
    Marker5(5, null);

    private int s;
    private DirectionEnum direction;
    private int number_markers, distance;
    boolean isVisual = true;
    ArrayList<Tile> tile = new ArrayList<Tile>();

    Marker(int s, DirectionEnum d)
    {
        this.s = s;
        this.direction =d;
    }

    public void setSpecifics(int number_markers, int distance){
        this.number_markers=number_markers;
        this.distance = distance;
    }

    public void setNumber_markers(int n){this.number_markers=n;}

    public void addMarker(Agent agent, Color c){
        if(c != null)
        {
            isVisual=true;
            agent.ownMap.getTile(agent.getX_position(), agent.getY_position()).setColor(c);
        }
        else
            isVisual=false;
        agent.ownMap.getTile(agent.getX_position(), agent.getY_position()).setHasMarker(true);
        tile.add(agent.ownMap.getTile(agent.getX_position(), agent.getY_position()));
        number_markers--;
    }

    public boolean isInArea(Agent agent){
        if(isVisual==false)
        {
            for(Tile t : tile) {
                double d = Math.sqrt(Math.abs(t.getX() - agent.getX_position()) * Math.abs(t.getX() - agent.getX_position()) + Math.abs(t.getY() - agent.getY_position()) * Math.abs(t.getY() - agent.getY_position()));
                    if (d <= distance)
                        return true;
            }
        }
        else
        {
            //check if the tile is in the visual field
            //does it make sense to also compute the visible tiles here or is it enough to just get the visible tiles from the agent parameter?
            agent.computeVisibleTiles(agent.ownMap);
            ArrayList<Tile> visibleTiles = agent.getVisibleTiles();
            for(Tile t : visibleTiles){
                //if(t.getY() == tile.getY() && t.getX()== tile.getX())
                if(t.getHasMarker()==true)
                    return true;
            }
        }
        return false;
    }

    public DirectionEnum getDirection(){return direction;}
    public int getNumber_markers(){return number_markers;}
    public int getDistance() {return distance;}
    public void setDistance(int d){ this.distance=d;}
}
