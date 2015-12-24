/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2010. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package edu.wpi.first.wpilibj;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SimpleRobot;
import edu.wpi.first.wpilibj.Timer;

/**
 * @author team 2084
 */

/**
 * Sample line tracking class for FIRST 2011 Competition
 * Jumpers on driver station digital I/O pins select the operating mode:
 * The Driver Station digital input 1 select whether the code tracks the straight
 * line or the forked line. Driver station digital input 2 selects whether the
 * code takes the left or right fork. You can set these inputs using jumpers on
 * the USB I/O module or in the driver station I/O Configuration pane (if there
 * is no Digital I/O module installed.
 *
 * Since there is the fork to contend with, the code tracks the edge of the line
 * using a technique similar to that used with a single-sensor Lego robot.
 *
 * This code worked on a simple bot built from the 2011 kit chassis and weighted
 * to behave similarly to complete robot complete with scoring mechanisms. Your
 * own robot will likely be geared differently, the CG will be different, the
 * wheels may be different - so expect to tune the program for your own robot
 * configuration. The two places to do tuning are:
 *
 * defaultSteeringGain - this is the amount of turning correction applied
 * forkProfile & straightProfile - these are power profiles applied at various
 * times (one power setting / second of travel) as the robot moves towards
 * the wall.
 *
 * In addition: this program uses dead reckoning - that is it drives at various
 * power settings so it will slow down and stop at the end of the line. This is
 * highly dependent on robot weight, wheel choice, and battery voltage. It will
 * behave differently on a fully charged vs. partially charged battery.
 *
 * To overcome these limitations, you should investigate the use of feedback to
 * have power/wheel independent control of the program. Examples of feedback
 * include using wheel encoders to measure the distance traveled or some kind of
 * rangefinder to determine the distance to the wall. With these sensors installed
 * your program can know precisely how far from the wall it is, and set the speeds
 * accordingly.
 *
 */
public class LineTracker {

    BestRobotDrive drive; // robot drive base object
    DigitalInput left; // digital inputs for line tracking sensors
    DigitalInput middle;
    DigitalInput right;
    DriverStation ds; // driver station object for getting selections
    double defaultSteeringGain = 0.65; // the default value for the steering gain

    // --------- more instance variables----------------
    // --------------------------------------------
    int binaryValue; // a single binary value of the three line tracking sensors
    int previousValue = 0; // the binary value from the previous loop
    double steeringGain; // the amount of steering correction to apply

    // the power profiles for the straight and forked robot path. They are
       // different to let the robot drive more slowly as the robot approaches
       // the fork on the forked line case.
    double forkProfile[] = {0.70, 0.70, 0.55, 0.60, 0.60, 0.50, 0.40, 0.00};
    double straightProfile[] = {0.7, 0.7, 0.6, 0.6, 0.35, 0.35, 0.35, 0.0};
    double powerProfile[];   // the selected power profile

    boolean straightLine;
    double stopTime;
    boolean goLeft;
    boolean atCross;
    Timer timer;
    int oldTimeInSeconds = -1;
    
    double time;
    double speed, turn;

    public LineTracker(BestRobotDrive dr, int leftChannel, int middleChannel, int rightChannel) {
        // create the robot drive and correct for the wheel direction. Our robot
        // was geared such that the motor speeds needed to be inverted for
        // positive speeds to go forward. You may not need these.
        drive = dr;
/*      drive.setInvertedMotor(RobotDrive.MotorType.kRearLeft, true);
        drive.setInvertedMotor(RobotDrive.MotorType.kFrontLeft, true);
        drive.setInvertedMotor(RobotDrive.MotorType.kFrontRight, true);
        drive.setInvertedMotor(RobotDrive.MotorType.kRearRight, true);
*/
        // set the MotorSafety expiration timer
        drive.setExpiration(15);

        // create the digital input objects to read from the sensors
        left = new DigitalInput(leftChannel);
        middle = new DigitalInput(middleChannel);
        right = new DigitalInput(rightChannel);

        // get the driver station instance to read the digital I/O pins
        ds = DriverStation.getInstance();
    }


