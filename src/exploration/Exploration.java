package exploration;

import agents.Agent;
import utils.DirectionEnum;

public abstract class Exploration {

    public Exploration() {}


    public abstract DirectionEnum makeMove(Agent agent);
}
