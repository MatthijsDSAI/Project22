package utils;

public enum DirectionEnum {
    //right is 0 degrees, like unit circle
    LEFT("left", 180),
    RIGHT("right", 0),
    UP("up", 90),
    DOWN("down", 270);


    private final String direction;
    private final int angle;
    DirectionEnum(String direction, int angle) {
        this.direction = direction;
        this.angle = angle;
    }
    public String getDirection(){
        return direction;
    }
    public int getAngle(){return angle;}

    public DirectionEnum getDirection(int angle) {
        if(angle>=360){
            angle-=360;
        }
        if(angle==0){
            return DirectionEnum.RIGHT;
        }
        if(angle==90){
            return DirectionEnum.UP;
        }
        if(angle==180){
            return DirectionEnum.LEFT;
        }
        if(angle==270){
            return DirectionEnum.DOWN;
        }
        throw new RuntimeException("Issue with angle passed to enum; invalid value: " + angle + " , must be multiple of 90 or 0.");
    }
}
