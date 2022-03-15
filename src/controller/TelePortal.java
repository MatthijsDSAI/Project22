package controller;

public class TelePortal extends Area {
    protected int yTarget;
    protected int xTarget;
    protected double outOrientation;

    public TelePortal(int x1, int y1, int x2, int y2, int targetX, int targetY){
        super(x1,y1,x2,y2, "TelePortal");
        yTarget=targetY;
        xTarget=targetX;
        outOrientation = 0.0;
    }

    public TelePortal(int x1, int y1, int x2, int y2, int targetX, int targetY, double orient){
        super(x1,y1,x2,y2, "TelePortal");
        yTarget=targetY;
        xTarget=targetX;
        outOrientation = orient;
    }

    public int[] getNewLocation(){
        return new int[] {xTarget,yTarget};
    }

    public double getNewOrientation(){
        return outOrientation;
    }

    public int getyTarget() {return yTarget;}

    public int getxTarget() {return xTarget;}

    public double getOutOrientation() {return outOrientation;}
}
