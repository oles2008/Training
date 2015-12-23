package com.iolab.sightlocator;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class MainActivity extends BaseActivity
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
                
        // check if gps module is presented, switched off
        LocationManager mgr = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        if (mgr != null) {
        	if (mgr.getAllProviders().contains(LocationManager.GPS_PROVIDER)) {
        		if(mgr.isProviderEnabled(LocationManager.GPS_PROVIDER)){
					Toast.makeText(Appl.appContext, "GPS is enabled", 
        					Toast.LENGTH_SHORT).show();
        		} else {
        			showGPSDisabledAlertToUser();
        		}
        	}
        	else {
        		Toast.makeText(getApplicationContext(), "GPS is not found", 
        					Toast.LENGTH_SHORT).show();
        	}
        }
    }

    private void showGPSDisabledAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
        .setCancelable(false)
        .setPositiveButton("Goto Settings Page To Enable GPS",
                new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){
                Intent callGPSSettingIntent = new Intent(
                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(callGPSSettingIntent);
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){
                dialog.cancel();
            }
        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
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
