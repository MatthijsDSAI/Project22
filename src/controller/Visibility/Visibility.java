package controller.Visibility;

import agents.Agent;
import controller.Map.Map;
import controller.Map.tiles.Tile;
import controller.Scenario;

import java.util.ArrayList;

public class Visibility {
    public static ArrayList<Tile> computeVisibleTiles(Map map, Agent agent) {
        int d = Scenario.config.getDistanceViewing();
        double angle = agent.getAngle();
        int agentX = agent.getAgentPosition().getX();
        int agentY = agent.getAgentPosition().getY();
        ArrayList<Tile> visibleTiles = new ArrayList<>();
        boolean middleLane = true;
        boolean leftLane = true;
        boolean rightLane = true;
        if(angle==0){
            int topLimit = Math.max(0, agentY-d+1);
            int leftLimit = 0;
            int rightLimit = map.getHorizontalSize()-1;
            for(int j = agentY; j>=topLimit && middleLane; j--){
                if((agentX+1)<=rightLimit){
                    visibleTiles.add(map.getTile(agentX+1, j));
                }
                if((agentX-1)>=leftLimit){
                    visibleTiles.add(map.getTile(agentX-1, j));
                }
                visibleTiles.add(map.getTile(agentX, j));
                if(!map.getTile(agentX, j).isSeeThrough()){
                    middleLane = false;
                }
            }

            return visibleTiles;

        }
        if(angle==45.0) {
            int topLimit = Math.max(0, agentY - d + 1);
            int leftLimit = Math.max(0, agentX - d + 1);
            int finalI = 0;
            int finalJ = 0;
            for (int i = agentX, j=agentY; i >= leftLimit && j >= topLimit && (middleLane || rightLane || leftLane); i--, j--) {

                if(i!=agentX && rightLane){
                    visibleTiles.add(map.getTile(i+1, j));
                    if(!map.getTile(i+1, j).isSeeThrough()){
                        rightLane = false;
                    }
                }
                if(i!=agentX && leftLane){
                    visibleTiles.add(map.getTile(i, j+1));
                    if(!map.getTile(i, j+1).isSeeThrough()){
                        leftLane = false;
                    }
                }

                if(middleLane){
                    visibleTiles.add(map.getTile(i, j));
                    if(!map.getTile(i,j).isSeeThrough()){
                        middleLane = false;
                    }
                }

                finalI = i;
                finalJ = j;
            }

            if(finalJ!=0 && rightLane){
                visibleTiles.add(map.getTile(finalI, finalJ-1));
            }
            if(finalI!=0 && leftLane){
                visibleTiles.add(map.getTile(finalI-1,finalJ));
            }
            return visibleTiles;
        }

        if(angle==90.0){
            int topLimit = 0;
            int bottomLimit = map.getVerticalSize()-1;
            int leftLimit = Math.max(0, agentX - d + 1);
            for(int i = agentX; i>=leftLimit && middleLane; i--){
                if((agentY +1)<=bottomLimit){
                    visibleTiles.add(map.getTile(i, agentY +1));
                }
                if((agentY -1)>=topLimit){
                    visibleTiles.add(map.getTile(i, agentY -1));
                }
                visibleTiles.add(map.getTile(i, agentY));
                if(!map.getTile(i, agentY).isSeeThrough()){
                    middleLane = false;
                }
            }
            return visibleTiles;
        }

        if(angle==135){
            int bottomLimit = Math.max(0, agentY + d - 1);
            int leftLimit = Math.max(0, agentX - d + 1);
            int finalI = 0;
            int finalJ = 0;
            for (int i = agentX, j=agentY; i >= leftLimit && j <= bottomLimit && (middleLane || rightLane || leftLane); i--, j++) {


                if(i!=agentX && rightLane){
                    visibleTiles.add(map.getTile(i, j-1));
                    if(!map.getTile(i,j-1).isSeeThrough()){
                        rightLane = false;
                    }
                }
                if(i!=agentX && leftLane){
                    visibleTiles.add(map.getTile(i+1, j));
                    if(!map.getTile(i+1,j).isSeeThrough()){
                        leftLane = false;
                    }
                }

                if(middleLane) {
                    visibleTiles.add(map.getTile(i, j));
                    if(!map.getTile(i,j).isSeeThrough()){
                        middleLane = false;
                    }
                }
                finalI = i;
                finalJ = j;
            }
            if(finalI!=0 && rightLane){
                visibleTiles.add(map.getTile(finalI-1,finalJ));
            }
            if(finalJ!=map.getHorizontalSize()-1 && leftLane){
                visibleTiles.add(map.getTile(finalI, finalJ+1));
            }
            return visibleTiles;
        }

        if(angle==180){
            int bottomLimit = Math.min(map.getVerticalSize()-1, agentY+d-1);
            int leftLimit = 0;
            int rightLimit = map.getHorizontalSize()-1;
            for(int j = agentY; j<=bottomLimit && middleLane; j++){
                if((agentX+1)<=rightLimit){
                    visibleTiles.add(map.getTile(agentX+1, j));
                }
                if((agentX-1)>=leftLimit){
                    visibleTiles.add(map.getTile(agentX-1, j));
                }
                visibleTiles.add(map.getTile(agentX, j));
                if(!map.getTile(agentX, j).isSeeThrough()){
                    middleLane = false;
                }
            }
            return visibleTiles;
        }

        if(angle==225){
            int bottomLimit = Math.min(map.getVerticalSize()-1, agentY + d - 1);
            int rightLimit = Math.min(map.getHorizontalSize()-1, agentX + d - 1);
            int finalI = 0;
            int finalJ = 0;
            for (int i = agentX, j=agentY; i <= rightLimit && j <= bottomLimit && (middleLane || rightLane || leftLane); i++, j++) {


                if(i!=agentX && rightLane){
                    visibleTiles.add(map.getTile(i-1, j));
                    if(!map.getTile(i-1,j).isSeeThrough()){
                        rightLane = false;
                    }
                }
                if(i!=agentX && leftLane){
                    visibleTiles.add(map.getTile(i, j-1));
                    if(!map.getTile(i,j-1).isSeeThrough()){
                        leftLane = false;
                    }
                }

                if(middleLane) {
                    visibleTiles.add(map.getTile(i, j));
                    if(!map.getTile(i,j).isSeeThrough()){
                        middleLane = false;
                    }
                }
                finalI = i;
                finalJ = j;
            }
            if(finalJ!=map.getHorizontalSize()-1 && rightLane){
                visibleTiles.add(map.getTile(finalI, finalJ+1));
            }
            if(finalI!=map.getVerticalSize()-1 && leftLane){
                visibleTiles.add(map.getTile(finalI+1,finalJ));
            }
            return visibleTiles;
        }

        if(angle==270.0){
            int topLimit = 0;
            int bottomLimit = map.getVerticalSize()-1;
            int rightLimit = Math.min(map.getHorizontalSize()-1, agentX + d - 1);
            for(int i = agentX; i<=rightLimit && middleLane; i++){
                if((agentY +1)<=bottomLimit){
                    visibleTiles.add(map.getTile(i, agentY +1));
                }
                if((agentY -1)>=topLimit){
                    visibleTiles.add(map.getTile(i, agentY -1));
                }
                visibleTiles.add(map.getTile(i, agentY));
                if(!map.getTile(i, agentY).isSeeThrough()){
                    middleLane = false;
                }
            }
            return visibleTiles;
        }
        if(angle==315.0) {
            int topLimit = Math.max(0, agentY - d + 1);
            int rightLimit = Math.min(map.getHorizontalSize(), agentX + d - 1);
            int finalI = 0;
            int finalJ = 0;
            for (int i = agentX, j=agentY; i <= rightLimit && j >= topLimit && (middleLane || rightLane || leftLane); i++, j--) {

                if(i!=agentX && rightLane){
                    visibleTiles.add(map.getTile(i, j+1));
                    if(!map.getTile(i,j+1).isSeeThrough()){
                        rightLane = false;
                    }
                }
                if(i!=agentX && leftLane){
                    visibleTiles.add(map.getTile(i-1, j));
                    if(!map.getTile(i-1,j).isSeeThrough()){
                        leftLane = false;
                    }
                }

                if(middleLane) {
                    visibleTiles.add(map.getTile(i, j));
                    if(!map.getTile(i,j).isSeeThrough()){
                        middleLane = false;
                    }
                }
                finalI = i;
                finalJ = j;
            }
            if(finalI!=map.getVerticalSize()-1 && rightLane){
                visibleTiles.add(map.getTile(finalI+1,finalJ));
            }
            if(finalJ!=0 && leftLane){
                visibleTiles.add(map.getTile(finalI, finalJ-1));
            }
            return visibleTiles;
        }

        throw new IllegalStateException("The angle of the agent is not a valid discrete value: " + angle);


    }

