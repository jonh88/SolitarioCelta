package es.upm.miw.SolitarioCelta;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

public class SCeltaPrefs extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_void);

		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction =
				fragmentManager.beginTransaction();
		SCeltaFragmentoPrefs fragment = new SCeltaFragmentoPrefs();
		fragmentTransaction.replace(android.R.id.content, fragment);
		fragmentTransaction.commit();
	}
}