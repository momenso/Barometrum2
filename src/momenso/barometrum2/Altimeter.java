package momenso.barometrum2;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;


public class Altimeter implements LocationListener {

    private Context context;
    //private float currentAltitude;
    //private boolean isSensorActive;
    private AltimeterListener listener;
    private int numberOfMeasurements;

    public Altimeter(Context context, AltimeterListener listener) {
        this.context = context;
        this.listener = listener;
        //this.isSensorActive = false;
        this.numberOfMeasurements = 0;
        //this.currentAltitude = retreiveAltitude();

        //Log.v("ALTITUDE", "Altitude=" + currentAltitude);
        enable();
    }

//    public float getAltitude() {
//        return currentAltitude;
//    }

//    public void setAltitude(float altitude) {
//        if (altitude != this.currentAltitude) {
//            this.currentAltitude = altitude;
//            persistAltitude(altitude);
//        }
//    }

//    private void persistAltitude(float altitude) {
//        try {
//            FileOutputStream fos = context.openFileOutput("altitude", Context.MODE_PRIVATE);
//            ObjectOutputStream os = new ObjectOutputStream(fos);
//            Float altitudeObject = altitude;
//
//            os.writeObject(altitudeObject);
//            os.close();
//
//        } catch (IOException ex) {
//            Log.v("ALTITUDE", "Exception persisting altitude: " + ex.getMessage());
//        }
//    }

//    private float retreiveAltitude() {
//        try {
//            FileInputStream fis = context.openFileInput("altitude");
//            ObjectInputStream is = new ObjectInputStream(fis);
//
//            Object item = is.readObject();
//            is.close();
//            fis.close();
//
//            if (item instanceof Float) {
//                Log.v("ALTITUDE", "Retrieved altitude = " + item);
//                return (Float) item;
//            }
//        } catch (Exception ex) {
//            Log.v("ALTITUDE", "Exception retreiving altitude: " + ex.getMessage());
//        }
//
//        Log.v("ALTITUDE", "Got nothing! Returning 0");
//        return 0;
//    }

//    public boolean switchSensor() {
//        if (!isSensorActive) {
//            enable();
//            isSensorActive = true;
//        } else {
//            disable();
//            isSensorActive = false;
//        }
//
//        Log.v("ALTITUDE", "Switing sensor to " + (isSensorActive ? "ON" : "FALSE"));
//
//        return isSensorActive;
//    }

    public void enable() {
        LocationManager lm =
                (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        //Log.v("ALTITUDE", "Enabling location service");

//	    Location fakeLocation = new Location(LocationManager.GPS_PROVIDER);
//	    fakeLocation.setAltitude(63);
//	    onLocationChanged(fakeLocation);
    }

    public void disable() {
        LocationManager lm =
                (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        lm.removeUpdates(this);
        //Log.v("ALTITUDE", "Disabling location service");
    }

    public void onLocationChanged(Location location) {
        float accuracy = location.getAccuracy();

        //Log.i("ALTITUDE", "Got altitude: " + location.getAltitude());

        Float altitude = (float) location.getAltitude();
        //setAltitude(altitude);
        
        listener.altitudeRefresh(altitude, accuracy);
        
        if (numberOfMeasurements++ > 5) {
            disable();
        }
    }

    public void onProviderDisabled(String provider) {
        //Log.v("ALTITUDE", "Provider disabled=" + provider);
    }

    public void onProviderEnabled(String provider) {
        //Log.v("ALTITUDE", "Provider enabled=" + provider);
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        //Log.v("ALTITUDE", "Status changed=" + status);
    }
}
