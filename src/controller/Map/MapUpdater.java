package controller.Map;

import agents.Agent;
import agents.Guard;
import agents.Intruder;
import controller.Area;
import controller.Hearing.Noise;
import controller.Map.tiles.*;
import controller.Scenario;
import controller.TelePortal;
import exploration.Exploration;
import javafx.scene.paint.Color;
import utils.DirectionEnum;
import utils.Utils;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MapUpdater {

    public static void moveAgent(Map map, Agent agent, DirectionEnum direction){
        if(Scenario.config.DEBUG){
            System.out.println("Agent movement initialized");
            System.out.println("Current angle: " + agent.getAngle());
        }
        Tile fromTile = null;
        Tile toTile = null;
        System.out.println(direction);

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
        }
    }

    public static void changeTiles(Map map, Agent agent, Tile fromTile, Tile toTile){
        if(toTile.isWalkable()) {
            map.getTile(fromTile.getX(),fromTile.getY()).removeAgent();
            map.getTile(toTile.getX(),toTile.getY()).addAgent(agent);

            Noise.removeNoises(map, map.getTile(fromTile.getX(),fromTile.getY()));
            Noise.addNoises(map, map.getTile(toTile.getX(),toTile.getY()));

            if (!map.getTile(fromTile.getX(),fromTile.getY()).getHasMarker()) map.getTile(fromTile.getX(),fromTile.getY()).setColor(Color.BLUEVIOLET);

            if (agent.getType().equals("Intruder") && (!map.getTile(fromTile.getX(),fromTile.getY()).getHasMarker())) {
                map.getTile(fromTile.getX(),fromTile.getY()).setColor(Color.CHARTREUSE);
            }

            agent.setAgentPosition(toTile);
        }
        else{
            throw new RuntimeException("Can not move to tile " + toTile.getX() + ", " + toTile.getY());
        }
    }


    /*
     * Call made to algorithm to rotate the agents to a certain direction.@a
     */
    public static void moveGuards(Map map, int j) {
        ArrayList<Guard> guards = map.getGuards();
        for(int i = guards.size()-1; i>=0; i--){
            Guard guard = guards.get(i);
            if (j == 0 || j % (Scenario.config.getTimeStepSize() / guard.getSpeed()) == 0) {
                Utils.sleep(20);
                Exploration explorer = guard.getExploration();
                DirectionEnum dir = explorer.makeMove(guard);
                MapUpdater.moveAgent(map, guard, dir);
                MapUpdater.refreshCurrentlyViewed(map, guard.getVisibleTiles());
                guard.computeVisibleTiles(map);
                MapUpdater.checkIntruderCapture(guard, map);
            }
        }
    }


    public static void moveIntruders(Map map, int j) {
        ArrayList<Intruder> intruders = map.getIntruders();
        for(int i = intruders.size()-1; i>=0; i--){
            Intruder intruder = intruders.get(i);
            if (j == 0 || j%(Scenario.config.getTimeStepSize()/intruder.getSpeed()) == 0) {
                Utils.sleep(20);
                Exploration explorer = intruder.getExploration();
                DirectionEnum dir = explorer.makeMove(intruder);
                MapUpdater.moveAgent(map, intruder, dir);
                MapUpdater.refreshCurrentlyViewed(map, intruder.getVisibleTiles());
                intruder.computeVisibleTiles(map);
                MapUpdater.checkIntruderCapture(intruder, map);

            }
        }

    }

    public static void checkIntruderCapture(Guard guard, Map map) {
        ArrayList<Tile> tiles = guard.getVisibleTiles();
        for(Tile tile: tiles){
            if(tile.hasAgent() && tile.getAgent().getType().equals("Intruder")){
                if(Utils.distanceBetweenTiles(guard.getAgentPosition(), tile) < 2){
                    map.getIntruders().remove((Intruder)tile.getAgent());
                }
            }
        }

        map.getIntruders().removeIf(intruder -> intruder.getAgentPosition() == guard.getAgentPosition());


    }

    public static void checkIntruderCapture(Intruder intruder, Map map) {
        ArrayList<Tile> tiles = Utils.getSurroundingTiles(map, intruder.getAgentPosition());
        for(Tile tile : tiles){
            if(tile.hasAgent() && tile.getAgent().getType().equals("Guard")){
                checkIntruderCapture((Guard)tile.getAgent(), map);
            }
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

    /*
     * Initialization of guards, including putting them on the map.
     */
    public static void initGuards(Map map, ArrayList<Guard> guards, String guardAlgorithm) { // "loadGuards" and "loadIntruders" can later be combined if either of them doesn't need any additional code
        for (Guard guard: guards) {
            guard.createExplorationAlgorithm(guardAlgorithm, map);
            int x = guard.getX_position();
            int y = guard.getY_position();
            Map.addAgent(map, guard, x, y);
            guard.setAgentPosition(map.getTile(x,y));
            guard.initializeEmptyMap(map);
            guard.computeVisibleTiles(map);
        }
    }

    public static void initIntruders(Map map, ArrayList<Intruder> intruders, String intruderAlgorithm) {
        for (Intruder intruder: intruders) {
            intruder.createExplorationAlgorithm(intruderAlgorithm, map);
            int x = intruder.getX_position();
            int y = intruder.getY_position();
            Map.addAgent(map, intruder, x, y);
            intruder.setAgentPosition(map.getTile(x,y));
            intruder.initializeEmptyMap(map);
            intruder.computeVisibleTiles(map);
        }
    }

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
                MapUpdater.spawnIntruder(map, scenario.getSpawnAreaIntruders());
            }
        }
    }

    public static void spawnGuard(Map map, Area givenArea){
        int rand1 = (int) (Math.random() * (givenArea.getRightBoundary() - givenArea.getLeftBoundary())) + givenArea.getLeftBoundary();
        int rand2 = (int) (Math.random() * (givenArea.getBottomBoundary() - givenArea.getTopBoundary())) + givenArea.getTopBoundary();
        Guard tempAgent = new Guard(map.getTile(rand1, rand2));
        map.getGuards().add(tempAgent);
        map.getTile(rand1, rand2).addAgent(tempAgent);
    }

    public static void spawnIntruder(Map map, Area givenArea){
        int rand1 = (int) (Math.random() * (givenArea.getRightBoundary() - givenArea.getLeftBoundary())) + givenArea.getLeftBoundary();
        int rand2 = (int) (Math.random() * (givenArea.getBottomBoundary() - givenArea.getTopBoundary())) + givenArea.getTopBoundary();
        Intruder tempAgent = new Intruder(map.getTile(rand1, rand2), Utils.findAngleToTargetArea(rand1, rand2));
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



    public static void refreshCurrentlyViewed(Map map, ArrayList<Tile> visibleTiles) {
        for(Tile tile: visibleTiles){
            map.getTile(tile.getX(), tile.getY()).setCurrentlyViewed(false);
        }
    }


}
