package controller.Map.tiles;

import agents.Agent;
import controller.Scenario;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.awt.*;


/*
 * Abstract class that all the tiles are based on. Contains mostly booleans that are important for logic.
 * Also contains a color, for GUI. If an agent is present on the tile, the tile also holds a reference to it.
 */
public abstract class Tile {

    private boolean exploredByDefault;
    private boolean exploredByDefaultForIntruders;
    private boolean walkable;
    private boolean seeThrough;
    private boolean explored;
    private boolean exploredForIntruders;
    private boolean hasMarker;
    private Color c;
    private Tile type;
    private Agent agent;
    private int x;
    private int y;
    private double sound;
    private boolean currentlyViewed = false;
    private boolean isP=false;
    private boolean isStandardizedTA = false;

    protected Tile(){}

    public Tile(boolean exploredByDefault, boolean walkable, boolean seeThrough, Color c, Tile type, Agent agent) {
        this.exploredByDefault = exploredByDefault;
        this.explored = exploredByDefault;
        this.exploredByDefaultForIntruders = exploredByDefault;
        this.exploredForIntruders = exploredByDefault;
        this.walkable = walkable;
        this.seeThrough = seeThrough;
        this.c = c;
        this.type = type;
        this.agent = agent;
        this.hasMarker=false;
    }

    public void setHasMarker(Boolean t){ this.hasMarker=t; }
    public boolean getHasMarker(){return  hasMarker;}

    public void setExploredByDefault(boolean exploredByDefault) {this.exploredByDefault = exploredByDefault;}
    public boolean isExploredByDefault() {
        return exploredByDefault;
    }

    public boolean isExploredByDefaultForIntruders() {
        return exploredByDefaultForIntruders;
    }

    public void setExploredByDefaultForIntruders(boolean exploredByDefaultForIntruders) {
        this.exploredByDefaultForIntruders = exploredByDefaultForIntruders;
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

    public String getCoordinates(){
        return "(" + this.x + ", " + this.y + ")";
    }

    public Agent getAgent() { // should agent part be abstracted?
        if (agent != null)
            return agent;
        else return null;
    }

    public void addAgent(Agent agent) {
        // this.setWalkable(false);
        this.agent = agent;
    }

    public void removeAgent() {
        // this.setWalkable(true);
        this.agent = null;
    }

    public boolean hasAgent() {return (agent != null);}

    public javafx.scene.shape.Rectangle createRec(int x, int y){return new Rectangle(x, y,10,10);}

    public Color getColor() {

        if (hasAgent() && !this.hasMarker) {
            return getAgent().getColor();
        }
        else if ((0.5 < getSound()) && (getSound() <= 1)) {
            return c.darker().darker();
        } else if ((0 < getSound()) && (getSound() <= 0.5)) {
            return c.darker();
        } else {

            if(Scenario.config.getGameMode()==1){
                if (currentlyViewed && this.toString().equals("Floor")) {
                    return Color.TAN;
                }
            } else if(Scenario.config.getGameMode()==0){
                if (currentlyViewed && this.toString().equals("Floor")) {
                    return Color.TAN;
                }
            }
            else{
                if(currentlyViewed && this.toString().equals("Floor")){
                    return Color.TAN.darker();
                }
            }

            return c;
        }
    }

    // calculates the Manhattan distance between this and other
    public int manhattanDist(Tile other) {
        return (Math.abs(this.getX() - other.getX()) + Math.abs(this.getY() - other.getY()));
    }

    public boolean isStandardizedTA() {
        return isStandardizedTA;
    }

    public void setStandardizedTA(boolean standardizedTA) {
        isStandardizedTA = standardizedTA;
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

    public boolean isExploredForIntruders() {
        return exploredForIntruders;
    }

    public void setExploredForIntruders(boolean exploredForIntruders) {
        this.exploredForIntruders = exploredForIntruders;
    }

    public boolean hasSound(){
        return sound!=0;
    }

    public double getSound(){
        return sound;
    }

    public void setSound(double sound){ // 1 is full sound
        this.sound = sound;
    }

    public void setCurrentlyViewed(boolean b){
        currentlyViewed = b;
    }

    public boolean equals(Tile tile){
        return this.getX()==tile.getX() && this.getY()== tile.getY();
    }

    public void setIsPheromone(boolean b) {this.isP=b;}

    public boolean getIsPheromone(){return this.isP;}
}
