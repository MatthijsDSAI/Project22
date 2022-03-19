package controller.Map.tiles;

import agents.Agent;
import controller.Scenario;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.awt.*;


public abstract class Tile {

    private boolean exploredByDefault;
    private boolean walkable;
    private boolean seeThrough;
    private boolean explored;
    private Color c;
    private Tile type;
    private Agent agent;
    private int x;
    private int y;

    protected Tile(){}

    public Tile(boolean exploredByDefault, boolean walkable, boolean seeThrough, Color c, Tile type, Agent agent) {
        this.exploredByDefault = exploredByDefault;
        this.explored = exploredByDefault;
        this.walkable = walkable;
        this.seeThrough = seeThrough;
        this.c = c;
        this.type = type;
        this.agent = agent;
    }

    public void setExploredByDefault(boolean exploredByDefault) {this.exploredByDefault = exploredByDefault;}
    public boolean isExploredByDefault() {
        return exploredByDefault;
    }

    public void setWalkable(boolean walkable) {this.walkable = walkable;}
    public boolean isWalkable() {
        return walkable;
    }

    public void setSeeThrough(boolean seeThrough) {this.seeThrough = seeThrough;}
    public boolean isSeeThrough() {return seeThrough;}

    public Tile getType() {return type;}
    public void setType(Tile type) {this.type = type;}

    public int getX() {return this.x;}
    public void setX(int x) {this.x = x;}

    public int getY() {return y;}
    public void setY(int y) {this.y = y;}

    public abstract String toString();


    public Agent getAgent() { // should agent part be abstracted?
        if (agent != null)
            return agent;
        else return null;
    }
    public void addAgent(Agent agent) {
        this.agent = agent;
    }

    public void removeAgent() {this.agent = null;}

    public boolean hasAgent() {return (agent != null);}

    public javafx.scene.shape.Rectangle createRec(int x, int y){return new Rectangle(x, y,10,10);}

    public Color getColor() { //TODO: find a solution to this, we need 2 different type of colour here
        if(hasAgent()){
            return Scenario.config.getAgentColor();
        }
        if(explored){
            return Color.TAN;
        }
        return c;
    }
    public void setColor(Color c) {this.c = c;}

    public Tile clone(){
        return null;
    }

    public void setExplored(boolean b){
        explored = true;
    }

    public boolean getExplored() {
        return explored;
    }
}
