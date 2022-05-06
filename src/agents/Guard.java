package agents;

import controller.Map.tiles.Tile;
import controller.Scenario;
import exploration.Exploration;
import exploration.FrontierBasedExploration;
import exploration.RandomExploration;
import utils.DirectionEnum;
import utils.Utils;

public class Guard extends Agent{

    public Guard(int x, int y){
        super(x,y);
        this.baseSpeed = Scenario.config.getBASESPEEDGUARD();
    }

    public Exploration createExplorationAlgorithm(String exploration, Tile[][] tiles) {
        switch (exploration){
            case "FrontierBasedExploration":
                return new FrontierBasedExploration(this, tiles);
            case "RandomExploration":
                return new RandomExploration(this, tiles);
        }
        throw new RuntimeException("Invalid Algorithm passed");
    }
}
