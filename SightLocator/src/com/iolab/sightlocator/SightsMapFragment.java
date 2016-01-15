package com.iolab.sightlocator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.annotation.TargetApi;
import android.app.ActionBar.OnNavigationListener;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.gms.maps.model.LatLngBounds;
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
	
	private static final String TAG = SightsMapFragment.class.getCanonicalName();
	
	private GoogleMap gMap;
	private AbstractMap mMap;
	private LocationSource sightLocationSource;
	private boolean mMoveMapOnLocationUpdate = true;
	private ClusterManager<SightMarkerItem> clusterManager;
	private SightsRenderer sightsRenderer;
	
	private Location mUserLocation;

	private Set<SightMarkerItem> itemSet = new HashSet<SightMarkerItem>();
	private Set<SightMarkerItem> mItemSetForGivenCategory = new HashSet<SightMarkerItem>();
	
	private SelectedMarkerManager mSelectedMarkerManager;
	
	private long updateViewCallIndex=0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(savedInstanceState!=null){
			mMoveMapOnLocationUpdate = savedInstanceState.getBoolean(Tags.MOVE_MAP_ON_LOCATION_UPDATE, false);
			updateViewCallIndex = savedInstanceState.getLong(Tags.VIEW_UPDATE_CALL_INDEX, 0);
		}
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			gMap = ((MapFragment) getChildFragmentManager()
										.findFragmentById(R.id.map))
						.getMap();
		} else {
			gMap = ((MapFragment) getFragmentManager()
										.findFragmentById(R.id.map))
						.getMap();
		}
		mMap = new MapImplementationGoogle(this);

		clusterManager = new ClusterManager<SightMarkerItem>(getActivity(),
				gMap);
		mSelectedMarkerManager = new SelectedMarkerManager(getView(), gMap, clusterManager, mItemSetForGivenCategory, savedInstanceState);
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
	 * Whether the map should move and zoom when user's location changes.
	 *
	 * @return true, if the map should move, false if only the dot showing the location
	 */
	public boolean shouldMoveMapOnUserLocationUpdate() {
		return mMoveMapOnLocationUpdate;
	}
	
	/**
	 * Disable map move on user location update.
	 */
	public void disableMapMoveOnUserLocationUpdate() {
		mMoveMapOnLocationUpdate = false;
	}
	
	/**
	 * Enable map move on user location update.
	 */
	public void enableMapMoveOnUserLocationUpdate() {
		mMoveMapOnLocationUpdate = true;
	}
	
	/**
	 * Whether the user's location should be shown on startup.
	 *
	 * @return true, if yes, false, if only default location should be shown
	 */
	public boolean shouldShowUserLocationOnStartup() {
		return true;
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
	public void onUserLocationChanged(Location location) {
		Log.d("MyLogs", "onUserLocationChanged");
		mUserLocation = location;
		Intent intent = new Intent(Appl.appContext, SightsIntentService.class);
		intent.putExtra(SightsIntentService.ACTION, new GetAppropriateZoomAction(location, 15));
		Appl.appContext.startService(intent);
		makeUseOfNewLocation(location, 15);
	}
	
	/**
	 * Updates the map camera position according to the new location.
	 *
	 * @param location the new location
	 * @param zoom the zoom level
	 */
	public void makeUseOfNewLocation(Location location, float zoom) {
		if(location == null) {
			Log.e(TAG, "makeUseOfNewLocation received null location");
			return;
		}
		LatLng newCoord = new LatLng(
				location.getLatitude(),
				location.getLongitude());
		
		if (shouldMoveMapOnUserLocationUpdate()) {
			gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newCoord, 15));
		}
	}

	private void registerMapClickListener() {
		gMap.setOnMapClickListener(new OnMapClickListener() {
			@Override
			public void onMapClick(LatLng arg0) {
				//the user wants to stay here
				disableMapMoveOnUserLocationUpdate();
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
				disableMapMoveOnUserLocationUpdate();
				Appl.notifyLongMapClickUpdates(arg0);
				mSelectedMarkerManager.removeSelectedItems();
			}
		});
	}
	
	@Override
    public boolean onClusterClick(Cluster<SightMarkerItem> cluster) {
		disableMapMoveOnUserLocationUpdate();
		Appl.notifyClusterClickUpdates(cluster);
		mSelectedMarkerManager.removeSelectedItems();
		mSelectedMarkerManager.selectItems(cluster.getItems());
        return true;
    }
	
	@Override
    public boolean onClusterItemClick(SightMarkerItem clickedItem) {
		disableMapMoveOnUserLocationUpdate();
		Appl.notifyClusterItemClickUpdates(clickedItem);
		mSelectedMarkerManager.selectItem(clickedItem);
        return true;
    }

	//this will disable automatic zooming to the user's location if the map has been touched
	private void registerOnMapTouchedListener() {
		((TouchEventListenerFrameLayout) getActivity().findViewById(R.id.map_fragment)).registerOnMapTouchListener(new OnMapTouchedListener() {
			
			@Override
			public void onMapTouched() {
				disableMapMoveOnUserLocationUpdate();
			}
		});
	}

	private void registerOnMyLocationButtonClickListener() {
		gMap.setOnMyLocationButtonClickListener(new OnMyLocationButtonClickListener() {
			@Override
			public boolean onMyLocationButtonClick() {
				//the user probably wants his location to be show and updated
				enableMapMoveOnUserLocationUpdate();
				
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
		args.putBoolean(Tags.MOVE_MAP_ON_LOCATION_UPDATE, mMoveMapOnLocationUpdate);
		args.putLong(Tags.VIEW_UPDATE_CALL_INDEX, updateViewCallIndex);
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
		ArrayList<Category> chosenCategories = CategoryUtils.getSelectedMarkerCategories();
		
		if(sightMarkerItemList!=null){
			for(SightMarkerItem item: sightMarkerItemList){
				addItemToMapIfCategoryIsChosen(item, chosenCategories);
			}
			clusterManager.cluster();
			mSelectedMarkerManager.onItemsUpdated(sightMarkerItemList);
		}
		
		if(bundle.containsKey(Tags.APPROPRIATE_ZOOM)) {
			float appropriateZoom = bundle.getFloat(Tags.APPROPRIATE_ZOOM);
			makeUseOfNewLocation(mUserLocation, appropriateZoom);
		}
	}

	private void addItemToMapIfCategoryIsChosen(SightMarkerItem item,
			ArrayList<Category> chosenCategories) {
		if (itemSet.add(item)) {
			if (CategoryUtils.isItemInCategories(chosenCategories, item)) {
				mItemSetForGivenCategory.add(item);
				clusterManager.addItem(item);
			}
		}
	}
	
	/* **************************************************************************** */
    /* ************************ OnMarkerCategoryUpdateListener ******************** */
    /* **************************************************************************** */

	@Override
	public void onMarkerCategoryChosen() {
		clusterManager.clearItems();
		//list of categories that has been selected by user
		ArrayList<Category> chosenCategories = CategoryUtils.getSelectedMarkerCategories();
		addFilteredItemsToMap(chosenCategories);
		mSelectedMarkerManager.reselectItemsAfterCategoryChange();;
	}

	private void addFilteredItemsToMap(ArrayList<Category> chosenCategories) {
		
		clusterManager.clearItems();
		mItemSetForGivenCategory.clear();
		
		//make only selected markers to be present in "visible" list
		for (SightMarkerItem item : itemSet){
			//add item to "visible" list if the item has "chosen" category
			for (Category chosenCategory : chosenCategories) {
				if (chosenCategory.isItemBelongsToThisCategory(item)){
					mItemSetForGivenCategory.add(item);
					clusterManager.addItem(item);
					break;
				}
			}
		}
		
		clusterManager.cluster();
	}
	
	/* **************************************************************************** */
    /* ************************ SightNavigationListener *************************** */
    /* **************************************************************************** */

	@Override
	public void onNavigation(Collection<SightMarkerItem> items) {
		moveToItemsAndSelectThem(items);
	}

	private void moveToItemsAndSelectThem(Collection<SightMarkerItem> items) {
		if (items != null && !items.isEmpty()) {
			if (items.size() == 1) {
				SightMarkerItem item = items.iterator().next();
				if (item.getPosition() != null) {
					mMap.moveCameraTo(item);
				}
			} else {
				int minDimension = Math.min(getView().getWidth(), getView().getHeight());
				mMap.moveCameraTo(items, minDimension/3);
			}
			mSelectedMarkerManager.selectItems(items);
		}
	}
}
