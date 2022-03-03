package exploration;

import static java.lang.Math.*;

public class RandomExploration {

    public static int randomMove() {
        int randomMove = (int) (Math.random() * 360);
        if (randomMove < 90) {
            return 0;
        } else if (randomMove < 180) {
            return 90;
        } else if (randomMove < 270) {
            return 180;
        } else return 270;
    }

}