package controller.Map;

import agents.Agent;
import agents.Guard;
import agents.Intruder;
import controller.Area;
import controller.Map.tiles.*;
import controller.Scenario;
import controller.TelePortal;
import utils.DirectionEnum;
import utils.Utils;

import java.util.ArrayList;

public class MapUpdater {

    public static void moveAgent(Map map, Agent agent, DirectionEnum direction){
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
        }
    }

    public static void changeTiles(Map map, Agent agent, Tile fromTile, Tile toTile){
        if(toTile.isWalkable()) {
            map.getTile(fromTile.getX(),fromTile.getY()).removeAgent();
            map.getTile(toTile.getX(),toTile.getY()).addAgent(agent);

            removeNoises(map, map.getTile(fromTile.getX(),fromTile.getY()));
            addNoises(map, map.getTile(toTile.getX(),toTile.getY()));

            agent.setAgentPosition(toTile);
        }
        else{
            throw new RuntimeException("Can not move to tile " + toTile.getX() + ", " + toTile.getY());
        }
    }

    public static void removeNoises(Map map, Tile newTile) {
        boolean debug = false;
        double numOfLoops = 2;
        int x = newTile.getX();
        int y = newTile.getY();

        if(debug) System.out.println("Current tile x:" + (x) + ", y:" + (y));
        newTile.setSound(0); // set agent's current position to 0, no sound

        for (int i=1; i <= numOfLoops; i++) {
            int forX = -i;
            int forY = 0;

            while (forY != -i) {
                if(debug) System.out.println("at x:" + (x + forX) + ", y:" + (y + forY));
                if ((x + forX >= 0) && (y + forY >= 0)) { // to avoid out of bounds error
                    if (map.getTile(x + forX, y + forY).isWalkable()) {
                        map.getTile(x + forX, y + forY).setSound(0);
                    }
                }
                forX++;
                forY--;
            }

            while (forX != i) {
                if(debug) System.out.println("at x:" + (x + forX) + ", y:" + (y + forY));
                if ((x + forX >= 0) && (y + forY >= 0)) { // to avoid out of bounds error
                    if (map.getTile(x + forX, y + forY).isWalkable()) {
                        if (map.getTile(x + forX, y + forY).isWalkable()) {
                            map.getTile(x + forX, y + forY).setSound(0);
                        }
                    }
                }
                forX++;
                forY++;
            }

            while (forY != i) {
                if(debug) System.out.println("at x:" + (x + forX) + ", y:" + (y + forY));
                if ((x + forX >= 0) && (y + forY >= 0)) { // to avoid out of bounds error
                    if (map.getTile(x + forX, y + forY).isWalkable()) {
                        if (map.getTile(x + forX, y + forY).isWalkable()) {
                            map.getTile(x + forX, y + forY).setSound(0);
                        }
                    }
                }
                forX--;
                forY++;
            }

            while (forX != -i) {
                if(debug) System.out.println("at x:" + (x + forX) + ", y:" + (y + forY));
                if ((x + forX >= 0) && (y + forY >= 0)) { // to avoid out of bounds error
                    if (map.getTile(x + forX, y + forY).isWalkable()) {
                        if (map.getTile(x + forX, y + forY).isWalkable()) {
                            map.getTile(x + forX, y + forY).setSound(0);
                        }
                    }
                }
                forX--;
                forY--;
            }
        }
        if(debug) System.out.println("finished");
        }

    public static void addNoises(Map map, Tile newTile) {
        boolean debug = false;
        double numOfLoops = 2;
        int x = newTile.getX();
        int y = newTile.getY();
        double decreasingVal = 1 / (numOfLoops + 1);

        if(debug) System.out.println("Current tile x:" + (x) + ", y:" + (y));
        newTile.setSound(1); // set agent's current position to 1, max sound

        for (int i=1; i <= numOfLoops; i++) {
            int forX = -i;
            int forY = 0;

            while (forY != -i) {
                if(debug) System.out.println("at x:" + (x + forX) + ", y:" + (y + forY));
                if ((x + forX >= 0) && (y + forY >= 0)) { // to avoid out of bounds error
                    if (map.getTile(x + forX, y + forY).isWalkable()) {

                        if (map.getTile(x + forX, y + forY).getSound() < 1 - decreasingVal * i) { // if that tile's current sound is higher skip
                            map.getTile(x + forX, y + forY).setSound(1 - decreasingVal * i);
                        }
                    }
                }
                forX++;
                forY--;
            }

            while (forX != i) {
                if(debug) System.out.println("at x:" + (x + forX) + ", y:" + (y + forY));
                if ((x + forX >= 0) && (y + forY >= 0)) { // to avoid out of bounds error
                    if (map.getTile(x + forX, y + forY).isWalkable()) {

                        if (map.getTile(x + forX, y + forY).getSound() < 1) { // if that tile's current sound is higher skip
                            map.getTile(x + forX, y + forY).setSound(1 - decreasingVal * i);
                        }
                    }
                }
                forX++;
                forY++;
            }

            while (forY != i) {
                if(debug) System.out.println("at x:" + (x + forX) + ", y:" + (y + forY));
                if ((x + forX >= 0) && (y + forY >= 0)) { // to avoid out of bounds error
                    if (map.getTile(x + forX, y + forY).isWalkable()) {

                        if (map.getTile(x + forX, y + forY).getSound() < 1) { // if that tile's current sound is higher skip
                            map.getTile(x + forX, y + forY).setSound(1 - decreasingVal * i);
                        }
                    }
                }
                forX--;
                forY++;
            }

            while (forX != -i) {
                if(debug) System.out.println("at x:" + (x + forX) + ", y:" + (y + forY));
                if ((x + forX >= 0) && (y + forY >= 0)) { // to avoid out of bounds error
                    if (map.getTile(x + forX, y + forY).isWalkable()) {

                        if (map.getTile(x + forX, y + forY).getSound() < 1) { // if that tile's current sound is higher skip
                            map.getTile(x + forX, y + forY).setSound(1 - decreasingVal * i);
                        }
                    }
                }
                forX--;
                forY--;
            }
        }
        if(debug) System.out.println("finished");

    }

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

    public static void checkIntruderCapture(Guard guard, Map map) {
        ArrayList<Tile> tiles = guard.getVisibleTiles();
        for(Tile tile: tiles){
            if(tile.hasAgent() && tile.getAgent().getType().equals("Intruder")){
                System.out.println(Utils.distanceBetweenTiles(guard.getAgentPosition(), tile));
                if(Utils.distanceBetweenTiles(guard.getAgentPosition(), tile)<=1){
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

    public static void refresh(Map map, ArrayList<Tile> visibleTiles) {
        for(Tile tile: visibleTiles){
            map.getTile(tile.getX(), tile.getY()).setCurrentlyViewed(false);
        }
    }


}
