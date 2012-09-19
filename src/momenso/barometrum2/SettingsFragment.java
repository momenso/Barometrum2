package momenso.barometrum2;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment {

	//private Preferences preferences;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        addPreferencesFromResource(R.xml.settings);
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
