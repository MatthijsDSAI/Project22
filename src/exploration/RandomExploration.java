package exploration;

import static java.lang.Math.*;

public class RandomExploration {

    public static int randomMove() {
        int randomMove = (int) (Math.random()*360);
        
        return randomMove;
    }

    public static void main(String[] args){
        int randomMove = randomMove();
        System.out.println(String.valueOf(randomMove));
    }

}
