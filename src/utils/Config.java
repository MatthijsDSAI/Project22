package utils;

public class Config {
    private double VISION;
    private double BASESPEEDINTRUDER;
    private double BASESPEEDGUARD;
    private double SPRINTSTEEDINTRUDER;
    private double SPRINTSPEEDGUARD;

    //store an instance of this in our main gamerunner class eventually
    //Then we can access that instance as a field from other classes and have all these constants in one place
    public Config(){
    }
    public double getVISION() {
        return VISION;
    }

    public void setVISION(double VISION) {
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

    public double getSPRINTSPEEDGUARD() {
        return SPRINTSPEEDGUARD;
    }

    public void setSPRINTSPEEDGUARD(double SPRINTSPEEDGUARD) {
        this.SPRINTSPEEDGUARD = SPRINTSPEEDGUARD;
    }
}
