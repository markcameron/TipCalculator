package m.cameron.android.tipcalculator;

import Tip.Calculator.R;
import android.app.Activity;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import java.util.Locale;
 
public class Preferences extends PreferenceActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
        	super.onCreate(savedInstanceState);       
        	addPreferencesFromResource(R.xml.preferences);
        	
        	// List preference
            ListPreference listPref = (ListPreference) this.findPreference("list");
            Locale[] availableLocales = Locale.getAvailableLocales();
            
            CharSequence[] entries = new CharSequence[Locale.getAvailableLocales().length];
            CharSequence[] entriesVal = new CharSequence[Locale.getAvailableLocales().length];
            
            for (int i = 0; i < entriesVal.length; i++) {
				entries[i] = availableLocales[i].getDisplayCountry();
				entriesVal[i] = Integer.toString(i);
			}
            
            listPref.setEntries(entries);
            listPref.setEntryValues(entriesVal);
        }
}