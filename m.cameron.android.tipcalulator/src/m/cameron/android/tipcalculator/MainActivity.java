package m.cameron.android.tipcalculator;

import Tip.Calculator.R;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class MainActivity extends SherlockFragmentActivity {

	private static final int BUTTON_SAVE = 1;
	private static final int BUTTON_BILL_PHOTO = 2;
	private static final int BUTTON_SETTINGS = 3;
	

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the
     * sections. We use a {@link android.support.v4.app.FragmentPagerAdapter} derivative, which will
     * keep every loaded fragment in memory. If this becomes too memory intensive, it may be best
     * to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
	FragmentAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion < android.os.Build.VERSION_CODES.HONEYCOMB){
            // Do something for froyo and above versions
        	setContentView(R.layout.main_holo);
        } else{
            // do something for phones running an SDK before froyo
            setContentView(R.layout.activity_main);
            // Create the adapter that will return a fragment for each of the three primary sections
            // of the app.
            mSectionsPagerAdapter = new FragmentAdapter(getSupportFragmentManager());


            // Set up the ViewPager with the sections adapter.
            mViewPager = (ViewPager) findViewById(R.id.pager);
            mViewPager.setAdapter(mSectionsPagerAdapter);
        }
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(0, BUTTON_SETTINGS, 0, "Settings")
			.setIcon(R.drawable.ic_settings_light)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//	    menu.add(0, BUTTON_SAVE, 0, "Save")
//			.setIcon(R.drawable.ic_save_light)
//			.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
//		menu.add(0, BUTTON_BILL_PHOTO, 0, "Camera")
//			.setIcon(R.drawable.ic_camera_light)
//			.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
//	    menu.add(0, BUTTON_SETTINGS, 0, "Settings")
//			.setIcon(R.drawable.ic_settings_light)
//			.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
//	    return true;
//	}
//
//    public boolean onOptionsItemSelected(MenuItem item) {
//    	switch (item.getItemId()) {
//    	case BUTTON_SETTINGS:
//    		startActivityForResult(new Intent(this, Preferences.class), 0);
//    		return true;
//    	}
//    	return false;
//	}
    
    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    public class FragmentAdapter extends FragmentPagerAdapter {

        public FragmentAdapter(FragmentManager fm) {
    		super(fm);
    		// TODO Auto-generated constructor stub
    	}

        @Override
        public Fragment getItem(int i) {
        	switch (i) {
        		case 0: return new TipCalculatorFragment();
    		}
            
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0: return getString(R.string.main_tip).toUpperCase();
                case 1: return getString(R.string.main_total).toUpperCase();
            }
            return null;
        }
    }
	
}
