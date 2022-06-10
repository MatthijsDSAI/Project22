package exploration;

import agents.Agent;
import controller.Map.tiles.Tile;

import java.util.ArrayList;
import java.util.Random;

public class Qlearning {
    private final double alpha = 0.1;
    private final double gamma = 0.9;


    private int standardTALength = 5;
    //gives the thickness of the surrounding of the TA
    private int standardBelt = 5;
    private int standardTotalLength = standardTALength + 2*standardBelt;
    private int numberOfActions = 4;
    private int states = standardTotalLength * standardTotalLength;
    private final int reward = 50;
    private final int penalty = 10;

    Tile[][] map;
    private double[][] Q;
    private int[][] R;

    Random r = new Random();

    public Qlearning(Tile[][] map) {
        this.map = map;
        standardTotalLength = map.length;
        Q = new double[states][numberOfActions];
        R = new int[states][numberOfActions];
    }

    public void initializeR(){
        for(int i=0; i<R.length; i++) {
            for (int j = 0; j < R[0].length; j++) {
                R[i][j] = 0;
            }
        }
        for(int i=0; i<map.length; i++){
            for(int j=0; j<map[0].length; j++){
                //North
                if(i-1 >= 0){
                    if(map[i-1][j].isWalkable()){
                        R[i * standardTotalLength + j][(i-1) * standardTotalLength + j] += reward;
                    }else {
                        R[i * standardTotalLength + j][(i-1) * standardTotalLength + j] -= penalty;
                    }
                }
                //South
                if(i+1 <= standardTotalLength){
                    if(map[i+1][j].isWalkable()){
                        R[i * standardTotalLength + j][(i+1) * standardTotalLength + j] += reward;
                    }else {
                        R[i * standardTotalLength + j][(i+1) * standardTotalLength + j] -= penalty;
                    }
                }
                //West
                if(j-1 >= 0){
                    if(map[i][j-1].isWalkable()){
                        R[i * standardTotalLength + j][i * standardTotalLength + j - 1] += reward;
                    }else {
                        R[i * standardTotalLength + j][i * standardTotalLength + j - 1] -= penalty;
                    }
                }
                //East
                if(j+1 < standardTotalLength){
                    if(map[i][j+1].isWalkable()){
                        R[i * standardTotalLength + j][i * standardTotalLength + j + 1] += reward;
                    }else {
                        R[i * standardTotalLength + j][i * standardTotalLength + j + 1] -= penalty;
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

    public void train(Agent agent, int maxStep){
        int x = agent.getX_position();
        int y = agent.getY_position();
        int currentState = y * standardTotalLength + x;
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
        int i = state / standardTotalLength;
        int j = state - i;
        return map[i][j].getType().equals("TargetArea");
    }
}
