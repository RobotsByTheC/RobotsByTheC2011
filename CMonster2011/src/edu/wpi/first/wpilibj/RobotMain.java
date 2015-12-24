package edu.wpi.first.wpilibj;

import edu.wpi.first.wpilibj.camera.*;

public class RobotMain extends IterativeRobot
{
    //final double kWheelCircumference = 25.13274122872; // 8 inches * pi

    AxisCamera cam;
    
    AutonomousFunctions aFunctions;

    double clock = 0;

    Joystick driveStick, armStick;
    Encoder samDriveEncoder1, samDriveEncoder2, ericArmEncoder;
    BestRobotDrive brDrive;
    RobotMenu robotMenu;
    RobotArm natArm;
    Jaguar corryJag;
    Victor claw;
    double armSpeed, now;
    boolean nowStart, run1, run2, run3;
    DigitalInput clawSwitchOpen, armLimitSwitch;

    Servo minibotArm, shuttle;
    
    RobotArm corryArm;

    //Jaguar arm;

    // baking pi
    int sss;
    double pi, piX, piY; // i renamed x and y to piX and piY because they're global variables now and we might need to use "x" and "y" somewhere else

    Gyro jacobGyro;
    Gyro airholeGyro;

    String direction, selected, clawSwitchValue;
    
    public void robotInit()
    {
        brDrive = new BestRobotDrive(1, 2, 3, 4);
        driveStick = new Joystick(1);
        armStick = new Joystick(2);
 //       samDriveEncoder1= new Encoder(1, 2);
 //       ericArmEncoder= new Encoder(3, 4);
        claw = new Victor(5);
 //       corryJag = new Jaguar(6);
 //       brDrive.setEncoder(samDriveEncoder1);
  //      corryArm = new RobotArm(ericArmEncoder, corryJag);
   //     jacobGyro=new Gyro(1);
    //    airholeGyro = new Gyro(2);
        cam = AxisCamera.getInstance();
        cam.writeResolution(AxisCamera.ResolutionT.k320x240);
        cam.writeCompression(70);
        cam.writeBrightness(0);

  //      minibotArm = new Servo(8);
  //      shuttle = new Servo(7);

        now=0;
        nowStart=false;
        run1=true;
        run2=true;
        run3=true;

        /// baking pi
        sss = 0;
        pi = 1;
        piX=-3;
        piY=-1;


  //      clawSwitchOpen = new DigitalInput(9);
  //      armLimitSwitch = new DigitalInput(10);
        clawSwitchValue = "not set";

  //      armSpeed=0;

        SmartDashboard.init();
    }

/// might be important: http://www.chiefdelphi.com/forums/showthread.php?t=93816
/////////
    public void autonomousInit()
    {
        /*
        samDriveEncoder1.start();
        samDriveEncoder1.reset();
        samDriveEncoder1.setReverseDirection(true);
        DriverStationLCD.getInstance().updateLCD();
        brDrive.setEncoder(samDriveEncoder1);
        brDrive.setGyro(jacobGyro);
        corryArm.Encoder().setReverseDirection(false);
        SmartDashboard.init();

        shuttle.set(0.3);
        minibotArm.set(0.1);
         *
         */
    }
    public void autonomousPeriodic() //BIG BOY CODE  AW YEAH.
    {
        /*
        DriverStationLCD.getInstance().updateLCD();
        SmartDashboard.log(brDrive.getGyro().getAngle(), "Angle: ");
        SmartDashboard.log(ericArmEncoder.getRaw(), "Arm encoder: ");
        DriverStationLCD.getInstance().updateLCD();
         *
         */
    }
    public void teleopInit(){
 /*       samDriveEncoder1.start();
        samDriveEncoder1.reset();
        samDriveEncoder1.setReverseDirection(true);
        brDrive.setEncoder(samDriveEncoder1);
        jacobGyro.reset();
        ericArmEncoder.start();
        ericArmEncoder.reset();
        //ericArmEncoder.start();*/

//        shuttle.set(0.3);
//        minibotArm.set(0.1);
        SmartDashboard.init();
    }

