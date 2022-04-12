package exploration;

import agents.Agent;
import agents.Guard;
import controller.Map.Map;
import controller.Map.tiles.Tile;
import utils.DirectionEnum;

public abstract class Exploration {

    public Exploration() {}


    public abstract DirectionEnum makeMove(Agent agent);
}
