package agents;

import utils.DirectionEnum;
import utils.Utils;

public class Guard extends Agent{
    public boolean enterTeleportal;
    public boolean firstAgent;
    public static boolean isFirstAgent = true;
    public boolean enteredTeleportal = false;
    public Guard(int x, int y){
        super(x,y);
        enterTeleportal = isFirstAgent;
        if(enterTeleportal){
            angle = DirectionEnum.SOUTH.getAngle();
        }
        firstAgent = isFirstAgent;
        Guard.isFirstAgent = false;
    }
}