    //// return the sensors
    public DigitalInput getLeftSensor()
    {
        return left;
    }

    public DigitalInput getMiddleSensor()
    {
        return middle;
    }

    public DigitalInput getRightSensor()
    {
        return right;
    }

    /**
     * This function is called from RobotMain.java once before autonomous begins
     */
    public void doAutonomousInit()
    {
        // set the straightLine and left-right variables depending on chosen path
        // ------ supposedly there's a "switch" in the Driver Station program that we can click on to set this
        // ------- I was wrong: instead we have to put jumpers on the pins on the Digital Inputs on the sidecar (unless there's something on the Driver Station we can click)
        straightLine = ds.getDigitalIn(1);
        powerProfile = (straightLine) ? straightProfile : forkProfile;
        stopTime = (straightLine) ? 2.0 : 4.0; // when the robot should look for end
        goLeft = !ds.getDigitalIn(2) && !straightLine;
        System.out.println("StraightLine: " + straightLine);
        System.out.println("GoingLeft: " + goLeft);

        atCross = false; // if robot has arrived at end

        // time the path over the line
        timer = new Timer();
        timer.start();
        timer.reset();

        oldTimeInSeconds = -1;
    }

    /**
     * This function is called from RobotMain.java every time the IterativeRobot loops
     */
    public void doAutonomousPeriodic()
    {
        time = timer.get() * 1000000; // get() returns the timer in microseconds (1/1000 of a millisecond), and there are 1,000,000 microseconds in a second

        // loop until robot reaches "T" at end or 8 seconds has past
        if ((timer.get()) < 8.0 && !atCross) {
            int timeInSeconds = (int) time;
            // read the sensors
            int leftValue = left.get() ? 1 : 0;
            int middleValue = middle.get() ? 1 : 0;
            int rightValue = right.get() ? 1 : 0;
            // compute the single value from the 3 sensors. Notice that the bits
            // for the outside sensors are flipped depending on left or right
            // fork. Also the sign of the steering direction is different for left/right.
            if (goLeft) {
                binaryValue = leftValue * 4 + middleValue * 2 + rightValue;
                steeringGain = -defaultSteeringGain;
            } else {
                binaryValue = rightValue * 4 + middleValue * 2 + leftValue;
                steeringGain = defaultSteeringGain;
            }

            // get the default speed and turn rate at this time
            speed = powerProfile[timeInSeconds];
            turn = 0;

            // different cases for different line tracking sensor readings
            switch (binaryValue) {
                case 1:  // on line edge
                    turn = 0;
                    break;
                case 7:  // all sensors on (maybe at cross)
                    if (time > stopTime) {
                        atCross = true;
                        speed = 0;
                    }
                    break;
                case 0:  // all sensors off
                    if (previousValue == 0 || previousValue == 1) {
                        turn = steeringGain;
                    } else {
                        turn = -steeringGain;
                    }
                    break;
                default:  // all other cases
                    turn = -steeringGain;
            }
            // print current status for debugging
            if (binaryValue != previousValue) {
                System.out.println("Time: " + time + " Sensor: " + binaryValue + " speed: " + speed + " turn: " + turn + " atCross: " + atCross);
            }

            // set the robot speed and direction
            drive.arcadeDrive(speed, turn);

            if (binaryValue != 0) {
                previousValue = binaryValue;
            }
            oldTimeInSeconds = timeInSeconds;

            Timer.delay(0.01);
        }
        else
        {
            // Done with loop - stop the robot. Robot ought to be at the end of the line
            drive.arcadeDrive(0, 0);
        }
    }

    /**
     * This function is called once each time the robot enters operator control.
     */
    public void operatorControl() {
        // supply your own teleop code here
    }
}
