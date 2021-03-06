package momenso.barometrum2;

import java.io.Serializable;
import java.util.Date;


public class PressureDataPoint implements Serializable {

    private static final long serialVersionUID = -3959936631531969908L;
    private float value;
    private long time;

    
	public static enum PressureMode {
        Absolute, MSLP
    };

    public static enum PressureUnit {
        mBar, Torr, Pascal, InHg, hPa
    };
    
    public PressureDataPoint() {
        this.setValue(0);
        this.setTime(0);
    }

    public PressureDataPoint(long time, float value) {
        this.setValue(value);
        this.setTime(time);
    }

    public void setValue(float value) {
        this.value = value;
    }

    public float getRawValue() {
        return this.value;
    }

    public float getValue(PressureMode mode, PressureUnit unit, float elevation, float correction) {
        return getPressure(mode, unit, elevation, value, correction);
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public void reset() {
        this.time = 0;
        this.value = 0;
    }

    private float convertToBarometric(float barometricPressure, float elevation) {
        double localStandardPressure =
                101325.0F * Math.pow(1.0F - 0.0000225577F * elevation, 5.25588F);
        double pressureDifference = 101325 - localStandardPressure;
        float mslp = barometricPressure + (float) pressureDifference / 100;

        return mslp;
    }

    private float convertToTorr(float bar) {
        return bar * 0.75006167382F;
    }

    private float convertToKiloPascal(float bar) {
        return bar / 10;
    }

    private float convertToInHg(float bar) {
        return bar / 33.86389F;
    }

    private float getPressure(PressureMode mode, PressureUnit unit, float elevation, float rawValue, float correction) {
        float value = 0;

        //if (rawValue == Float.MAX_VALUE ||
        //	rawValue == Float.MIN_VALUE) return 0;

        // mode
        if (mode == PressureMode.MSLP) {
            value = convertToBarometric(rawValue, elevation);
        } else {
            value = rawValue;
        }
        
        // unit
        if (unit == PressureUnit.Pascal) {
            value = convertToKiloPascal(value);
        } else if (unit == PressureUnit.Torr) {
            value = convertToTorr(value);
        } else if (unit == PressureUnit.InHg) {
            value = convertToInHg(value);
        }

        return value + correction;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (time ^ (time >>> 32));
        result = prime * result + Float.floatToIntBits(value);

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof PressureDataPoint)) {
            return false;
        }
        PressureDataPoint other = (PressureDataPoint) obj;
        if (time != other.time) {
            return false;
        }
        if (Float.floatToIntBits(value) != Float.floatToIntBits(other.value)) {
            return false;
        }

        return true;
    }
    
    @Override
    public String toString() {
    	return String.format("%f (%s)", this.value, new Date(this.time));
    }
    
}
