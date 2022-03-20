package agents;

import controller.Map.tiles.Tile;
import javafx.scene.paint.Color;
import utils.DirectionEnum;

import java.util.ArrayList;

public class MarkerType {

    public String name;
    private int number;
    //the distance from which it's perceived.
   private int distance;
   private boolean isVisual, isPheromone;
   private Color markerColor;
   Tile tile;

    public MarkerType(){}

    public MarkerType(String typename, int number_markers, int distance, boolean visual, Color c){
        this.name=typename;
        this.number=number_markers;
        this.distance=distance;
        this.isVisual=visual;
        this.isPheromone=false;
        markerColor=c;
    }

    public MarkerType(String typename, int number_markers, int distance, boolean isPheromone){
        this.name=typename;
        this.number=number_markers;
        this.distance=distance;
        this.isVisual=false;
        this.isPheromone=isPheromone;
    }

    public void setMarkerColor(Color c){this.markerColor=c;}

    public void addMarker(Agent agent){
        if(isVisual==true)
        {
                agent.ownMap.getTile(agent.getX_position(), agent.getY_position()).setColor(markerColor);
        }
        agent.ownMap.getTile(agent.getX_position(), agent.getY_position()).setHasMarker(true);
        tile = agent.getAgentPosition();
        number--;
    }

    //Check if the marker is sensed by an agent
    public boolean isInArea(Agent agent){
       if(isVisual==false)
       {
          double d = Math.sqrt(Math.abs(tile.getX()-agent.getX_position())*Math.abs(tile.getX()-agent.getX_position())+Math.abs(tile.getY()-agent.getY_position())*Math.abs(tile.getY()-agent.getY_position()));
          if(d<=distance)
              return true;
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

    public DirectionEnum performAction(Agent agent){
        double initialAngle = agent.getAngle();
        if(isVisual==true){
            if(isInArea(agent)==true)
                if(name.equals("Red"))
                    //Go east
                    return DirectionEnum.getDirection(270);
                if(name.equals("Orange"))
                    //Go north
                    return DirectionEnum.getDirection(0);
                    //Go south
                if(name.equals("Green"))
                    return DirectionEnum.getDirection(180);
                    //Go south
                if(name.equals("White"))
                    return DirectionEnum.getDirection(90);
        }
        return DirectionEnum.getDirection(agent.angle);
    }

    public void setNumber(int number_markers){ this.number=number_markers; }

    public int getDistance(){return distance;}

}
