package m.cameron.android.tipcalculator;

import Tip.Calculator.R;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import m.cameron.android.tipcalculator.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
 
public class Preferences extends PreferenceActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
        	super.onCreate(savedInstanceState);       
        	addPreferencesFromResource(R.xml.preferences);
        	
        	// List preference
            ListPreference listPref = (ListPreference) this.findPreference("list");
            Locale[] availableLocales = Locale.getAvailableLocales();


            
            HashMap<Integer, String> entriesMap = new HashMap<Integer, String>();

            for (int i = 0; i < availableLocales.length; i++) {
            	entriesMap.put(i, availableLocales[i].getDisplayCountry());
            }
            HashMap entriesMapSorted = new LinkedHashMap();
            
            List yourMapKeys = new ArrayList(entriesMap.keySet());
            List yourMapValues = new ArrayList(entriesMap.values());
            TreeSet sortedSet = new TreeSet(yourMapValues);
            Object[] sortedArray = sortedSet.toArray();
            int size = sortedArray.length;

            for (int i=0; i<size; i++) {
            	entriesMapSorted.put
                  (yourMapKeys.get(yourMapValues.indexOf(sortedArray[i])),
                   sortedArray[i]);
            }

            CharSequence[] entries = new CharSequence[entriesMapSorted.size()];
            CharSequence[] entriesVal = new CharSequence[entriesMapSorted.size()];
            
            int i = 0;
            for (Object key : entriesMapSorted.keySet()) {
                entries[i] = (CharSequence) entriesMapSorted.get(key);
				entriesVal[i++] = key.toString();
            }

            listPref.setEntries(entries);
            listPref.setEntryValues(entriesVal);
        }
}