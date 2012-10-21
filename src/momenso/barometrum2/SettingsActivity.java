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

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        final Context context = getApplicationContext();
        final ReadingsData pressureData = ReadingsData.getInstance(context);
        
        if (key.equals("BarometerMode")) {
        	updateModeSetting(pressureData);
        } else if (key.equals("BarometerUnit")) {
        	updateUnitSetting(pressureData);
        } else if (key.equals("GraphTimeScale")) {
        	updateTimeScaleSetting(pressureData);
        } else if (key.equals("KnownAltitude")) {
        	updateElevationSetting(pressureData);
        }
    }
    
    private void updateElevationSetting(ReadingsData pressureData) {
    	try {
    		String unit = sharedPreferences.getString("KnownAltitude", "0");
            pressureData.setCurrentElevation(Integer.valueOf(unit));
    	} catch (Exception e) {
    		// TODO notify failure to adjust settings
    	}
    }
    
    private void updateTimeScaleSetting(ReadingsData pressureData) {
    	try {
    		int interval = Integer.valueOf(sharedPreferences.getString("GraphTimeScale", "1")) * 60000;
            pressureData.setHistoryInterval(interval);
    	} catch (Exception e) {
    		// TODO notify failure to adjust settings
    	}
    }
    
    private void updateUnitSetting(ReadingsData pressureData) {
    	try {
	    	String unit = sharedPreferences.getString("BarometerUnit", "Bar");
	        pressureData.setUnit(PressureDataPoint.PressureUnit.valueOf(unit));
    	} catch (Exception e) {
    		// TODO notify failure to adjust settings
    	}
    }
    
    private void updateModeSetting(ReadingsData pressureData) {
    	try {
	    	String mode = sharedPreferences.getString("BarometerMode", "Absolute");
	        pressureData.setMode(PressureDataPoint.PressureMode.valueOf(mode));
    	} catch (Exception e) {
    		// TODO notify failure to adjust settings
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
