package utils;

public class Utils {
    public static int TransFormIntoValidAngle(int angle){
        while(angle<0){
            angle+=360;
        }
        while(angle>=360){
            angle-=360;
        }
        return angle;
    }
}
