package utils;

import agents.Guard;
import controller.Map.tiles.Tile;

import java.util.ArrayList;
import java.util.LinkedList;

public class AdjacencyList {

    private Tile[][] tiles;
    private Guard guard;
    public LinkedList<LinkedList<Tile>> adjacencyLists;
    private int numTiles;

    public AdjacencyList(Tile[][] tiles, ArrayList<Tile> visibleTiles) {
        this.tiles = tiles;
        this.numTiles = tiles.length*tiles[0].length;
        addNodes(visibleTiles);
        adjacencyLists = new LinkedList<>();
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

    public void addNodes(ArrayList<Tile> newTiles) {
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
        }
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
