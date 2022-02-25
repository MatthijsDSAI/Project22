package controller;

import GUI.MapGui;
import agents.Agent;
import agents.TestAgent;
import controller.Map.Map;
import utils.DirectionEnum;

//idea is to make this the class where we run store everything and have our main loop
public class GameRunner {
    private Scenario scenario;
    private Map map;

    private Agent agent;

    private int t;

    public GameRunner(Scenario scenario) {
        MapGui gui = new MapGui();
        this.scenario = scenario;
        init(scenario);
        
        TelePortal.GraphicsConnector graphicsConnector = new TelePortal.GraphicsConnector(this);
        if(Scenario.config.GUI){
            try{
                gui.launchGUI(graphicsConnector);
            }
            catch(RuntimeException e ){
                System.out.println("There has been an issue with the initialization of the GUI");
            }
        }
    }


    public Map getMap() {
        return map;
    }

    public void init(Scenario scenario){
        agent = new TestAgent(0,0);
        map = new Map(scenario.getMapHeight()+1, scenario.getMapWidth()+1, agent);
        map.loadMap(scenario);
        map.printMap();
        int x = 0, y = 0;
        map.addAgent(agent,x,y);
        t = 0;
    }


    //does nothing yet
    public void step(){
        t++;
        map.moveAgent(agent, DirectionEnum.RIGHT.getDirection());
        System.out.println(map.getAgentPosition(agent));
        for(int i =0; i<Scenario.config.getBASESPEEDINTRUDER(); i++){
            //agent.update();
            //if i understand correctly per timestep we can do 15 things, where 15 is the speed
            //so we can either walk 15 steps, or walk 14 and turn once, etc.
            //which is decided in agent.update()
        }
    }


    public void run(){
        boolean explored = false;
        while(!explored){
            try {
                Thread.sleep(50);
            }
            catch(InterruptedException e){
                System.out.println("Threading issue");
            }
            step();
            explored = map.isExplored();
            //System.out.println(map.explored() + " of map has been explored");
            //map.printMap();
        }
    }



    public Agent getAgent() {
        return agent;
    }
}
