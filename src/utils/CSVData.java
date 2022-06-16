package utils;

import controller.GameRunner;

import java.util.ArrayList;
import java.util.List;

public class CSVData {
    private String guardType;
    private String intruderType;
    private int numGuards;
    private int numIntruders;
    private String mapName;
    private List<GameData> games;

    public CSVData(String guardType, String intruderType, int numGuards, int numIntruders, String mapName, List<GameData> games) {
        this.guardType = guardType;
        this.intruderType = intruderType;
        this.numGuards = numGuards;
        this.numIntruders = numIntruders;
        this.mapName = mapName;
        if(games == null)
            this.games = new ArrayList<>();
        else this.games = games;
    }

    public void addGame(GameData gd){
        games.add(gd);
    }

    public String getGuardType() {
        return guardType;
    }

    public void setGuardType(String guardType) {
        this.guardType = guardType;
    }

    public String getIntruderType() {
        return intruderType;
    }

    public void setIntruderType(String intruderType) {
        this.intruderType = intruderType;
    }

    public int getNumGuards() {
        return numGuards;
    }

    public void setNumGuards(int numGuards) {
        this.numGuards = numGuards;
    }

    public int getNumIntruders() {
        return numIntruders;
    }

    public void setNumIntruders(int numIntruders) {
        this.numIntruders = numIntruders;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public List<GameData> getGames() {
        return games;
    }

    public void setGames(List<GameData> games) {
        this.games = games;
    }
}
