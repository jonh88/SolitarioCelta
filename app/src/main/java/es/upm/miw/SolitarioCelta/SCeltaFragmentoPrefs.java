package es.upm.miw.SolitarioCelta;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class SCeltaFragmentoPrefs extends PreferenceFragment{

    public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
    }
}
