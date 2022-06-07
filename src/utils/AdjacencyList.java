package utils;

import agents.Agent;
import agents.Guard;
import controller.Map.tiles.Tile;

import java.util.ArrayList;
import java.util.LinkedList;

public class AdjacencyList {

    private Tile[][] tiles;
    public LinkedList<LinkedList<Tile>> adjacencyLists;
    private int numTiles;

    public AdjacencyList(Tile[][] tiles, Agent agent, ArrayList<Tile> visibleTiles) {
        this.tiles = tiles;
        this.numTiles = tiles.length*tiles[0].length;
        adjacencyLists = new LinkedList<>();
        addNodes(visibleTiles, agent);
        for(int i = 0; i < numTiles; i++) {
            adjacencyLists.add(new LinkedList<Tile>());
        }
    }

    public LinkedList<Tile> get(Tile tile) {
        return adjacencyLists.get(getTileIndex(tile));
    }

    public int getTileIndex(Tile tile) {
        int tileIndex = tile.getY()*tiles[0].length + tile.getX();
        return tileIndex;
    }

    public void addNodes(ArrayList<Tile> newTiles, Agent agent) {
        for(Tile tile : newTiles) {
            for(Tile otherTile : newTiles) {
                if(otherTile.getX() == tile.getX() && otherTile.getY() == tile.getY()-1 ) {
                    LinkedList<Tile> curAdjacencyList = adjacencyLists.get(getTileIndex(tile));
                    if(!curAdjacencyList.contains(otherTile)) curAdjacencyList.add(otherTile);
                }
                else if(otherTile.getX() == tile.getX() && otherTile.getY() == tile.getY()+1) {
                    LinkedList<Tile> curAdjacencyList = adjacencyLists.get(getTileIndex(tile));
                    if(!curAdjacencyList.contains(otherTile)) curAdjacencyList.add(otherTile);
                }
                else if(otherTile.getX() == tile.getX()-1 && otherTile.getY() == tile.getY()) {
                    LinkedList<Tile> curAdjacencyList = adjacencyLists.get(getTileIndex(tile));
                    if(!curAdjacencyList.contains(otherTile)) curAdjacencyList.add(otherTile);
                }
                else if(otherTile.getX() == tile.getX()+1 && otherTile.getY() == tile.getY()) {
                    LinkedList<Tile> curAdjacencyList = adjacencyLists.get(getTileIndex(tile));
                    if(!curAdjacencyList.contains(otherTile)) curAdjacencyList.add(otherTile);
                }
            }
            ArrayList<Tile> neighbours = getNeighbours(tile);
            for(Tile neighbourTile : neighbours) {
                if(agent.isExplored(neighbourTile)) {
                    LinkedList<Tile> curAdjacencyList = adjacencyLists.get(getTileIndex(neighbourTile));
                    if(!curAdjacencyList.contains(tile)) curAdjacencyList.add(tile);
                }
            }
        }
    }

    public ArrayList<Tile> getNeighbours(Tile tile) {

        ArrayList<Tile> neighbours = new ArrayList<>();

        int row = tile.getY();
        int col = tile.getX();
        int maxRow = tiles.length;
        int maxCol = tiles[0].length;

        if(row-1 >= 0) neighbours.add(tiles[row-1][col]);
        if(row+1 <= maxRow) neighbours.add(tiles[row+1][col]);
        if(col-1 >= 0) neighbours.add(tiles[row][col-1]);
        if(col+1 <= maxCol) neighbours.add(tiles[row][col+1]);

        return neighbours;
    }

    @Override
    public String toString() {
        String result = "";
        for(int i = 0; i < numTiles; i++) {
            result += "Tile" + i + ": ";
            LinkedList<Tile> curList = adjacencyLists.get(i);
            for(int j = 0; j < curList.size(); j++) {
                result += "Tile" + getTileIndex(curList.get(j)) + " ";
            }
            result += "\n";
        }

        return result;
    }
}
