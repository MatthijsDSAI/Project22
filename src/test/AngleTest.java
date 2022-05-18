import org.junit.jupiter.api.Test;
import utils.Utils;
import static org.junit.jupiter.api.Assertions.*;
public class AngleTest {

    @Test
    public void angleDifferenceOne(){
        double alpha = 90;
        double beta = 15;
        double expectedResult = 75;
        double actualResult = Utils.differenceBetweenAngles(alpha, beta);
        assertEquals(expectedResult, actualResult);
    }
    @Test
    public void angleDifferenceTwo(){
        double alpha = 0;
        double beta = 0;
        double expectedResult = 0;
        double actualResult = Utils.differenceBetweenAngles(alpha, beta);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void angleDifferenceThree(){
        double alpha = 360;
        double beta = 360;
        double expectedResult = 0;
        double actualResult = Utils.differenceBetweenAngles(alpha, beta);
        assertEquals(expectedResult, actualResult);
    }
    @Test
    public void angleDifferenceFour(){
        double alpha = 0;
        double beta = 85;
        double expectedResult = 85;
        double actualResult = Utils.differenceBetweenAngles(alpha, beta);
        assertEquals(expectedResult, actualResult);
    }
    @Test
    public void angleDifferenceFive(){
        double alpha = 315;
        double beta = 90;
        double expectedResult = 135;
        double actualResult = Utils.differenceBetweenAngles(alpha, beta);
        assertEquals(expectedResult, actualResult);
    }
    @Test
    public void angleDifferenceSix(){
        double alpha = 40;
        double beta = 270;
        double expectedResult = 130;
        double actualResult = Utils.differenceBetweenAngles(alpha, beta);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void angleDifferenceSeven(){
        double alpha = 190;
        double beta = 90;
        double expectedResult = 100;
        double actualResult = Utils.differenceBetweenAngles(alpha, beta);
        assertEquals(expectedResult, actualResult);
    }



}
