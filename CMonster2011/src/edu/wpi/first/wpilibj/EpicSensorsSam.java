package edu.wpi.first.wpilibj;

import com.sun.squawk.util.MathUtils;

/*
 *  
 *  ____________________________________ 
 * |O                                  O|
 * |                                    |    ||
 * |                                    |    ||
 * |                                    |    ||
 * |                                    |    ||
 * |                                    |    ||
 * |                                    |    || Y-Axis of accelerometer
 * |                                    |    || (Up = positive)
 * |                                    |    ||
 * |                                    |    ||
 * |                                    |    ||
 * |O__________________________________O|
 * 
 *       ------------------------->         ___
 *        X-axis, right = positive         /_|_\ Z-axis (towards you = positive)
 *                                         \_|_/
 
 */                                             

public class EpicSensorsSam{
    /*
     * The only issue left is turning acceleration into time
     */
    
    //<editor-fold defaultstate="collapsed" desc="Longs, Ints, Sensors, and more">
    public static double defaultX = 0;  //used for stuff with X acceleration
    public static double defaultY = 0;  //used for stuff with Y acceleration
    public static double defaultZ = -1; //used for stuff with Z acceleration
    
    public static final double g = 32.1740486; //conversion from g forces to feet per second per second
    
    static final long distanceBetweenSideUltrasonics  = 0;  //Measure in inches on robot
    static final long distanceBetweenFrontUltrasonics = 0; 
    /*
     * Ultrasonics MUST be equidistant from their respective axis 
     * (e.g. each sensor is the same distance from the center of
     * the side of the robot they're on)
     */
    static final boolean USE_SIDE_FOR_STARTANGLE = true; //True if we're going to be using the side ultrasonics for calulating the starting angle, else false
    static final double detectionRange = 24; //Inches
    static final double angleDeviation = 15; //Angle deviation
        //0 <= angleDeviation <= ~30 (for more accuracy, keep it between ~10 and ~20)
        //If 0, then only detects if perfectly parallel
        //But values over ~30 result in wacky signals because of rebounding pings going everywhere
    //<editor-fold defaultstate="collapsed" desc="Obstacle booleans">
    public boolean obstacleToFront = false;
        public boolean obstacleToFrontIsWall = false;
        
    public boolean obstacleToLeft  = false;
        public boolean obstacleToLeftIsWall = false;
        
    public boolean obstacleToRight = false;
        public boolean obstacleToRightIsWall = false;
        
    public boolean obstacleToBack  = false;
        public boolean obstacleToBackIsWall = false;
        //</editor-fold>
    
    
    public Gyro gyro;
    /* ~~NOTE~~
     * Due to inaccuracy of the gyro (about ±0.7 degrees),
     * Peter and I have decided to use ints for all degree
     * measurements. There will be some loss of accuracy,
     * but at least there will not be as many times when
     * the robot thinks it's turning when it actually isn't.
     * Therefore, ints are actually more accurate than doubles in this case.
     * If we can get a more accurate gyro (accurate to within ±0.1 degrees or so),
     * then we shouldn't use ints.
     * --Sam
     */
    public ADXL345_I2C accelerometer;
    public Ultrasonic[] front, left, right, back;
    
    public static final int field_length = 52;
    public static final int field_width = 26;
    public int    startingAngle;     //degrees
    public long    currentTime,       //milliseconds
                   lastTime;          //milliseconds
    public double  distanceX,         //feet
                   distanceY,         //feet
                   startX,            //feet (This should NEVER be changed after the calculation)
                   startY,            //feet (This should NEVER be changed after the calculation)
                   currentX,          //feet
                   currentY;          //feet
    
    //<editor-fold defaultstate="collapsed" desc="Ultrasonic channels (temporary)">     
    //These values are only temporary placeholders until we hook up the Ultrasonic sensors and know the channels to go in
    
    final int pingChannelLeft1 = 1; 
    final int echoChannelLeft1 = 1;   //Channels for
    final int pingChannelLeft2 = 2;   //left sensors
    final int echoChannelLeft2 = 2;
    
    final int pingChannelFront1 = 3;
    final int echoChannelFront1 = 3;  //Channels for
    final int pingChannelFront2 = 4;  //front sensors
    final int echoChannelFront2 = 4;
    
