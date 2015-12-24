/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj;

import com.sun.squawk.util.MathUtils;
import edu.wpi.first.wpilibj.*;


/**
 * Utility class for handling Robot drive based on a definition of the motor configuration.
 * The robot drive class handles basic driving for a robot. Currently, 2 and 4 motor standard
 * drive trains are supported. In the future other drive types like swerve and meccanum might
 * be implemented. Motor channel numbers are passed supplied on creation of the class. Those are
 * used for either the drive function (intended for hand created drive code, such as autonomous)
 * or with the Tank/Arcade functions intended to be used for Operator Control driving.
 */
public class BetterRobotDrive {
    public final double kWheelCircumference = 25.13274122872;

    /**
     * The location of a motor on the robot for the purpose of driving
     */
    public static class MotorType {

        /**
         * The integer value representing this enumeration
         */
        public final int value;
        static final int kFrontLeft_val = 0;
        static final int kFrontRight_val = 1;
        static final int kRearLeft_val = 2;
        static final int kRearRight_val = 3;
        /**
         * motortype: front left
         */
        public static final MotorType kFrontLeft = new MotorType(kFrontLeft_val);
        /**
         * motortype: front right
         */
        public static final MotorType kFrontRight = new MotorType(kFrontRight_val);
        /**
         * motortype: rear left
         */
        public static final MotorType kRearLeft = new MotorType(kRearLeft_val);
        /**
         * motortype: rear right
         */
        public static final MotorType kRearRight = new MotorType(kRearRight_val);

        private MotorType(int value) {
            this.value = value;
        }
    }
    /**
     * default sensitivity to use when not specified
     */
    public static final double kDefaultSensitivity = 0.5;
    private static final int kMaxNumberOfMotors = 4;
    private final int m_invertedMotors[] = new int[4];
    private double m_sensitivity;
    private SpeedController m_frontLeftMotor;
    private SpeedController m_frontRightMotor;
    private SpeedController m_rearLeftMotor;
    private SpeedController m_rearRightMotor;
    private boolean m_allocatedSpeedControllers;

    /** Constructor for RobotDrive with 2 motors specified with channel numbers.
     * Set up parameters for a two wheel drive system where the
     * left and right motor pwm channels are specified in the call.
     * This call assumes Jaguars for controlling the motors.
     * @param leftMotorChannel The PWM channel number on the default digital module that drives the left motor.
     * @param rightMotorChannel The PWM channel number on the default digital module that drives the right motor.
     * @param sensitivity Effectively sets the turning sensitivity (or turn radius for a given value).
     */
    public BetterRobotDrive(final int leftMotorChannel, final int rightMotorChannel, double sensitivity) {
        m_sensitivity = sensitivity;
        m_frontLeftMotor = null;
        m_rearLeftMotor = new Jaguar(leftMotorChannel);
        m_frontRightMotor = null;
        m_rearRightMotor = new Jaguar(rightMotorChannel);
        for (int i = 0; i < kMaxNumberOfMotors; i++) {
            m_invertedMotors[i] = 1;
        }
        drive(0, 0);
        m_allocatedSpeedControllers = true;
    }

    /**
     * RobotDrive constructor that uses a default sensitivity
     * @param leftMotorChannel Left motor channel
     * @param rightMotorChannel Right motor channel
     */
    public BetterRobotDrive(final int leftMotorChannel, final int rightMotorChannel) {
        this(leftMotorChannel, rightMotorChannel, kDefaultSensitivity);
    }

