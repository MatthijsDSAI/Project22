package GivenCode;

import controller.Scenario;

public class Explorer {
    protected String mapDoc;
    protected Scenario scenario;

    ExGamePlayer p;

    public static void main(String[] args){
        // the mapscenario should be passed as a parameter
        String mapD="testmap.txt";
        Explorer game = new Explorer(mapD);
        //game.writeGameFile();
        game.p.start();
    }

    public Explorer(String scn){
        mapDoc=scn;
        scenario = new Scenario(mapDoc);
        System.out.println("ok: " + scenario.getGameFile());
        p = new ExGamePlayer(scenario);
    }
}
