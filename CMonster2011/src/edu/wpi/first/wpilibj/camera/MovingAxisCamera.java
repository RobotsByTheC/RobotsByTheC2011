/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.wpi.first.wpilibj.camera;

import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.camera.AxisCamera;
import edu.wpi.first.wpilibj.parsing.ISensor;
//import edu.wpi.first.wpilibj.Servo;

/**
 *
 * @author Sean Halloran
 *//*
public class MovingAxisCamera extends AxisCamera implements ISensor{
    private Servo xServo, yServo;
    private final double xServoStart, yServoStart;

    public MovingAxisCamera(int xSlot, int xChannel, int ySlot, int yChannel){
        xServo=new Servo(xSlot, xChannel);
        yServo=new Servo(ySlot, yChannel);
        xServoStart=xServo.get();
        yServoStart=yServo.get();
    }

    public MovingAxisCamera(int x, int y){
        xServo=new Servo(x);
        yServo=new Servo(y);
        xServoStart=xServo.get();
        yServoStart=yServo.get();
    }

    public MovingAxisCamera(){
        xServo=new Servo(1);
        yServo=new Servo(2);
        xServoStart=xServo.get();
        yServoStart=yServo.get();
    }

    public void resetView(){
        xServo.set(xServoStart);
        yServo.set(yServoStart);
    }
}
*/