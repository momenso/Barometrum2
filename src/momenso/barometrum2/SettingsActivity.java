package momenso.barometrum2;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;


public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.settings, false);

        Context context = getApplicationContext();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Context context = getApplicationContext();
        ReadingsData pressureData = ReadingsData.getInstance(context);
        if (key.equals("BarometerMode")) {
            String mode = sharedPreferences.getString("BarometerMode", "Absolute");
            pressureData.setMode(PressureDataPoint.PressureMode.valueOf(mode));
        }
        else if (key.equals("BarometerUnit")) {
            String unit = sharedPreferences.getString("BarometerUnit", "Bar");
            pressureData.setUnit(PressureDataPoint.PressureUnit.valueOf(unit));
        }
        else if (key.equals("KnownAltitude")) {
            String unit = sharedPreferences.getString("KnownAltitude", "0");
            pressureData.setCurrentElevation(Integer.valueOf(unit));
        }
        else if (key.equals("AltimeterGPSEnabled")) {
           boolean altimeterEnabled = sharedPreferences.getBoolean("AltimeterGPSEnabled", false);
           if (altimeterEnabled) {
               
           }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }
}
