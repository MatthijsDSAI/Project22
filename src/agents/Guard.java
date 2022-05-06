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

    @Override
    public void createExplorationAlgorithm(String exploration, Tile[][] tiles) {
        System.out.println(exploration);
        switch (exploration) {
            case "FrontierBasedExploration":
                this.exploration = new FrontierBasedExploration(this, tiles);
                break;
            case "RandomExploration":
                this.exploration = new RandomExploration(this, tiles);
                break;
            default:
                throw new RuntimeException("Invalid Algorithm passed");

        }
    }
}
