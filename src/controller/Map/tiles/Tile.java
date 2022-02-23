package controller.Map.tiles;

import agents.HumanPlayer;
import agents.Player;

public class Tile {

    private TileType type;
    private Player player;
    private boolean explored;
    private boolean walkable;
    private boolean seeThrough;
    public Tile(TileType type){
        this.type = type;
        walkable = type.isWalkable();
        seeThrough = type.isSeeThrough();
        explored = type.isExploredByDefault();
        player = null;
    }

    public Player getPlayer() {
        return player;
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
        return type.toString();
    }

    public void addPlayer(Player player) {
        this.player = player;
    }

    //Boyun,you can use this method to get the type, it will return for example "wall" or "floor"
    public String getTypeAsString(){
        return type.toString();
    }

    public void removePlayer() {
        this.player = null;
    }

    public boolean isExploredByDefault() {
        return type.isExploredByDefault();
    }
}
