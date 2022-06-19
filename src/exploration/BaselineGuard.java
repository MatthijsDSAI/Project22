package exploration;

import agents.Agent;
import controller.Map.Map;
import controller.Map.tiles.Tile;
import javafx.scene.paint.Color;
import utils.DirectionEnum;
import utils.Path;

public class BaselineGuard extends FrontierBasedExploration{

    Color[] c = {Color.RED, Color.ORANGE, Color.GREEN, Color.WHITE, null};
    int ok=0, halfway=0, part =0;
    int i=0;

    public BaselineGuard(Agent agent, Map map) {
        super(agent, map);
        //System.out.println("Creating markers...");
        agent.createMarkers(5,6, c);
        //System.out.println("Markers created");
    }

    @Override
    public DirectionEnum makeMove(Agent agent) {
        Tile curTile = agent.getAgentPosition();
        visibleTiles = agent.getVisibleTiles();
        boolean updated = updateKnowledge(agent, visibleTiles);
        Tile goalTile = null;
        for(Tile tile : visibleTiles) {
            Agent agentFound = tile.getAgent();
            if(agentFound != null && agentFound.getType().equals("Intruder")) {
                goalTile = agentFound.getAgentPosition();
                Path path = findPath(agent, goalTile);
                //agent.addMarkers(3,map);
                return findNextMoveDirection(agent, path.get(1));
            }
            if(tile.toString().equals("TargetArea")) {
            }
            if(tile.toString().equals("TargetArea") && !(tile.getX() == curTile.getX() && tile.getY() == curTile.getY())) {
                goalTile = tile;
            }
            if(tile.getHasMarker()==true)
                MarkerInterpretation(agent);
        }
        if(part == 0)
        {
            if(agent.getY_position()+agent.getSpeed()<agent.ownMap.getVerticalSize()/2)
            {
                if(agent.getNumberMarekr(0)>0 && i>3) {
                    System.out.println("Add marker: "+ i + " at x:" +agent.getX_position() + " y: " + agent.getY_position());
                    agent.addMarkers(0, map);
                    System.out.println("Marker added");
                   // goalTile = agent.ownMap.getTile(agent.getX_position()+3, agent.getY_position());
                }
                else part=1;
                if(agent.getX_position()+agent.getSpeed()<agent.ownMap.getHorizontalSize() && agent.ownMap.getTile(agent.getX_position()+agent.getSpeed(), agent.getY_position())!=null && agent.ownMap.getTile(agent.getX_position()+agent.getSpeed(), agent.getY_position()).isWalkable())
                {
                    goalTile = agent.ownMap.getTile(agent.getX_position()+agent.getSpeed(), agent.getY_position());
                    i++;
                }
                else if(agent.getX_position()-agent.getSpeed()>0 && agent.ownMap.getTile(agent.getX_position()-agent.getSpeed(), agent.getY_position())!=null && agent.ownMap.getTile(agent.getX_position()-agent.getSpeed(), agent.getY_position()).isWalkable()==true)
                {
                    goalTile = agent.ownMap.getTile(agent.getX_position()-agent.getSpeed(), agent.getY_position());
                    i++;
                }
//                else
//                    part=1;
            }
        }
        if(agent.ownMap.getTile(agent.getX_position(),agent.getY_position()+1)!=null)
            if(agent.ownMap.getTile(agent.getX_position(),agent.getY_position()+1).toString().equals("TelePortal"))
        {
                agent.addMarkers(4,map);
        }
        if(agent.ownMap.getTile(agent.getX_position(),agent.getY_position()-1)!=null)
            if(agent.ownMap.getTile(agent.getX_position(),agent.getY_position()-1).toString().equals("TelePortal"))
            {
                agent.addMarkers(4,map);
            }
        if(agent.ownMap.getTile(agent.getX_position()+1,agent.getY_position())!=null)
            if(agent.ownMap.getTile(agent.getX_position()+1,agent.getY_position()).toString().equals("TelePortal"))
            {
                agent.addMarkers(4,map);
            }
         if(agent.ownMap.getTile(agent.getX_position()-1,agent.getY_position())!=null)
            if(agent.ownMap.getTile(agent.getX_position()-1,agent.getY_position()).toString().equals("TelePortal"))
        {
                agent.addMarkers(4,map);
        }
        if(goalTile != null) {
            Path path = findPath(agent, goalTile);
            if(path == null) {
                return findNextMoveDirection(agent, goalTile);
            }
            return findNextMoveDirection(agent, path.get(1));
        }
        if(frontierQueue.isEmpty()){
            return null;
        }
        goalTile = updateGoal(agent, updated); // update the goal tile for the agent
        DirectionEnum dir = findNextMoveDirection(agent, goalTile);
        return dir;
    }

