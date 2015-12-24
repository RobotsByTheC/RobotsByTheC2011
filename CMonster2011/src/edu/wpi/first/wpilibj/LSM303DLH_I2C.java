package edu.wpi.first.wpilibj;

import java.util.Vector;
import com.sun.squawk.util.MathUtils;

/**
 *
 * @author Team 2084
 * Designed for use with the LSM303DLH accelerometer sensor manufactured by Pololu
 */

public class LSM303DLH_I2C {
    
    int x,
        y,
        z;
    double xAccel, yAccel, zAccel;

    /**
     * 
     * @NOTE: these methods were originally written in Arduino's version of C++ and therefore used different syntax than Java.
     * These translations may be inaccurate, especially considering the fact that they were designed to use Vectors and not arrays.
     * 
     */
    
    private I2C m_i2c;
    public LSM303DLH_I2C(int slot){
        DigitalModule module = DigitalModule.getInstance(slot);
        m_i2c = module.getI2C(0x0F);
    }
    private static class Axes{
        public final byte value;
        static final byte X_val = 0x29;
        static final byte Y_val = 0x2B;
        static final byte Z_val = 0x2D;
        public static final Axes X = new Axes(X_val);
        public static final Axes Y = new Axes(Y_val);
        public static final Axes Z = new Axes(Z_val);

        private Axes(byte value) {
            this.value = value;
        }
    }
    
    public double getAcceleration(Axes axis){
        byte[] rawAccel = new byte[2];
        m_i2c.read(axis.value, rawAccel.length, rawAccel);
        return accelFromBytes(rawAccel[0], rawAccel[1]);
    }
    
    private double accelFromBytes(byte first, byte second) {
        short tempLow = (short) (first & 0xff);
        short tempHigh = (short) ((second << 8) & 0xff00);
        return (tempLow | tempHigh) * 0.00390625; // 1/256
    }
    
    private void vector_cross(float[] a, float[] b, float[] out){
        out[x] = a[y]*b[z] - a[z]*b[y];
        out[y] = a[z]*b[x] - a[x]*b[z];
        out[z] = a[x]*b[y] - a[y]*b[x];
    }

    private float vector_dot(float[] a, float[] b)
    {
        return a[x]*b[x] + a[y]*b[y] + a[z]*b[z];
    }
    
    private float[] vector_sum(float[] a,float[] b){
        float[] sum = new float[3];
        sum[x] = a[x]+b[x];
        sum[y] = a[y]+b[y];
        sum[z] = a[z]+b[z];
        return sum;
    }
    
    private void vector_normalize(float[] a){
        float mag = (float) Math.sqrt(this.vector_dot(a,a));
        a[x] /= mag;
        a[y] /= mag;
        a[z] /= mag;
    }
    
        private float[] a, m, m_max, m_min, N, E, from = {-1, 0, 0};
        float[] temp_a = a;
        float[] temp_m = m;
        float yaw, pitch, roll;
        //Yaw is around Z axis
        //Pitch is around Y axis
        //Roll is around X axis
        
    public void attitude(){            
        vector_normalize(a);            
        vector_normalize(m);
        pitch = (float) MathUtils.asin(-temp_a[x]);
        roll = (float) MathUtils.asin(temp_a[y]/Math.cos(pitch));
        vector_cross(temp_a, temp_m, E);
        vector_cross(E, temp_a, N);
            float tmp_yaw = yaw;
            float PI = (float) Math.PI;
            tmp_yaw = 2*PI;
            tmp_yaw *= MathUtils.atan2(vector_dot(E, from), vector_dot(N, from));
            tmp_yaw /= (2*PI);
        yaw = tmp_yaw;             
        }
    
}
