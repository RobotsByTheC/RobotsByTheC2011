/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.wpi.first.wpilibj;
// jaguarDrive((jX+jY)/2, -(jY-jX)/2, (jY-jX)/2, -(jX+jY)/2);
/**
 *
 * @author Sean Halloran
 */
public class BestRobotDrive extends RobotDrive{
/** PETER: These don't need to be here because the same variables in RobotDrive are 'protected' so we can use them
 * I think that's why we got the error about PWM 1 already being allocated... blah blah...
 *
 * SEAN: They are protected, so you're right we don't need them. But I am still having the problem with PWM 1 already
 * being allocated.
 *
 * SEAN: I noticed that the parameters for this class went FL, FR, RR, RL; but the parameters for the constructor
 * of the super class we were passing them go FL, RL, FR, RR. I fixed this by changing the super line in the
 * constructor. This may be why we had problems with jaguar drive, so I took the negative signs off of
 * jaguar drive, and took away the unneccesary negative signs in other methods that use jaguar drive.
 *
 * SEAN: We also should not need to have "this." before the jaguar drive method, but I guess if it ain't broke
 * don't fix it.
 *
 * Eric: lololololololololol I r a Rob0t!!!!!111!111!!!!!
 *
    private SpeedController m_frontLeftMotor;
    private SpeedController m_frontRightMotor;
    private SpeedController m_rearLeftMotor;
    private SpeedController m_rearRightMotor;
 */
    double currentDistance, startingDistance, jspeed, travelDistance, incriment, period, A, B, C, D, startAngle, precisionDriving, curve;
    int lastEnc, currentEnc;
    boolean sd=true, stx=true;
    Encoder sylvieEncoder;
    public final double kClicksPerInch = 81.238888;
    Gyro kevinGyro;
    EpicSensors EpicSensors;

    public BestRobotDrive(int FL, int RL, int FR, int RR){
        super(FL, RL, FR, RR);
        setInvertedMotor(MotorType.kFrontLeft, true);
        setInvertedMotor(MotorType.kRearLeft, true);
        setInvertedMotor(MotorType.kFrontRight, true);
        setInvertedMotor(MotorType.kRearRight, true);
        precisionDriving=1;
    }
    public BestRobotDrive(int leftMotor, int rightMotor){
        super(leftMotor, rightMotor);
        precisionDriving=1;
    }

    public void setPrecisionDriving(double x){ precisionDriving=x;}
    public double getprecisionDriving(){ return precisionDriving;}

    public void mecanumDrive(Joystick j)
    {
        double jX = j.getX()*precisionDriving;
        double jY = j.getY()*precisionDriving;



        jY = -jY;
        /********** our custom Mecanum drive *******/

        // CHRIS WILLIAMS's MECANUM MAGIC
        jaguarDrive((jX+jY)/2, (jY-jX)/2, -(jY-jX)/2, -(jX+jY)/2);
        //          FrontLeft, RearLeft, FrontRight , RearRight
    }

    public void jaguarDrive(double FL, double RL, double FR, double RR)
    /* there is an interesting problem: this method goes FR, FL, RR, RL,
     * but look at the comment under Chris Williams's mecanum magic and there, it goes FL, FR, RL, RR. That might be a problem.
     */
    {
        m_frontLeftMotor.set(FL);
        m_rearLeftMotor.set(RL);
        m_frontRightMotor.set(FR);
        m_rearRightMotor.set(RR);
    }

    public void crabLeft(double speed)
    {
        double jX = -speed*precisionDriving;
        double jY = 0;
        jaguarDrive((jX+jY)/2, (jY-jX)/2, -(jY-jX)/2, -(jX+jY)/2);
    }

    public void crabRight(double speed)
    {
        double jX = speed*precisionDriving;
        double jY = 0;
        jaguarDrive((jX+jY)/2, (jY-jX)/2, -(jY-jX)/2, -(jX+jY)/2);
    }

    public void circleDrive(double speed)
    {

        double turnSpeed;
        if(Math.abs(speed) > 0.4){
            if(speed>0)
                turnSpeed = 0.4;
            else
                turnSpeed = -0.4;
        }
        else
            turnSpeed = speed*0.8;
        //turnSpeed = speed*0.4;
        this.jaguarDrive(turnSpeed, turnSpeed, turnSpeed, turnSpeed);
    }

    public void setEncoder(Encoder enc){
        sylvieEncoder=enc;
    }
    public Encoder getEncoder()
    {
        return sylvieEncoder;
    }

    public void setGyro(Gyro x){
        kevinGyro = x;
    }
    public Gyro getGyro(){
        return kevinGyro;
    }

