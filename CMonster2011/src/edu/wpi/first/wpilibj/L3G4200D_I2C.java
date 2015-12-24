package edu.wpi.first.wpilibj;

/**
 *
 * @author Team 2084
 * Made for the L3G4200D 3-axis gyro
 */

public class L3G4200D_I2C extends SensorBase{
    static final int kOversampleBits = 10;
    static final int kAverageBits = 0;
    static final double kSamplesPerSecond = 50.0;
    static final double kCalibrationSampleTime = 5.0;
    static final double kDefaultVoltsPerDegreePerSecond = 0.007;
    double m_voltsPerDegreePerSecond = kDefaultVoltsPerDegreePerSecond;
    double m_offset;
    
    AccumulatorResult result;
    static final AnalogChannel m_analog_X = new AnalogChannel(0x28);
    static final AnalogChannel m_analog_Y = new AnalogChannel(0x2A); 
    static final AnalogChannel m_analog_Z = new AnalogChannel(0x2C); 
    
    private static final byte L3G4200D_WHO_AM_I      = 0x0F;

    private static final byte L3G4200D_CTRL_REG1     = 0x20;
    private static final byte L3G4200D_CTRL_REG2     = 0x21;
    private static final byte L3G4200D_CTRL_REG3     = 0x22;
    private static final byte L3G4200D_CTRL_REG4     = 0x23; //Important (if changing accuracy, default is ±250° per second)
    private static final byte L3G4200D_CTRL_REG5     = 0x24;
    private static final byte L3G4200D_REFERENCE     = 0x25;
    private static final byte L3G4200D_OUT_TEMP      = 0x26;
    private static final byte L3G4200D_STATUS_REG    = 0x27;

    private static final byte L3G4200D_OUT_X_L       = 0x28;
    private static final byte L3G4200D_OUT_X_H       = 0x29;
    private static final byte L3G4200D_OUT_Y_L       = 0x2A;
    private static final byte L3G4200D_OUT_Y_H       = 0x2B;
    private static final byte L3G4200D_OUT_Z_L       = 0x2C;
    private static final byte L3G4200D_OUT_Z_H       = 0x2D;

    private static final byte L3G4200D_FIFO_CTRL_REG = 0x2E; //FIFO = First In, First Out.
    private static final byte L3G4200D_FIFO_SRC_REG  = 0x2F;

    private static final byte L3G4200D_INT1_CFG      = 0x30;
    private static final byte L3G4200D_INT1_SRC      = 0x31;
    private static final byte L3G4200D_INT1_THS_XH   = 0x32;
    private static final byte L3G4200D_INT1_THS_XL   = 0x33;
    private static final byte L3G4200D_INT1_THS_YH   = 0x34;
    private static final byte L3G4200D_INT1_THS_YL   = 0x35;
    private static final byte L3G4200D_INT1_THS_ZH   = 0x36;
    private static final byte L3G4200D_INT1_THS_ZL   = 0x37;
    private static final byte L3G4200D_INT1_DURATION = 0x38;
    
    public static class Axes {

        /**
          * Uses the high bit for data 
          */
        public final byte value;
        static final byte kX_val_L = 0x28; //X-axis is yaw (Used for corrections
        static final byte kX_val_H = 0x29;
        static final byte kY_val_L = 0x2A; //Y-axis is pitch (Used for corrections)
        static final byte kY_val_H = 0x2B;
        static final byte kZ_val_L = 0x2C; //Z-axis is roll (Parallel to field)
        static final byte kZ_val_H = 0x2D;
        public static final Axes kX = new Axes(kX_val_L); //Change "L" to "H" if using the lower bits
        public static final Axes kY = new Axes(kY_val_L);
        public static final Axes kZ = new Axes(kZ_val_L);