    final int pingChannelRight1 = 5; 
    final int echoChannelRight1 = 5;  //Channels for
    final int pingChannelRight2 = 6;  //right sensors
    final int echoChannelRight2 = 6;
    
    final int pingChannelBack1 = 7; 
    final int echoChannelBack1 = 7;   //Channels for
    final int pingChannelBack2 = 8;   //back sensors
    final int echoChannelBack2 = 8;
    //</editor-fold>
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Time and Gyro readings">
    public long currentTimeMillis(){
        return System.currentTimeMillis();
    
    }
    public int angle(){  //int because double is very inaccurate (Again, gyro is off by ±0.7 degrees or more)
        int a = MathUtils.round((float)(gyro.getAngle() + startingAngle) % 360); //Makes 0 <= angle < 360.
        return a;
    }
    
    public int rawAngle(){ //returns between -360 and 359
        int a = 0;
        if(getRawAngle() < -360) 
            a = (MathUtils.round((float)getRawAngle()) % 360) - 360;
        if(getRawAngle() >= 360)
            a = MathUtils.round((float)getRawAngle()) % 360;
        if(getRawAngle() < 360 && getRawAngle() >= 0)
            a = MathUtils.round((float)getRawAngle());
        return a;
    }
    //</editor-fold>
    
    public EpicSensorsSam(int AccelSlotNum, ADXL345_I2C.DataFormat_Range gRange, int gyroChannel){
        accelerometer = new ADXL345_I2C(AccelSlotNum, gRange);
        gyro          = new Gyro(gyroChannel);
        initUltrasonics();
    }
    
    private void initUltrasonics(){            
        left [1] = new Ultrasonic(pingChannelLeft1  ,  echoChannelLeft1);
        left [2] = new Ultrasonic(pingChannelLeft2  ,  echoChannelLeft2);
        front[1] = new Ultrasonic(pingChannelFront1 , echoChannelFront1);
        front[2] = new Ultrasonic(pingChannelFront2 , echoChannelFront2);
        right[1] = new Ultrasonic(pingChannelRight1 , echoChannelRight1);
        right[2] = new Ultrasonic(pingChannelRight2 , echoChannelRight2);
        back [1] = new Ultrasonic(pingChannelBack1  ,  echoChannelBack1);
        back [2] = new Ultrasonic(pingChannelBack2  ,  echoChannelBack2);
    }
    
    /*
     * Call during autonomousInit(). This gets the default readings for the accelerometer for the match.
     * The results should be 0, 0, -1 under perfect conditions.
     */
    public void getAccelerationDefaults(){
        defaultX = accelerometer.getAcceleration(ADXL345_I2C.Axes.kX);
        defaultY = accelerometer.getAcceleration(ADXL345_I2C.Axes.kY);
        defaultZ = accelerometer.getAcceleration(ADXL345_I2C.Axes.kZ);
    }
    
    /*
     * This'll only work well if the robot's basically at the correct angle already.
     * We'll need to make sure that we place the robot close to perfect at the beginning.
     * Allowable error should be fine within ±10 degrees
     * Also, ONLY CALL BEFORE WE DO ANYTHING ELSE! kthx
     */
    public void calculateStartAngle(){ //returns an integer 0 through 359 inclusive
        if(USE_SIDE_FOR_STARTANGLE) 
            startingAngle = MathUtils.round((float)
                            (MathUtils.acos(Math.abs(left[1].getRangeInches() - left[2].getRangeInches()) / distanceBetweenSideUltrasonics)) % 360);
        else
            startingAngle = MathUtils.round((float)
                            (MathUtils.acos(Math.abs(front[1].getRangeInches() - front[2].getRangeInches()) / distanceBetweenFrontUltrasonics) + 90) % 360);
    }
    
    //<editor-fold desc="Location Methods">
    
    public void calculateStartX(){ //ONLY CALL WHEN robot's long edge is parallel to the long edge of the field
                                    //(so after we turn the robot properly aka startingAngle == 0)
        double dfl = left[1].getRangeInches() * 12; //Use the side that's facing the left side of the field (not necessarily going to be the robot's left)
        startX = 0 - dfl;
        currentX = startX;
    }
    
    public void calculateStartY(){ //Ditto
        double dff = front[1].getRangeInches() * 12; //Use the side that's facing us, not necessarily the front of robot
        startY = 0 - dff;
        currentY = startY;
    }
    
