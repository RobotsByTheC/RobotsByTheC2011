package edu.wpi.first.smartdashboard.gui;

import java.awt.Color;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Dimension;
import java.awt.Shape;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.text.DecimalFormat;

/**
 *
 * @author team 2084
 */
public class TrackerPanel extends JPanel
{
    private int robotX = 0;
    private int robotY = 0;
    private final int robotWidth = 24; // looking down at it in 2D
    private final int robotHeight = 32; // looking down at it in 2D
    private double robotAngleDegrees = 0.0;
    
    public TrackerPanel()
    {
        super();
        setPreferredSize(new Dimension(280, 560));
        setBackground(Color.BLACK);
        
        Thread move = new Thread(new RobotMovingThread());
        move.start();
    }
    
    public void setRobotX(int x)
    {
        robotX = x;
    }
    
    public void setRobotY(int y)
    {
        robotY = y;
    }
    
    public void setRobotAngleDegrees(double deg)
    {
        robotAngleDegrees = deg;
    }
    
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        
        // draw the rectangle for the field floor
        g2.setColor(Color.lightGray);
        g2.drawRect(0, 0, 280, 560); // 27 feet by 54 feet
                
        // say the robot is 28 inches by 38 inches -- one pixel is 1.2 inches (0.1 feet) -- 
        Rectangle robot = new Rectangle(robotX, robotY, robotWidth, robotHeight);
        
        // now we are going to rotate the rectangle (if it needs to)
        AffineTransform at = new AffineTransform();
        at.rotate(Math.toRadians(robotAngleDegrees), robotX + (robotWidth / 2), robotY + (robotHeight / 2));
        Shape rotatedRobot = at.createTransformedShape(robot);
        
        // set the color
        g2.setColor(Color.white);
        
        // draw the rotated robot
        g2.draw(rotatedRobot);
        
        // draw the center of the robot and print out the center point at the bottom of the window
        int centerX = robotX + (robotWidth / 2);
        int centerY = robotY + (robotHeight / 2);
        g2.drawLine(centerX, centerY, centerX, centerY);
        
        String centerXStr = new DecimalFormat("#.###").format(centerX * 0.1); //// chop off the gazillion "0"s that always end up
        String centerYStr = new DecimalFormat("#.###").format(centerY * 0.1); ////// to the right of the decimal
        
        String strX = "x feet: " + centerXStr;
        g2.drawString(strX, 5, 560);
        
        String strY = "y feet: " + centerYStr;
        g2.drawString(strY, 5, 580);
    }
    
    /////// this just makes it look like the robot's moving----until I/you guys figure out how to send our data over the network
    private class RobotMovingThread implements Runnable
    {
        private boolean stop = false;
        int x = 0;
        int y = 0;
        double angle = 0;
        
        public void run()
        {
            while(!stop)
            {
                setRobotX(x);
                setRobotY(y);
                setRobotAngleDegrees(angle);
                
                repaint(); /// redraw the display, the robot won't "move" unless this is here

                try
	    	{
                    Thread.sleep(10); // every centisecond (a one-hundredth of one second) --- just for testing
	    	}
	    	catch(Exception e)
	    	{}
                
                /// increment
                if(x <= 230)
                    x++;
                
                if(y <= 100)
                    y++;
                
                if(angle <= 360)
                    angle += 0.5;
                else
                    stop = true; /// stop this thread
            }
        }
    }
}