    /**
     * Constructor for RobotDrive with 4 motors specified with channel numbers.
     * Set up parameters for a four wheel drive system where all four motor
     * pwm channels are specified in the call.
     * This call assumes Jaguars for controlling the motors.
     * @param frontLeftMotor Front left motor channel number on the default digital module
     * @param rearLeftMotor Rear Left motor channel number on the default digital module
     * @param frontRightMotor Front right motor channel number on the default digital module
     * @param rearRightMotor Rear Right motor channel number on the default digital module
     * @param sensitivity Effectively sets the turning sensitivity (or turn radius for a given value)
     */
    public BetterRobotDrive(final int frontLeftMotor, final int rearLeftMotor, final int frontRightMotor, final int rearRightMotor, double sensitivity) {
        m_sensitivity = sensitivity;
        m_rearLeftMotor = new Jaguar(rearLeftMotor);
        m_rearRightMotor = new Jaguar(rearRightMotor);
        m_frontLeftMotor = new Jaguar(frontLeftMotor);
        m_frontRightMotor = new Jaguar(frontRightMotor);
        for (int i = 0; i < kMaxNumberOfMotors; i++) {
            m_invertedMotors[i] = 1;
        }
        drive(0, 0);
        m_allocatedSpeedControllers = true;
    }

    /**
     * Constructor for RobotDrive with a default sensitivity
     * @param frontLeftMotor Front left motor port
     * @param rearLeftMotor Rear left motor port
     * @param frontRightMotor Front right motor port
     * @param rearRightMotor Rear right motor port
     */
    public BetterRobotDrive(final int frontLeftMotor, final int rearLeftMotor, final int frontRightMotor, final int rearRightMotor) {
        this(frontLeftMotor, rearLeftMotor, frontRightMotor, rearRightMotor, kDefaultSensitivity);
    }

    /**
     * Constructor for RobotDrive with 2 motors specified as SpeedController objects.
     * The SpeedController version of the constructor enables programs to use the RobotDrive classes with
     * subclasses of the SpeedController objects, for example, versions with ramping or reshaping of
     * the curve to suit motor bias or deadband elimination.
     * @param leftMotor The left SpeedController object used to drive the robot.
     * @param rightMotor the right SpeedController object used to drive the robot.
     * @param sensitivity Effectively sets the turning sensitivity (or turn radius for a given value)
     */
    public BetterRobotDrive(SpeedController leftMotor, SpeedController rightMotor, double sensitivity) {
        if (leftMotor == null || rightMotor == null) {
            m_rearLeftMotor = m_rearRightMotor = null;
            throw new NullPointerException("Null motor provided");
        }
        m_frontLeftMotor = null;
        m_rearLeftMotor = leftMotor;
        m_frontRightMotor = null;
        m_rearRightMotor = rightMotor;
        m_sensitivity = sensitivity;
        for (int i = 0; i < kMaxNumberOfMotors; i++) {
            m_invertedMotors[i] = 1;
        }
        m_allocatedSpeedControllers = false;
    }

    /**
     * RobotDrive constructor with SpeedControllers as parameters. Use this method if the
     * speed controllers are not plugged into the default digital sidecar
     * @param leftMotor Left motor port
     * @param rightMotor Right motor port
     */
    public BetterRobotDrive(SpeedController leftMotor, SpeedController rightMotor) {
        this(leftMotor, rightMotor, kDefaultSensitivity);
    }

    /**
     * Constructor for RobotDrive with 4 motors specified as SpeedController objects.
     * Speed controller input version of RobotDrive (see previous comments).
     * @param rearLeftMotor The back left SpeedController object used to drive the robot.
     * @param frontLeftMotor The front left SpeedController object used to drive the robot
     * @param rearRightMotor The back right SpeedController object used to drive the robot.
     * @param frontRightMotor The front right SpeedController object used to drive the robot.
     * @param sensitivity Effectively sets the turning sensitivity (or turn radius for a given value)
     */
    public BetterRobotDrive(SpeedController frontLeftMotor, SpeedController rearLeftMotor, SpeedController frontRightMotor, SpeedController rearRightMotor, double sensitivity) {
        if (frontLeftMotor == null || rearLeftMotor == null || frontRightMotor == null || rearRightMotor == null) {
            m_frontLeftMotor = m_rearLeftMotor = m_frontRightMotor = m_rearRightMotor = null;
            throw new NullPointerException("Null motor provided");
        }
        m_frontLeftMotor = frontLeftMotor;
        m_rearLeftMotor = rearLeftMotor;
        m_frontRightMotor = frontRightMotor;
        m_rearRightMotor = rearRightMotor;
        m_sensitivity = sensitivity;
        for (int i = 0; i < kMaxNumberOfMotors; i++) {
            m_invertedMotors[i] = 1;
        }
        m_allocatedSpeedControllers = false;
    }

