package controller;

import GUI.MapGui;
import agents.Agent;
import agents.Guard;
import agents.Intruder;
import agents.TestAgent;
import controller.Map.Map;
import utils.Config;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

//idea is to make this the class where we run store everything and have our main loop
public class GameRunner {
    private Map map;
    private final Config config = Scenario.config;
    private Agent agent;
    private ArrayList<Guard> guards;
    private ArrayList<Intruder> intruders;
    private MapGui gui;
    private Scenario scenario;
    private int t;
    private boolean isGameMode1;
    private String GuardAlgo;
    private String IntruderAlgo;

    public GameRunner(Scenario scenario, String GuardAlgo, String IntruderAlgo) {
        this.scenario = scenario;
        this.GuardAlgo = GuardAlgo;
        this.IntruderAlgo = IntruderAlgo;

        isGameMode1 = (scenario.getGameMode() == 1);
        init();
        GraphicsConnector graphicsConnector = new GraphicsConnector(this);
        graphicsConnector.setGuiHeight((scenario.getMapHeight()+1)*10);
        graphicsConnector.setGuiWidth((scenario.getMapWidth()+1)*10);
        gui = new MapGui();
        map.setGraphicsConnector(graphicsConnector);



        if(Scenario.config.GUI){
            try{
                gui.launchGUI(graphicsConnector);
            }
            catch(IllegalStateException e ){
                System.out.println("There has been an issue with the initialization of the GUI");
            }
        }

    }


    public Map getMap() {
        return map;
    }

    public void init(){
        map = new Map(scenario.getMapHeight()+1, scenario.getMapWidth()+1);
        map.loadMap(scenario);
        guards = map.getGuards();
        intruders = map.getIntruders();
        loadGuards();
        if (isGameMode1) {
            loadIntruders();
        }

        t = 0;
    }

    public void loadGuards() { // "loadGuards" and "loadIntruders" can later be combined if either of them doesn't need any additional code
        for (Guard guard: guards) {
            int x = guard.getX_position();
            int y = guard.getY_position();
            map.addAgent(guard, x, y);
            guard.setAgentPosition(map.getTile(x,y));
            guard.initializeEmptyMap(map);
            guard.computeVisibleTiles(map);
        }

    }

    public void loadIntruders() {
        for (Intruder intruder: intruders) {
            int x = intruder.getX_position();
            int y = intruder.getY_position();
            map.addAgent(intruder, x, y);
            intruder.setAgentPosition(map.getTile(x,y));
            intruder.initializeEmptyMap(map);
            intruder.computeVisibleTiles(map);
        }
    }


    //does nothing yet
    public void step(){
        t++;
        //map.moveAgent(agent, DirectionEnum.RIGHT.getDirection());
        //System.out.println(map.getAgentPosition(agent));
        //for(int i =0; i<Scenario.config.getBASESPEEDINTRUDER(); i++){
        //        map.moveAgent(agent, DirectionEnum.EAST);
//        agent.computeVisibleTiles(map);
        // }
        //map.getGraphicsConnector().updateGraphics();

        for (Guard guard: guards) {
            //guard.setCurrentDirection(guard.makeMove());
            map.test(guard, map);
            //map.moveAgent(guard);
            guard.computeVisibleTiles(map);
        }
        if (isGameMode1) {
            for (Intruder intruder : intruders) {
                intruder.setCurrentDirection(intruder.makeMove());
                map.moveAgent(intruder);
                intruder.computeVisibleTiles(map);
            }
        }
    }


    public void run(){
        AtomicBoolean explored = new AtomicBoolean(false);
        Thread t = new Thread(() ->{
            while(!explored.get()){
                try {
                    Thread.sleep(Scenario.config.getSleep());
                }
                catch(InterruptedException e){
                    System.out.println("Threading issue");
                }
                step();
                explored.set(map.isExplored());
                System.out.println(map.explored() + " of map has been explored");
                //map.printMap();

        }});
        t.start();

    }

    public ArrayList<Guard> getGuards() {return guards;}

    public ArrayList<Intruder> getIntruders() {return intruders;}

    public boolean isGameMode1() {return isGameMode1;}

    public String getGuardAlgo() {
        return GuardAlgo;
    }

    public String getIntruderAlgo() {
        return IntruderAlgo;
    }
}
