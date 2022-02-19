package controller.Map.tiles;

import agents.Player;

public abstract class TileType {

    protected boolean walkable;
    protected boolean seeThrough;
    protected TileType type;
    protected Player player;
    protected boolean hasPlayer;
    protected boolean explored;
    protected int speed; // just made this up, maybe we can have different tiles where speed is different on it
    protected boolean muteSound;

    public boolean isMuteSound() {return muteSound;}
    public void setMuteSound(boolean muteSound) {this.muteSound = muteSound;}

    public boolean isExplored() {return explored;}
    public void setExplored(boolean explored) {this.explored = explored;}

    public boolean isWalkable() {return walkable;}
    public void setWalkable(boolean walkable) {this.walkable = walkable;}

    public int getSpeed() {return speed;}
    public void setSpeed(int speed) {this.speed = speed;}

    public boolean isSeeThrough() {return seeThrough;}
    public void setSeeThrough(boolean seeThrough) {this.seeThrough = seeThrough;}

    public TileType getType() {return type;}
    public void setType(TileType type) {this.type = type;}

    public void setPlayer(Player player) {
        this.player = player;
        setHasPlayer(true);
    }
    public Player getPlayer() {
        if (hasPlayer)
            return player;
        else throw new RuntimeException("There is no player on this tile");
    }
    public void removePlayer() {
        this.player = null;
        setHasPlayer(false);
    }

    public boolean getHasPlayer() {return hasPlayer;}
    public void setHasPlayer(boolean hasPlayer) {this.hasPlayer = hasPlayer;}

    //Boyun,you can use this method to get the type, it will return for example "wall" or "floor"
    public String getTypeAsString(){ // I get your idea but I think it's useless, because you can use getType method instead
        return type.toString();
    }

    @Override
    public String toString() {
        return "TileType{" +
                "walkable=" + walkable +
                ", seeThrough=" + seeThrough +
                ", type=" + type +
                ", player=" + player +
                ", hasPlayer=" + hasPlayer +
                ", explored=" + explored +
                ", speed=" + speed +
                '}';
    }
}
