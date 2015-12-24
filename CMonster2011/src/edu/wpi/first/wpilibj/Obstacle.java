package edu.wpi.first.wpilibj;

import com.sun.squawk.util.MathUtils;


/*
 * This file is unneeded and therefore should not be used.
 * I'm not deleting it, though, because it keeps all the obstacle stuff in the same place.
 * --Sam
 */


public class Obstacle{
    /*
     * This class uses Ultrasonic sensors to detect obstacles around the robot. (@link
     * This class is responisble for detecting field obstacles that can be avoided
     * (like other robots, posts in the 2011 game, etc). Field walls/barriers
     * are also detected by the Ultrasonics, but they are tossed out as vaild
     * obstacles because the robot can't avoid them, so if in autonomous there is
     * an "avoidObstacle" method, it won't try to drive around a border wall.
    */
    
    private EpicSensorsSam EpicSensors;
    
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
    
    double currentXFeet = EpicSensors.currentX/1000000000;
    double currentYFeet = EpicSensors.currentY/1000000000;
    static double detectionRange = 24; //Inches
    static double angDev = 20; //Angle deviation
        //0 <= angDev <= ~30 (for more accuracy, keep it between ~10 and ~20)
    
    private int angle(){
        return EpicSensors.getAngleDegrees();
    } //Assuming counterclockwise rotation results in positive degrees
    
    private int rawAngle(){
        int i = 0;
        if(EpicSensors.getRawAngle() < -360) 
            i = MathUtils.round((float)EpicSensors.getRawAngle()) % 360 - 360;
        if(EpicSensors.getRawAngle() >= 360)
            i = MathUtils.round((float)EpicSensors.getRawAngle()) % 360;
        if(EpicSensors.getRawAngle() < 360 && EpicSensors.getRawAngle() >= 0)
            i = MathUtils.round((float)EpicSensors.getRawAngle());
        return i;
    }
    
    public void detectObstacles(){
   /* This method detects all obstacle on each side of the robot.
    * And can also tell if they're walls or not
   */   
        //<editor-fold defaultstate="collapsed" desc="Left side (done!)">
        if(EpicSensors.left[1].getRangeInches() <= detectionRange ||
           EpicSensors.left[2].getRangeInches() <= detectionRange){
                if(currentXFeet < -12){ //If on the left side of the field, less than 2 feet from the wall
                    if(angle() < 0+angDev && rawAngle() > 0-angDev){
                            obstacleToLeftIsWall = true; //The sensed obstacle is the wall and a non-avoidable obstacle
                            obstacleToLeft = false;
                    } 
                }
                
                else if(currentXFeet > 12){ //If on the right side of the field, 2 feet or less from the wall
                    if(angle() > 180-angDev && angle() < 180+angDev){ //If robot's left side is more or less parallel with the right wall (±20 degrees)
                            obstacleToLeftIsWall = true; //The sensed obstacle is the wall and a non-avoidable obstacle
                            obstacleToLeft = false;
                    }
                }
                else if(currentYFeet < -26){
                    if(angle() < 90+angDev && angle() > 90-angDev){
                        obstacleToLeftIsWall = true;
                        obstacleToLeft = false;
                    }
                }
                else if(currentYFeet > 26){
                    if(angle() < 270+angDev && angle() > 270-angDev){
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
        
        //<editor-fold defaultstate="collapsed" desc="Right side (done!)">
            if(EpicSensors.right[1].getRangeInches() < detectionRange ||
               EpicSensors.right[2].getRangeInches() < detectionRange){
               if(currentXFeet < -12){
                    if(angle() < 270+angDev && angle() > 270-angDev){
                            obstacleToRightIsWall = true;
                            obstacleToRight = false;
                    } 
                }
                
                else if(currentXFeet > 12){ 
                    if(angle() < 0+angDev && rawAngle() > 0-angDev){
                            obstacleToRightIsWall = true;
                            obstacleToRight = false;
                    }
                }
                else if(currentYFeet < -26){
                    if(angle() < 90+angDev && angle() > 90-angDev){
                        obstacleToRightIsWall = true;
                        obstacleToRight = false;
                    }
                }
                else if(currentYFeet > 26){
                    if(angle() < 270+angDev && angle() > 270-angDev){
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
        
        // <editor-fold defaultstate="collapsed" desc="Front side (done!)">
        if(EpicSensors.front[1].getRangeInches() < detectionRange ||
           EpicSensors.front[2].getRangeInches() < detectionRange){
            if(currentXFeet < -12){
                    if(angle() < 90+angDev && angle() > 90-angDev){
                            obstacleToFrontIsWall = true;
                            obstacleToFront = false;
                    } 
                }
                
                else if(currentXFeet > 12){ 
                    if(angle() < 270+angDev && angle() > 270-angDev){
                            obstacleToFrontIsWall = true;
                            obstacleToFront = false;
                    }
                }
                else if(currentYFeet < -26){
                    if(angle() < 180+angDev && angle() > 180-angDev){
                        obstacleToFrontIsWall = true;
                        obstacleToFront = false;
                    }
                }
                else if(currentYFeet > 26){
                    if(angle() < 0+angDev && rawAngle() > 0-angDev){
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
        
        // <editor-fold defaultstate="collapsed" desc="Back side (done!)">
        if(EpicSensors.back[1].getRangeInches() < detectionRange ||
           EpicSensors.back[2].getRangeInches() < detectionRange){
            if(currentXFeet < -12){
                    if(angle() < 270+angDev && angle() > 270-angDev){
                            obstacleToBackIsWall = true;
                            obstacleToBack = false;
                    } 
                }
                
                else if(currentXFeet > 12){ 
                    if(angle() < 90+angDev && angle() > 90-angDev){
                            obstacleToBackIsWall = true;
                            obstacleToBack = false;
                    }
                }
                else if(currentYFeet < -26){
                    if(angle() < 0+angDev && rawAngle() > 0-angDev){
                        obstacleToBackIsWall = true;
                        obstacleToBack = false;
                    }
                }
                else if(currentYFeet > 26){
                    if(angle() < 180+angDev && angle() > 180-angDev){
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
}
