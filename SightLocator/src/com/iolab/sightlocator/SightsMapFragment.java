package com.iolab.sightlocator;

import android.app.Fragment;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.LocationSource.OnLocationChangedListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.iolab.sightlocator.OnUserLocationChangedListener.NewLocationUser;

public class SightsMapFragment extends Fragment implements OnMarkerClickListener, NewLocationUser{
	private GoogleMap gMap;
	private LocationSource sightLocationSource;

	private static final LatLng RAILWAY_STATION = new LatLng(49.839860, 23.993669);
	private static final LatLng STS_OLHA_AND_ELISABETH = new LatLng(49.8367019,24.0048451);
	private static final LatLng SOFTSERVE_OFFICE_4 = new LatLng(49.832786, 23.997022);

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
	private boolean zoomInToUsersLastLocation() {
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
	
	private void registerLocationListener() {

		// Create a listener that responds to location updates by calling makeUseOfNewLocation() when it
		//thinks it's necessary (since not all location updates should be taken into account), 
		//and activate this listener by sightLocationSource

		sightLocationSource = new SightLocationSource(getActivity());
		sightLocationSource.activate(new OnUserLocationChangedListener(this));
	}
	
	@Override
	public void makeUseOfNewLocation(Location location) {
		
		LatLng newCoord = new LatLng(
				location.getLatitude(),
				location.getLongitude());
		
		gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newCoord, 15));
	}
	
	/**
	 * Change text in text fragment to new one.
	 *
	 * @param newText the new text to be displayed
	 */
	private void changeTextFragment(String newText) {
		Fragment fragment = getFragmentManager()
				.findFragmentById(R.id.text_fragment);
		TextView textView = (TextView) fragment
				.getView()
				.findViewById(R.id.textView);
		textView.setText(newText);
	}

	public boolean onMarkerClick(final Marker marker) {
		String railwayStation = this.getString(R.string.railway_station_wiki);
		String softserveOffice4 = this.getString(R.string.softserve_office_4);
		String stsOlhaAndElisabeth = this.getString(R.string.sts_olha_and_elisabeth);
		
		if (marker.getPosition().equals(RAILWAY_STATION)) {
			marker.showInfoWindow();
			changeTextFragment(railwayStation);
			return true;
		}
		
		if (marker.getPosition().equals(SOFTSERVE_OFFICE_4)) {
			marker.showInfoWindow();
			changeTextFragment(softserveOffice4);
			return true;
		}
		
		if (marker.getPosition().equals(STS_OLHA_AND_ELISABETH)) {
			marker.showInfoWindow();
			changeTextFragment(stsOlhaAndElisabeth);
			return true;
		}

		return false;
	}
	
	public void addMarkers() {
		if (gMap != null) {
			gMap.addMarker(new MarkerOptions()
				.position(STS_OLHA_AND_ELISABETH)
				.title("Church of Sts. Olha and Elizabeth, Lviv")
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
			gMap.addMarker(new MarkerOptions()
				.position(RAILWAY_STATION)
				.title("Railway station, Lviv")
				.snippet("Snippet string")
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
			gMap.addMarker(new MarkerOptions()
				.position(SOFTSERVE_OFFICE_4)
				.title("Softserve office #4, Lviv")
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));			
		}
	}

	private void registerMapClickListener() {
		gMap.setOnMapClickListener(new OnMapClickListener() {
			@Override
			public void onMapClick(LatLng arg0) {
				String loremIpsum = getString(R.string.lorem_ipsum);
				changeTextFragment(loremIpsum);
			}
		});
	}

	private void addMarkersPositions() {
		MarkerOptions markersPositions = new MarkerOptions();
		markersPositions.position(RAILWAY_STATION);
		markersPositions.position(SOFTSERVE_OFFICE_4);
		markersPositions.position(STS_OLHA_AND_ELISABETH);
		gMap.addMarker(markersPositions);
	}
	
	@Override
	public void onStart() {

		super.onStart();
		
		gMap = ((MapFragment) getFragmentManager()
				.findFragmentById(R.id.map))
				.getMap();
		

		//here, we do not set sightLocationSource as the location source, because that instance of SightLocationSource  
		//is used for the marking the user's location and for zooming in to the user's location,
		//different instances of SightLocationSource are needed
		gMap.setLocationSource(new SightLocationSource(getActivity()));

		//this will show the user's location on the map; in this way we won't need to mark it ourselves
		gMap.setMyLocationEnabled(true);
		
		// Define a map listener that responds on map clicks and register it
		registerMapClickListener();

		// Register a marker listener to receive marker clicks updates
		gMap.setOnMarkerClickListener(this);
		
		//zooming in to the user's location so that the user doesn't have to press the Google-provided "Locate me" button
		zoomInToUsersLastLocation();
		
		// Define a listener that responds to location updates and register it
		registerLocationListener();

		// add markers LatLng positions
		addMarkersPositions();

		// add markers with markers details
		addMarkers();
	}
	
	@Override
	public void onStop() {
		super.onStop();
		sightLocationSource.deactivate();
	}
}