    /**
     * Constructor for RobotDrive with 4 motors specified as SpeedController objects.
     * Speed controller input version of RobotDrive (see previous comments).
     * @param rearLeftMotor The back left SpeedController object used to drive the robot.
     * @param frontLeftMotor The front left SpeedController object used to drive the robot
     * @param rearRightMotor The back right SpeedController object used to drive the robot.
     * @param frontRightMotor The front right SpeedController object used to drive the robot.
     */
    public BetterRobotDrive(SpeedController frontLeftMotor, SpeedController rearLeftMotor, SpeedController frontRightMotor, SpeedController rearRightMotor) {
        this(frontLeftMotor, rearLeftMotor, frontRightMotor, rearRightMotor, kDefaultSensitivity);
    }

    /**
     * drive the motors at "speed" and "curve".
     *
     * The speed and curve are -1.0 to +1.0 values where 0.0 represents stopped and
     * not turning. The algorithm for adding in the direction attempts to provide a constant
     * turn radius for differing speeds.
     *
     * This function sill most likely be used in an autonomous routine.
     *
     * @param speed The forward component of the speed to send to the motors.
     * @param curve The rate of turn, constant for different forward speeds.
     */
    public void drive(double speed, double curve) {
        double leftSpeed, rightSpeed;

        if (curve < 0) {
            double value = MathUtils.log(-curve);
            double ratio = (value - m_sensitivity) / (value + m_sensitivity);
            if (ratio == 0) {
                ratio = .0000000001;
            }
            leftSpeed = speed / ratio;
            rightSpeed = speed;
        } else if (curve > 0) {
            double value = MathUtils.log(curve);
            double ratio = (value - m_sensitivity) / (value + m_sensitivity);
            if (ratio == 0) {
                ratio = .0000000001;
            }
            leftSpeed = speed;
            rightSpeed = speed / ratio;
        } else {
            leftSpeed = speed;
            rightSpeed = speed;
        }
        setLeftRightMotorSpeeds(leftSpeed, rightSpeed);
    }

    /**
     * Provide tank steering using the stored robot configuration.
     * drive the robot using two joystick inputs. The Y-axis will be selected from
     * each Joystick object.
     * @param leftStick The joystick to control the left side of the robot.
     * @param rightStick The joystick to control the right side of the robot.
     */
    public void tankDrive(GenericHID leftStick, GenericHID rightStick) {
        if (leftStick == null || rightStick == null) {
            throw new NullPointerException("Null HID provided");
        }
        tankDrive(leftStick.getY(), rightStick.getY());
    }

    /**
     * Provide tank steering using the stored robot configuration.
     * This function lets you pick the axis to be used on each Joystick object for the left
     * and right sides of the robot.
     * @param leftStick The Joystick object to use for the left side of the robot.
     * @param leftAxis The axis to select on the left side Joystick object.
     * @param rightStick The Joystick object to use for the right side of the robot.
     * @param rightAxis The axis to select on the right side Joystick object.
     */
    public void tankDrive(GenericHID leftStick, final int leftAxis, GenericHID rightStick, final int rightAxis) {
        if (leftStick == null || rightStick == null) {
            throw new NullPointerException("Null HID provided");
        }
        tankDrive(leftStick.getRawAxis(leftAxis), rightStick.getRawAxis(rightAxis));
    }

    /**
     * Provide tank steering using the stored robot configuration.
     * This function lets you directly provide joystick values from any source.
     * @param leftValue The value of the left stick.
     * @param rightValue The value of the right stick.
     */
    public void tankDrive(double leftValue, double rightValue) {
        // square the inputs (while preserving the sign) to increase fine control while permitting full power
        leftValue = limit(leftValue);
        rightValue = limit(rightValue);
        if (leftValue >= 0.0) {
            leftValue = (leftValue * leftValue);
        } else {
            leftValue = -(leftValue * leftValue);
        }
        if (rightValue >= 0.0) {
            rightValue = (rightValue * rightValue);
        } else {
            rightValue = -(rightValue * rightValue);
        }

        setLeftRightMotorSpeeds(leftValue, rightValue);
    }

