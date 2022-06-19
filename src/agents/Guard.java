package agents;

import controller.Map.Map;
import controller.Map.tiles.Tile;
import controller.Scenario;
import controller.Visibility.Visibility;
import exploration.*;
import javafx.scene.paint.Color;
import utils.Config;
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
            case "CombinedGuard": // 10 12 14 16; x: 9 - 15, y: 11 - 17
                int[] temp = Scenario.config.getStandardizedAreaBoundaries();
                int size = 5; // this defines the area difference between the standardized area and TA

                int northBoundaryOfStandardized = temp[1] - size;
                int southBoundaryOfStandardized = temp[3] + size;
                int westBoundaryOfStandardized = temp[0] - size;
                int eastBoundaryOfStandardized = temp[2] + size;

                // adding Standardized Area to Map
                for (int i = westBoundaryOfStandardized; i < eastBoundaryOfStandardized; i++) {
                    map.getTile(i, northBoundaryOfStandardized).setStandardizedTA(true);
                }
                for (int i = northBoundaryOfStandardized; i < southBoundaryOfStandardized; i++) {
                    map.getTile(eastBoundaryOfStandardized, i).setStandardizedTA(true);
                }
                for (int i = eastBoundaryOfStandardized; i > westBoundaryOfStandardized; i--) {
                    map.getTile(i, southBoundaryOfStandardized).setStandardizedTA(true);
                }
                for (int i = southBoundaryOfStandardized; i > northBoundaryOfStandardized; i--) {
                    map.getTile(westBoundaryOfStandardized, i).setStandardizedTA(true);
                }

                this.exploration = new CombinedGuard(this, map, temp[1] - size, temp[3] + size, temp[0] - size, temp[2] + size);
                break;
            case "QLGuard":
                this.exploration = new QLGuard(this, map);
                break;
            default:
                throw new RuntimeException("Invalid Algorithm passed");

        }
    }

    @Override
    public Color getColor(){
        return Color.CYAN;
    }

    @Override
    public void computeVisibleTiles(Map map){
        this.visibleTiles = Visibility.computeVisibleTiles(map, this);
        int num = visibleTiles.size();
        if(hasRotatedOnPastIteration){
            this.visibleTiles.addAll(Visibility.computeVisibleTilesIntermediateAngle(map, this));
        }
        int count = 0;
        for(Tile tile : visibleTiles){
            tile.setExplored(true);
            ownMap.setTile(tile.clone());
            if(count<num)
                tile.setCurrentlyViewed(true);
            count++;
        }
        this.hasRotatedOnPastIteration = false;
    }
}
