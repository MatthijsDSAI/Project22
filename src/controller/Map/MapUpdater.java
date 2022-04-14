package controller.Map;

import agents.Agent;
import agents.Guard;
import agents.Intruder;
import controller.Area;
import controller.Map.tiles.*;
import controller.Scenario;
import controller.TelePortal;
import utils.DirectionEnum;

import java.util.ArrayList;

public class MapUpdater {

    public static Agent moveAgent(Map map, Agent agent, DirectionEnum direction){
        if(Scenario.config.DEBUG){
            System.out.println("Agent movement initialized");
            System.out.println("Current angle: " + agent.getAngle());
        }
        Tile fromTile = null;
        Tile toTile = null;
        if(agent.getAngle() != direction.getAngle()) {
            agent.rotate(direction.getAngle());
            if(agent.getAngle() == direction.getAngle()) {
                fromTile = agent.getAgentPosition();
                toTile = Map.getTileFromDirection(map, agent.getAgentPosition(), direction);

                changeTiles(map, agent, fromTile, toTile);
            }
        }
        else{
            fromTile = agent.getAgentPosition();
            toTile = Map.getTileFromDirection(map, agent.getAgentPosition(), direction);
            changeTiles(map, agent, fromTile, toTile);
        }
        assert toTile != null;
        if (toTile.toString().equals("TelePortal")) {

            changeTiles(map, agent, fromTile, toTile);
            TeleportalTile teleportalTile = (TeleportalTile) map.getTile(toTile.getX(), toTile.getY());
            fromTile = toTile;
            toTile = map.getTile(teleportalTile.getTargetX(), teleportalTile.getTargetY());
            changeTiles(map, agent, fromTile, toTile);
            agent.setAngle((int) teleportalTile.getAngle());
            ((Guard)agent).enterTeleportal=false;
            ((Guard)agent).enteredTeleportal=true;
        }
        return agent;
    }

    public static void changeTiles(Map map, Agent agent, Tile fromTile, Tile toTile){
        if(toTile.isWalkable()) {
            map.getTile(fromTile.getX(),fromTile.getY()).removeAgent();
            map.getTile(toTile.getX(),toTile.getY()).addAgent(agent);
            agent.setAgentPosition(toTile);
        }
        else{
            throw new RuntimeException("Can not move to tile " + toTile.getX() + ", " + toTile.getY());
        }
    }

    /*
     * Initialization of guards, including putting them on the map.
     */
    public static void initGuards(Map map, ArrayList<Guard> guards) { // "loadGuards" and "loadIntruders" can later be combined if either of them doesn't need any additional code
        for (Guard guard: guards) {
            int x = guard.getX_position();
            int y = guard.getY_position();
            Map.addAgent(map, guard, x, y);
            guard.setAgentPosition(map.getTile(x,y));
            guard.initializeEmptyMap(map);
            guard.computeVisibleTiles(map);
        }
    }

    public static void initIntruders(Map map, ArrayList<Intruder> intruders) {
        for (Intruder intruder: intruders) {
            int x = intruder.getX_position();
            int y = intruder.getY_position();
            Map.addAgent(map, intruder, x, y);
            intruder.setAgentPosition(map.getTile(x,y));
            intruder.initializeEmptyMap(map);
            intruder.computeVisibleTiles(map);
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////
    ///////////////////---------INITIALIZATION----------/////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////

    public static void loadMap(Map map, Scenario scenario){
        int left;
        int right;
        int up;
        int down;
        initializeEmptyMap(map);

        for (Area area : scenario.getAreas()) {
            left = area.getLeftBoundary();
            right = area.getRightBoundary();
            up = area.getTopBoundary();
            down = area.getBottomBoundary();

            switch (area.getType()) {
                case "targetArea":
                    for (int i = left; i <= right; i++) {
                        for (int j = down; j <= up; j++) {
                            map.setTile(new TargetArea(i,j));
                        }
                    }
                    break;
                case "spawnAreaIntruders":
                    for (int i = left; i <= right; i++) {
                        for (int j = down; j <= up; j++) {
                            map.setTile(new SpawnAreaIntruders(i,j));
                        }
                    }
                    break;
                case "spawnAreaGuards":
                    for (int i = left; i <= right; i++) {
                        for (int j = down; j <= up; j++) {
                            map.setTile(new SpawnAreaGuards(i,j));
                        }
                    }
                    break;
                case "wall":
                    for (int i = left; i <= right; i++) {
                        for (int j = down; j <= up; j++) {
                            map.setTile(new Wall(i,j));
                        }
                    }
                    break;
                case "shaded":
                    for (int i = left; i <= right; i++) {
                        for (int j = down; j <= up; j++) {
                            map.setTile(new Shaded(i,j));
                        }
                    }
                    break;
            }
        }

        for(TelePortal telePortal : scenario.getTeleportals()){ // teleportal here because it needs additional parameters
            left = telePortal.getLeftBoundary();
            right = telePortal.getRightBoundary();
            up = telePortal.getTopBoundary();
            down = telePortal.getBottomBoundary();
            for (int i = left; i <= right; i++) {
                for (int j = down; j <= up; j++) {
                    map.setTile(new TeleportalTile(i, j, telePortal.getxTarget(), telePortal.getyTarget(), telePortal.getOutOrientation()));
                }
            }
        }

        if (scenario.getGameMode() == 0) {
            for (int i = 0; i < scenario.getNumGuards(); i++) {
                MapUpdater.spawnGuard(map, scenario.getSpawnAreaGuards());
            }
        } else if (scenario.getGameMode() == 1) {
            for (int i = 0; i < scenario.getNumGuards(); i++) {
                MapUpdater.spawnGuard(map, scenario.getSpawnAreaGuards());
            }
            for (int i = 0; i < scenario.getNumIntruders(); i++) {
                MapUpdater.spawnGuard(map, scenario.getSpawnAreaIntruders());
            }
        }
    }

    public static void spawnGuard(Map map, Area givenArea){
        int rand1 = (int) (Math.random() * (givenArea.getRightBoundary() - givenArea.getLeftBoundary())) + givenArea.getLeftBoundary();
        int rand2 = (int) (Math.random() * (givenArea.getBottomBoundary() - givenArea.getTopBoundary())) + givenArea.getTopBoundary();
        Guard tempAgent = new Guard(rand1, rand2);
        tempAgent.setAgentPosition(map.getTile(rand1,rand2));
        map.getGuards().add(tempAgent);
        map.getTile(rand1, rand2).addAgent(tempAgent);
    }

    public static void spawnIntruder(Map map, Area givenArea){
        int rand1 = (int) (Math.random() * (givenArea.getRightBoundary() - givenArea.getLeftBoundary())) + givenArea.getLeftBoundary();
        int rand2 = (int) (Math.random() * (givenArea.getBottomBoundary() - givenArea.getTopBoundary())) + givenArea.getTopBoundary();
        Intruder tempAgent = new Intruder(rand1, rand2);
        tempAgent.setAgentPosition(map.getTile(rand1,rand2));
        map.getIntruders().add(tempAgent);
        map.getTile(rand1, rand2).addAgent(tempAgent);
    }

    private static void initializeEmptyMap(Map map) {
        for (int i = 0; i < map.getTiles()[0].length; i++) {
            for (int j = 0; j < map.getTiles().length; j++) {
                map.setTile(new Floor(i,j));
            }
        }
    }
}
