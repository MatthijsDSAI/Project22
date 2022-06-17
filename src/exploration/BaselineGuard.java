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

    public BaselineGuard(Agent agent, Map map) {
        super(agent, map);
        //System.out.println("Creating markers...");
        agent.createMarkers(5, c);
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
                agent.addMarkers(3,map);
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
        if(part ==0)
        {
            if(halfway == 1)
            {
                if(agent.getNumberMarekr(0)>0)
                    agent.addMarkers(0,map);
                else part=1;
                if(agent.getX_position()+agent.getSpeed()<agent.ownMap.getHorizontalSize())
                {
                    goalTile = agent.ownMap.getTile(agent.getX_position()+agent.getSpeed(), agent.getY_position());
                }
                else if(agent.getX_position()-agent.getSpeed()>0 && agent.ownMap.getTile(agent.getX_position()-agent.getSpeed(), agent.getY_position()).isWalkable()==true)
                {
                    goalTile = agent.ownMap.getTile(agent.getX_position()-agent.getSpeed(), agent.getY_position());
                }
            }
            else if(agent.getY_position()<agent.ownMap.getVerticalSize()/2)
                goalTile = agent.ownMap.getTile(agent.getX_position(), agent.getY_position()+1);
            else if(agent.getY_position()>agent.ownMap.getVerticalSize()/2)
                goalTile = agent.ownMap.getTile(agent.getX_position(), agent.getY_position()-1);
            else halfway=1;
        }
       // if(agent.ownMap.getTile(agent.getX_position(),agent.getY_position()+1)!=null)
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
            else if(c==Color.WHITE){
                System.out.println("An intruder was caught");
            }
            else if(f.getIsPheromone()==true)
            {
               if(agent.ownMap.getTile(agent.getX_position(),agent.getY_position()-1)!=null)
               {
                   Tile t = agent.ownMap.getTile(agent.getX_position(),agent.getY_position()-1).getType();
                   if(t.toString().equals("TelePortal"))
                       if(agent.ownMap.getTile(agent.getX_position()+2,agent.getY_position()).isWalkable())
                           return agent.ownMap.getTile(agent.getX_position()+2,agent.getY_position());
                       else if(agent.ownMap.getTile(agent.getX_position()-2,agent.getY_position()).isWalkable())
                           return agent.ownMap.getTile(agent.getX_position()-2,agent.getY_position());
               }
               if(agent.ownMap.getTile(agent.getX_position(),agent.getY_position()+1)!=null) {
                   Tile t = agent.ownMap.getTile(agent.getX_position(), agent.getY_position() + 1).getType();
                   if (t.toString().equals("TelePortal"))
                       if(agent.ownMap.getTile(agent.getX_position()+2,agent.getY_position()).isWalkable())
                            return agent.ownMap.getTile(agent.getX_position()+2,agent.getY_position());
                       else if(agent.ownMap.getTile(agent.getX_position()-2,agent.getY_position()).isWalkable())
                            return agent.ownMap.getTile(agent.getX_position()-2,agent.getY_position());
               }
               if(agent.ownMap.getTile(agent.getX_position()-1,agent.getY_position())!=null)
               {
                   Tile t = agent.ownMap.getTile(agent.getX_position()-1,agent.getY_position()).getType();
                   if(t.toString().equals("TelePortal"))
                       if(agent.ownMap.getTile(agent.getX_position(),agent.getY_position()+2).isWalkable())
                           return agent.ownMap.getTile(agent.getX_position(),agent.getY_position()+2);
                       else if(agent.ownMap.getTile(agent.getX_position(),agent.getY_position()-2).isWalkable())
                           return agent.ownMap.getTile(agent.getX_position(),agent.getY_position()-2);
               }
               if(agent.ownMap.getTile(agent.getX_position()+1,agent.getY_position())!=null)
               {
                   Tile t = agent.ownMap.getTile(agent.getX_position()+1,agent.getY_position()).getType();
                   if(t.toString().equals("TelePortal"))
                       return agent.ownMap.getTile(agent.getX_position()+2,agent.getY_position());
               }
                System.out.println("Agent already entered a teleportal.");
            }
            return f;
        }
        return null;
    }
}
