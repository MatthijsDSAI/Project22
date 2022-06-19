package agents;

import controller.Map.Map;
import controller.Map.tiles.Tile;
import controller.Scenario;
import controller.Visibility.Visibility;
import exploration.*;
import javafx.scene.paint.Color;

public class Intruder extends Agent{
    public double angleOfTarget;
    private double sprintSpeed;
    private boolean sprinting;
    private int stamina = 5;
    Color[] c = {Color.LAVENDER, Color.BROWN, Color.YELLOW, Color.PINK, null};
    int i = 0;
    private int numberOfTargetAreaEntries = 0;
    private int tOfLastEntry=-1;
    private int numOfTimestepsInTargetArea = 0;
    public Intruder(Tile tile, double angleOfTarget){
        super(tile);
        this.angleOfTarget = angleOfTarget;
        this.baseSpeed = Scenario.config.getBASESPEEDINTRUDER();
        this.sprintSpeed = Scenario.config.getSPRINTSTEEDINTRUDER();
        this.sprinting = false;
    }

    @Override
    public void addMarkers(int i, Color c, Map map) {
        super.addMarkers(i, c, map);
    }

    @Override
    public int getSpeed(){
        if(sprinting){
            return (int) sprintSpeed;
        }
        return (int) baseSpeed;
    }

    @Override
    public String getType() {
        return "Intruder";
    }

    public void sprint(){
        if(stamina>4){
            this.sprinting = true;
        }
        checkStamina();
    }

    public void checkStamina(){
        if(stamina<=0){
            sprinting = false;
        }
        if(sprinting){
            stamina--;
        }
    }

    public void handleRest(){
        if(!sprinting && stamina<5){
            stamina++;
        }
    }
    public double getAngleOfTarget() {
        return angleOfTarget;
    }

    @Override
    public void createExplorationAlgorithm(String exploration, Map map) {
        switch (exploration){
            case "RandomExploration":
                this.exploration = new RandomExploration(this, map);
                break;
            case "BaseLineIntruder":
                this.exploration = new BaseLineIntruder(this, map);
                break;
            case "CombinedIntruder":
                this.exploration = new CombinedIntruder(this, map);
                break;
            default:
                throw new RuntimeException("Invalid Algorithm passed");
        }

    }
    public boolean completedObjective(){
        numOfTimestepsInTargetArea++;
        return (numberOfTargetAreaEntries>1) || (numOfTimestepsInTargetArea>2);

    }

    public void incrementTargetEntries(int t){
        if(t-tOfLastEntry>2 || tOfLastEntry==-1)
            numberOfTargetAreaEntries++;
        if(t-tOfLastEntry>2)
            tOfLastEntry = t;
    }

    public void resetCounter(){
        numOfTimestepsInTargetArea=0;
    }

    @Override
    public Color getColor(){
        return Color.ORANGE;
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
            tile.setExploredForIntruders(true);
            ownMap.setTile(tile.clone());
            if(count<num)
                tile.setCurrentlyViewed(true);
            count++;
        }
        this.hasRotatedOnPastIteration = false;
    }
}
