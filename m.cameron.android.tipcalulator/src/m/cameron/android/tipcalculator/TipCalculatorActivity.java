package m.cameron.android.tipcalculator;


import Tip.Calculator.R;
import org.holoeverywhere.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import org.holoeverywhere.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import org.holoeverywhere.widget.EditText;
import org.holoeverywhere.widget.SeekBar;
import org.holoeverywhere.widget.TextView;
import org.holoeverywhere.widget.Button;

import com.actionbarsherlock.view.MenuInflater;

import m.cameron.android.tipcalculator.TipManager;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.HashMap;
import java.util.Locale;

/**
 * @author mark.cameron
 *
 */
public class TipCalculatorActivity extends Activity {
	// Variables
	private EditText tipText, personText, totalText;
	private TextView tipResultTextView, tipResultPerPersonTextView, 
					 billTotalWithTipTextView, billTotalWithTipPerPersonTextView,
					 labelTip, labelTotal, labelTipPerPerson, labelTotalPerPerson;
	private SeekBar  tipSeekBar;
	private Button   ButtonPeopleMinus, ButtonPeoplePlus;
	
	private String prevValTotal,  prevValTip, prevValPersons;
	private String ListPreference;
	
	TipManager tipManager;
	
	SharedPreferences settings;
	
	// Key for the menu ID's
	private static final int EDIT_ID = Menu.FIRST+2;
	// Other Constants
	public static final String APP_PREFERENCES = "TipCalculatorPreferences";
	public static final String PREF_TOTAL = "Total";
	public static final String PREF_TIP = "Tip";
	public static final String PREF_PERSONS = "Persons";
	private static final int BUTTON_SAVE = 1;
	private static final int BUTTON_BILL_PHOTO = 2;
	private static final int BUTTON_SETTINGS = 3;
	NumberFormat currency = NumberFormat.getCurrencyInstance();
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_holo);
        
        tipManager = new TipManager();
        // initialize and load settings (state of the App to return to)
        settings = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        loadSettings();
        
        // EditTexts
        totalText = (EditText) findViewById(R.id.editTextTotal);
        tipText = (EditText) findViewById(R.id.EditTextTip);
        personText = (EditText) findViewById(R.id.EditTextPeople);
        Log.d("TAG", personText.getText().toString());
        // TextViews
        tipResultTextView = (TextView) findViewById(R.id.TextViewTipResult);
        tipResultPerPersonTextView = (TextView) findViewById(R.id.TextViewPerPersonResult);
        billTotalWithTipTextView = (TextView) findViewById(R.id.TextViewTotalWithTip);
        billTotalWithTipPerPersonTextView = (TextView) findViewById(R.id.TextViewTotalWithTipPerPerson);
        labelTip = (TextView) findViewById(R.id.TextView03);
        labelTotal = (TextView) findViewById(R.id.TextView06);
        labelTipPerPerson = (TextView) findViewById(R.id.TextView05);
        labelTotalPerPerson = (TextView) findViewById(R.id.TextView07);
        // SeekBars
        tipSeekBar = (SeekBar) findViewById(R.id.seekBarTip);
        // Buttons
        ButtonPeopleMinus = (Button) findViewById(R.id.ButtonPeopleMinus);
        ButtonPeoplePlus = (Button) findViewById(R.id.ButtonPeoplePlus);
         
        tipText.setText(String.valueOf(tipSeekBar.getProgress()));
        
        // Listeners
        tipSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        tipText.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String tipValue = tipText.getText().toString();
				// Check for empty tip EditText.
				if (tipValue.equals("")) {
					zeroResults();
					return;
				} 
				
				// Don't allow tip percentage greater than a 100%
				if (Integer.parseInt(tipValue) > 100) {
					tipText.setText(Integer.toString(100));
				}
				
				try {
					tipSeekBar.setProgress(Integer.parseInt(tipValue));
					tipManager.setTipPercent(tipValue);
					calculateTip();
				}				
				catch (NullPointerException e) {
					
				}
				catch (NumberFormatException numberFormatException) {
					// Only Zero results on numberFormatException, otherwise 
					// we cannot load old state, since TipManager values get overwritten.
					zeroResults();
				}
			}
		});
        totalText.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				try {
					tipManager.setTotalBeforeTip(totalText.getText().toString());
					calculateTip();
				}				
				catch (NullPointerException e) {
					
				}
				catch (NumberFormatException numberFormatException) {
					// Only Zero results on numberFormatException, otherwise 
					// we cannot load old state, since TipManager values get overwritten.
					zeroResults();
				}
			}
		});
        personText.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				try {
					calculateTip();
				}				
				catch (NullPointerException e) {
					
				}
				catch (NumberFormatException numberFormatException) {
					// Only Zero results on numberFormatException, otherwise 
					// we cannot load old state, since TipManager values get overwritten.
					zeroResults();
				}
			}
		});
        
        ButtonPeopleMinus.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				final int action = event.getAction();
		    	switch (action & MotionEvent.ACTION_MASK) {
			    	case MotionEvent.ACTION_UP: {
						// TODO Auto-generated method stub
						Integer persons = Integer.parseInt(personText.getText().toString());
						if (persons <= 1) {
							persons = 1;
						}
						else {
							persons--;
						}
						personText.setText(persons.toString());
						break;
			    	}
		    	}
				
				return false;
			}
		});
        
        ButtonPeoplePlus.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				final int action = event.getAction();
		    	switch (action & MotionEvent.ACTION_MASK) {
			    	case MotionEvent.ACTION_UP: {
						Integer persons = Integer.parseInt(personText.getText().toString());
						if (persons == Integer.MAX_VALUE) {
							persons = Integer.MAX_VALUE;
						}
						else {
							persons++;
						}
						personText.setText(persons.toString());
			    	}
		    	}
		    	
				return false;
			}
		});
        
        getPrefs();
        
        totalText.setText(prevValTotal);
        tipText.setText(prevValTip);
        personText.setText(prevValPersons);
    }

	private SeekBar.OnSeekBarChangeListener seekBarChangeListener
    = new SeekBar.OnSeekBarChangeListener() {
	    @Override
	    public void onProgressChanged(SeekBar seekBar, int progress,
	      boolean fromUser) {
	        // TODO Auto-generated method stub
	    	if (fromUser) {
	    		tipText.setText(Integer.toString(tipSeekBar.getProgress()));
			}	    	
	    }
	    
	    @Override
	    public void onStartTrackingTouch(SeekBar seekBar) {
	    // TODO Auto-generated method stub
	    }
	
	    @Override
	    public void onStopTrackingTouch(SeekBar seekBar) {
	    // TODO Auto-generated method stub
	    }
    };
    
    public void onResume(){
    	// Recalculate app state incase things were changed in the settings
    	getPrefs();
    	calculateTip();
    	super.onResume();
    }
   
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    menu.add(0, BUTTON_SAVE, 0, "Save")
			.setIcon(R.drawable.ic_save_light)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		menu.add(0, BUTTON_BILL_PHOTO, 0, "Camera")
			.setIcon(R.drawable.ic_camera_light)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
	    menu.add(0, BUTTON_SETTINGS, 0, "Settings")
			.setIcon(R.drawable.ic_settings_light)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
	    return true;
	}

    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case BUTTON_SETTINGS:
    		startActivityForResult(new Intent(this, Preferences.class), 0);
    		return true;
    	}
    	return false;
	}
    
	public static boolean isNumeric(String str) {
		NumberFormat formatter = NumberFormat.getInstance();
		ParsePosition pos = new ParsePosition(0);
		formatter.parse(str, pos);
		return str.length() == pos.getIndex();
	}
    
    private void getPrefs() {
        // Get the xml/preferences.xml preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        ListPreference = prefs.getString("list", Locale.getDefault().toString());
        Locale[] availableLocales = Locale.getAvailableLocales();

        if (isNumeric(ListPreference)) {
        	currency = NumberFormat.getCurrencyInstance(availableLocales[Integer.parseInt(ListPreference)]);
		} else {
			currency = NumberFormat.getCurrencyInstance(availableLocales[getCountryPositionInList(ListPreference, availableLocales)]);
		}
    }
    
    private int getCountryPositionInList(String needle, Locale[] availableLocales) {
        // Create HashMap(K,V) of country codes and position.
        HashMap<String, Integer> entriesMap = new HashMap<String, Integer>();
        for (int i = 0; i < availableLocales.length; i++) {
        	entriesMap.put(availableLocales[i].toString(), i);
        }
        
        int temp = entriesMap.get(needle);
        return temp;
    }
    
    /**
     * Does the all the math to calculate the amount of Tip, as well as all the respective
     * calculations, such as tip per person, total per person, etc...
     */
    public void calculateTip() {
    	// Save all the App values for next App start
    	saveSettings();
    	checkNumberOfPeople();
    	
    	// Do the juicy calculations
    	tipManager.calculateTip();
    	outputResults();
    }
    
    private void checkNumberOfPeople() {
    	BigDecimal numberOfPersons = new BigDecimal(personText.getText().toString());
    	if (numberOfPersons.compareTo(BigDecimal.ZERO) <= 0) {
    		tipManager.setNumberOfPeople(BigDecimal.ONE.toString());
		} else {
    		tipManager.setNumberOfPeople(personText.getText().toString());
    	}    	
    }
    
    private void outputResults() {
    	if (tipManager.getNumberOfPeople().equals(BigDecimal.ONE)) {
			setResultsForOnePerson();
		} else {
			setResultsForMultiplePeople();
		}
    }
    
    private void setResultsForMultiplePeople() {
    	labelTip.setText(R.string.main_tip_result);
    	labelTipPerPerson.setText(R.string.main_per_person);
    	labelTotal.setText(R.string.main_total_with_tip);
    	labelTotalPerPerson.setText(R.string.main_total_with_tip_per_person);
    	
    	tipResultTextView.setText(currency.format(tipManager.getTipAmount()));
    	tipResultPerPersonTextView.setText(currency.format(tipManager.getTipAmountPerPerson()));
    	billTotalWithTipTextView.setText(currency.format(tipManager.getTotalWithTip()));
    	billTotalWithTipPerPersonTextView.setText(currency.format(tipManager.getTotalWithTipPerPerson()));
    }
    
    private void setResultsForOnePerson() {
    	labelTip.setText(R.string.main_tip_result);
    	labelTipPerPerson.setText(R.string.main_total_with_tip);
		labelTotal.setText("");
		labelTotalPerPerson.setText("");
    	
    	tipResultTextView.setText(currency.format(tipManager.getTipAmount()));
    	tipResultPerPersonTextView.setText(currency.format(tipManager.getTotalWithTip()));
    	billTotalWithTipTextView.setText("");
    	billTotalWithTipPerPersonTextView.setText("");
    }
    
    /**
     * Set all results to zero for data that doesn't compute
     */
    private void zeroResults() {
    	tipManager.setTipPercent("0");
    	tipManager.setTotalBeforeTip("0");
    }
    
    /**
     * Save the App state to sharedPreferences for next start
     */
    private void saveSettings() {
    	SharedPreferences.Editor prefEditor = settings.edit();
    	prefEditor.putString(PREF_TOTAL, totalText.getText().toString());
    	prefEditor.putString(PREF_TIP, tipText.getText().toString());
    	prefEditor.putString(PREF_PERSONS, personText.getText().toString());
    	prefEditor.commit();
    }
    
    /**
     * Load the App state for the Edit Boxes from last result.
     */
    private void loadSettings() {
    	prevValTotal = settings.getString(PREF_TOTAL, "1.0");
    	prevValTip = settings.getString(PREF_TIP, "14");
    	prevValPersons = settings.getString(PREF_PERSONS, "2");
	}
}


