package controller;

import agents.Agent;
import utils.Config;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.ArrayList;

public class Scenario {

    public static Config config;

    protected double baseSpeedIntruder;
    protected double sprintSpeedIntruder;
    protected double baseSpeedGuard;

    protected String mapDoc;
    protected int gameMode;
    private final Path filePath;
    private final static Charset ENCODING = StandardCharsets.UTF_8;

    protected String name;
    protected String gameFile;
    protected int mapHeight;
    protected int mapWidth;

    protected double scaling;
    protected int numIntruders;
    protected int numGuards;
    protected Area spawnAreaIntruders;
    protected Area spawnAreaGuards;
    protected Area targetArea;
    protected ArrayList<TelePortal> teleports;
    protected ArrayList<Area> areas;
    protected ArrayList<Agent> agents;
    private int distanceViewing;
    private int distanceHearingWalking, distanceHearingSprinting;
    private int distanceSmelling;

    public Scenario(String mapFile){
        // set parameters
        mapDoc=mapFile;
        // initialize variables
        areas = new ArrayList<>();
        teleports = new ArrayList<>(); // create list of teleports e.g. stairs
        agents = new ArrayList<>();
        //temporary
        config = new Config();
        // read scenario
        filePath = Paths.get(mapDoc); // get path
        System.out.println(filePath);
        readMap();
    }

    public void readMap(){
        try (Scanner scanner =  new Scanner(filePath, ENCODING.name())){
            while (scanner.hasNextLine()){
                parseLine(scanner.nextLine());
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    protected void parseLine(String line){
        //use a second Scanner to parse the content of each line
        try(Scanner scanner = new Scanner(line)){
            scanner.useDelimiter("=");
            if (scanner.hasNext()){
                // read id value pair
                String id = scanner.next();
                String value = scanner.next();
                // trim excess spaces
                value=value.trim();
                id=id.trim();
                // in case multiple parameters
                String[] items=value.split(" ");
                switch(id) {
                    case "name":
                        name = value;
                        break;
                    case "gameFile":
                        gameFile = value;
                        break;
                    case "gameMode":
                        gameMode = Integer.parseInt(value); // 0 is exploration, 1 evasion pursuit game
                        Scenario.config.setGameMode(gameMode);
                        break;
                    case "scaling":
                        scaling = Double.parseDouble(value);
                        break;
                    case "height":
                        mapHeight = Integer.parseInt(value);
                        break;
                    case "width":
                        mapWidth = Integer.parseInt(value);
                        break;
                    case "numGuards":
                        numGuards = Integer.parseInt(value);
                        break;
                    case "numIntruders":
                        numIntruders = Integer.parseInt(value);
                        break;
                    case "distanceViewing":
                        distanceViewing = Integer.parseInt(value);
                        config.setDistanceViewing(distanceViewing);
                        break;
                    case "distanceSmelling":
                        distanceSmelling = Integer.parseInt(value);
                        config.setDistanceViewing(distanceSmelling);
                        break;
                    case "distanceHearingWalking":
                        distanceHearingWalking = Integer.parseInt(value);
                        config.setDistanceViewing(distanceHearingWalking);
                        break;
                    case "distanceHearingSprinting":
                        distanceHearingSprinting = Integer.parseInt(value);
                        config.setDistanceViewing(distanceHearingSprinting);
                        break;
                    case "baseSpeedIntruder":
                        baseSpeedIntruder = Double.parseDouble(value);
                        config.setBASESPEEDINTRUDER(baseSpeedIntruder);
                        break;
                    case "sprintSpeedIntruder":
                        sprintSpeedIntruder = Double.parseDouble(value);
                        config.setSPRINTSTEEDINTRUDER(sprintSpeedIntruder);
                        break;
                    case "baseSpeedGuard":
                        baseSpeedGuard = Double.parseDouble(value);
                        config.setBASESPEEDGUARD(baseSpeedGuard);
                        break;
                    case "targetArea":
                        areas.add(new Area(Integer.parseInt(items[0]),Integer.parseInt(items[1]),Integer.parseInt(items[2]),Integer.parseInt(items[3]), "targetArea"));
                        this.targetArea = new Area(Integer.parseInt(items[0]),Integer.parseInt(items[1]),Integer.parseInt(items[2]),Integer.parseInt(items[3]), "targetArea");
                        int middleX = (targetArea.getLeftBoundary()+targetArea.getRightBoundary())/2;
                        int middleY = (targetArea.getTopBoundary()+targetArea.getBottomBoundary())/2;
                        config.setCenterOfTargetArea(new int[]{middleX, middleY});
                        break;
                    case "spawnAreaIntruders":
                        spawnAreaIntruders = new Area(Integer.parseInt(items[0]),Integer.parseInt(items[1]),Integer.parseInt(items[2]),Integer.parseInt(items[3]), "spawnAreaIntruders");
                        areas.add(new Area(Integer.parseInt(items[0]),Integer.parseInt(items[1]),Integer.parseInt(items[2]),Integer.parseInt(items[3]), "spawnAreaIntruders"));
                        break;
                    case "spawnAreaGuards":
                        spawnAreaGuards = new Area(Integer.parseInt(items[0]),Integer.parseInt(items[1]),Integer.parseInt(items[2]),Integer.parseInt(items[3]), "spawnAreaGuards");
                        areas.add(new Area(Integer.parseInt(items[0]),Integer.parseInt(items[1]),Integer.parseInt(items[2]),Integer.parseInt(items[3]), "spawnAreaGuards"));
                        break;
                    case "wall":
                        areas.add(new Area(Integer.parseInt(items[0]),Integer.parseInt(items[1]),Integer.parseInt(items[2]),Integer.parseInt(items[3]), "wall"));
                        break;
                    case "shaded":
                        areas.add(new Area(Integer.parseInt(items[0]),Integer.parseInt(items[1]),Integer.parseInt(items[2]),Integer.parseInt(items[3]), "shaded"));
                        break;
                    case "teleport":
                        teleports.add(new TelePortal(Integer.parseInt(items[0]),Integer.parseInt(items[1]),Integer.parseInt(items[2]),Integer.parseInt(items[3]),Integer.parseInt(items[4]),Integer.parseInt(items[5]),Double.parseDouble(items[6])));
                        break;
                    case "texture":
                        // still to do. First the coordinates, then an int with texture type and then a double with orientation
                }
            }
        }
    }

    public ArrayList<TelePortal> getTeleportals(){
        return teleports;
    }

    public Area getTargetArea(){
        return targetArea;
    }

    public int getGameMode() {return gameMode;}

    public ArrayList<Area> getAreas() {return areas;}

    public int getDistanceHearingWalking() {
        return distanceHearingWalking;
    }

    public int getDistanceHearingSprinting(){
        return  distanceHearingSprinting;
    }

    public int getNumGuards(){
        return numGuards;
    }

    public int getDistanceSmelling(){
        return  distanceSmelling;
    }

    public String getGameFile(){
        return gameFile;
    }

    public String getMapDoc(){
        return mapDoc;
    }

    public double getScaling(){
        return scaling;
    }

    public int getMapHeight() {
        return mapHeight;
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public Area getSpawnAreaGuards() {
        return spawnAreaGuards;
    }

    public Area getSpawnAreaIntruders() {
        return spawnAreaIntruders;
    }

    public int getNumIntruders() {
        return numIntruders;
    }

}
