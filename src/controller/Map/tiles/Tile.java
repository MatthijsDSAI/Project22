package controller.Map.tiles;

import agents.HumanPlayer;
import agents.Player;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Tile {

    private TileType type;
    private Player player;
    private boolean explored;
    private boolean walkable;
    private boolean seeThrough;
    private boolean hasPlayer;


    private Color c;
    private int x;
    private int y;

    public Tile(TileType type){
        this.type = type;
        walkable = type.isWalkable();
        seeThrough = type.isSeeThrough();
        explored = type.isExploredByDefault();
        player = null;
        c = null;
    }

    public Tile(TileType type, int x, int y){
        this.type = type;
        walkable = type.isWalkable();
        seeThrough = type.isSeeThrough();
        explored = type.isExploredByDefault();
        player = null;
        c = null;
        this.x = x;
        this.y = y;
    }

    public Player getPlayer() {
        if (hasPlayer)
            return player;
        else throw new RuntimeException("There is no player on this tile");
    }

    public boolean isExplored() {
        return explored;
    }

    public boolean isWalkable() {
        return walkable;
    }

    public boolean isSeeThrough() {
        return seeThrough;
    }

    public String toString(){
        if(!hasPlayer)
            return type.toString();
        return type.toString() + " + p";
    }

    public void addPlayer(Player player) {
        this.player = player;
        hasPlayer = true;
    }

    //Boyun,you can use this method to get the type, it will return for example "wall" or "floor"
    public String getTypeAsString(){
        return type.toString();
    }

    public void removePlayer() {
        this.player = null;
        hasPlayer = false;
    }

    public boolean isExploredByDefault() {
        return type.isExploredByDefault();
    }

    public boolean hasPlayer() {
        return hasPlayer;
    }

    public Rectangle createRec(int x, int y){
        return new Rectangle(x, y,10,10);
    }

    public Color getColor() {
        return c;
    }

    public void setColor(Color c) {
        this.c = c;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public TileType getType() {
        return type;
    }

    public void setType(TileType type) {
        this.type = type;
    }
}
