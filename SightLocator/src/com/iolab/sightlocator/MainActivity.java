package com.iolab.sightlocator;

import android.app.Dialog;
import android.app.Fragment;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class MainActivity extends BaseActivity {

    public static boolean mapFragmentVisible = true;
    public static boolean textFragmentVisible = true;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// checking if GooglePlayServices is installed
		int status = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getBaseContext());

		// Showing status
		if (status != ConnectionResult.SUCCESS) { // Google Play Services are not available

			int requestCode = 10;
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this,
					requestCode);
			dialog.show();

		} else {
			setContentView(R.layout.activity_main);
		}

        if (savedInstanceState != null) {
            // get the visibility state for Map Fragment
            MainActivity.mapFragmentVisible = savedInstanceState.getBoolean(Tags.MAP_FRAGMENT_VISIBLE);
        }

        Fragment mapFragment = getFragmentManager().findFragmentById(R.id.map_fragment);

        if (!MainActivity.mapFragmentVisible) {
            getFragmentManager().beginTransaction().hide(mapFragment).addToBackStack(null).commit();
        } else {
            getFragmentManager().beginTransaction().show(mapFragment).addToBackStack(null).commit();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle args) {
        super.onSaveInstanceState(args);
        args.putBoolean(Tags.MAP_FRAGMENT_VISIBLE, getFragmentManager().findFragmentById(R.id.map_fragment).isVisible());
    }


    @Override
    public void onBackPressed() {
        Fragment mapFragment = getFragmentManager().findFragmentById(R.id.map_fragment);
        Fragment textFragment = getFragmentManager().findFragmentById(R.id.text_fragment);
        // if we are in "Home" screen ("Map" and "Text" fragments are visible) - close the app
        if (mapFragment.isVisible() && textFragment.isVisible()) {
            finish();
        // else - go back one step
        } else {
            super.onBackPressed();
        }
    }

}