    public void teleopPeriodic()
    {
        ////////////// DRIVE MODES ///////////////////////////// EVERYTHING IS NOW IN BestRobotDrive
        if(driveStick.getRawButton(6)||driveStick.getRawButton(7))
            brDrive.setPrecisionDriving(.6);
        else
            brDrive.setPrecisionDriving(1);

        if(driveStick.getRawButton(3) && !driveStick.getRawButton(4) && !driveStick.getRawButton(5))   //when button 3 is pressed and not 4 and 5 which are used for crab walk
            brDrive.mecanumDrive(driveStick);
        else if(!driveStick.getRawButton(4) && !driveStick.getRawButton(5))                       //if no buttons are pressed
            brDrive.arcadeDrive(driveStick);

        else if(driveStick.getRawButton(4))                                                  //if button 4 is pressed
            brDrive.crabLeft(.75);
        else if(driveStick.getRawButton(5))                                                  //if button 5 is pressed
            brDrive.crabRight(.75);
        
//        if(driveStick.getRawButton(8)){
//            aFunctions.enable();
//            aFunctions.balance(200, 1.2);
//        }
//        else if(!driveStick.getRawButton(8))
//            aFunctions.disable();
        
        //////////////CAMERA OPERATIONS////////////////        
        DriverStationLCD.getInstance().updateLCD();

/*        SmartDashboard.log(jacobGyro.getAngle(), "Gyro");
        SmartDashboard.log(airholeGyro.getAngle(), "Gyro 2:");
*/
        //// output light sensors///////

//        boolean x1 = lineTrack.getLeftSensor().get();
//        SmartDashboard.log(x1, "left sensor");
//        boolean x2 = lineTrack.getMiddleSensor().get();
//        SmartDashboard.log(x2, "middle sensor");
//        boolean x3 = lineTrack.getRightSensor().get();
//        SmartDashboard.log(x3, "right sensor");


        //////////////// ARM CONTROL////////////////////
/*
        corryArm.manualControl(armStick);
        DriverStationLCD.getInstance().println(DriverStationLCD.Line.kUser5, 1, "Arm Encoder: "+Double.toString(ericArmEncoder.getRaw()));

        ///////////////// CLAW CONTROL /////////////////
        if(armStick.getRawButton(8)&&!clawSwitchOpen.get()) // open and limit switch is not hit
            claw.setSpeed(0.47);
        else if(armStick.getRawButton(9)) // close
            claw.setSpeed(-0.45);
        else
            claw.setSpeed(0.0);

        if(clawSwitchOpen.get())
            clawSwitchValue = "Switch is True";
        else
            clawSwitchValue = "Switch is False";

        DriverStationLCD.getInstance().println(DriverStationLCD.Line.kUser6, 1, clawSwitchValue);

*/
        ///////////// MINIBOT STUFF ////////////////////
/*
        /// output servo positions
        SmartDashboard.log(minibotArm.get(), "minibot arm servo value");
        SmartDashboard.log(shuttle.get(), "shuttle servo value");

        if(armStick.getRawButton(11))
        {
            minibotArm.set(1.0);
        }
        if(armStick.getRawButton(10))
        {
            shuttle.set(1.0);
        }
        

            /////// MANUAL MINIBOT ///////////
        if(driveStick.getRawButton(6))
            minibotArm.set(minibotArm.get()+0.02);
        if(driveStick.getRawButton(7))
            minibotArm.set(minibotArm.get()-0.02);
        if(driveStick.getRawButton(11))
            shuttle.set(shuttle.get()+0.02);
        if(driveStick.getRawButton(10))
            shuttle.set(shuttle.get()-0.02);*/
    }
    
    /////////////////////// OTHER /////////////////////////
    public double calculatePi(int accuracy){
    //// i moved the variables to the top of RobotMain
        if(sss<accuracy){ ////// i turned this "for" loop into an "if" so that it can go inside of disabledPeriodic() instead of disabledInit()
            pi+=(1/piX);      ////// this way, you can stop the pi-baking process right in the middle (Enabling the robot) without it crashing the robot
            piX +=(2*piY);
            piX =-piX;
            piY =-piY;
            sss++;
        }
        return pi*4;
    }

    public void disabledInit()
    {
		/// if these aren't commented then the pi will get thrown out every time the robot stops doing stuff
 ///       sss = 0;
 //       pi = 1;
 //       piX=-3;
 //       piY=-1;
    }

    public void disabledPeriodic()
    {        
        // this is where we can bake the pi
        DriverStationLCD.getInstance().println(DriverStationLCD.Line.kMain6, 1, "Pi = "+Double.toString(calculatePi(10000)));

        ///// minibot arm must be set to 0.1 to be locked
        
    }

    //// copied this from a sample project (Dashboard Example)
    //Yo Peter, what is this? And do we need it?

