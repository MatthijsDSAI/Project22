package agents;

import controller.Map.Map;
import controller.Map.tiles.Tile;
import controller.Scenario;
import exploration.BaselineGuard;
import exploration.Exploration;
import exploration.FrontierBasedExploration;
import exploration.RandomExploration;
import javafx.scene.paint.Color;
import utils.DirectionEnum;
import utils.Utils;

public class Guard extends Agent{

    Color[] c = {Color.RED, Color.ORANGE, Color.GREEN, Color.WHITE, null};
    int i = 0;

    public Guard(Tile tile){
        super(tile);
        this.baseSpeed = Scenario.config.getBASESPEEDGUARD();
    }

    @Override
    public void addMarkers(int i, Color c, Map map) {
        super.addMarkers(i, c, map);
    }

    @Override
    public String getType() {
        return "Guard";
    }

    @Override
    public void createExplorationAlgorithm(String exploration, Map map) {
        switch (exploration) {
            case "FrontierBasedExploration":
                this.exploration = new FrontierBasedExploration(this, map);
                break;
            case "RandomExploration":
                this.exploration = new RandomExploration(this, map);
                break;
            case "BaseLineGuard":
                this.exploration = new BaselineGuard(this, map);
                break;
            default:
                throw new RuntimeException("Invalid Algorithm passed");

        }
    }

    @Override
    public Color getColor(){
        return Color.CYAN;
    }
}
