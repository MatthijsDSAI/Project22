import controller.GameRunner;
import controller.Scenario;

public class Explorer {

    public static void main(String[] args){
        String mapD="testMap1.txt";
        Scenario scenario = new Scenario(mapD);
        GameRunner gr = new GameRunner(scenario);
    }


}
