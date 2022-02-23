package agents;

import controller.Area;

public abstract class Agent implements AgentI{
    double audiostdeviation;
    int x_position,y_position, angle;
    public String a_name;
    double baseSpeed, range, visangle, visibility,restTime,sprintTime, turn_speed, noiseProd;

    public Agent(int x_position, int y_position)
    {
        this.x_position = x_position;
        this.y_position = y_position;
        this.a_name = "Agent";
        this.baseSpeed = 10.0;
        this.audiostdeviation=10;
        angle=0;
    }

    public Agent(double baseSpeed, int x_position, int y_position, int angle)
    {
        this.a_name="Agent";
        this.baseSpeed = baseSpeed;
        this.x_position = x_position;
        this.y_position = y_position;
        this.audiostdeviation=10;
        this.angle=angle;
    }

    public Agent(String name, double baseSpeed, int x_position, int y_position, int angle)
    {
        this.a_name=name;
        this.baseSpeed = baseSpeed;
        this.audiostdeviation=10;
        this.x_position = x_position;
        this.y_position = y_position;
        this.angle=angle;
    }

    @Override
    public void setVelocities(double speed, double rest, double sprint_time, double turn_speed, double noise_level)
    {
        this.baseSpeed=speed;
        this.restTime = rest;
        this.sprintTime=sprint_time;
        this.turn_speed= turn_speed;
        this.noiseProd = noise_level;
    }

    @Override
    public void setVisualcap(double range, double angle, double visibility){
        this.range = range;
        this.visangle = angle;
        this.visibility = visibility;
    }

    //To be done
    @Override
    public void setAudiocap(){
        this.audiostdeviation=10;
    }

    @Override
    //1-> visited, the rest will be determined later
    public void setCommunication(Area[] markers, int[] type){
        //To be done
    }

    public void checkarea()
    {
        //if object is a wall turnDirection
    }

    public void turnNorth()
    {
        angle=0;
    }

    public void turnEast()
    {
        angle=90;
    }

    public void turnSouth()
    {
        angle=180;
    }

    public void turnWest()
    {
        angle=270;
    }

    public void move(int angle)
    {
        //0  -> north
        if(angle == 0)
        {
            y_position+=baseSpeed;
            //checkarea();
        }
        //90 -> east
        if(angle == 90)
        {
            x_position+=baseSpeed;
            //checkarea();
        }
        //180 -> South
        if(angle == 180)
        {
            y_position-=baseSpeed;
            //checkarea();
        }
        // 270 -> west
        if(angle == 270)
        {
            x_position-=baseSpeed;
            //checkarea();
        }
        //check relationship between speed and position when related to time
    }


    public String runIntoAgent(String agent1, String agent2)
    {
        if(agent1.equals(agent2)) {
            System.out.println("Agent can't run into itself");
            return null;
        }
        double rand = Math.random();
        if(rand%2==0)
            return agent1;
        return agent2;
    }

}
