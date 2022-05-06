package agents;

import controller.Map.tiles.Tile;
import controller.Scenario;
import exploration.Exploration;
import exploration.FrontierBasedExploration;
import exploration.RandomExploration;

public class Intruder extends Agent{
    public double angleOfTarget;
    private double sprintSpeed;
    private boolean sprinting;
    private int stamina = 5;
    public Intruder(int x, int y, double angleOfTarget){
        super(x,y);
        this.angleOfTarget = angleOfTarget;
        this.baseSpeed = Scenario.config.getBASESPEEDINTRUDER();
        this.sprintSpeed = Scenario.config.getSPRINTSTEEDINTRUDER();
        this.sprinting = false;
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
            default:
                throw new RuntimeException("Invalid Algorithm passed");
        }

    }
}
