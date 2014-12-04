package com.iolab.sightlocator;


import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class SightsMapFragment extends Fragment {
	private GoogleMap gMap;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View sightsmapFragment = inflater.inflate(
				R.layout.map_fragment,
				container,
				false);

		return sightsmapFragment;
	}
	
	/**
	 * Zoom in to user's last location during activity creation.
	 *
	 * @return true, if successful, false if the location could not be obtained
	 */
	public boolean zoomInToUsersLastLocation() {
		LocationManager locationManager = (LocationManager) getActivity()
				.getSystemService(Context.LOCATION_SERVICE);
		
		Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if(lastKnownLocation==null){
			lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
		}
		if(lastKnownLocation==null){
			lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		//zooming in to the user's location
		if (lastKnownLocation != null) {
			gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
					new LatLng(lastKnownLocation.getLatitude(),
							lastKnownLocation.getLongitude()), 15));
			return true;
		}else{
			return false;
		}
	}
	
	public void registerLocationListener() {
		// Acquire a reference to the system Location Manager
				LocationManager locationManager = (LocationManager) getActivity()
						.getSystemService(Context.LOCATION_SERVICE);
				
		// Define a listener that responds to location updates
				LocationListener locationListener = new LocationListener() {
					public void onLocationChanged(Location location) {
						// Called when a new location is found by the network location provider.
						makeUseOfNewLocation(location);
					}

					@SuppressLint("SimpleDateFormat")
					private void makeUseOfNewLocation(Location location) {
						
						LatLng newCoord = new LatLng(
								location.getLatitude(),
								location.getLongitude());
						
						gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newCoord, 15));
					}

					public void onStatusChanged(String provider, int status,
							Bundle extras) {
					}

					public void onProviderEnabled(String provider) {
					}

					public void onProviderDisabled(String provider) {
					}
				};

				// Register the listener with the Location Manager to receive NETWORK location updates
				locationManager.requestLocationUpdates(
						LocationManager.NETWORK_PROVIDER, 
						60000,	//1 min - minimum time interval between location updates
						50,		// 50 m - minimum distance between location updates
						locationListener);

				// Register the listener with the Location Manager to receive GPS location updates
				locationManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, 
						60000,	//1 min - minimum time interval between location updates
						50,		// 50 m - minimum distance between location updates
						locationListener);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
		
		gMap = ((MapFragment) getFragmentManager()
				.findFragmentById(R.id.map))
				.getMap();
		//this will show the user's location on the map; in this way we won't need to mark it ourselves
		gMap.setMyLocationEnabled(true);
		
		//zooming in to the user's location so that the user doesn't have to press the Google-provided "Locate me" button
		zoomInToUsersLastLocation();
		
		// Define a listener that responds to location updates and register it
		registerLocationListener();
		
		if (gMap != null) {
			gMap.addMarker(new MarkerOptions()
					.position(new LatLng(49.8367019,24.0048451))
					.title("Church of Sts. Olha and Elizabeth, Lviv")
					.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
		}
		
	}
}