    public Tile MarkerInterpretation(Agent agent){
        Tile f = agent.findMarker();
        if(f!=null)
        {
            Color c = agent.ownMap.getTile(f.getX(),f.getY()).getColor();
            if(c==Color.RED){
                    if(agent.ownMap.getTile(agent.getX_position()+1,agent.getY_position())!=null && agent.ownMap.getTile(agent.getX_position()+1,agent.getY_position()).isWalkable())
                        return agent.ownMap.getTile(agent.getX_position()+1,agent.getY_position());
                    else if(agent.ownMap.getTile(agent.getX_position()-1,agent.getY_position())!=null && agent.ownMap.getTile(agent.getX_position()-1,agent.getY_position()).isWalkable())
                        return agent.ownMap.getTile(agent.getX_position()-1,agent.getY_position());
            }
//            else if(c==Color.WHITE){
//                System.out.println("An intruder was caught");
//            }
            else if(f.getIsPheromone()==true)
            {
                if(agent.ownMap.getTile(agent.getX_position(),agent.getY_position()-1).toString().equals("TelePortal")){
                       if(agent.ownMap.getTile(agent.getX_position()+2,agent.getY_position()).isWalkable())
                           return agent.ownMap.getTile(agent.getX_position()+2,agent.getY_position());
                       else if(agent.ownMap.getTile(agent.getX_position()-2,agent.getY_position()).isWalkable())
                           return agent.ownMap.getTile(agent.getX_position()-2,agent.getY_position());
               }
               if(agent.ownMap.getTile(agent.getX_position(),agent.getY_position()+1).toString().equals("TelePortal")) {
                       if(agent.ownMap.getTile(agent.getX_position()+2,agent.getY_position()).isWalkable())
                            return agent.ownMap.getTile(agent.getX_position()+2,agent.getY_position());
                       else if(agent.ownMap.getTile(agent.getX_position()-2,agent.getY_position()).isWalkable())
                            return agent.ownMap.getTile(agent.getX_position()-2,agent.getY_position());
               }
               if(agent.ownMap.getTile(agent.getX_position()-1,agent.getY_position()).toString().equals("TelePortal"))
               {
                       if(agent.ownMap.getTile(agent.getX_position(),agent.getY_position()+2).isWalkable())
                           return agent.ownMap.getTile(agent.getX_position(),agent.getY_position()+2);
                       else if(agent.ownMap.getTile(agent.getX_position(),agent.getY_position()-2).isWalkable())
                           return agent.ownMap.getTile(agent.getX_position(),agent.getY_position()-2);
               }
                if(agent.ownMap.getTile(agent.getX_position()+1,agent.getY_position()).toString().equals("TelePortal"))
               {
                   if(agent.ownMap.getTile(agent.getX_position(),agent.getY_position()+2).isWalkable())
                       return agent.ownMap.getTile(agent.getX_position(),agent.getY_position()+2);
                   else if(agent.ownMap.getTile(agent.getX_position(),agent.getY_position()-2).isWalkable())
                       return agent.ownMap.getTile(agent.getX_position(),agent.getY_position()-2);
               }
                System.out.println("Agent already entered a teleportal.");
            }
            return f;
        }
        return null;
    }
}
