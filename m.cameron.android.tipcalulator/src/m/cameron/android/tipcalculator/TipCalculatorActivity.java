package m.cameron.android.tipcalculator;

import Tip.Calculator.R;
import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;


public class TipCalculatorActivity extends Activity {
	private EditText tipText, personText, totalText;
	private TextView tipResultTextView, tipResultPerPersonTextView, billTotalWithTipTextView, billTotalWithTipPerPersonTextView;
	private SeekBar tipSeekBar;
	
	String prevValTotal, prevValTip, prevValPersons;
	String ListPreference;
	
	private static final int EDIT_ID = Menu.FIRST+2;
	
	public static final String APP_PREFERENCES = "TipCalculatorPreferences";
	public static final String PREF_TOTAL = "Total";
	public static final String PREF_TIP = "Tip";
	public static final String PREF_PERSONS = "Persons";
	NumberFormat currency = NumberFormat.getCurrencyInstance();
	
	SharedPreferences settings;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        settings = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        
        // EditTexts
        totalText = (EditText) findViewById(R.id.editTextTotal);
        tipText = (EditText) findViewById(R.id.EditTextTip);
        personText = (EditText) findViewById(R.id.EditTextPeople);
        
        loadSettings();
        // TextViews
        tipResultTextView = (TextView) findViewById(R.id.TextViewTipResult);
        tipResultPerPersonTextView = (TextView) findViewById(R.id.TextViewPerPersonResult);
        billTotalWithTipTextView = (TextView) findViewById(R.id.TextViewTotalWithTip);
        billTotalWithTipPerPersonTextView = (TextView) findViewById(R.id.TextViewTotalWithTipPerPerson);
        // SeekBars
        tipSeekBar = (SeekBar) findViewById(R.id.seekBarTip);
 
        tipText.setText(String.valueOf(tipSeekBar.getProgress()));
        
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
	    		try {
					calculateTip();
				}				
				catch (Exception e) {
					zeroResults();
				}
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
    
    public void calculateTip() {
    	saveSettings();
    	
    	final BigDecimal HUNDRED = new BigDecimal("100");
    	
    	BigDecimal billTotal = new BigDecimal(totalText.getText().toString());
    	BigDecimal tipPercent = new BigDecimal(tipText.getText().toString());
    	tipPercent = tipPercent.divide(HUNDRED, 2, RoundingMode.HALF_UP);
    	BigDecimal numberOfPersons = new BigDecimal(personText.getText().toString());
    	
    	if (numberOfPersons.compareTo(BigDecimal.ZERO) <= 0) {
    		numberOfPersons = BigDecimal.ONE;
		}
    	
    	BigDecimal tipResult = billTotal.multiply(tipPercent);
    	BigDecimal tipPerPerson = tipResult.divide(numberOfPersons, 2, RoundingMode.HALF_UP);
    	BigDecimal billTotalWithTip = tipResult.add(billTotal);
    	BigDecimal billTotalWithTipPerPerson = billTotalWithTip.divide(numberOfPersons, 2, RoundingMode.HALF_UP);
    	
    	tipResultTextView.setText(currency.format(tipResult));
    	tipResultPerPersonTextView.setText(currency.format(tipPerPerson));
    	billTotalWithTipTextView.setText(currency.format(billTotalWithTip));
    	billTotalWithTipPerPersonTextView.setText(currency.format(billTotalWithTipPerPerson));
    }
    
    private void zeroResults() {
    	tipResultTextView.setText(currency.format(0));
    	tipResultPerPersonTextView.setText(currency.format(0));
    	billTotalWithTipTextView.setText(currency.format(0));
    	billTotalWithTipPerPersonTextView.setText(currency.format(0));
    }
    
    private void saveSettings() {
    	SharedPreferences.Editor prefEditor = settings.edit();
    	prefEditor.putString(PREF_TOTAL, totalText.getText().toString());
    	prefEditor.putString(PREF_TIP, tipText.getText().toString());
    	prefEditor.putString(PREF_PERSONS, personText.getText().toString());
    	prefEditor.commit();
    }
    
    private void loadSettings() {
    	prevValTotal = settings.getString(PREF_TOTAL, "1.0");
    	prevValTip = settings.getString(PREF_TIP, "14");
    	prevValPersons = settings.getString(PREF_PERSONS, "2");
	}
}