    /**
     * Arcade drive implements single stick driving.
     * Given a single Joystick, the class assumes the Y axis for the move value and the X axis
     * for the rotate value.
     * (Should add more information here regarding the way that arcade drive works.)
     * @param stick The joystick to use for Arcade single-stick driving. The Y-axis will be selected
     * for forwards/backwards and the X-axis will be selected for rotation rate.
     * @param squaredInputs If true, the sensitivity will be increased for small values
     */
    public void arcadeDrive(GenericHID stick, boolean squaredInputs) {
        // simply call the full-featured arcadeDrive with the appropriate values
        arcadeDrive(stick.getX(), stick.getY(), squaredInputs);
    }

    /**
     * Arcade drive implements single stick driving.
     * Given a single Joystick, the class assumes the Y axis for the move value and the X axis
     * for the rotate value.
     * (Should add more information here regarding the way that arcade drive works.)
     * @param stick The joystick to use for Arcade single-stick driving. The Y-axis will be selected
     * for forwards/backwards and the X-axis will be selected for rotation rate.
     */
    public void arcadeDrive(GenericHID stick) {
        this.arcadeDrive(stick,true);
    }

    /**
     * Arcade drive implements single stick driving.
     * Given two joystick instances and two axis, compute the values to send to either two
     * or four motors.
     * @param moveStick The Joystick object that represents the forward/backward direction
     * @param moveAxis The axis on the moveStick object to use for fowards/backwards (typically Y_AXIS)
     * @param rotateStick The Joystick object that represents the rotation value
     * @param rotateAxis The axis on the rotation object to use for the rotate right/left (typically X_AXIS)
     * @param squaredInputs Setting this parameter to true increases the sensitivity at lower speeds
     */
    public void arcadeDrive(GenericHID moveStick, final int moveAxis, GenericHID rotateStick, final int rotateAxis, boolean squaredInputs) {
        double moveValue = moveStick.getRawAxis(moveAxis);
        double rotateValue = rotateStick.getRawAxis(rotateAxis);

        arcadeDrive(moveValue, rotateValue, squaredInputs);
    }

    /**
     * Arcade drive implements single stick driving.
     * Given two joystick instances and two axis, compute the values to send to either two
     * or four motors.
     * @param moveStick The Joystick object that represents the forward/backward direction
     * @param moveAxis The axis on the moveStick object to use for fowards/backwards (typically Y_AXIS)
     * @param rotateStick The Joystick object that represents the rotation value
     * @param rotateAxis The axis on the rotation object to use for the rotate right/left (typically X_AXIS)
     */
    public void arcadeDrive(GenericHID moveStick, final int moveAxis, GenericHID rotateStick, final int rotateAxis) {
        this.arcadeDrive(moveStick,moveAxis,rotateStick,rotateAxis,true);
    }

