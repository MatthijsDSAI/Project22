package exploration;
import agents.Agent;
import controller.Map.Map;
import controller.Map.tiles.Tile;
import javafx.scene.paint.Color;
import utils.DirectionEnum;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;

public class FrontierBasedExploration {
    //no prior information about the map
    int x, y;
//    boolean visited[];
//    boolean map_covered=false;
    Map map;
    Tile tile;
    //1st in, 1st out
    Queue<Tile> frontiers = new PriorityQueue<Tile> ();

    public FrontierBasedExploration(Tile t){
        this.tile = t;
    }

    public FrontierBasedExploration(int x, int y){
        this.x=x;
        this.y=y;
    }

    private double calculateWeight(Tile t)
    {
        if(t.isWalkable()==true)
            return 0.5;
        else
            return 1;
    }

    //x is the starting Tile
    // a is the agent on the current position
    public void dfs(Tile x, Agent a) {
        x.setExplored(true);
        //agent moves a tile
        a.makeMove();
        for(Tile t:frontiers) {
            //add traverse of right and left nodes when dif of 0 and not visited before
            Tile z;
            //add the neighboring tiles
            //depending on position I get x+1,y+1
            if(a.getCurrentDirection()== DirectionEnum.NORTH)
            {

                int topLimit = Math.min(0, a.getY_position()  -a.getViewingDistance()+1);
                int leftBorder = a.getX_position()-2;
                int rightBorder = a.getX_position()+2;
                for(int j=a.getY_position(); j>=topLimit; j--){
                    if(leftBorder>=0){
                        //set the weight of the frontier based on the previous seen tile
                        map.getTile(leftBorder,j).setWeight(calculateWeight(map.getTile(leftBorder+1,j)));
                        frontiers.add(map.getTile(leftBorder, j));
                        map.getTile(leftBorder, j).setColor(Color.CYAN);

                    }
                    if(rightBorder<=map.getHorizontalSize()-1){
                        map.getTile(leftBorder,j).setWeight(calculateWeight(map.getTile(leftBorder-1,j)));
                        frontiers.add(map.getTile(rightBorder, j));

                        map.getTile(rightBorder, j).setColor(Color.CYAN);
                    }
                }
                if(a.getY_position()+1<=map.getVerticalSize()-1){
                    for(int i = leftBorder; i<=rightBorder; i++) {
                        if(i>=0 && i<=map.getHorizontalSize()-1){
                            map.getTile(i,a.getY_position()+1).setWeight(calculateWeight(map.getTile(i,a.getY_position())));
                            frontiers.add(map.getTile(i, a.getY_position()+1));

                            map.getTile(i, a.getY_position()+1).setColor(Color.CYAN);
                        }
                    }
                }

                if(a.getY_position()-a.getViewingDistance()>=0){
                    for(int i = leftBorder; i<=rightBorder; i++) {
                        if(i>=0 && i<=map.getHorizontalSize()-1){
                            frontiers.add(map.getTile(i, a.getY_position()-a.getViewingDistance()));
                            map.getTile(i, a.getY_position()-a.getViewingDistance()).setColor(Color.CYAN);
                        }
                    }
                }
            }
            if(a.getCurrentDirection()== DirectionEnum.SOUTH)
            {
//             int limit = Math.min(0, a.getY_position()+a.getViewingDistance()+1);
//             for(int j = a.getY_position(); j<limit;j--){
//
//             }
            }
            if(a.getCurrentDirection() == DirectionEnum.EAST)
            {

            }
            if(a.getCurrentDirection() == DirectionEnum.WEST)
            {

            }
//            else
//            {
//                z=map.getTile(getX()+2,getY());
//                if(z.isWalkable()==true)
//                    frontiers.add(z);
//                z=map.getTile(getX()-2,getY());
//                if(z.isWalkable()==true)
//                    frontiers.add(z);
//            }
            dfs(t,a);
        }
    }

    //makes all tiles unexplored
    public void initv(){
        for(int i=0; i< map.getHorizontalSize(); i++) //getTiles [][]
            for(int j=0; j< map.getVerticalSize(); j++)
            {
                Tile t = map.getTile(i,j);
                t.setExplored(false);
            }
    }

    public void computeVisibleTiles(Map map, Agent a){
        ArrayList<Tile> visibleTiles = map.computeVisibleTiles(a);
        for(Tile tile : visibleTiles){
            tile.setExplored(true);
            map.setTile(tile.clone());
        }
    }

    public void explore(Agent a){
        //while map is not explored
        //while (map_covered != true) {
            //add frontiers as they are visited, to explore all frontiers connected to a tile
                //I think we should add the frontiers here
//                frontiers.add();
            //check visual field
            computeVisibleTiles(map, a);
            //add end of visual field to frontier
            dfs(frontiers.remove(), a);
            //move forward
           a.makeMove();
            // check if the whole map has been explored
            //map_covered=map.isExplored();
        //}
        //decide which fronteier to explore
    }

    public void setLocation(int x, int y) {
        this.x=x;
        this.y=y;
    }

    public void setX(int x) {
        this.x=x;
    }

    public void setY(int y) {
        this.y=y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Map getMap(){
        return map;
    }
}
