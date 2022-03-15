package utils;

public enum DirectionEnum {
    //right is 0 degrees, like unit circle
    WEST("west", 90),
    EAST("east", 270),
    NORTH("north", 0),
    SOUTH("south", 180);


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


    //i think this should work, but might require debugging
    public static DirectionEnum getDirection(double angle) {
        if(angle>=360){
            angle-=360;
        }
        if(angle<0){
            angle+=360;
        }
        if(angle==DirectionEnum.NORTH.getAngle()){
            return DirectionEnum.NORTH;
        }
        if(angle==DirectionEnum.EAST.getAngle()){
            return DirectionEnum.EAST;
        }
        if(angle==DirectionEnum.SOUTH.getAngle()){
            return DirectionEnum.SOUTH;
        }
        if(angle==DirectionEnum.WEST.getAngle()){
            return DirectionEnum.WEST;
        }
        throw new RuntimeException("Issue with angle passed to enum; invalid value: " + angle + " , must be multiple of 90 or 0.");
    }
}
