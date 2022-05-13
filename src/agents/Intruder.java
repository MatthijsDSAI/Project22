package agents;

import controller.Map.Map;
import controller.Map.tiles.Tile;
import controller.Scenario;
import exploration.BaseLineIntruder;
import exploration.Exploration;
import exploration.FrontierBasedExploration;
import exploration.RandomExploration;
import javafx.scene.paint.Color;

public class Intruder extends Agent{
    public double angleOfTarget;
    private double sprintSpeed;
    private boolean sprinting;
    private int stamina = 5;
    Color[] c = {Color.LAVENDER, Color.BROWN, Color.YELLOW, Color.PINK, null};
    int i = 0;
    public Map testmap;
    public Intruder(int x, int y, double angleOfTarget, Map testmap){
        super(x,y);
        this.testmap = testmap;
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
    public void createExplorationAlgorithm(String exploration, Tile[][] tiles) {
        switch (exploration){
            case "RandomExploration":
                this.exploration = new RandomExploration(this, tiles);
                break;
            case "BaseLineIntruder":
                this.exploration = new BaseLineIntruder(this, tiles);
                break;
            default:
                throw new RuntimeException("Invalid Algorithm passed");
        }

    }
}
