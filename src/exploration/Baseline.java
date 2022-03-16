package exploration;

import agents.Agent;
import controller.Map.tiles.Tile;
import utils.DirectionEnum;

public class Baseline extends Exploration{
    @Override
    public DirectionEnum makeMove() {
        return null;
    }

    @Override
    public String getExplorationName() {
        return "FrontierBasedExploration";
    }


}
