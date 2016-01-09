package com.iolab.sightlocator;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class MainActivity extends Activity
        implements SightsTextFragment.OnTextFragmentClickListener{

    public static boolean mapFragmentVisible = true;
    public static boolean textFragmentVisible = true;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Context context = getBaseContext();
		
		// checking if GooglePlayServices is installed
		int status = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(context);

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
        System.out.println(" >>> map fragment is null > " + (mapFragment == null));
        System.out.println(" >>> map visible > " + MainActivity.mapFragmentVisible);
        if (!MainActivity.mapFragmentVisible) {
            getTextView().setTextSize(24);
            getFragmentManager().beginTransaction().hide(mapFragment).addToBackStack(null).commit();
        } else {
            getTextView().setTextSize(14);
            getFragmentManager().beginTransaction().show(mapFragment).addToBackStack(null).commit();
        }
    }	
    
    private TextView getTextView() {
        Fragment textFragment = getFragmentManager()
                .findFragmentById(R.id.text_fragment);
        System.out.println(" >>> text fragment is null > " + (textFragment == null));
        System.out.println(" >>> text visible > " + MainActivity.textFragmentVisible);

        return (TextView) textFragment.getView().findViewById(R.id.textView);
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
            MainActivity.mapFragmentVisible = true;
            finish();
        // else - go back one step
        } else if(textFragment.isVisible()) {
            getTextView().setTextSize(14);
            super.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onTextFragmentLongClick() {
        Fragment mapFragment = getFragmentManager().findFragmentById(R.id.map_fragment);
        if (mapFragment.isVisible()) {
            getTextView().setTextSize(24);
            getFragmentManager().beginTransaction().hide(mapFragment).addToBackStack(null).commit();
            MainActivity.mapFragmentVisible = false;
        } else {
            getTextView().setTextSize(14);
            getFragmentManager().beginTransaction().show(mapFragment).addToBackStack(null).commit();
            MainActivity.mapFragmentVisible = true;
        }
    }
}
