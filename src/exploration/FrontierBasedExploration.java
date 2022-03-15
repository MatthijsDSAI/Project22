package exploration;
import agents.Agent;
import controller.Map.Map;
import controller.Map.tiles.Tile;

import java.util.PriorityQueue;
import java.util.Queue;

public class FrontierBasedExploration extends Agent {
    //no prior information about the map
    int x, y, angle;
    int visited[];
    boolean map_covered=false;
    Map map;
    Queue<Tile> frontiers = new PriorityQueue<Tile> ();
    public FrontierBasedExploration(int x, int y){
        super(x,y);
        explore();
    }

    //x is the starting node
    public void dfs(int x){
        initv();
        visited[x]=1;
        for(Tile t:frontiers) {
            //add traverse of right and left nodes when dif of 0 and not visited before
                dfs(t.getX());
        }
    }
    public void initv(){
        for(int i=0; i<visited.length;i++)
            visited[i]=0;
    }

    public void explore(){
    /* while ( !map_covered & !no_frontier_with_e
    ܯ map -> Current_map
ܴ    R -> Robot_position
    F <- Find_frontier_points(M)
    Fc <-Distances_based_connected_component(F)
    for each Fc
    Fcg <- Find_cluster_center(Fc)
    Ftarget <= argmin g {Traversible_distance(Fcg, R)} */
        //initialize map as empty
        initializeEmptyMap(map);
        while (map_covered != true)
        {
            //add frontiers as they are visited, to explore all frontiers connected to a tile
            frontiers.add(getAgentPosition());
            //check visual field
            computeVisibleTiles(map);
            //move forward by 1 step
            move();
        }
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
