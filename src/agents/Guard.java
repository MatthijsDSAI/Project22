package agents;

import controller.Scenario;
import utils.DirectionEnum;
import utils.Utils;

public class Guard extends Agent{

    public Guard(int x, int y){
        super(x,y);
        this.baseSpeed = Scenario.config.getBASESPEEDGUARD();

    }
}
