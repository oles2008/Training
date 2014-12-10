package com.iolab.sightlocator;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.maps.LocationSource;

public class SightLocationSource implements LocationSource, LocationListener {
	
	private LocationManager locationManager;
	private OnLocationChangedListener onLocationChangedListener;
	
	public SightLocationSource(Context context){
		
		// Acquire a reference to the system Location Manager
		
		this.locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
	}
	
	public SightLocationSource(LocationManager locationManager){
		this.locationManager = locationManager;
	}

	@Override
	public void activate(OnLocationChangedListener onLocationChangedListener) {
		
		this.onLocationChangedListener = onLocationChangedListener;
		
		// Register the listener with the Location Manager to receive NETWORK location updates
		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 
				60000,	//1 min - minimum time interval between location updates
				50,		// 50 m - minimum distance between location updates
				this);

		// Register the listener with the Location Manager to receive GPS location updates
		locationManager.requestLocationUpdates(
				LocationManager.GPS_PROVIDER, 
				60000,	//1 min - minimum time interval between location updates
				50,		// 50 m - minimum distance between location updates
				this);
	}

	@Override
	public void deactivate() {
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
