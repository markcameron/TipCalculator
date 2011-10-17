package m.cameron.android.tipcalculator;

import Tip.Calculator.R;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;
 
public class Preferences extends PreferenceActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
        	super.onCreate(savedInstanceState);       
        	addPreferencesFromResource(R.xml.preferences);
        	
        	// Get available Locales
            ListPreference listPref = (ListPreference) this.findPreference("list");
            Locale[] availableLocales = Locale.getAvailableLocales();

            // Create HashMap(K,V) of country codes and position.
            HashMap<Integer, String> entriesMap = new HashMap<Integer, String>();
            for (int i = 0; i < availableLocales.length; i++) {
            	entriesMap.put(i, availableLocales[i].getDisplayCountry());
            }
            
            // Create a LinkedHashmap to sort on values instead of keys.
            HashMap<Object, Object> entriesMapSorted = new LinkedHashMap<Object, Object>();
            List<Integer> yourMapKeys = new ArrayList<Integer>(entriesMap.keySet());
            List<String> yourMapValues = new ArrayList<String>(entriesMap.values());
            TreeSet<String> sortedSet = new TreeSet<String>(yourMapValues);
            Object[] sortedArray = sortedSet.toArray();
            
            // Loop through the sorted arrays and place them in the LinkedHashMap
            int size = sortedArray.length;
            for (int i = 0; i < size; i++) {
            	entriesMapSorted.put
                  (yourMapKeys.get(yourMapValues.indexOf(sortedArray[i])),
                   sortedArray[i]);
            }

            // Variable setup, to Length minus 1 to remove the first blank one.
            // Might only be my phone.
            // @TODO: add check for blankness.
            CharSequence[] entries = new CharSequence[entriesMapSorted.size()-1];
            CharSequence[] entriesVal = new CharSequence[entriesMapSorted.size()-1];
            
            // place the Countries and keys in the ListPreference
            int i = -1;
            for (Object key : entriesMapSorted.keySet()) {
            	if (i < 0) {
            		i++;
					continue;
				}
                entries[i] = (CharSequence) entriesMapSorted.get(key);
				entriesVal[i++] = key.toString();
            }

            listPref.setEntries(entries);
            listPref.setEntryValues(entriesVal);
        }
}