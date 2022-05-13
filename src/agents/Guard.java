package agents;

import controller.Map.Map;
import controller.Map.tiles.Tile;
import controller.Scenario;
import exploration.Exploration;
import exploration.FrontierBasedExploration;
import exploration.RandomExploration;
import javafx.scene.paint.Color;
import utils.DirectionEnum;
import utils.Utils;

public class Guard extends Agent{

    Color[] c = {Color.RED, Color.ORANGE, Color.GREEN, Color.WHITE, null};
    int i = 0;

    public Guard(int x, int y){
        super(x,y);
        this.baseSpeed = Scenario.config.getBASESPEEDGUARD();
    }

    @Override
    public void addMarkers(int i, Color c, Map map) {
        super.addMarkers(i, c, map);
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
