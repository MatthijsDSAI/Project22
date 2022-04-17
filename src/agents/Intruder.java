package agents;

public class Intruder extends Agent{
    public double angleOfTarget;
    public Intruder(int x, int y, double angleOfTarget){
        super(x,y);
        this.angleOfTarget = angleOfTarget;
    }
}
