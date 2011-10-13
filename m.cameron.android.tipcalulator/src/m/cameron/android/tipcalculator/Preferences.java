package m.cameron.android.tipcalculator;

import Tip.Calculator.R;
import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceActivity;
 
public class Preferences extends PreferenceActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
        	super.onCreate(savedInstanceState);       
        	addPreferencesFromResource(R.xml.preferences);  
        }
}