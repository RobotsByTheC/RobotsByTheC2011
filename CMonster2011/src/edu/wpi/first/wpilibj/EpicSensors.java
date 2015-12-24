package edu.wpi.first.wpilibj;

import com.sun.squawk.util.MathUtils;

public class EpicSensors{ //I'm going to rename this to "Tracker" when I have it in a project 
    
    static final double g = 32.1740486; //Citation needed!!!!
                                        //http://www.bipm.org/utils/common/pdf/si_brochure_8_en.pdf#page=51
                                        //Convert cm/s2 to ft/s2
    //nanofeet: 32,174,048,600 (exact!)
    
    //NO DOUBLESSSSSSS!!!!!!!!!!!!!!!!!
    static final long gNano = 32174048600L; //<--- needs the "L"
    static final double PI    = 3.14159265358979323846264338327950288419716939937510582097494459; //goes to 63 digits after the decimal, instead of 15 in Math.PI
    private Gyro gyro;
    
    
    private ADXL345_I2C accelerometer;
    private double startingAngle;
    private long currentTime, 
                 lastTime;
    private double distance, 
                   velocity, 
                   acceleration;
    
    public EpicSensors(int AccelSlotNum, ADXL345_I2C.DataFormat_Range gRange, int gyroChannel){
        accelerometer = new ADXL345_I2C(AccelSlotNum, gRange);
        gyro          = new Gyro(gyroChannel);
        startingAngle = 0; //this should be adjusted later with a system for finding the starting angle //It should be within about 5* when we start, anyway
    }
    
    public double toRadians(double degrees){ //MUCH more accurate than Math.toRadians
        return degrees * PI / 180;
    }    
    
    //SOMEBODY MAKE SURE, that the gyro returns positive degrees for rotation counterclockwise
    public double angle = (gyro.getAngle() + startingAngle) % 360; //find an equivilant angle between 0 and 360
    //One line, aww yeah. This works, so don't change it!
    
    //HEY YOU!!! CHECK TO SEE IF THE ACCELEROMETER EVER RETURNS A NEGATIVE ACCELERATION! Answer: yes (decceleration = -acceleration)
    public double getAccelerationX(){ 
        //make this nanofeet
        double AXRobot = Math.abs(accelerometer.getAcceleration(ADXL345_I2C.Axes.kX)*g); //acceleration in ft/s^2 in the x direction relative to the robot
        double AYRobot = Math.abs(accelerometer.getAcceleration(ADXL345_I2C.Axes.kY)*g); //acceleration in ft/s^2 in the y direction relative to the robot
        double theta   = this.toRadians(angle);
        return AXRobot * Math.cos(theta) - AYRobot * Math.sin(theta);
    }
    
    public double getAccelerationY(){ 
        //make this nanofeet
        double AXRobot = Math.abs(accelerometer.getAcceleration(ADXL345_I2C.Axes.kX)*g); //acceleration in ft/s^2 in the x direction relative to the robot
        double AYRobot = Math.abs(accelerometer.getAcceleration(ADXL345_I2C.Axes.kY)*g); //acceleration in ft/s^2 in the y direction relative to the robot
        double theta   = this.toRadians(angle);
        return AYRobot * Math.cos(theta) + AXRobot * Math.sin(theta);
    }
    
    public double getAccelerationMagnitude(){
        return Math.sqrt(MathUtils.pow(this.getAccelerationX(), 2)+MathUtils.pow(this.getAccelerationY(), 2));
    }
    
    public Gyro getGyro(){
        return gyro;
    }
    
    public ADXL345_I2C getAccelerometer(){
        return accelerometer;
    }
    
    public double getAngleDegrees(){ 
        return angle; 
    }
    
    public double getAngleRadians(){
        return this.toRadians(angle);
    }
    
    public double getPI(){
        return PI;
    }
    
    public double getNanoTime(){
        return Timer.getFPGATimestamp();
    }
    
}


//Comment the sh!t out of everything. Oh wait, I can't use that language in code for a school project...
//Better:
//Comment the pancakes out of everything. (Because I like pancakes)