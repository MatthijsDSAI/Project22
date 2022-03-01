package controller.Map.tiles;

import agents.Agent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;



public abstract class Tile {

    private boolean exploredByDefault;
    private boolean walkable;
    private boolean seeThrough;
    private Color c;
    private Tile type;
    private Agent agent;
    private int x;
    private int y;

    protected Tile(){}

    public Tile(boolean exploredByDefault, boolean walkable, boolean seeThrough, Color c, Tile type, Agent agent) {
        this.exploredByDefault = exploredByDefault;
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

    public Color getC() {return c;}
    public void setC(Color c) {this.c = c;}

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
        else throw new RuntimeException("There is no agent on this tile");
    }
    public void addAgent(Agent agent) {this.agent = agent;}
    public void removeAgent() {this.agent = null;}
    public boolean hasAgent() {return (agent != null);}

    public javafx.scene.shape.Rectangle createRec(int x, int y){return new Rectangle(x, y,10,10);}

    public javafx.scene.paint.Color getColor() { //TODO: find a solution to this, we need 2 different type of colour here
        return c;
    }
    public void setColor(javafx.scene.paint.Color c) {this.c = c;}

}
