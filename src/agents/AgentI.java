package agents;

import controller.Area;

public interface AgentI {
    public double audiostdeviation= 10;
    public void setVelocities(double speed, double rest, double sprint_time, double turn_speed, double noise_level);
    public void setVisualcap(double range, double angle, double visibility);
    public void setAudiocap();
    public void setCommunication(Area[] marker, int[] type);
}
