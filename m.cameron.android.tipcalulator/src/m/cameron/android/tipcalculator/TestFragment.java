package m.cameron.android.tipcalculator;

import java.math.BigDecimal;
import java.text.NumberFormat;

import android.view.LayoutInflater;
//import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.EditText;
import org.holoeverywhere.widget.SeekBar;

import Tip.Calculator.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.TextView;

import android.support.v4.app.Fragment;

public class TestFragment extends Fragment {
	private String mContent = "???";
	private static final String KEY_TAB_NUM = "key.tab.num";
	
	private EditText tipText, personText, totalText;
	private TextView tipResultTextView, tipResultPerPersonTextView, 
					 billTotalWithTipTextView, billTotalWithTipPerPersonTextView,
					 labelTip, labelTotal, labelTipPerPerson, labelTotalPerPerson;
	private SeekBar  tipSeekBar;
	private Button   ButtonPeopleMinus, ButtonPeoplePlus;
	
	private String prevValTotal,  prevValTip, prevValPersons;
	private String ListPreference;
	
	NumberFormat currency = NumberFormat.getCurrencyInstance();
	
	TipManager tipManager;
	public static final String APP_PREFERENCES = "TipCalculatorPreferences";
	public static final String PREF_TOTAL = "Total";
	public static final String PREF_TIP = "Tip";
	public static final String PREF_PERSONS = "Persons";
	SharedPreferences settings;
	
	public static TestFragment newInstance(String text) {
        TestFragment fragment = new TestFragment();
        
        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString(KEY_TAB_NUM, text);
        fragment.setArguments(args);

        
        return fragment;
    }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContent =  getArguments() != null ? getArguments().getString(KEY_TAB_NUM) : "???";
		
        settings = getActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        Log.d("Counting", "no?2222??? "+ settings.toString());
        loadSettings();
	}	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d("Counting", "no?e 33 3??? "+ settings.toString());
        View view = (View) inflater.inflate(R.layout.main_holo, null);
//        setContentView(R.layout.main_holo);
        Log.d("Counting", "and here? 1111 "+ settings.toString());
        tipManager = new TipManager();

        Log.d("Counting", "and here? "+ settings.toString());
        // EditTexts
        totalText = (EditText) view.findViewById(R.id.editTextTotal);
        tipText = (EditText) view.findViewById(R.id.EditTextTip);
        personText = (EditText) view.findViewById(R.id.EditTextPeople);
        Log.d("Counting", "no???? "+ personText.getText().toString());
        // TextViews
        tipResultTextView = (TextView) view.findViewById(R.id.TextViewTipResult);
        tipResultPerPersonTextView = (TextView) view.findViewById(R.id.TextViewPerPersonResult);
        billTotalWithTipTextView = (TextView) view.findViewById(R.id.TextViewTotalWithTip);
        billTotalWithTipPerPersonTextView = (TextView) view.findViewById(R.id.TextViewTotalWithTipPerPerson);
        labelTip = (TextView) view.findViewById(R.id.TextView03);
        labelTotal = (TextView) view.findViewById(R.id.TextView06);
        labelTipPerPerson = (TextView) view.findViewById(R.id.TextView05);
        labelTotalPerPerson = (TextView) view.findViewById(R.id.TextView07);
        // SeekBars
        tipSeekBar = (SeekBar) view.findViewById(R.id.seekBarTip);
        // Buttons
        ButtonPeopleMinus = (Button) view.findViewById(R.id.ButtonPeopleMinus);
        ButtonPeoplePlus = (Button) view.findViewById(R.id.ButtonPeoplePlus);
         
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
        
//        getPrefs();
        
        totalText.setText(prevValTotal);
        tipText.setText(prevValTip);
        personText.setText(prevValPersons);
        
        return view;
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
    
    /**
     * Set all results to zero for data that doesn't compute
     */
    private void zeroResults() {
    	tipManager.setTipPercent("0");
    	tipManager.setTotalBeforeTip("0");
    }
    
    /**
     * Does the all the math to calculate the amount of Tip, as well as all the respective
     * calculations, such as tip per person, total per person, etc...
     */
    public void calculateTip() {
    	// Save all the App values for next App start
//    	saveSettings();
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
     * Load the App state for the Edit Boxes from last result.
     */
    private void loadSettings() {
    	prevValTotal = settings.getString(PREF_TOTAL, "1.0");
    	Log.d("TAG", prevValTotal);
    	prevValTip = settings.getString(PREF_TIP, "14");
    	prevValPersons = settings.getString(PREF_PERSONS, "2");
	}
}