    public void driveDistance(double goalDistance, double speed, int clicksPerSpin, double wheelDiameter){
        /**
         * The parameters are as follows
         * Goal Distance is how far the robot should move IN INCHES
         * Speed is how fast the robot should move when traveling that distance (-1.0 is full reverse, 1.0 is full forward)
         * Clicks Per Spin is the number of clicks the encoder reports in one rotation of the wheel
         * Wheel Radius the radius of the robot wheel, it is used to calculate circumference
         * Encoder is the encoder created in the main method that is used for everything
         */
        /**
         *
        //jspeed = 0.2;
        if(sd){
            startingDistance = Math.abs(sylvieEncoder.getRaw())*((wheelDiameter*Math.PI)/clicksPerSpin); //This only measures distance traveled, distance backwards should count to this number
            A = speed;
            period = 2*(Math.abs(goalDistance-startingDistance))*1.1;
            B = Math.PI/(Math.abs(startingDistance-goalDistance)*1.1);
            C = (.5*period-(Math.abs(startingDistance-goalDistance)))+startingDistance;
            D = 0;
            jspeed=0;
            startAngle = kevinGyro.getAngle();
            sd=false;
        }
        currentDistance = Math.abs(sylvieEncoder.getRaw())*((wheelDiameter*Math.PI)/clicksPerSpin); //This only measures distance traveled, distance backwards should count to this number
        
        if(currentDistance<goalDistance && stx){
            jspeed=A*Math.sin(B*(currentDistance+C))+D;
            this.jaguarDrive(jspeed, jspeed, -jspeed, -jspeed);
        }
        else{
            if(stx){
                this.jaguarDrive(0,0,0,0);
                stx=false;
            }
            if(startAngle<kevinGyro.getAngle())
                rotate(.25);
            else if(startAngle>kevinGyro.getAngle())
                rotate(-.25);
            else
                jaguarDrive(0,0,0,0);
        }
        System.out.println("Current Distance: "+currentDistance+"   jSpeed: "+jspeed);

        //currentDistance = Math.abs((sylvieEncoder.getRaw() * (wheelDiameter*Math.PI)) / clicksPerSpin);
        //SmartDashboard.log(currentDistance, "current distance");

//        if(currentDistance<=goalDistance)
//            this.jaguarDrive(speed, speed, -speed, -speed);
//        else
//            this.jaguarDrive(0, 0, 0, 0);
         *
         *
         */
        if(sd){
            startingDistance = Math.abs(sylvieEncoder.getRaw())*((wheelDiameter*Math.PI)/clicksPerSpin); //This only measures distance traveled, distance backwards should count to this number
            A = speed;
            period = 2*(Math.abs(goalDistance-startingDistance))*1.1;
            B = Math.PI/(Math.abs(startingDistance-goalDistance)*1.1);
            C = (.5*period-(Math.abs(startingDistance-goalDistance)))+startingDistance;
            D = 0;
            jspeed=0;
            startAngle = kevinGyro.getAngle();
            curve=0;
            //this.setInvertedMotor(MotorType.kFrontRight, false);
            //this.setInvertedMotor(MotorType.kRearRight, false);
            sd=false;
        }
        currentDistance = Math.abs(sylvieEncoder.getRaw())*((wheelDiameter*Math.PI)/clicksPerSpin); //This only measures distance traveled, distance backwards should count to this number
        SmartDashboard.log(startAngle, "starting Angle");

        if(currentDistance<goalDistance&&stx){
            if(currentDistance>12)
                curve = (kevinGyro.getAngle()-startAngle)*.07;
            else
                curve = 0;
            jspeed=-A*Math.sin(B*(currentDistance+C))+D;
            //this.jaguarDrive(jspeed, jspeed, -jspeed, -jspeed);
            this.drive(jspeed, -curve);
        }
        else{
            stx=false;
            //this.setInvertedMotor(MotorType.kFrontRight, true);
            //this.setInvertedMotor(MotorType.kRearRight, true);
//            curve = (kevinGyro.getAngle()-startAngle)*.05;
//            drive(0, curve);
            if(kevinGyro.getAngle()<startAngle)
                rotate(.2);
            else if(kevinGyro.getAngle()>startAngle)
                rotate(-.2);
            else
                jaguarDrive(0,0,0,0);
        }
    }

    public void driveDistance(double goalDistance, double speed){
        this.driveDistance(goalDistance, speed, 2120, 2.988*2);
    }

    public void driveDistance(double goalDistance){
        this.driveDistance(goalDistance, 0.5);
    }

    public void driveDistanceClicks(int goalClicks, double speed){
        if(sylvieEncoder.getRaw()<goalClicks)
            this.jaguarDrive(speed, speed, -speed, -speed);
        else
            this.jaguarDrive(0, 0, 0, 0);
    }

    public void rotate(double speed){ //pos numbers go counterclockwise
        jaguarDrive(speed, speed, speed, speed);
    }

    public void Drive(double speed, double curve){
        this.setInvertedMotor(MotorType.kFrontRight, false);
        this.setInvertedMotor(MotorType.kRearRight, false);
        super.drive(speed, curve);
        this.setInvertedMotor(MotorType.kFrontRight, true);
        this.setInvertedMotor(MotorType.kRearRight, true);
    }
    
    public void turnToAngle(int goalAngle){ //good for resetting the startAngle in EpicSensors. Example: turnToAngle(360-EpicSensors.startingAngle)
        while (EpicSensors.getAngleDegrees() != goalAngle)
            rotate(.1);
    }
}