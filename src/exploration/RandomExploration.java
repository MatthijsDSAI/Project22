package exploration;

import agents.Agent;
import controller.Map.Map;
import controller.Map.tiles.Tile;
import utils.DirectionEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomExploration extends Exploration {

    private final Agent agent;
    int x,y;
    Map map;
    Tile tile;
    Random r = new Random();


    //Constructor
    public RandomExploration(Agent agent, Tile[][] tiles) {
        this.agent = agent;
//        this.x = tile.getX();
//        this.y = tile.getY();
    }

    public RandomExploration(Agent agent, int x, int y) {
        this.agent = agent;
        this.x = x;
        this.y = y;
    }

    public List<DirectionEnum> checkTile(){
        List<DirectionEnum> validMove= new ArrayList<>();
        if (map.getTiles()[x + 1][y].isWalkable()){
            validMove.add(DirectionEnum.EAST);
        }
        if (map.getTiles()[x - 1][y].isWalkable()){
           validMove.add(DirectionEnum.WEST);
        }
        if (map.getTiles()[x][y-1].isWalkable()){
            validMove.add(DirectionEnum.NORTH);
        }
        if (map.getTiles()[x][y+1].isWalkable()){
            validMove.add(DirectionEnum.SOUTH);
        }
        return validMove;
    }

//    public DirectionEnum randomDirection(){
//        int randomMove = (int) (Math.random()*3);
//        return switch (randomMove) {
//            case 0 -> DirectionEnum.WEST;
//            case 1 -> DirectionEnum.EAST;
//            case 2 -> DirectionEnum.NORTH;
//            default -> DirectionEnum.SOUTH;
//        };
//    }
    @Override
    public DirectionEnum makeMove(Agent agent) {
        List<DirectionEnum> l = checkTile();
        return l.get(r.nextInt(l.size()));
    }


    public String getExplorationName() {
        return "RandomExploration";
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
}
