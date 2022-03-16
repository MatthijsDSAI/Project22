package exploration;

import agents.Agent;
import controller.Map.Map;
import controller.Map.tiles.Tile;
import utils.DirectionEnum;

public abstract class Exploration {

    public Exploration() {}

    public abstract DirectionEnum makeMove();
    public abstract String getExplorationName();

    public abstract void dfs(Tile x, Agent a);
}