    public static ArrayList<Tile> computeVisibleTilesIntermediateAngle(Map map, Agent agent) {
        int d = Scenario.config.getDistanceViewing();
        double angle = agent.getIntermediateAngle();
        int agentX = agent.getAgentPosition().getX();
        int agentY = agent.getAgentPosition().getY();
        ArrayList<Tile> visibleTiles = new ArrayList<>();
        boolean middleLane = true;
        boolean leftLane = true;
        boolean rightLane = true;
        if(angle==0){
            int topLimit = Math.max(0, agentY-d+1);
            int leftLimit = 0;
            int rightLimit = map.getHorizontalSize()-1;
            for(int j = agentY; j>=topLimit && middleLane; j--){
                if((agentX+1)<=rightLimit){
                    visibleTiles.add(map.getTile(agentX+1, j));
                }
                if((agentX-1)>=leftLimit){
                    visibleTiles.add(map.getTile(agentX-1, j));
                }
                visibleTiles.add(map.getTile(agentX, j));
                if(!map.getTile(agentX, j).isSeeThrough()){
                    middleLane = false;
                }
            }

            return visibleTiles;

        }
        if(angle==45.0) {
            int topLimit = Math.max(0, agentY - d + 1);
            int leftLimit = Math.max(0, agentX - d + 1);
            int finalI = 0;
            int finalJ = 0;
            for (int i = agentX, j=agentY; i >= leftLimit && j >= topLimit && (middleLane || rightLane || leftLane); i--, j--) {

                if(i!=agentX && rightLane){
                    visibleTiles.add(map.getTile(i+1, j));
                    if(!map.getTile(i+1, j).isSeeThrough()){
                        rightLane = false;
                    }
                }
                if(i!=agentX && leftLane){
                    visibleTiles.add(map.getTile(i, j+1));
                    if(!map.getTile(i, j+1).isSeeThrough()){
                        leftLane = false;
                    }
                }

                if(middleLane){
                    visibleTiles.add(map.getTile(i, j));
                    if(!map.getTile(i,j).isSeeThrough()){
                        middleLane = false;
                    }
                }

                finalI = i;
                finalJ = j;
            }

            if(finalJ!=0 && rightLane){
                visibleTiles.add(map.getTile(finalI, finalJ-1));
            }
            if(finalI!=0 && leftLane){
                visibleTiles.add(map.getTile(finalI-1,finalJ));
            }
            return visibleTiles;
        }

        if(angle==90.0){
            int topLimit = 0;
            int bottomLimit = map.getVerticalSize()-1;
            int leftLimit = Math.max(0, agentX - d + 1);
            for(int i = agentX; i>=leftLimit && middleLane; i--){
                if((agentY +1)<=bottomLimit){
                    visibleTiles.add(map.getTile(i, agentY +1));
                }
                if((agentY -1)>=topLimit){
                    visibleTiles.add(map.getTile(i, agentY -1));
                }
                visibleTiles.add(map.getTile(i, agentY));
                if(!map.getTile(i, agentY).isSeeThrough()){
                    middleLane = false;
                }
            }
            return visibleTiles;
        }

        if(angle==135){
            int bottomLimit = Math.max(0, agentY + d - 1);
            int leftLimit = Math.max(0, agentX - d + 1);
            int finalI = 0;
            int finalJ = 0;
            for (int i = agentX, j=agentY; i >= leftLimit && j <= bottomLimit && (middleLane || rightLane || leftLane); i--, j++) {


                if(i!=agentX && rightLane){
                    visibleTiles.add(map.getTile(i, j-1));
                    if(!map.getTile(i,j-1).isSeeThrough()){
                        rightLane = false;
                    }
                }
                if(i!=agentX && leftLane){
                    visibleTiles.add(map.getTile(i+1, j));
                    if(!map.getTile(i+1,j).isSeeThrough()){
                        leftLane = false;
                    }
                }

                if(middleLane) {
                    visibleTiles.add(map.getTile(i, j));
                    if(!map.getTile(i,j).isSeeThrough()){
                        middleLane = false;
                    }
                }
                finalI = i;
                finalJ = j;
            }
            if(finalI!=0 && rightLane){
                visibleTiles.add(map.getTile(finalI-1,finalJ));
            }
            if(finalJ!=map.getHorizontalSize()-1 && leftLane){
                visibleTiles.add(map.getTile(finalI, finalJ+1));
            }
            return visibleTiles;
        }

        if(angle==180){
            int bottomLimit = Math.min(map.getVerticalSize()-1, agentY+d-1);
            int leftLimit = 0;
            int rightLimit = map.getVerticalSize()-1;
            for(int j = agentY; j<=bottomLimit && middleLane; j++){
                if((agentX+1)<=rightLimit){
                    visibleTiles.add(map.getTile(agentX+1, j));
                }
                if((agentX-1)>=leftLimit){
                    visibleTiles.add(map.getTile(agentX-1, j));
                }
                visibleTiles.add(map.getTile(agentX, j));
                if(!map.getTile(agentX, j).isSeeThrough()){
                    middleLane = false;
                }
            }
            return visibleTiles;
        }

        if(angle==225){
            int bottomLimit = Math.min(map.getVerticalSize()-1, agentY + d - 1);
            int rightLimit = Math.min(map.getHorizontalSize()-1, agentX + d - 1);
            int finalI = 0;
            int finalJ = 0;
            for (int i = agentX, j=agentY; i <= rightLimit && j <= bottomLimit && (middleLane || rightLane || leftLane); i++, j++) {


                if(i!=agentX && rightLane){
                    visibleTiles.add(map.getTile(i-1, j));
                    if(!map.getTile(i-1,j).isSeeThrough()){
                        rightLane = false;
                    }
                }
                if(i!=agentX && leftLane){
                    visibleTiles.add(map.getTile(i, j-1));
                    if(!map.getTile(i,j-1).isSeeThrough()){
                        leftLane = false;
                    }
                }

                if(middleLane) {
                    visibleTiles.add(map.getTile(i, j));
                    if(!map.getTile(i,j).isSeeThrough()){
                        middleLane = false;
                    }
                }
                finalI = i;
                finalJ = j;
            }
            if(finalJ!=map.getHorizontalSize()-1 && rightLane){
                visibleTiles.add(map.getTile(finalI, finalJ+1));
            }
            if(finalI!=map.getVerticalSize()-1 && leftLane){
                visibleTiles.add(map.getTile(finalI+1,finalJ));
            }
            return visibleTiles;
        }

        if(angle==270.0){
            int topLimit = 0;
            int bottomLimit = map.getVerticalSize()-1;
            int rightLimit = Math.min(map.getHorizontalSize()-1, agentX + d - 1);
            for(int i = agentX; i<=rightLimit && middleLane; i++){
                if((agentY +1)<=bottomLimit){
                    visibleTiles.add(map.getTile(i, agentY +1));
                }
                if((agentY -1)>=topLimit){
                    visibleTiles.add(map.getTile(i, agentY -1));
                }
                visibleTiles.add(map.getTile(i, agentY));
                if(!map.getTile(i, agentY).isSeeThrough()){
                    middleLane = false;
                }
            }
            return visibleTiles;
        }
        if(angle==315.0) {
            int topLimit = Math.max(0, agentY - d + 1);
            int rightLimit = Math.min(map.getHorizontalSize(), agentX + d - 1);
            int finalI = 0;
            int finalJ = 0;
            for (int i = agentX, j=agentY; i <= rightLimit && j >= topLimit && (middleLane || rightLane || leftLane); i++, j--) {

                if(i!=agentX && rightLane){
                    visibleTiles.add(map.getTile(i, j+1));
                    if(!map.getTile(i,j+1).isSeeThrough()){
                        rightLane = false;
                    }
                }
                if(i!=agentX && leftLane){
                    visibleTiles.add(map.getTile(i-1, j));
                    if(!map.getTile(i-1,j).isSeeThrough()){
                        leftLane = false;
                    }
                }

                if(middleLane) {
                    visibleTiles.add(map.getTile(i, j));
                    if(!map.getTile(i,j).isSeeThrough()){
                        middleLane = false;
                    }
                }
                finalI = i;
                finalJ = j;
            }
            if(finalI!=map.getVerticalSize()-1 && rightLane){
                visibleTiles.add(map.getTile(finalI+1,finalJ));
            }
            if(finalJ!=0 && leftLane){
                visibleTiles.add(map.getTile(finalI, finalJ-1));
            }
            return visibleTiles;
        }

        throw new IllegalStateException("The angle of the agent is not a valid discrete value: " + angle);


    }
}
