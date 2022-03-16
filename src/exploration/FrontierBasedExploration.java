package exploration;
import agents.Agent;
import controller.Map.Map;
import controller.Map.tiles.Tile;
import utils.DirectionEnum;

import java.util.PriorityQueue;
import java.util.Queue;

public class FrontierBasedExploration extends Exploration {
    //no prior information about the map
    int x, y, angle;
    boolean visited[];
    boolean map_covered=false;
    Map map;
    //1st in, 1st out
    Queue<Tile> frontiers = new PriorityQueue<Tile> ();
    public FrontierBasedExploration(int x, int y){
        super(x,y);
        explore();
    }

    //x is the starting node
    public void dfs(Tile x) {
        x.setExplored(true);
        for(Tile t:frontiers) {
            //add traverse of right and left nodes when dif of 0 and not visited before
            Tile z= null;
            //depending on position I get x+1,y+1
            if(getAngle()==90 || getAngle()==180)
            {
                z.setY(getY_position()+1);
                z.setX(getX_position());
                frontiers.add(z);
                z.setY(getY_position()-1);
            }
            else
            {
                z.setY(getX_position()+1);
                z.setX(getY_position());
                frontiers.add(z);
                z.setY(getX_position()-1);
            }
            dfs(t);
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

    public void explore(){
        //initialize map as empty
        initializeEmptyMap(map);
        //while map is not explored
        while (map_covered != true)
        {
            //add frontiers as they are visited, to explore all frontiers connected to a tile
            frontiers.add(getAgentPosition());
            //check visual field
            computeVisibleTiles(map);
            //add end of visual field to frontier
            dfs(frontiers.remove());
            //move forward
            move();
            map_covered=map.isExplored();
        }
        //decide which fronteier to explore
    }
    @Override
    public DirectionEnum makeMove() {
        return null;
    }

    @Override
    public String getExplorationName() {
        return "FrontierBasedExploration";
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

    public void setAngle(int angle){
        this.angle=angle;
    }

    public double getAngle(){
        return angle;
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
