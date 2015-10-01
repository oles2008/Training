package com.iolab.sightlocator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.annotation.TargetApi;
import android.app.ActionBar.OnNavigationListener;
import android.app.Fragment;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.iolab.sightlocator.Appl.ViewUpdateListener;
import com.iolab.sightlocator.OnUserLocationChangedListener.NewLocationUser;
import com.iolab.sightlocator.TouchEventListenerFrameLayout.OnMapTouchedListener;

public class SightsMapFragment extends Fragment implements 
											NewLocationUser, 
											ViewUpdateListener, 
											ClusterManager.OnClusterClickListener<SightMarkerItem>,
											ClusterManager.OnClusterItemClickListener<SightMarkerItem>,
											OnMarkerCategoryUpdateListener,
											SightNavigationListener {
	
	private GoogleMap gMap;
	private AbstractMap mMap;
	private LocationSource sightLocationSource;
	private boolean moveMapOnLocationUpdate = true;
	private ClusterManager<SightMarkerItem> clusterManager;
	private SightsRenderer sightsRenderer;

	private Set<SightMarkerItem> itemSet = new HashSet<SightMarkerItem>();
	
	private SelectedMarkerManager mSelectedMarkerManager;
	
	private long updateViewCallIndex=0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(savedInstanceState!=null){
			moveMapOnLocationUpdate = savedInstanceState.getBoolean("moveMapOnLocationUpdate", false);
			updateViewCallIndex = savedInstanceState.getLong("updateViewCallIndex", 0);
		}
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			gMap = ((MapFragment) getChildFragmentManager().findFragmentById(
					R.id.map)).getMap();
		} else {
			gMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map)).getMap();
		}
		mMap = new MapImplementationGoogle(this);

		clusterManager = new ClusterManager<SightMarkerItem>(getActivity(),
				gMap);
		mSelectedMarkerManager = new SelectedMarkerManager(getView(), gMap, savedInstanceState);
		sightsRenderer = new SightsRenderer(getActivity(), gMap, clusterManager);
		sightsRenderer.registerOnBeforeClusterRenderedListener(mSelectedMarkerManager);
		clusterManager.setRenderer(sightsRenderer);
		clusterManager.setAlgorithm(new SightsHierarchichalAlgorithm());
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
				mSelectedMarkerManager.removeSelectedItems();
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
				mSelectedMarkerManager.removeSelectedItems();
			}
		});
	}
	
	@Override
    public boolean onClusterClick(Cluster<SightMarkerItem> cluster) {
		moveMapOnLocationUpdate = false;
		Appl.notifyClusterClickUpdates(cluster);
		mSelectedMarkerManager.removeSelectedItems();
        return true;
    }
	
	@Override
    public boolean onClusterItemClick(SightMarkerItem clickedItem) {
		moveMapOnLocationUpdate = false;
		Appl.notifyClusterItemClickUpdates(clickedItem);
		mSelectedMarkerManager.selectItem(clickedItem, false);
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
		
		registerMapClickListener();
		registerMapLongClickListener();
		registerOnMapTouchedListener();
		registerOnMyLocationButtonClickListener();
		
		Appl.subscribeForNavigationUpdates(this);		
		Appl.subscribeForMarkerCategoryUpdates(this);
	}

	@Override
	public void onSaveInstanceState(Bundle args){
		super.onSaveInstanceState(args);
		args.putBoolean("moveMapOnLocationUpdate", moveMapOnLocationUpdate);
		args.putLong("updateViewCallIndex", updateViewCallIndex);
		mSelectedMarkerManager.saveSelectedItems(args);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		sightLocationSource.deactivate();
		Appl.unsubscribeFromViewUpdates(this);
		Appl.unsubscribeFromNavigationUpdates(this);
		Appl.unsubscribeFromMarkerCategoryUpdates(this);
	}
	
	@Override
	public void onUpdateView(Bundle bundle) {
		List<SightMarkerItem> sightMarkerItemList = bundle.getParcelableArrayList(Tags.MARKERS);
		
		if(sightMarkerItemList!=null){
			for(SightMarkerItem item: sightMarkerItemList){
				if(itemSet.add(item)){
					clusterManager.addItem(item);
				}
			}
			clusterManager.cluster();
			mSelectedMarkerManager.onItemsUpdated(sightMarkerItemList);
		}
	}
	
	/* **************************************************************************** */
    /* ************************ OnMarkerCategoryUpdateListener ******************** */
    /* **************************************************************************** */

	@Override
	public void onMarkerCategoryChosen() {
		// TODO Auto-generated method stub
		
	}
	
	/* **************************************************************************** */
    /* ************************ SightNavigationListener *************************** */
    /* **************************************************************************** */

	@Override
	public void onNavigation(SightMarkerItem item) {
		mMap.moveCameraTo(item);
		mSelectedMarkerManager.selectItem(item, true);
	}

}
