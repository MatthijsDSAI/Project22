package controller.Map.tiles;

import agents.HumanPlayer;
import agents.Player;

public class Tile {

    private TileType type;
    private Player player;
    private boolean explored;
    private boolean walkable;
    private boolean seeThrough;
    private boolean hasPlayer;



    public Tile(TileType type){
        this.type = type;
        walkable = type.isWalkable();
        seeThrough = type.isSeeThrough();
        explored = type.isExploredByDefault();
        player = null;
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
}
