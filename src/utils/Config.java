package utils;

import javafx.scene.paint.Color;


public class Config {
    private int VISION = 5;
    private double BASESPEEDINTRUDER;
    private double BASESPEEDGUARD;
    private double SPRINTSTEEDINTRUDER;
    private Color agentColor = Color.CYAN;
    public boolean GUI = true;
    private int SLEEP = 500; //must be >50


    //store an instance of this in our main gamerunner class eventually
    //Then we can access that instance as a field from other classes and have all these constants in one place
    public Config(){
    }
    public int getSleep(){
        return SLEEP;
    }
    public int getDistanceViewing() {
        return VISION;
    }

    public void setDistanceViewing(int VISION) {
        this.VISION = VISION;
    }

    public double getBASESPEEDINTRUDER() {
        return BASESPEEDINTRUDER;
    }

    public void setBASESPEEDINTRUDER(double BASESPEEDINTRUDER) {
        this.BASESPEEDINTRUDER = BASESPEEDINTRUDER;
    }

    public double getBASESPEEDGUARD() {
        return BASESPEEDGUARD;
    }

    public void setBASESPEEDGUARD(double BASESPEEDGUARD) {
        this.BASESPEEDGUARD = BASESPEEDGUARD;
    }

    public double getSPRINTSTEEDINTRUDER() {
        return SPRINTSTEEDINTRUDER;
    }

    public void setSPRINTSTEEDINTRUDER(double SPRINTSTEEDINTRUDER) {
        this.SPRINTSTEEDINTRUDER = SPRINTSTEEDINTRUDER;
    }

    public Color getAgentColor(){
        return agentColor;
    }

}
