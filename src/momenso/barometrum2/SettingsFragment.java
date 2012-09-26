package momenso.barometrum2;

import android.content.Context;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;


public class SettingsFragment extends PreferenceFragment {

    //private Preferences preferences;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);
        
        Preference button = (Preference) findPreference("UseGPSAltimeter");
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference pref) {
                Context context = getActivity().getApplicationContext();
                final ReadingsData pressureData = ReadingsData.getInstance(context);
                final Altimeter altimeter = new Altimeter(context, new AltimeterListener() {
                    public void altitudeRefresh(float altitude, float accuracy) {
                        //Log.i("Settings", "Alt=" + altitude + ", acc=" + accuracy);
                        pressureData.setCurrentElevation(altitude);
                        
                        String altitudeValue = String.valueOf(Math.round(altitude));
                        //Log.v("Altitude", altitudeValue);
                        
                        EditTextPreference manualAltitude = (EditTextPreference) findPreference("KnownAltitude");
                        manualAltitude.setText(altitudeValue);
                    }
                });
                
                return true;
            }
        });
    }
//	@Override
//	protected void onListItemClick(ListView l, View v, int position, long id) {
//		Toast.makeText(v.getContext(), l.getSelectedItem().toString(), Toast.LENGTH_LONG);
//		super.onListItemClick(l, v, position, id);
//	}
//	
//	@Override
//	public void onBackPressed() {
//		//savePreferences();
//		super.onBackPressed();
//	}
    /**
     * Populate the activity with the top-level headers.
     */
//    @Override
//    public void onBuildHeaders(List<Header> target) {
//        loadHeadersFromResource(R.xml.settings, target);
//    }
}
