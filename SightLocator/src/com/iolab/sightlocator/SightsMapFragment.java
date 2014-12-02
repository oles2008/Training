package com.iolab.sightlocator;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import android.view.ViewGroup;

public class SightsMapFragment extends Fragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View sightsmapFragment = inflater.inflate(R.layout.map_fragment,
				container, false);

		return sightsmapFragment;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		LocationManager locationManager = (LocationManager) getActivity()
				.getSystemService(Context.LOCATION_SERVICE);
		// locationManager.req(LocationManager.NETWORK_PROVIDER, 0,0,)
		Location initialLocation = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (initialLocation == null) {
			initialLocation = locationManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		GoogleMap gMap = ((MapFragment) getFragmentManager().findFragmentById(
				R.id.map)).getMap();
		if ((gMap != null) && (initialLocation != null)) {
			LatLng initialCoord = new LatLng(initialLocation.getLatitude(),
					initialLocation.getLongitude());

			gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialCoord, 15));
			gMap.addMarker(new MarkerOptions()
					.position(initialCoord)
					.title("You are here")
					.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
			
			gMap.addMarker(new MarkerOptions()
		    .position(new LatLng(49.8367019,24.0048451))
		    .title("Church of Sts. Olha and Elizabeth, Lviv")
			.icon(BitmapDescriptorFactory
					.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

		}
	}
	
	
}
