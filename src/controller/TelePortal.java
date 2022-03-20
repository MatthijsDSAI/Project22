package controller;

public class TelePortal extends Area {
    protected int yTarget;
    protected int xTarget;
    protected double outOrientation;

    public TelePortal(int x1, int x2, int y1, int y2, int targetX, int targetY, double orient){
        super(x1,x2,y1,y2, "TelePortal");
        yTarget=targetY;
        xTarget=targetX;
        outOrientation = orient;
    }

    public int[] getNewLocation(){
        int[] target = new int[] {xTarget,yTarget};
        return target;
    }

    public double getNewOrientation(){
        return outOrientation;
    }

    public int getyTarget() {return yTarget;}

    public int getxTarget() {return xTarget;}

    public double getOutOrientation() {return outOrientation;}
}