    /**
     * Rotation around x-axis (tilt front-back, changes in readings of y-axis)
     * This uses accelerometers rather than gyros due to the low accuracy and precision of gyros.
     * 
     * @return accelY The compensated acceleration value
     */
    public double compensateForPitch(){
        double alpha = 0;
        double accelX = accelerometer.getAcceleration(ADXL345_I2C.Axes.kX),     //g's  -- should be zero if motionless and flat
               accelY = accelerometer.getAcceleration(ADXL345_I2C.Axes.kY),     //g's  -- should be zero if motionless and flat
               accelZ = accelerometer.getAcceleration(ADXL345_I2C.Axes.kZ);     //g's  -- If there is no tilt, this value should be negative one (-1)
        if(accelZ == defaultZ && accelY == defaultY && accelX == defaultX)                    //No tilt, no motion
            return accelY;
        
        if(accelZ != defaultZ && accelY == defaultY && accelX == defaultX){     //If there's a problem with the reading on the z-axis, this should fix it
            while(accelZ < defaultZ){
                accelZ += 0.001;
                if(accelZ > defaultZ - 0.001 && accelZ < defaultZ + 0.001)
                    accelZ = defaultZ;
            }
            while(accelZ > defaultZ){
                accelZ -= 0.001;
                if(accelZ > defaultZ - 0.001 && accelZ < defaultZ + 0.001)
                    accelZ = defaultZ;
            }
            
        }
        
        if(accelZ > defaultZ && accelY != defaultY){                            //Pitched to front or back                                                
            while(accelZ > defaultZ && accelZ != 0){
                alpha = MathUtils.acos(accelY/accelZ);
                accelY *= Math.cos(alpha);
                accelZ += 0.001;
                if(accelZ > defaultZ - 0.001 && accelZ < defaultZ + 0.001){     //Close enough to being correct (no tilt)
                    accelZ = defaultZ;
                    break;
                }
            }
        }
        
        return accelY;
    }
    
    /**
     * Rotation around y-axis (tilt left-right, changes in readings on x-axis)
     * 
     * @return accelX The compensated acceleration value
     * Accurate to 1/1000 g
     */
    public double compensateForRoll(){
        double beta = 0;
        double accelX = accelerometer.getAcceleration(ADXL345_I2C.Axes.kX),     //g's
               accelY = accelerometer.getAcceleration(ADXL345_I2C.Axes.kY),     //g's
               accelZ = accelerometer.getAcceleration(ADXL345_I2C.Axes.kZ);     //g's  -- If there is no tilt, this value should be negative one (-1)
        
        if(accelZ == defaultZ && accelX == defaultX && accelY == defaultY)      //Not tilted, no motion
            return accelX;
        
        if(accelZ != defaultZ && accelX == defaultX && accelY == defaultY){     //If there's a problem with the reading on the z-axis, this should fix it
            while(accelZ < defaultZ){
                accelZ += 0.00001; 
            }
            while(accelZ > defaultZ)
                accelZ -= 0.00001;
        }
        
        if(accelZ > defaultZ && accelX != defaultX){                            //Pitched to left or right (or moving)                                                
            while(accelZ > defaultZ && accelZ != 0){                            
                beta = MathUtils.acos(accelX/accelZ);
                accelX *= Math.cos(beta);
                accelZ -= 0.001;
                if(accelZ > defaultZ - 0.001 && accelZ < defaultZ + 0.001){     //Close enough to being correct (no tilt) that it can't be compensated anyway
                    accelZ = defaultZ;
                    break;
                }
            }
        }
        
        return accelX;
    }
    
    /**
     * @return Acceleration on field's x-axis in feet/second^2
     */
    public double getAccelerationX(){ 
        double AXRobot = this.compensateForRoll()  * g;
        double AYRobot = this.compensateForPitch() * g;
        double theta   = Math.toRadians(angle());
        return AXRobot * Math.cos(theta) - AYRobot * Math.sin(theta);
    }
    
    /**
     * @return Acceleration on field's y-axis in feet/second^2
     */
    public double getAccelerationY(){ 
        double AXRobot = this.compensateForRoll()  * g;
        double AYRobot = this.compensateForPitch() * g;
        double theta   = Math.toRadians(angle());
        return AYRobot * Math.cos(theta) + AXRobot * Math.sin(theta);
    }
    
