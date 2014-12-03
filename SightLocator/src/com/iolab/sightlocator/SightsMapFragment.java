package com.iolab.sightlocator;

import java.text.SimpleDateFormat;
import java.util.Calendar;

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
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
		
		gMap = ((MapFragment) getFragmentManager()
				.findFragmentById(R.id.map))
				.getMap();
		
		// Acquire a reference to the system Location Manager
		LocationManager locationManager = (LocationManager) getActivity()
				.getSystemService(Context.LOCATION_SERVICE);
		
		gMap.setMyLocationEnabled(true);

		// Define a listener that responds to location updates
		LocationListener locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				// Called when a new location is found by the network location provider.
				//makeUseOfNewLocation(location);
			}

			@SuppressLint("SimpleDateFormat")
			private void makeUseOfNewLocation(Location location) {
				Calendar cal = Calendar.getInstance();
				SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
				
				LatLng newCoord = new LatLng(
						location.getLatitude(),
						location.getLongitude());
				
				gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newCoord, 15));
				
				gMap.addMarker(new MarkerOptions()
						.position(newCoord)
						.title("You are here at " + sdf.format(cal.getTime()))
						.icon(BitmapDescriptorFactory
								.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
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
		
		if (gMap != null) {
			gMap.addMarker(new MarkerOptions()
					.position(new LatLng(49.8367019,24.0048451))
					.title("Church of Sts. Olha and Elizabeth, Lviv")
					.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
		}
		
	}
}