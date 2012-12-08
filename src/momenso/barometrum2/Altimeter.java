package momenso.barometrum2;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;


public class Altimeter implements LocationListener {

    private Context context;
    //private float currentAltitude;
    //private boolean isSensorActive;
    private AltimeterListener listener;
    private int numberOfMeasurements;

    public Altimeter(Context context, AltimeterListener listener) {
        this.context = context;
        this.listener = listener;
        this.numberOfMeasurements = 0;
        enable();
    }

    public void enable() {
        LocationManager lm =
                (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    public void disable() {
        LocationManager lm =
                (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        lm.removeUpdates(this);
    }

    public void onLocationChanged(Location location) {
        float accuracy = location.getAccuracy();

        Float altitude = (float) location.getAltitude();
        
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
