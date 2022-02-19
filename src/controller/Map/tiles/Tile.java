package controller.Map.tiles;

import agents.HumanPlayer;
import agents.Player;

public class Tile extends TileType{

    public Tile(){ // on default will create floor if no type given
        this.type = new Floor();
        player = null;
    }

    public Tile(TileType type){
        this.type = type;
        walkable = type.isWalkable();
        seeThrough = type.isSeeThrough();
        explored = type.isExplored();
        player = null;
    }

    public String toString(){
        if(!hasPlayer)
            return type.toString();
        return type.toString() + " + p";
    }

}
