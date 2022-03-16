package exploration;
import agents.Agent;
import controller.Map.Map;
import controller.Map.tiles.Tile;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;

public class FrontierBasedExploration {
    //no prior information about the map
    int x, y;
    boolean visited[];
    boolean map_covered=false;
    Map map;
    Tile tile;
    //1st in, 1st out
    Queue<Tile> frontiers = new PriorityQueue<Tile> ();

    public FrontierBasedExploration(Tile t){
        this.tile = t;
//        explore();
    }
    public FrontierBasedExploration(int x, int y){
        this.x=x;
        this.y=y;
//        explore();
    }


    //x is the starting Tile
    // a is the agent on the current position
    public void dfs(Tile x, Agent a) {
        x.setExplored(true);
        //agent moves a tile
        a.move();
        for(Tile t:frontiers) {
            //add traverse of right and left nodes when dif of 0 and not visited before
            Tile z;
            //add the neighboring tiles
            //depending on position I get x+1,y+1
            if(a.getAngle()==90 || a.getAngle()==180)
            {
                z=map.getTile(getX(),getY()+2);
                if(z.isWalkable()==true)
                    frontiers.add(z);
                z=map.getTile(getX(),getY()-2);
                if(z.isWalkable()==true)
                    frontiers.add(z);

            }
            else
            {
                z=map.getTile(getX()+2,getY());
                if(z.isWalkable()==true)
                    frontiers.add(z);
                z=map.getTile(getX()-2,getY());
                if(z.isWalkable()==true)
                    frontiers.add(z);
            }
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
        //while (map_covered != true)
        //{
            Tile positionTile = getMap().getTile(x,y);
            if(positionTile.isWalkable()==true)
            //add frontiers as they are visited, to explore all frontiers connected to a tile
                //I think we should add the frontiers here
                frontiers.add(positionTile);
            //check visual field
            computeVisibleTiles(map, a);
            //add end of visual field to frontier
            dfs(frontiers.remove(), a);
            //move forward
            //move();
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
