package com.iolab.sightlocator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.MarkerManager.Collection;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.ClusterManager.OnClusterClickListener;
import com.google.maps.android.clustering.ClusterManager.OnClusterItemClickListener;
import com.iolab.sightlocator.Appl.ViewUpdateListener;
import com.iolab.sightlocator.OnUserLocationChangedListener.NewLocationUser;
import com.iolab.sightlocator.TouchEventListenerFrameLayout.OnMapTouchedListener;

public class SightsMapFragment extends Fragment implements 
											NewLocationUser, 
											ViewUpdateListener, 
											ClusterManager.OnClusterClickListener<SightMarkerItem>,
											ClusterManager.OnClusterItemClickListener<SightMarkerItem> {
	private GoogleMap gMap;
	private LocationSource sightLocationSource;
	private boolean moveMapOnLocationUpdate = true;
	private ClusterManager<SightMarkerItem> clusterManager;
	private SightsRenderer sightsRenderer;

	private List<Marker> markerList = new ArrayList<Marker>();
	private Set<SightMarkerItem> itemSet = new HashSet<SightMarkerItem>();
	public Marker currentSelectedMarker;
	
	private long updateViewCallIndex=0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(savedInstanceState!=null){
			moveMapOnLocationUpdate = savedInstanceState.getBoolean("moveMapOnLocationUpdate", false);
			updateViewCallIndex = savedInstanceState.getLong("updateViewCallIndex", 0);
		}
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		gMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();

		clusterManager = new ClusterManager<SightMarkerItem>(getActivity(),
				gMap);
		sightsRenderer = new SightsRenderer(getActivity(), gMap, clusterManager);
		clusterManager.setRenderer(sightsRenderer);
		//clusterManager.setAlgorithm(new SightsHierarchichalAlgorithm());

		if (savedInstanceState != null) {
			SightMarkerItem selectedItem = savedInstanceState
					.getParcelable("currentSelectedItem");
			if (selectedItem != null) {
				itemSet.add(selectedItem);
				clusterManager.addItem(selectedItem);
				clusterManager.cluster();
				currentSelectedMarker = gMap
						.addMarker(selectedItem
								.getMarkerOptions()
								.icon(BitmapDescriptorFactory
										.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
			}
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

		sightLocationSource = new SightsLocationSource(getActivity());
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

	private void registerMapClickListener() {
		gMap.setOnMapClickListener(new OnMapClickListener() {
			@Override
			public void onMapClick(LatLng arg0) {
				//the user wants to stay here
				moveMapOnLocationUpdate = false;
				Appl.notifyMapClickUpdates(arg0);
				if(currentSelectedMarker!=null){
					currentSelectedMarker.remove();
					currentSelectedMarker=null;
				}
			}
		});
	}
	
	private void registerMapLongClickListener() {
		gMap.setOnMapLongClickListener(new OnMapLongClickListener() {
			@Override
			public void onMapLongClick(LatLng arg0) {
				//the user wants to stay here
				moveMapOnLocationUpdate = false;
				
				Appl.notifyLongMapClickUpdates(arg0);
				if(currentSelectedMarker!=null){
					currentSelectedMarker.remove();
					currentSelectedMarker=null;
				}
			}
		});
	}
	
	private void registerInfoWindowClickListener(){
		gMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
			@Override
			public void onInfoWindowClick(Marker marker) {
				Toast toast = Toast
						.makeText(
								Appl.appContext,
								"onInfoWindowClick",
								Toast.LENGTH_SHORT);
				toast.show();
			}
		});
	}
	
	private void registerCameraChangeListener() {
		gMap.setOnCameraChangeListener(new OnCameraChangeListener() {
			@Override
			public void onCameraChange(CameraPosition position) {
				LatLngBounds currentMapBounds = gMap.getProjection().getVisibleRegion().latLngBounds;
				Intent intent = new Intent(getActivity(), SightsIntentService.class);
				intent.putExtra(SightsIntentService.ACTION, new GetMarkersOnCameraUpdateAction(currentMapBounds, ++updateViewCallIndex));
				intent.putExtra(Tags.ON_CAMERA_CHANGE_CALL_INDEX, updateViewCallIndex);
				getActivity().startService(intent);
			}
		});
	}

	@Deprecated
	private void registerMarkerClickListener() {
		gMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			
			@Override
			public boolean onMarkerClick(Marker marker) {
//				Log.d("MSG", "registerMarkerClickListener>onMarkerClick, marker " + marker.getTitle());

				for(OnMarkerClickListener listener: Appl.onMarkerClickListeners){
					listener.onMarkerClick(marker);
				}

				moveMapOnLocationUpdate = false;
				marker.showInfoWindow();
				
				return true;
			}
		});
	}
	
	@Override
    public boolean onClusterClick(Cluster<SightMarkerItem> cluster) {
		moveMapOnLocationUpdate = false;
		Appl.notifyClusterClickUpdates(cluster);
        
        return true;
    }
	
	@Override
    public boolean onClusterItemClick(SightMarkerItem clickedItem) {
		moveMapOnLocationUpdate = false;
		Appl.notifyClusterItemClickUpdates(clickedItem);
		if(currentSelectedMarker!=null){
			if(clickedItem.equals(new SightMarkerItem(currentSelectedMarker))){
				return true;
			}
			//currentSelectedMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
			currentSelectedMarker.remove();
		}
		currentSelectedMarker = gMap.addMarker(clickedItem.getMarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
//		for(Marker marker: clusterManager.getMarkerCollection().getMarkers()){
//			Log.d("MyLogs", "    "+marker.getId()+"  "+marker.getTitle());
//		}
//		for(Marker marker: clusterManager.getMarkerCollection().getMarkers()){
//			SightMarkerItem item = new SightMarkerItem(marker);
//			if(item.equals(clickedItem)){
//				if(currentSelectedMarker!=null){
//					if(item.equals(new SightMarkerItem(currentSelectedMarker))){
//						return true;
//					}
//					//currentSelectedMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
//					currentSelectedMarker.remove();
//				}
//				currentSelectedMarker = marker;
//				currentSelectedMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
//				break;
//			}
//		}
        return true;
    }

	//this will disable automatic zooming to the user's location if the map has been touched
	private void registerOnMapTouchedListener() {
		((TouchEventListenerFrameLayout) getActivity().findViewById(R.id.map_fragment)).registerOnMapTouchListener(new OnMapTouchedListener() {
			
			@Override
			public void onMapTouched() {
				moveMapOnLocationUpdate = false;
			}
		});
	}

	private void registerOnMyLocationButtonClickListener() {
		gMap.setOnMyLocationButtonClickListener(new OnMyLocationButtonClickListener() {
			@Override
			public boolean onMyLocationButtonClick() {
				//the user probably wants his location to be show and updated
				moveMapOnLocationUpdate = true;
				
				//this means that the location will be now shown and updated, 
				//so if the user wants to navigate away, they should perform long clock again
				//showToastToNavigateClickOnMap = true;
				//returning false means that the primary function of the button -- showing the user's
				//location, should be performed
				return false;
			}
		});
	}


	@Override
	public void onResume() {

		super.onResume();
		
		Appl.subscribeForViewUpdates(this);

		//here, we do not set sightLocationSource as the location source, because that instance of SightLocationSource  
		//is used for the marking the user's location and for zooming in to the user's location,
		//different instances of SightLocationSource are needed
		gMap.setLocationSource(new SightsLocationSource(getActivity()));

		//this will show the user's location on the map; in this way we won't need to mark it ourselves
		gMap.setMyLocationEnabled(true);
		
		gMap.setOnCameraChangeListener(clusterManager);
		gMap.setOnMarkerClickListener(clusterManager);
		
		clusterManager.setOnClusterItemClickListener(this);
		clusterManager.setOnClusterClickListener(this);
		
		// Define a map listener that responds on map clicks and register it
		//registerMapClickListener();

		// Register a marker listener to receive marker clicks updates
		//registerMarkerClickListener();
		
		//zooming in to the user's location so that the user doesn't have to press the Google-provided "Locate me" button
		//zoomInToUsersLastLocation();
		
		// Define a listener that responds to location updates and register it
		registerLocationListener();
		
		// define a listener that responds to clicks on markers Info Window
		registerInfoWindowClickListener();
		registerMapClickListener();
		registerMapLongClickListener();
		registerOnMapTouchedListener();
		registerOnMyLocationButtonClickListener();

		//for debugging
		//Log.d("MyLogs", "DBhelper null: "+(Appl.sightsDatabaseOpenHelper == null));
	}

	@Override
	public void onSaveInstanceState(Bundle args){
		super.onSaveInstanceState(args);
		args.putBoolean("moveMapOnLocationUpdate", moveMapOnLocationUpdate);
		args.putLong("updateViewCallIndex", updateViewCallIndex);
		if(currentSelectedMarker!=null){
			args.putParcelable("currentSelectedItem", new SightMarkerItem(currentSelectedMarker));
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		sightLocationSource.deactivate();
		Appl.subscribeForViewUpdates(this);
	}

//	@Override
//	public void onUpdateView(Bundle bundle) {
//		List<MarkerOptions> markerOptionsList = bundle.getParcelableArrayList(Tags.MARKERS);
//		//Log.d("MyLogs", "onUpdateView: updateViewIndex: "+updateViewCallIndex+" , from Bundle: "+bundle.getLong(Tags.ON_CAMERA_CHANGE_CALL_INDEX));
//		
//		if(markerOptionsList!=null && bundle.getLong(Tags.ON_CAMERA_CHANGE_CALL_INDEX)==updateViewCallIndex){
//			for(Marker marker: markerList){
//				marker.remove();
//			}
//			markerList.clear();
//			for(MarkerOptions markerOptions: markerOptionsList){
//				markerList.add(gMap.addMarker(markerOptions));
//			}
//		}
//	}
	
	@Override
	public void onUpdateView(Bundle bundle) {
		List<MarkerOptions> markerOptionsList = bundle.getParcelableArrayList(Tags.MARKERS);
		//Log.d("MyLogs", "onUpdateView: updateViewIndex: "+updateViewCallIndex+" , from Bundle: "+bundle.getLong(Tags.ON_CAMERA_CHANGE_CALL_INDEX));
		
		if(markerOptionsList!=null){
			for(MarkerOptions markerOptions: markerOptionsList){
				SightMarkerItem item = new SightMarkerItem(markerOptions);
				if(itemSet.add(item)){
					clusterManager.addItem(item);
				}
			}
			clusterManager.cluster();
		}
		if(currentSelectedMarker!=null){
			SightMarkerItem selectedItem = new SightMarkerItem(currentSelectedMarker);
			currentSelectedMarker.remove();
			currentSelectedMarker = gMap
					.addMarker(selectedItem
							.getMarkerOptions()
							.icon(BitmapDescriptorFactory
									.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
		}
	}

}
