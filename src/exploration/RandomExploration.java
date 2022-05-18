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
    Tile[][] tiles;
    Random r = new Random();


    //Constructor
    public RandomExploration(Agent agent, Map map) {
        this.agent = agent;
        this.tiles = map.getTiles();

        this.x = agent.getX_position();
        this.y = agent.getY_position();
    }

    public RandomExploration(Agent agent, int x, int y) {
        this.agent = agent;
        this.x = x;
        this.y = y;
    }

    public List<DirectionEnum> checkTile(){
        List<DirectionEnum> validMove= new ArrayList<>();
        if (tiles[x + 1][y].isWalkable()){
            validMove.add(DirectionEnum.EAST);
        }
        if (tiles[x - 1][y].isWalkable()){
           validMove.add(DirectionEnum.WEST);
        }
        if (tiles[x][y-1].isWalkable()){
            System.out.println(tiles[x][y-1]);
            validMove.add(DirectionEnum.NORTH);
        }
        if (tiles[x][y+1].isWalkable()){
            validMove.add(DirectionEnum.SOUTH);
        }
        System.out.println(validMove);
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
        DirectionEnum dir = l.get(r.nextInt(l.size()));
        changePosition(dir);
        return dir;
    }

    private void changePosition(DirectionEnum dir) {
        if(dir == DirectionEnum.NORTH){
            y--;
        }
        if(dir == DirectionEnum.EAST){
            x++;
        }
        if(dir == DirectionEnum.SOUTH){
            y++;
        }
        if(dir == DirectionEnum.WEST){
            x--;
        }
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