    /**
     * Arcade drive implements single stick driving.
     * This function lets you directly provide joystick values from any source.
     * @param moveValue The value to use for fowards/backwards
     * @param rotateValue The value to use for the rotate right/left
     * @param squaredInputs If set, increases the sensitivity at low speeds
     */
    public void arcadeDrive(double moveValue, double rotateValue, boolean squaredInputs) {
        
            DriverStationLCD.getInstance().println(DriverStationLCD.Line.kMain6, 1, "Arcade Drive Engaged");
            //It may not make sense, but the X and Y were doing the opposite function they were supposed to do, so I have to switch them.
            rotateValue = limit(rotateValue);
            moveValue = limit(moveValue);

            double leftMotorSpeed;
            double rightMotorSpeed;

            if (moveValue < -1.0)
                    moveValue = -1.0;
            else if (moveValue > 1.0)
                    moveValue = 1.0;

            if (rotateValue < -1.0)
                    rotateValue = -1.0;
            else if (rotateValue > 1.0)
                    rotateValue = 1.0;

            if (squaredInputs){
                // square the inputs (while preserving the sign) to increase fine control while permitting full power
                if (moveValue >= 0.0)
                {
                    moveValue = (moveValue * moveValue);
                }
                else
                {
                moveValue = -(moveValue * moveValue);
                }
                if (rotateValue >= 0.0)
                {
                rotateValue = (rotateValue * rotateValue);
                }
                else
                {
                rotateValue = -(rotateValue * rotateValue);
                }
            }

            if (moveValue > 0.0)
            {
                if (rotateValue > 0.0)
                {
                    leftMotorSpeed = moveValue - rotateValue;
                    rightMotorSpeed = Math.max(moveValue, rotateValue);
                }
                else
                {
                    leftMotorSpeed = Math.max(moveValue, -rotateValue);
                    rightMotorSpeed = moveValue + rotateValue;
                }
            }
            else
            {
                if (rotateValue > 0.0)
                {
                    leftMotorSpeed = -Math.max(-moveValue, rotateValue);
                    rightMotorSpeed = moveValue + rotateValue;
                }
                else
                {
                    leftMotorSpeed = moveValue - rotateValue;
                    rightMotorSpeed = -Math.max(-moveValue, -rotateValue);
                }
            }
            //Posative rotate values go counterClockWise, and negative go clockwise;

            //The controlls were entirely inverted, so I put negatives in front of all parameters.
            jaguarDrive(-leftMotorSpeed, -rightMotorSpeed, -leftMotorSpeed, -rightMotorSpeed);
    }

    /**
     * Arcade drive implements single stick driving.
     * This function lets you directly provide joystick values from any source.
     * @param moveValue The value to use for fowards/backwards
     * @param rotateValue The value to use for the rotate right/left
     */
    public void arcadeDrive(double moveValue, double rotateValue) {
        this.arcadeDrive(moveValue,rotateValue,true);
    }

    /**
     * Holonomic drive class for Mecanum wheeled robots.
     *
     * Experimental class for driving with Mecanum wheeled robots. There are 4 wheels
     * on the robot, arranged so that the front and back wheels are toed in 45 degrees.
     *
     * For holonomic drive with omni-wheels, the rotation value will need to be
     * offset based on the drive configuration.
     *
     * @param magnitude The speed that the robot should drive in a given direction.
     * @param direction The direction the robot should drive in degrees. The direction and maginitute are
     * independent of the rotation rate.
     * @param rotation The rate of rotation for the robot that is completely independent of
     * the magnitute or direction.
     */
    public void holonomicDrive(double magnitude, double direction, double rotation) {
        double frontLeftSpeed, rearLeftSpeed, frontRightSpeed, rearRightSpeed;
        magnitude = limit(magnitude);
        double cosD = Math.cos((direction + 45.0) * 3.14159 / 180.0);
        double sinD = Math.cos((direction - 45.0) * 3.14159 / 180.0);
        frontLeftSpeed = limit((sinD * magnitude + rotation));
        rearLeftSpeed = limit((cosD * magnitude + rotation));
        frontRightSpeed = limit((cosD * magnitude - rotation));
        rearRightSpeed = limit((sinD * magnitude - rotation));

        m_frontLeftMotor.set(frontLeftSpeed * m_invertedMotors[MotorType.kFrontLeft_val]);
        m_frontRightMotor.set(frontRightSpeed * m_invertedMotors[MotorType.kFrontRight_val]);
        m_rearLeftMotor.set(rearLeftSpeed * m_invertedMotors[MotorType.kRearLeft_val]);
        m_rearRightMotor.set(rearRightSpeed * m_invertedMotors[MotorType.kRearRight_val]);
    }