        private Axes(byte value) {
            this.value = value;
        }
    }
    private void init() {
        result = new AccumulatorResult();
        m_voltsPerDegreePerSecond = kDefaultVoltsPerDegreePerSecond;
        m_analog_X.setAverageBits(kAverageBits);
        m_analog_Y.setAverageBits(kAverageBits);
        m_analog_Z.setAverageBits(kAverageBits);
        
        m_analog_X.setOversampleBits(kOversampleBits);
        m_analog_Y.setOversampleBits(kOversampleBits);
        m_analog_Z.setOversampleBits(kOversampleBits);
        
        double sampleRate = kSamplesPerSecond * (1 << (kAverageBits + kOversampleBits));
        m_analog_X.getModule().setSampleRate(sampleRate);
        m_analog_Y.getModule().setSampleRate(sampleRate);
        m_analog_Z.getModule().setSampleRate(sampleRate);

        Timer.delay(1.0);
        m_analog_X.initAccumulator();
        m_analog_Y.initAccumulator();
        m_analog_Z.initAccumulator();

        Timer.delay(kCalibrationSampleTime);

        m_analog_X.getAccumulatorOutput(result);
        m_analog_Y.getAccumulatorOutput(result);
        m_analog_Z.getAccumulatorOutput(result);

        int center = (int) ((double)result.value / (double)result.count + .5);

        m_offset = ((double)result.value / (double)result.count) - (double)center;

        m_analog_X.setAccumulatorCenter(center);
        m_analog_Y.setAccumulatorCenter(center);
        m_analog_Z.setAccumulatorCenter(center);

        m_analog_X.setAccumulatorDeadband(0);
        m_analog_Y.setAccumulatorDeadband(0);
        m_analog_Z.setAccumulatorDeadband(0);
        
        m_analog_X.resetAccumulator();
        m_analog_Y.resetAccumulator();
        m_analog_Z.resetAccumulator();
    }
    
    private I2C m_i2c;
    public L3G4200D_I2C(int slot){
        DigitalModule module = DigitalModule.getInstance(slot);
        m_i2c = module.getI2C(L3G4200D_WHO_AM_I);
        init();
    }
    
    public void setSensitivity(double voltsPerDegreePerSecond) {
        m_voltsPerDegreePerSecond = voltsPerDegreePerSecond;
    }
    /**
     * 
     * @param Axis is the character representation of the axis wanted to be measured. Can be upper or lowercase.
     * @param Axis accepted values: x, X, y, Y, z, Z
     * @return the angle of the given axis
     */
    public double getAngle(char Axis) {
        char axis = Character.toUpperCase(Axis);
        double scaledValue = 0;
        long value = 0;
        if(axis == 'X'){
            m_analog_X.getAccumulatorOutput(result);
            value = result.value - (long) (result.count * m_offset);

            scaledValue = value * 1e-9 * m_analog_X.getLSBWeight() * (1 << m_analog_X.getAverageBits()) /
                    (m_analog_X.getModule().getSampleRate() * m_voltsPerDegreePerSecond);
        }
        else if(axis == 'Y'){
            m_analog_Y.getAccumulatorOutput(result);
            value = result.value - (long) (result.count * m_offset);

            scaledValue = value * 1e-9 * m_analog_Y.getLSBWeight() * (1 << m_analog_Y.getAverageBits()) /
                    (m_analog_Y.getModule().getSampleRate() * m_voltsPerDegreePerSecond);
        }
        else if(axis == 'Z'){
            m_analog_Z.getAccumulatorOutput(result);
            value = result.value - (long) (result.count * m_offset);

            scaledValue = value * 1e-9 * m_analog_Z.getLSBWeight() * (1 << m_analog_Z.getAverageBits()) /
                    (m_analog_Z.getModule().getSampleRate() * m_voltsPerDegreePerSecond);
        }
        else throw new NullPointerException("No axis given.");
        return scaledValue;
    }
    
    public void reset(){
        m_analog_X.resetAccumulator();
        m_analog_Y.resetAccumulator();
        m_analog_Z.resetAccumulator();
    }
    
}
