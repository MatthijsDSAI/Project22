package exploration;

import agents.Agent;
import controller.Map.tiles.Tile;

import java.util.ArrayList;
import java.util.Random;

public class Qlearning {
    private final double alpha = 0.1;
    private final double gamma = 0.9;


    private int width;
    private int height;
    private int states = width * height;
    private final int reward = 50;
    private final int penalty = 10;

    Tile[][] map;
    private double[][] Q;
    private int[][] R;

    Random r = new Random();

    public Qlearning(Tile[][] map) {
        this.map = map;
        width = map.length;
        height = map[0].length;
        Q = new double[states][states];
        R = new int[states][states];
    }

    public void initializeR(){
        for(int i=0; i<R.length; i++) {
            for (int j = 0; j < R[0].length; j++) {
                R[i][j] = 0;
            }
        }
        // stateCount = i * width + j not sure is correct
        for(int i=0; i<map.length; i++){
            for(int j=0; j<map[0].length; j++){
                //North
                if(i-1 >= 0){
                    if(map[i-1][j].isWalkable()){
                        R[i * width + j][(i-1) * width + j] += reward;
                    }else {
                        R[i * width + j][(i-1) * width + j] -= penalty;
                    }
                }
                //South
                if(i+1 <= height){
                    if(map[i+1][j].isWalkable()){
                        R[i * width + j][(i+1) * width + j] += reward;
                    }else {
                        R[i * width + j][(i+1) * width + j] -= penalty;
                    }
                }
                //West
                if(j-1 >= 0){
                    if(map[i][j-1].isWalkable()){
                        R[i * width + j][i * width + j - 1] += reward;
                    }else {
                        R[i * width + j][i * width + j - 1] -= penalty;
                    }
                }
                //East
                if(j+1 < width){
                    if(map[i][j+1].isWalkable()){
                        R[i * width + j][i * width + j + 1] += reward;
                    }else {
                        R[i * width + j][i * width + j + 1] -= penalty;
                    }
                }
            }
        }
    }

    public void initializeQ(){
        for (int i = 0; i < states; i++){
            for(int j = 0; j < states; j++){
                Q[i][j] = R[i][j];
            }
        }
    }

    public void tarin(Agent agent, int maxStep){
        int x = agent.getX_position();
        int y = agent.getY_position();
        int currentState = y * width + x;
        for (int i = 0; i < maxStep; i++){
            while (!isTarget(currentState)){
                //Choose an action from current state by using policy
                ArrayList<Integer> possibleActions = possibleActionsFromState(currentState);
                int nextState = possibleActions.get(r.nextInt(possibleActions.size()));

                qFunction(currentState,nextState);

                currentState = nextState;
            }
        }

    }

    public double getMaxQ(int nextState){
        ArrayList<Integer> possibleActions = possibleActionsFromState(nextState);
        double maxValue = Double.MIN_VALUE;
//        For each possible action of next state
        for(int action: possibleActions){
            double value = Q[nextState][action];
            if(value > maxValue){
                maxValue = value;
            }
        }
        return maxValue;
    }

    // Q(state,action)= Q(state,action) + alpha * (R(state,action) + gamma * Max(next state, all actions) - Q(state,action))
    public void qFunction(int currentState, int nextState){
        double q = Q[currentState][nextState];
        double maxQ = getMaxQ(nextState);
        int r = R[currentState][nextState];

        double qValue = q + alpha * (r + gamma * maxQ - q);
        Q[currentState][nextState] = qValue;
    }

    public ArrayList<Integer> possibleActionsFromState(int state) {
        ArrayList<Integer> possibleActions = new ArrayList<>();
        for (int i = 0; i < states; i++) {
            if (R[state][i] >= 0) {
                possibleActions.add(i);
            }
        }
        return possibleActions;
    }


    public boolean isTarget(int state){
        boolean result = false;
        // stateCount = i * width + j
        int i = state / width;
        int j = state - i;
        return map[i][j].getType().equals("TargetArea");
    }
}
