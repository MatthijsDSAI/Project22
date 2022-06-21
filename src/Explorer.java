import controller.GameRunner;
import controller.Scenario;

public class Explorer {

    public static void main(String[] args){
        String mapD="experimentMap_1.txt";
        Scenario scenario = new Scenario(mapD);
        GameRunner gr = new GameRunner(scenario);
    }

}
