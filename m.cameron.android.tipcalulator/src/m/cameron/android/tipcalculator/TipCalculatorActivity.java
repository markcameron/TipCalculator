package m.cameron.android.tipcalculator;

import Tip.Calculator.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import m.cameron.android.tipcalculator.TipManager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * @author mark.cameron
 *
 */
public class TipCalculatorActivity extends Activity {
	// Variables
	private EditText tipText, personText, totalText;
	private TextView tipResultTextView, tipResultPerPersonTextView, billTotalWithTipTextView, billTotalWithTipPerPersonTextView;
	private SeekBar tipSeekBar;
	
	String prevValTotal, prevValTip, prevValPersons;
	String ListPreference;
	
	TipManager tipManager;
	
	SharedPreferences settings;
	
	// Key for the menu ID's
	private static final int EDIT_ID = Menu.FIRST+2;
	// Other Constants
	public static final String APP_PREFERENCES = "TipCalculatorPreferences";
	public static final String PREF_TOTAL = "Total";
	public static final String PREF_TIP = "Tip";
	public static final String PREF_PERSONS = "Persons";
	NumberFormat currency = NumberFormat.getCurrencyInstance();
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        tipManager = new TipManager();
        // initialize and load settings (state of the app to return to)
        settings = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        loadSettings();
        
        // EditTexts
        totalText = (EditText) findViewById(R.id.editTextTotal);
        tipText = (EditText) findViewById(R.id.EditTextTip);
        personText = (EditText) findViewById(R.id.EditTextPeople);  
        // TextViews
        tipResultTextView = (TextView) findViewById(R.id.TextViewTipResult);
        tipResultPerPersonTextView = (TextView) findViewById(R.id.TextViewPerPersonResult);
        billTotalWithTipTextView = (TextView) findViewById(R.id.TextViewTotalWithTip);
        billTotalWithTipPerPersonTextView = (TextView) findViewById(R.id.TextViewTotalWithTipPerPerson);
        // SeekBars
        tipSeekBar = (SeekBar) findViewById(R.id.seekBarTip);
 
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
				// Don't allow tip percentage greater than a 100%
				if (tipText.getText() != null && Integer.parseInt(tipText.getText().toString()) > 100) {
					tipText.setText(Integer.toString(100));
				}
				
				try {
					tipSeekBar.setProgress(Integer.parseInt(tipText.getText().toString()));
					tipManager.setTipPercent(tipText.getText().toString());
					calculateTip();
				}				
				catch (Exception e) {
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
				catch (Exception e) {
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
				catch (Exception e) {
					zeroResults();
				}
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
	    menu.add(Menu.NONE, EDIT_ID, Menu.NONE, "Settings")
	    		.setIcon(android.R.drawable.ic_menu_preferences)
	    		.setAlphabeticShortcut('e');
	    menu.add(Menu.NONE, EDIT_ID+1, Menu.NONE, "Exit")
    			.setIcon(android.R.drawable.ic_menu_close_clear_cancel)
    			.setAlphabeticShortcut('q');
	    return(super.onCreateOptionsMenu(menu));
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case EDIT_ID:
    		startActivityForResult(new Intent(this, Preferences.class), 0);
    		return true;
    	case EDIT_ID+1:
    		finish();
    		return true;
    	}
    	return false;
	}
    
    
    private void getPrefs() {
        // Get the xml/preferences.xml preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        ListPreference = prefs.getString("list", Locale.getDefault().toString());
        Locale[] availableLocales = Locale.getAvailableLocales();

        currency = NumberFormat.getCurrencyInstance(availableLocales[Integer.parseInt(ListPreference)]);
    }
    
    /**
     * Does the all the math to cacluate the amount of Tip, as well as all the respective
     * calculations, such as tip per person, total per person, etc...
     */
    public void calculateTip() {
    	// Save all the app values for next app start
    	saveSettings();
    	
    	
    	
    	BigDecimal numberOfPersons = new BigDecimal(personText.getText().toString());
    	if (numberOfPersons.compareTo(BigDecimal.ZERO) <= 0) {
    		tipManager.setNumberOfPeople(BigDecimal.ONE.toString());
		} else {
    		tipManager.setNumberOfPeople(personText.getText().toString());
    	}
    	
    	// Do the juicy calculations
    	tipManager.calculateTip();
    	
    	tipResultTextView.setText(currency.format(tipManager.getTipAmount()));
    	tipResultPerPersonTextView.setText(currency.format(tipManager.getTipAmountPerPerson()));
    	billTotalWithTipTextView.setText(currency.format(tipManager.getTotalWithTip()));
    	billTotalWithTipPerPersonTextView.setText(currency.format(tipManager.getTotalWithTipPerPerson()));
    }
    
    /**
     * Set all results to zero for data that doesn't compute
     */
    private void zeroResults() {
    	tipResultTextView.setText(currency.format(0));
    	tipResultPerPersonTextView.setText(currency.format(0));
    	billTotalWithTipTextView.setText(currency.format(0));
    	billTotalWithTipPerPersonTextView.setText(currency.format(0));
    }
    
    /**
     * Save the app state to sharedPreferences for next start
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


