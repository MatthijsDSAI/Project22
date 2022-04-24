package exploration;

import controller.Map.tiles.Tile;

public class Qlearning {
    private final double alpha = 0.1;
    private final double gamma = 0.9;


    private int width;
    private int height;
    private int states = width * height;

    Tile[][] map;
    private double[][] Q;
    private int[][] R;

    public Qlearning(Tile[][] map) {
        this.map = map;
        width = map.length;
        height = map[0].length;
        Q = new double[states][states];
        R = new int[states][states];
    }

    public void initializeQ(){
        for (int i = 0; i < states; i++){
            for(int j = 0; j < states; j++){
                Q[i][j] = R[i][j];
            }
        }
    }

    public void caluQ(){
        int steps = 0;
        int maxSteps = 1000;
        while (steps<maxSteps){
            //Choose an action from current state by using policy
//            qFunction();
            //currentState = nextState
            steps++;
        }
    }


    public double getMaxQ(int nextState){

        double maxValue = Double.MIN_VALUE;
        //For each possible action of next state
//        for(){
//            double value = Q[nextState][]
//            if(value>maxValue){
//                maxValue = value;
//            }
//        }

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
}
