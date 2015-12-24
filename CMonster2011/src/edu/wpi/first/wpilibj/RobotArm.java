/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.wpi.first.wpilibj;

/**
 *
 * @author Sean Halloran
 */
public class RobotArm {
    Encoder dustinEncoder;
    DigitalInput armLimitSwitch;
    Jaguar wolfJag;
    boolean reverseEncoder;
    int pos[];
    int tolerance=10;
    
    /**
     * =========================heights array==============================
     *      i           position        height         encoderValue
     * --------------------------------------------------------------
     *      0           Edge Low
     *      1           Edge Mid
     *      2           Edge High
     *      3           Mid Low
     *      4           Mid Mid
     *      5           Mid High
     *      6           Floor
     *      7           Feeder
     */

    public RobotArm(int aChannel, int bChannel, int jagChannel){
        dustinEncoder = new Encoder(aChannel, bChannel);
        wolfJag = new Jaguar(jagChannel);
        reverseEncoder=false;
        pos = new int[8];
        dustinEncoder.start();
    }
    
    public RobotArm(Encoder newEncoder, Jaguar newJaguar){
        dustinEncoder = newEncoder;
        wolfJag = newJaguar;
        reverseEncoder=false;
        pos = new int[8];
        dustinEncoder.start();
    }

    public void reverseEncoder(boolean x){
        if(reverseEncoder)
            reverseEncoder=false;
        else
            reverseEncoder=true;
        dustinEncoder.setReverseDirection(reverseEncoder);
    }

    public int getTolerance(){ return tolerance;}
    public void setTolerance(int i){ tolerance=i;}

    public Encoder Encoder(){ return dustinEncoder;}
    public Jaguar Jaguar(){ return wolfJag;}

    public DigitalInput getLimitSwitch(){ return armLimitSwitch;}
    public void setLimitSwitch(DigitalInput x){ armLimitSwitch = x;}

    public void manualControl(Joystick joy){
        double joyY = joy.getY() / 2;
        if(armLimitSwitch!=null)
            if(armLimitSwitch.get() && joyY>0)
                joyY=0;
        wolfJag.set(joyY);
        DriverStationLCD.getInstance().println(DriverStationLCD.Line.kUser4, 1, Double.toString(joyY));
    }

    public void setPosition(String goalPos, double speed)
    {
        if(Math.abs(dustinEncoder.get()-pos[posStringToPosIndex(goalPos)])>tolerance){
            if(dustinEncoder.get() < pos[posStringToPosIndex(goalPos)])
                wolfJag.setSpeed(speed);
            else if(dustinEncoder.get() > pos[posStringToPosIndex(goalPos)])
                wolfJag.setSpeed(-speed);
        }
        else
            wolfJag.setSpeed(0);
    }

    public void setPosition(String goalPos)
    {
        setPosition(goalPos, 1);
    }

    public String getPosition()
    {
        int x=-1;
        for(int i=0; i<10; i++){
            if(Math.abs(pos[i]-dustinEncoder.get())<tolerance)
                x=i;
        }
        if     (x==0)       return "edge low";
        else if(x==1)       return "edge mid";
        else if(x==2)       return "edge high";
        else if(x==3)       return "mid low";
        else if(x==4)       return "mid mid";
        else if(x==5)       return "mid high";
        else if(x==6)       return "floor";
        else if(x==7)       return "feeder";
        
        return "OUT OF BOUNDS";
    }

    public void unfold(double speed)
    {
 ///       this.setPosition("floor", speed);

        ///// quick fix:
        if(dustinEncoder.get() < 600)
            wolfJag.setSpeed(-0.5);
        else if(dustinEncoder.get() > 600)
            wolfJag.setSpeed(0.0);
    }

    public void goToStartPosition()
    {
        if(dustinEncoder.get()>tolerance){
            wolfJag.setSpeed(-.8);
        }
        else
        {
            wolfJag.setSpeed(0.0);
        }
    }

    private int posStringToPosIndex(String pos)
    { //takes the string for the pos, and returns the index of that pos in the array
        pos=pos.toLowerCase();

        if(pos.equals("edge low"))          return 0;
        else if(pos.equals("edge mid"))     return 1;
        else if(pos.equals("edge high"))    return 2;
        else if(pos.equals("mid low"))      return 3;
        else if(pos.equals("mid mid"))      return 4;
        else if(pos.equals("mid high"))     return 5;
        else if(pos.equals("floor"))        return 6;
        else if(pos.equals("feeder"))       return 7;

        return -1;  //if none match return -1
    }

    public void goToEncPos(int goal, double speed)
    {
        if(Math.abs(Math.abs(dustinEncoder.getRaw()-goal))>tolerance)
        {
            wolfJag.setSpeed(speed);
        }
        else
            wolfJag.setSpeed(0);
    }
}