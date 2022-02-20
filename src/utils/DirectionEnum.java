package utils;

public enum DirectionEnum {
    LEFT("left"),
    RIGHT("right"),
    UP("up"),
    DOWN("down");


    private final String direction;
    DirectionEnum(String direction) {
        this.direction = direction;
    }
    public String getDirection(){
        return direction;
    }
}
