package exploration;

import agents.Agent;
import controller.Map.tiles.Tile;
import utils.DirectionEnum;

public class RandomExploration extends Exploration {

    @Override
    public DirectionEnum makeMove() {
        int randomMove = (int) (Math.random()*3);
        return switch (randomMove) {
            case 0 -> DirectionEnum.WEST;
            case 1 -> DirectionEnum.EAST;
            case 2 -> DirectionEnum.NORTH;
            default -> DirectionEnum.SOUTH;
        };
    }

    @Override
    public String getExplorationName() {
        return "RandomExploration";
    }

    @Override
    public void dfs(Tile x, Agent a) {

    }
}