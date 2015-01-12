package com.iolab.sightlocator;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.LocationSource;

public class SightsLocationSource implements LocationSource, LocationListener {
	
	private LocationManager locationManager;
	private OnLocationChangedListener onLocationChangedListener;
	
	public SightsLocationSource(Context context){
		
		// Acquire a reference to the system Location Manager
		
		this.locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
	}
	
	public SightsLocationSource(LocationManager locationManager){
		this.locationManager = locationManager;
	}

	@Override
	public void activate(OnLocationChangedListener onLocationChangedListener) {
		//for debugging
		Toast toast = Toast.makeText(Appl.appContext, "activating locationListener", Toast.LENGTH_SHORT);
		toast.show();
		//
		
		this.onLocationChangedListener = onLocationChangedListener;
		
		//to set initial location
		setInitialLocation();
		
		// Register the listener with the Location Manager to receive NETWORK location updates
		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 
				20000,	//20 sec - minimum time interval between location updates
				20,		//20 m - minimum distance between location updates
				this);

		// Register the listener with the Location Manager to receive GPS location updates
		locationManager.requestLocationUpdates(
				LocationManager.GPS_PROVIDER, 
				20000,	//20 sec - minimum time interval between location updates
				20,		//20 m - minimum distance between location updates
				this);
	}
	
	private void setInitialLocation(){
		Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if(lastKnownLocation==null){
			lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
		}
		if(lastKnownLocation==null){
			lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		if(lastKnownLocation!=null){
		onLocationChangedListener.onLocationChanged(lastKnownLocation);}
	}

	@Override
	public void deactivate() {
		//for debugging
		Toast toast = Toast.makeText(Appl.appContext, "deactivating locationListener", Toast.LENGTH_SHORT);
		toast.show();
		//
		
		locationManager.removeUpdates(this);
	}

	@Override
	public void onLocationChanged(Location location) {
		onLocationChangedListener.onLocationChanged(location);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

}