    /**
     * @return Acceleration on robot's z-axis in feet/second^2
     */
    public double getAccelerationZ(){
        return accelerometer.getAcceleration(ADXL345_I2C.Axes.kZ) * g;
    } 
    
    /**
     * @return timePassed The amount of time in seconds that the robot has been accelerating at a constant rate on it's x-axis
     * Resolution of 1 millisecond
     */
    public double accelerationTimeX(){
        double timePassedMillis = 0;
        double lastAcceleration = 0;
        double currentAcceleration = 0;
        while(true){
            lastAcceleration    = accelerometer.getAcceleration(ADXL345_I2C.Axes.kX);
            Timer.delay(0.001);
            currentAcceleration = accelerometer.getAcceleration(ADXL345_I2C.Axes.kX);
            if(lastAcceleration == currentAcceleration && lastAcceleration != 0){
                timePassedMillis ++;
            }
            else if(lastAcceleration == currentAcceleration && lastAcceleration != 0){
                timePassedMillis = 0;
            } 
            else timePassedMillis = 0;
            return timePassedMillis * 1000;
        }
    }
    
    /**
     * @return timePassed The amount of time in seconds that the robot has been accelerating at a constant rate on it's y-axis
     * Resolution of 1 millisecond
     */
    public double accelerationTimeY(){
        double timeStartMillis = currentTimeMillis();
        double timePassed = 0;
        double lastAcceleration = getAccelerationY();
        Timer.delay(0.001);
        double currentAcceleration = getAccelerationY();
        while(currentAcceleration == lastAcceleration)
            timePassed += (currentTimeMillis() - timeStartMillis)*1000;
        if(currentAcceleration != lastAcceleration){
            timePassed = 0;
            timeStartMillis = 0;
        }
        return timePassed;
    }
    
    public double xMoved(){ //Since a = d/t2, d = a * t^2
        return getAccelerationX() * MathUtils.pow(accelerationTimeX(), 2);
    }
    
    public double yMoved(){
        return getAccelerationY() * MathUtils.pow(accelerationTimeY(), 2);
    }
    
    public void calcCurrentX(){ //This should always be run
        currentX += xMoved();
    }
    
    public void calcCurrentY(){ //This should always be run
        currentY += yMoved();
    }
    
    public double getX(){
        return this.currentX;
    }
    
    public double getY(){
        return this.currentY;
    }
    //</editor-fold>
    
