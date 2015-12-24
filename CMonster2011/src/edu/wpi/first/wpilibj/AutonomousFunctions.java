package edu.wpi.first.wpilibj;



public class AutonomousFunctions{
    
    private BestRobotDrive robotDrive;
    
    private ADXL345_I2C accelerometer;
    
    /**Default X acceleration*/
    public double defaultX = 0;
    /**Default Y acceleration*/
    public double defaultY = 0;
    /**Default Z acceleration*/
    public double defaultZ = -1;
    
    /**Enables balancing*/
    public boolean enabled = false;
    
    public void init(){
        accelerometer = new ADXL345_I2C(1, ADXL345_I2C.DataFormat_Range.k4G);
    }
    
    /**
     * Gets the default readings of the accelerometer at the beginning of the match.
     * Call during an init() period. (robotInit(), autonomousInit(), or teleopInit()).
     * These values should be ~0, ~0, and ~(-1), respectively.
     */
    public void getAccelerationDefaults(){
        defaultX = accelerometer.getAcceleration(ADXL345_I2C.Axes.kX);
        defaultY = accelerometer.getAcceleration(ADXL345_I2C.Axes.kY);
        defaultZ = accelerometer.getAcceleration(ADXL345_I2C.Axes.kZ);
    }
    
    /**
     * Enables the balancing method.
     */
    public void enable(){
        enabled = true;
    }
    
    /**
     * Disables the balancing method.
     */
    public void disable(){
        enabled = false;
    }
    
    /**
     * Automatically balances on a bridge.
     * An easy way to test this method is having a slider or similar on the SmartDashboard, so we won't need to reboot the robot a lot.
     * @param speedMod Modifier for the speed - higher numbers result in lower speeds. 
     * @param turnMod  Modifier for turning while driving - higher number is higher speed. 
     * X is short axis of robot, Y is long axis. If we crabwalk onto the ramp, swap X and Y in this method.
     */
    public void balance(double speedMod, double turnMod){
        double x = 0,
               y = 0;
        double motorSpeed = 0;
        while(enabled){
            x = accelerometer.getAcceleration(ADXL345_I2C.Axes.kX);
            y = accelerometer.getAcceleration(ADXL345_I2C.Axes.kY);
            motorSpeed = Math.sqrt((Math.abs(defaultZ - accelerometer.getAcceleration(ADXL345_I2C.Axes.kZ)) / speedMod));
            if(y < defaultY){     //If front is higher than back, goes forward
                if(x == defaultX) //Going on perfectly
                    robotDrive.jaguarDrive(motorSpeed, motorSpeed, motorSpeed, motorSpeed);
                if(x < defaultX)  //Angled to the left on the bridge
                    robotDrive.jaguarDrive(motorSpeed * turnMod, motorSpeed, motorSpeed * turnMod, motorSpeed);
                if(x > defaultX)  //Angled to the right on the bridge
                    robotDrive.jaguarDrive(motorSpeed, motorSpeed * turnMod, motorSpeed, motorSpeed * turnMod);
            }
            if(y > defaultY){    //If front is lower than back, goes backward
                if(x == defaultX)
                    robotDrive.jaguarDrive(-motorSpeed, -motorSpeed, -motorSpeed, -motorSpeed);
                if(x < defaultX)
                    robotDrive.jaguarDrive(-motorSpeed * turnMod, -motorSpeed, -motorSpeed * turnMod, -motorSpeed);
                if(x > defaultX)
                    robotDrive.jaguarDrive(-motorSpeed, -motorSpeed * turnMod, -motorSpeed, -motorSpeed * turnMod);
            }
            if(y == defaultY)   //If bridge is horizontal
                robotDrive.jaguarDrive(0, 0, 0, 0);
        }
    } 
}