    /** Set the speed of the right and left motors.
     * This is used once an appropriate drive setup function is called such as
     * twoWheelDrive(). The motors are set to "leftSpeed" and "rightSpeed"
     * and includes flipping the direction of one side for opposing motors.
     * @param leftSpeed The speed to send to the left side of the robot.
     * @param rightSpeed The speed to send to the right side of the robot.
     */
    public void setLeftRightMotorSpeeds(double leftSpeed, double rightSpeed) {
        if (m_rearLeftMotor == null || m_rearRightMotor == null)
            throw new NullPointerException("Null motor provided");

        leftSpeed = limit(leftSpeed);
        rightSpeed = limit(rightSpeed);

        if (m_frontLeftMotor != null) {
            m_frontLeftMotor.set(leftSpeed * m_invertedMotors[MotorType.kFrontLeft_val]);
        }
        m_rearLeftMotor.set(leftSpeed * m_invertedMotors[MotorType.kRearLeft_val]);

        if (m_frontRightMotor != null) {
            m_frontRightMotor.set(-rightSpeed * m_invertedMotors[MotorType.kFrontRight_val]);
        }
        m_rearRightMotor.set(-rightSpeed * m_invertedMotors[MotorType.kRearRight_val]);
    }

    /**
     * Limit motor values to the -1.0 to +1.0 range.
     */
    private double limit(double num) {
        if (num > 1.0) {
            return 1.0;
        }
        if (num < -1.0) {
            return -1.0;
        }
        return num;
    }

    /**
     * Invert a motor direction.
     * This is used when a motor should run in the opposite direction as the drive
     * code would normally run it. Motors that are direct drive would be inverted, the
     * drive code assumes that the motors are geared with one reversal.
     * @param motor The motor index to invert.
     * @param isInverted True if the motor should be inverted when operated.
     */
    public void setInvertedMotor(MotorType motor, boolean isInverted)
    {
        m_invertedMotors[motor.value] = isInverted ? -1 : 1;
    }

    public void mecanumDrive(Joystick j)
    {
        double jX = j.getX();
        double jY = j.getY();

        /********** our custom Mecanum drive *******/
        DriverStationLCD.getInstance().println(DriverStationLCD.Line.kMain6, 1, "Mecanum Drive Engaged");
        jX = -jX; //This is put in to reverse the X axis and fix the inverted left to right crab.

        // CHRIS WILLIAMS's MECANUM MAGIC
        jaguarDrive((jX+jY)/2, -(jY-jX)/2, (jY-jX)/2, -(jX+jY)/2);
        //          FrontLeft, FrontRight, RearLeft , RearRight
    }


    /**
     *
     * @param FL front left speed
     * @param FR front right speed
     * @param RL rear left speed
     * @param RR rear right speed
     */
    public void jaguarDrive(double FL, double FR, double RL, double RR)
    {
        m_frontLeftMotor.set(FL);
        m_frontRightMotor.set(FR);
        m_rearLeftMotor.set(RL);
        m_rearRightMotor.set(RR);
    }

    public void crabLeft(double speed)
    {
        double jX = -0.75;
        double jY = 0;
        jX = -jX;
        jaguarDrive((jX+jY)/2, -(jY-jX)/2, (jY-jX)/2, -(jX+jY)/2);
    }

    public void crabRight(double speed)
    {
        double jX = 0.75;
        double jY = 0;
        jX = -jX;
        jaguarDrive((jX+jY)/2, -(jY-jX)/2, (jY-jX)/2, -(jX+jY)/2);
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
        this.jaguarDrive(-turnSpeed, -turnSpeed, -turnSpeed, -turnSpeed);
    }

    public void driveDistance(double distance){

    }

    /**
     * Free the speed controllers if they were allocated locally
     */
    protected void free() {
        if (m_allocatedSpeedControllers) {
            if (m_frontLeftMotor != null) {
                ((PWM) m_frontLeftMotor).free();
            }
            if (m_frontRightMotor != null) {
                ((PWM) m_frontRightMotor).free();
            }
            if (m_rearLeftMotor != null) {
                ((PWM) m_rearLeftMotor).free();
            }
            if (m_rearRightMotor != null) {
                ((PWM) m_rearRightMotor).free();
            }
        }
    }
}