    public void detectObstacles(){
   /* This method detects all obstacle on each side of the robot.
    * And can also tell if they're walls or not.
    * The difference between each part (left, right, etc) is effectively nil
    * So I put them in <editor-fold> tags.
    */   
        //<editor-fold defaultstate="collapsed" desc="Left side">
        if(left[1].getRangeInches() <= detectionRange ||
           left[2].getRangeInches() <= detectionRange){
                if(currentX < -12){ //If on the left side of the field, less than 2 feet from the wall
                    if(angle() < 0+angleDeviation && rawAngle() > 0-angleDeviation){
                            obstacleToLeftIsWall = true; //The sensed obstacle is the wall and a non-avoidable obstacle
                            obstacleToLeft = false;
                    } 
                }
                
                else if(currentX > 12){ //If on the right side of the field, 2 feet or less from the wall
                    if(angle() > 180-angleDeviation && angle() < 180+angleDeviation){ //If robot's left side is more or less parallel with the right wall (±20 degrees)
                            obstacleToLeftIsWall = true; //The sensed obstacle is the wall and a non-avoidable obstacle
                            obstacleToLeft = false;
                    }
                }
                else if(currentY < -26){
                    if(angle() < 90+angleDeviation && angle() > 90-angleDeviation){
                        obstacleToLeftIsWall = true;
                        obstacleToLeft = false;
                    }
                }
                else if(currentY > 26){
                    if(angle() < 270+angleDeviation && angle() > 270-angleDeviation){
                        obstacleToLeftIsWall = true;
                        obstacleToLeft = false;
                    }
                }
                else{
                    obstacleToLeft = true;
                    obstacleToLeftIsWall = false;
                }
        }
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Right side">
            if(right[1].getRangeInches() < detectionRange ||
               right[2].getRangeInches() < detectionRange){
               if(currentX < -12){
                    if(angle() < 270+angleDeviation && angle() > 270-angleDeviation){
                            obstacleToRightIsWall = true;
                            obstacleToRight = false;
                    } 
                }
                
                else if(currentX > 12){ 
                    if(angle() < 0+angleDeviation && rawAngle() > 0-angleDeviation){
                            obstacleToRightIsWall = true;
                            obstacleToRight = false;
                    }
                }
                else if(currentY < -26){
                    if(angle() < 90+angleDeviation && angle() > 90-angleDeviation){
                        obstacleToRightIsWall = true;
                        obstacleToRight = false;
                    }
                }
                else if(currentY > 26){
                    if(angle() < 270+angleDeviation && angle() > 270-angleDeviation){
                        obstacleToRightIsWall = true;
                        obstacleToRight = false;
                    }
                }
                else{
                    obstacleToRight = true;
                    obstacleToRightIsWall = false;
                }
        }
        //</editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc="Front side">
        if(front[1].getRangeInches() < detectionRange ||
           front[2].getRangeInches() < detectionRange){
            if(currentX < -12){
                    if(angle() < 90+angleDeviation && angle() > 90-angleDeviation){
                            obstacleToFrontIsWall = true;
                            obstacleToFront = false;
                    } 
                }
                
                else if(currentX > 12){ 
                    if(angle() < 270+angleDeviation && angle() > 270-angleDeviation){
                            obstacleToFrontIsWall = true;
                            obstacleToFront = false;
                    }
                }
                else if(currentY < -26){
                    if(angle() < 180+angleDeviation && angle() > 180-angleDeviation){
                        obstacleToFrontIsWall = true;
                        obstacleToFront = false;
                    }
                }
                else if(currentY > 26){
                    if(angle() < 0+angleDeviation && rawAngle() > 0-angleDeviation){
                        obstacleToFrontIsWall = true;
                        obstacleToFront = false;
                    }
                }
                else{
                    obstacleToFront = true;
                    obstacleToFrontIsWall = false;
                }
        }

        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Back side">
        if(back[1].getRangeInches() < detectionRange ||
           back[2].getRangeInches() < detectionRange){
            if(currentX < -12){
                    if(angle() < 270+angleDeviation && angle() > 270-angleDeviation){
                            obstacleToBackIsWall = true;
                            obstacleToBack = false;
                    } 
                }
                
                else if(currentX > 12){ 
                    if(angle() < 90+angleDeviation && angle() > 90-angleDeviation){
                            obstacleToBackIsWall = true;
                            obstacleToBack = false;
                    }
                }
                else if(currentY < -26){
                    if(angle() < 0+angleDeviation && rawAngle() > 0-angleDeviation){
                        obstacleToBackIsWall = true;
                        obstacleToBack = false;
                    }
                }
                else if(currentY > 26){
                    if(angle() < 180+angleDeviation && angle() > 180-angleDeviation){
                        obstacleToBackIsWall = true;
                        obstacleToBack = false;
                    }
                }
                else{
                    obstacleToBack = true;
                    obstacleToBackIsWall = false;
                }
        }
        //</editor-fold>

    }
        
    //<editor-fold defaultstate="collapsed" desc="Miscellaneous methods">
    
    public double getAccelerationMagnitude(){
        return Math.sqrt(MathUtils.pow(getAccelerationX(), 2) + MathUtils.pow(getAccelerationY(), 2));
    }
        
    public Gyro getGyro(){
        return gyro;
    }
    
    public ADXL345_I2C getAccelerometer(){
        return accelerometer;
    }
    
    public int getAngleDegrees(){ 
        return angle(); 
    }
    
    public double getRawAngle(){
        return gyro.getAngle();
    }
    
    public double getStartAngle(){
        return startingAngle;
    }
    
    public void resetObstacles(){   //Removes obstacles from memory
      obstacleToFront = false;
      obstacleToFrontIsWall = false;
      
      obstacleToLeft  = false;
      obstacleToLeftIsWall = false;
      
      obstacleToRight = false;
      obstacleToRightIsWall = false;
      
      obstacleToBack  = false;
      obstacleToBackIsWall = false;
    }
    
    /*
    public double pow(double base, int power){
        return (power<1) ? 1 : base * pow(base, power-1);
    }
    */
    //</editor-fold>
}

