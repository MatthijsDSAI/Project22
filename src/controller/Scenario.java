package controller;

import agents.Player;
import utils.Config;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

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
    private int mapHeight;
    private int mapWidth;

    private double scaling;
    private int numIntruders;
    private int numGuards;
    private Area spawnAreaIntruders;
    private Area spawnAreaGuards;
    private Area targetArea;
    private ArrayList<Area> walls;
    private ArrayList<TelePortal> teleports;
    private ArrayList<Area> shaded;
    private ArrayList<Player> players;

    public Scenario(String mapFile){
        // set parameters
        mapDoc=mapFile;
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
        catch(Exception e) {}
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
                Area tmp;
                switch(id)
                {
                    case "name":
                        name = value;
                        break;
                    case "gameFile":
                        gameFile = value;
                        break;
                    case "gameMode":
                        gameMode = Integer.parseInt(value); // 0 is exploration, 1 evasion pursuit game
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
                        targetArea = new Area(Integer.parseInt(items[0]),Integer.parseInt(items[1]),Integer.parseInt(items[2]),Integer.parseInt(items[3]));
                        break;
                    case "spawnAreaIntruders":
                        spawnAreaIntruders = new Area(Integer.parseInt(items[0]),Integer.parseInt(items[1]),Integer.parseInt(items[2]),Integer.parseInt(items[3]));
                        break;
                    case "spawnAreaGuards":
                        spawnAreaGuards = new Area(Integer.parseInt(items[0]),Integer.parseInt(items[1]),Integer.parseInt(items[2]),Integer.parseInt(items[3]));
                        break;
                    case "wall":
                        tmp = new Area(Integer.parseInt(items[0]),Integer.parseInt(items[1]),Integer.parseInt(items[2]),Integer.parseInt(items[3]));
                        walls.add(tmp);
                        break;
                    case "shaded":
                        tmp = new Area(Integer.parseInt(items[0]),Integer.parseInt(items[1]),Integer.parseInt(items[2]),Integer.parseInt(items[3]));
                        shaded.add(tmp);
                        break;
                    case "teleport":
                        TelePortal teletmp = new TelePortal(Integer.parseInt(items[0]),Integer.parseInt(items[1]),Integer.parseInt(items[2]),Integer.parseInt(items[3]),Integer.parseInt(items[4]),Integer.parseInt(items[5]),Double.parseDouble(items[6]));
                        teleports.add(teletmp);
                        break;
                    case "texture":
                        // still to do. First the coordinates, then an int with texture type and then a double with orientation
                }
            }
        }
    }

    public ArrayList<Area> getWalls(){
        return walls;
    }

    public ArrayList<Area> getShaded(){
        return shaded;
    }

    public ArrayList<TelePortal> getTeleportals(){
        return teleports;
    }

    public Area getTargetArea(){
        return targetArea;
    }


    public int getGameMode() {return  gameMode;}

    public int getNumGuards(){
        return numGuards;
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
