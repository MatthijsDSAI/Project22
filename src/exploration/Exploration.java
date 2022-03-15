package exploration;

import utils.DirectionEnum;

public abstract class Exploration {

    public Exploration() {}

    public abstract DirectionEnum makeMove();
    public abstract String getExplorationName();

}
