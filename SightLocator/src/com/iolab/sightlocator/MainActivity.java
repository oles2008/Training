package com.iolab.sightlocator;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer.InitiateMatchResult;

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
            mapFragmentVisible = savedInstanceState.getBoolean(Tags.MAP_FRAGMENT_VISIBLE);
        }
    }
	
	@Override
	protected void onStart() {
		super.onStart();
		postInitFragmentsProperties();
	}
	
	@Override
	protected void onResume() {
		super.onStart();
		postInitFragmentsProperties();
	}
    
    private void setTextSize(int textSize) {
    	Fragment textFragment = getFragmentManager()
                .findFragmentById(R.id.text_fragment);

        View fragmentView = textFragment.getView();
        ((TextView) fragmentView.findViewById(R.id.textView)).setTextSize(textSize);
        ((TextView) fragmentView.findViewById(R.id.textView_invisible)).setTextSize(textSize);
        ((TextView) fragmentView.findViewById(R.id.text_view_object_title)).setTextSize(textSize);
        ((TextView) fragmentView.findViewById(R.id.text_view_object_title_invisible)).setTextSize(textSize);
    }
    
    private void postInitFragmentsProperties() {
    	new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				Fragment mapFragment = getFragmentManager().findFragmentById(R.id.map_fragment);
				Fragment textFragment = getFragmentManager().findFragmentById(R.id.text_fragment);
				if(textFragment!=null) {
		        if (!mapFragmentVisible) {
		            setTextSize(getResources().getDimensionPixelSize(R.dimen.description_text_size_full_screen));
		            getFragmentManager().beginTransaction().hide(mapFragment).addToBackStack(null).commit();
		        } else {
		            setTextSize(getResources().getDimensionPixelSize(R.dimen.description_text_size));
		            getFragmentManager().beginTransaction().show(mapFragment).addToBackStack(null).commit();
		        }
				}
			}
		}, 100);
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
            mapFragmentVisible = true;
            finish();
        // else - go back one step
        } else if(textFragment.isVisible()) {
            setTextSize(getResources().getDimensionPixelSize(R.dimen.description_text_size));
            super.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onTextFragmentLongClick() {
        Fragment mapFragment = getFragmentManager().findFragmentById(R.id.map_fragment);
        if (mapFragment.isVisible()) {
            setTextSize(getResources().getDimensionPixelSize(R.dimen.description_text_size_full_screen));
            getFragmentManager().beginTransaction().hide(mapFragment).addToBackStack(null).commit();
            mapFragmentVisible = false;
        } else {
            setTextSize(getResources().getDimensionPixelSize(R.dimen.description_text_size));
            getFragmentManager().beginTransaction().show(mapFragment).addToBackStack(null).commit();
            mapFragmentVisible = true;
        }
    }
}
