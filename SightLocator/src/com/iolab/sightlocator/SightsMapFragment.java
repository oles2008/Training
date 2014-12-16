package com.iolab.sightlocator;

import android.app.Fragment;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.iolab.sightlocator.OnUserLocationChangedListener.NewLocationUser;

public class SightsMapFragment extends Fragment implements OnMarkerClickListener, NewLocationUser{
	private GoogleMap gMap;
	private LocationSource sightLocationSource;
	private boolean moveMapOnLocationUpdate = true;
	private boolean showToastToNavigateClickOnMap = true;

	private static final LatLng RAILWAY_STATION = new LatLng(49.839860, 23.993669);
	private static final LatLng STS_OLHA_AND_ELISABETH = new LatLng(49.8367019,24.0048451);
	private static final LatLng SOFTSERVE_OFFICE_4 = new LatLng(49.832786, 23.997022);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(savedInstanceState!=null){
			moveMapOnLocationUpdate = savedInstanceState.getBoolean("moveMapOnLocationUpdate", false);
			showToastToNavigateClickOnMap = savedInstanceState.getBoolean("showToastToNavigateClickOnMap", false);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		TouchEventListenerFrameLayout touchEventListenerFrameLayout = new TouchEventListenerFrameLayout(getActivity());
		touchEventListenerFrameLayout.addView(inflater.inflate(
				R.layout.map_fragment,
				container,
				false));

		return touchEventListenerFrameLayout;
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
		
		if (moveMapOnLocationUpdate) {
			gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newCoord, 15));
		}
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
		//the user wants to stay here
		moveMapOnLocationUpdate = false;
		
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
				.position(RAILWAY_STATION)
				.title("Railway station, Lviv")
				.snippet("Address")
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
			gMap.addMarker(new MarkerOptions()
				.position(SOFTSERVE_OFFICE_4)
				.title("Softserve office #4, Lviv")
				.snippet("Address")
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
			gMap.addMarker(new MarkerOptions()
				.position(STS_OLHA_AND_ELISABETH)
				.title("Church of Sts. Olha and Elizabeth, Lviv")
				.snippet("Address")
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
		}
	}

	private void addMarkersPositions() {
		MarkerOptions markersPositions = new MarkerOptions();
		markersPositions.position(RAILWAY_STATION);
		markersPositions.position(SOFTSERVE_OFFICE_4);
		markersPositions.position(STS_OLHA_AND_ELISABETH);
		gMap.addMarker(markersPositions);
	}
	
	private void registerMapClickListener() {
		gMap.setOnMapClickListener(new OnMapClickListener() {
			@Override
			public void onMapClick(LatLng arg0) {
				//the user wants to stay here
				moveMapOnLocationUpdate = false;
				showToastToNavigateClickOnMap = false;
				
				String loremIpsum = getString(R.string.lorem_ipsum);
				changeTextFragment(loremIpsum);
			}
		});
	}
	
	private void registerMapLongClickListener() {
		gMap.setOnMapLongClickListener(new OnMapLongClickListener() {
			@Override
			public void onMapLongClick(LatLng arg0) {
				//the user wants to stay here
				moveMapOnLocationUpdate = false;
				showToastToNavigateClickOnMap = false;
				
				String loremIpsum = getString(R.string.lorem_ipsum);
				changeTextFragment(loremIpsum);
			}
		});
	}
	
	private void registerInfoWindowClickListener(){
		gMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
			@Override
			public void onInfoWindowClick(Marker marker) {
				Toast toast = Toast.makeText(Appl.appContext, "onInfoWindowClick", Toast.LENGTH_SHORT);
				toast.show();
			}
		});
	}
	
	private void registerCameraChangeListener() {
		gMap.setOnCameraChangeListener(new OnCameraChangeListener() {
			@Override
			public void onCameraChange(CameraPosition position) {
				boolean mapIsTouched = ((TouchEventListenerFrameLayout) getActivity().findViewById(R.id.map_fragment)).mMapIsTouched;
				if (mapIsTouched && showToastToNavigateClickOnMap) {
					Toast toast = Toast
							.makeText(
									Appl.appContext,
									"To NAVIGATE away from your position, LONG CLICK on the map",
									Toast.LENGTH_SHORT);
					toast.show();
					showToastToNavigateClickOnMap = false;
				}
			}
		});
	}

	private void registerOnMyLocationButtonClickListener() {
		gMap.setOnMyLocationButtonClickListener(new OnMyLocationButtonClickListener() {
			@Override
			public boolean onMyLocationButtonClick() {
				if(!moveMapOnLocationUpdate){
					Toast toast = Toast.makeText(Appl.appContext, "To NAVIGATE away from your position again, LONG CLICK on the map", Toast.LENGTH_SHORT);
					toast.show();
				}
				
				//the user probably wants his location to be show and updated
				moveMapOnLocationUpdate = true;
				
				//this means that the location will be now shown and updated, 
				//so if the user wants to navigate away, they should perform long clock again
				showToastToNavigateClickOnMap = true;
				//returning false means that the primary function of the button -- showing the user's
				//location, should be performed
				return false;
			}
		});
	}

	@Override
	public void onResume() {

		super.onResume();
		
		//himnokod for debugging
		Appl.appContext = getActivity();
		
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
		//registerMapClickListener();

		// Register a marker listener to receive marker clicks updates
		gMap.setOnMarkerClickListener(this);
		
		//zooming in to the user's location so that the user doesn't have to press the Google-provided "Locate me" button
		//zoomInToUsersLastLocation();
		
		// Define a listener that responds to location updates and register it
		registerLocationListener();
		
		// define a listener that responds to clicks on markers Info Window
		registerInfoWindowClickListener();
		registerCameraChangeListener();
		registerMapClickListener();
		registerMapLongClickListener();
		registerOnMyLocationButtonClickListener();

		// add markers LatLng positions
		//addMarkersPositions();

		// add markers with markers details
		addMarkers();
	}
	
	@Override
	public void onSaveInstanceState(Bundle args){
		super.onSaveInstanceState(args);
		args.putBoolean("moveMapOnLocationUpdate", moveMapOnLocationUpdate);
		args.putBoolean("showToastToNavigateClickOnMap", showToastToNavigateClickOnMap);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		sightLocationSource.deactivate();
		
		//himnokod for debugging
		Appl.appContext=null;
	}

}