    // this is from the DashboardExample project that f.i.r.s.t. gave us to make the Dashboard update the PWM and the analog voltage graphs and all those digitalinput lights
    // the Dashboard doesn't update by itself--it needs code
    // i guess we don't really need it but it could be useful if we want to see if something is working (like the gyro or a limit switch--they all have their own green lights)
    // I don't really think it slows down the program


    /////// NOT USED ANYMORE
/*
    void updateDashboard() {
        Dashboard lowDashData = DriverStation.getInstance().getDashboardPackerLow();
        lowDashData.addCluster();
        {
            lowDashData.addCluster();
            {     //analog modules
                lowDashData.addCluster();
                {
                    for (int i = 1; i <= 8; i++) {
                        lowDashData.addFloat((float) AnalogModule.getInstance(1).getAverageVoltage(i));
                    }
                }
                lowDashData.finalizeCluster();
                lowDashData.addCluster();
                {
                    for (int i = 1; i <= 8; i++) {
                        lowDashData.addFloat((float) AnalogModule.getInstance(2).getAverageVoltage(i));
                    }
                }
                lowDashData.finalizeCluster();
            }
            lowDashData.finalizeCluster();

            lowDashData.addCluster();
            { //digital modules
                lowDashData.addCluster();
                {
                    lowDashData.addCluster();
                    {
                        int module = 4;
                        lowDashData.addByte(DigitalModule.getInstance(module).getRelayForward());
                        lowDashData.addByte(DigitalModule.getInstance(module).getRelayForward());
                        lowDashData.addShort(DigitalModule.getInstance(module).getAllDIO());
                        lowDashData.addShort(DigitalModule.getInstance(module).getDIODirection());
                        lowDashData.addCluster();
                        {
                            for (int i = 1; i <= 10; i++) {
                                lowDashData.addByte((byte) DigitalModule.getInstance(module).getPWM(i));
                            }
                        }
                        lowDashData.finalizeCluster();
                    }
                    lowDashData.finalizeCluster();
                }
                lowDashData.finalizeCluster();

                lowDashData.addCluster();
                {
                    lowDashData.addCluster();
                    {
                        int module = 6;
                        lowDashData.addByte(DigitalModule.getInstance(module).getRelayForward());
                        lowDashData.addByte(DigitalModule.getInstance(module).getRelayReverse());
                        lowDashData.addShort(DigitalModule.getInstance(module).getAllDIO());
                        lowDashData.addShort(DigitalModule.getInstance(module).getDIODirection());
                        lowDashData.addCluster();
                        {
                            for (int i = 1; i <= 10; i++) {
                                lowDashData.addByte((byte) DigitalModule.getInstance(module).getPWM(i));
                            }
                        }
                        lowDashData.finalizeCluster();
                    }
                    lowDashData.finalizeCluster();
                }
                lowDashData.finalizeCluster();

            }
            lowDashData.finalizeCluster();

            lowDashData.addByte(Solenoid.getAllFromDefaultModule());
        }
        lowDashData.finalizeCluster();
        lowDashData.commit();

    }
*/
}

//Top Line:     Encoder Value
//Line 2:
//Line 3:       
//Line 4:       ArmSpeed
//Line 5:       
//Line 6:       Switch Value

/*
 * ************BUTTON LAYOUT****************
 * 
 * Button|| Function
 * 2     || 
 * 3     || Drive in mecanum mode
 * 4     || Quick crab left
 * 5     || Quick crab right
 * 6     ||
 * 7     ||
 * 8     || 
 * 9     || 
 * 10    || 
 * 11    ||
 */





//OLD AUTONOMOUS CODE FOR AUTONOMOUS PERIODIC
//
//        corryArm.goToEncPos(730, .55); //arm unfolds
//
//        if(!clawSwitchOpen.get() && run1) //claw opens all the way
//            claw.setSpeed(.3);
//        else if(run1){
//            claw.setSpeed(0);
//            run1=false;
//        }
//
//        if(corryArm.Encoder().getRaw()>730 && run1==false){ //if arm is unfolded, start closing claw
//            claw.setSpeed(-.47);
//            if(run2){
//                now=Timer.getFPGATimestamp();
//                run2=false;
//            }
//            nowStart=true;
//        }
//        if(nowStart) //if the timer has been started
//            if((Timer.getFPGATimestamp()-now)>.75 && run3){ //if .75 seconds have passed, stop claw
//                claw.setSpeed(0);
//                run3=false;
//            }
//        if(corryArm.Encoder().getRaw()>720 && run3==false) //if arm is unfolded, the timer has finished and the claw is closed
//            brDrive.driveDistance(20*12, .8); //driveforward
//

