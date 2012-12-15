package momenso.barometrum2;

import java.util.Observable;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


public class Barometer extends Observable implements SensorEventListener {

    private Context context;
    private boolean isSensorActive;

    public Barometer(Context context) {
        this.context = context;
        this.isSensorActive = false;

        enable();
    }

    public void enable() {
        if (!isSensorActive) {
            SensorManager sm =
                    (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            Sensor barometer = sm.getDefaultSensor(Sensor.TYPE_PRESSURE);
            if (barometer != null) {
            	sm.registerListener(this, barometer, SensorManager.SENSOR_DELAY_NORMAL);

                isSensorActive = true;
            }
        }
    }

    public void disable() {
        SensorManager sm =
                (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sm.unregisterListener(this);

        isSensorActive = false;
    }

    public void terminate() {
        disable();
    }

    public boolean switchSensor() {
        if (!isSensorActive) {
            enable();
        } else {
            disable();
        }

        return isSensorActive;
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not interested in this event
    }

    private float average(float[] values) {
    	float total = 0;
    	for (float value : values) {
    		total += value;
    	}
    	
    	return total / values.length;
    }
    
    private float lastValue = -1;
    private float[] buffer = new float[5];
    private int index = 0;
    private boolean initial = true;
    
    public void onSensorChanged(SensorEvent event) {

    	buffer[(index++) % buffer.length] = event.values[0];
    	
    	if (initial) {
    		initial = false;
    		setChanged();
	        notifyObservers(event.values[0]);
    	}
    	
        if (index % buffer.length == 0) {
            float average = average(buffer);
            if (lastValue != -1) {	    	        
    	        setChanged();
    	        notifyObservers(average);
            }
	        lastValue = average;
        }
    }
}
