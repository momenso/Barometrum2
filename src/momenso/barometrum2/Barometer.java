package momenso.barometrum2;

import java.util.Observable;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


public class Barometer extends Observable implements SensorEventListener {

    private Context context;
    //private long startReadingTime;
    private boolean isSensorActive;
//    private Timer workerTimer;

    public Barometer(Context context) {
        this.context = context;
        this.isSensorActive = false;
        //this.startReadingTime = 0;

//        workerTimer = new Timer();
//        workerTimer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                active();
//            }
//        }, 0, 2000);
        enable();
    }

    public void enable() {
        //Log.v("Barometer", "Enable: registering sensor");

        if (!isSensorActive) {
            SensorManager sm =
                    (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            Sensor barometer = sm.getDefaultSensor(Sensor.TYPE_PRESSURE);
            if (barometer != null) {
                //sm.registerListener(this, barometer, SensorManager.SENSOR_DELAY_NORMAL);
            	sm.registerListener(this, barometer, SensorManager.SENSOR_DELAY_NORMAL);

                isSensorActive = true;
                //startReadingTime = System.currentTimeMillis();
            }
        }
    }

    public void disable() {

        //Log.v("Barometer", "Disable: unregistering sensor");

        SensorManager sm =
                (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sm.unregisterListener(this);

        isSensorActive = false;
    }

    public void terminate() {
        disable();
        //workerTimer.cancel();
    }

    public boolean switchSensor() {

        //Log.v("Barometer", "Switching sensor");

        if (!isSensorActive) {
            enable();
        } else {
            disable();
        }

        return isSensorActive;
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not interested in this event
        //Log.v("Barometer", "onAccuracyChanged: sensor=" + sensor.getName() + ", accuracy=" + accuracy);
    }

    private float average(float[] values) {
    	float total = 0;
    	for (float value : values) {
    		total += value;
    	}
    	
    	return total / values.length;
    }
    
    private long lastTime = System.currentTimeMillis();
    private float lastValue = -1;
    private float[] buffer = new float[5];
    private int index = 0;
    private boolean initial = true;
    
    public void onSensorChanged(SensorEvent event) {

        //Log.v("Barometer", "onSensorChanged: received new data");
    	buffer[(index++) % buffer.length] = event.values[0];
    	
    	if (initial) {
    		initial = false;
    		setChanged();
	        notifyObservers(event.values[0]);
    	}
    	
        if (index % buffer.length == 0) {
            long current = System.currentTimeMillis();
            long elapsed = current - lastTime;
            lastTime = current;

            float average = average(buffer);
            if (lastValue != -1) {
            	float change = (average - lastValue) / elapsed;

            	//Log.i("BAROMETER", String.format("%4.2f %.6f", average, change));
	    	        
    	        setChanged();
    	        notifyObservers(average);
            }
	        lastValue = average;
        }

        /*if (System.currentTimeMillis() - startReadingTime > 1000)*/ {
            //disable();
        }
    }
}
