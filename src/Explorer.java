import controller.GameRunner;
import controller.Scenario;

public class Explorer {
    protected String mapDoc;
    protected Scenario scenario;


    public static void main(String[] args){
        String mapD="testmap.txt";
        Scenario scenario = new Scenario(mapD);
        GameRunner gr = new GameRunner(scenario);
    }


}
