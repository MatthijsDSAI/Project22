package exploration;

import agents.Agent;
import controller.Map.Map;
import controller.Map.tiles.Tile;
import utils.DirectionEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomExploration extends Exploration {

    private Agent agent;
    int x,y;
    Map map;
    Random r = new Random();


    //Constructor
    public RandomExploration(Agent agent, Map map) {
        this.map = map;
        this.agent = agent;
    }

    public List<DirectionEnum> checkTile(Agent agent){
        List<DirectionEnum> validMove= new ArrayList<>();
        if (checkIfWalkable(agent.getAgentPosition(), DirectionEnum.EAST)){
            validMove.add(DirectionEnum.EAST);
        }
        if (checkIfWalkable(agent.getAgentPosition(), DirectionEnum.WEST)){
           validMove.add(DirectionEnum.WEST);
        }
        if (checkIfWalkable(agent.getAgentPosition(), DirectionEnum.NORTH)){
            validMove.add(DirectionEnum.NORTH);
        }
        if (checkIfWalkable(agent.getAgentPosition(), DirectionEnum.SOUTH)){
            validMove.add(DirectionEnum.SOUTH);
        }
        return validMove;
    }

    public boolean checkIfWalkable(Tile agentTile, DirectionEnum dir) {
        int x = agentTile.getX();
        int y = agentTile.getY();

        if (dir == DirectionEnum.EAST) {
            return (map.getTile(x + 1, y).isWalkable());
        } else if (dir == DirectionEnum.WEST) {
            return (map.getTile(x - 1, y).isWalkable());
        } else if (dir == DirectionEnum.SOUTH) {
            return (map.getTile(x, y + 1).isWalkable());
        } else if (dir == DirectionEnum.NORTH) {
            return (map.getTile(x, y - 1).isWalkable());
        }
        return false;
    }

    @Override
    public DirectionEnum makeMove(Agent agent) {
        List<DirectionEnum> l = checkTile(agent);
        DirectionEnum dir = l.get(r.nextInt(l.size()));
        return dir;
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
