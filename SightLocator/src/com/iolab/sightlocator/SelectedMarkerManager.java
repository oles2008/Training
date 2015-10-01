package com.iolab.sightlocator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.iolab.sightlocator.SightsRenderer.OnBeforeClusterRenderedListener;

public class SelectedMarkerManager implements OnBeforeClusterRenderedListener {

	private static final int CLUSTER_ANIMATION_DURATION = 100;
	private static final int ITEM_RETRIEVAL_DURATION = 1500;
	private static final String KEY_CURRENT_SELECTED_ITEMS = "currentSelectedItems";
	private static final String KEY_CURRENT_SELECTED_ITEMS_CLUSTERED = "currentSelectedItemsClustered";

	private GoogleMap mGoogleMap;
	
	/** The root view that contains the {@link MapFragment} on which the markers are placed. */
	private View mRootView;

	/**
	 * Represents the current selected items.
	 */
	private ArrayList<SightMarkerItem> mCurrentSelectedItems;

	/**
	 * Keeps the link to the markers representing the selected items.
	 */
	private Map<SightMarkerItem, Marker> mCurrentSelectedMarkersMap = new HashMap<SightMarkerItem, Marker>();

	/**
	 * The list of currently selected items hidden in clusters.
	 */
	private ArrayList<SightMarkerItem> mCurrentSelectedItemsClustered;

	public SelectedMarkerManager(View rootView, GoogleMap gMap,
			Bundle savedInstanceState) {
		mRootView = rootView;
		mGoogleMap = gMap;
		if (savedInstanceState != null) {
			mCurrentSelectedItems = savedInstanceState
					.getParcelableArrayList(KEY_CURRENT_SELECTED_ITEMS);
			mCurrentSelectedItemsClustered = savedInstanceState
					.getParcelableArrayList(KEY_CURRENT_SELECTED_ITEMS_CLUSTERED);
		}
		if(mCurrentSelectedItems == null){
			mCurrentSelectedItems = new ArrayList<SightMarkerItem>();
		}
		if(mCurrentSelectedItemsClustered == null){
			mCurrentSelectedItemsClustered = new ArrayList<SightMarkerItem>();
		}
		for(SightMarkerItem savedSelectedItem: mCurrentSelectedItems){
			markSavedSelectedItem(savedSelectedItem);
		}
	}

	/**
	 * Should be called when saving instance state in order to save selected
	 * item's position.
	 * 
	 * @param savedInstanceState
	 *            the {@link Bundle} for saving the state
	 */
	public void saveSelectedItems(Bundle savedInstanceState) {
		if (mCurrentSelectedItems != null && !mCurrentSelectedItems.isEmpty()) {
			savedInstanceState.putParcelableArrayList(
					KEY_CURRENT_SELECTED_ITEMS, mCurrentSelectedItems);
			savedInstanceState.putParcelableArrayList(
					KEY_CURRENT_SELECTED_ITEMS_CLUSTERED,
					mCurrentSelectedItemsClustered);
		}
	}

	public void removeSelectedItems() {
		for (Marker selectedMarker : mCurrentSelectedMarkersMap.values()) {
			selectedMarker.remove();
		}
		mCurrentSelectedMarkersMap.clear();
		mCurrentSelectedItems.clear();
		mCurrentSelectedItemsClustered.clear();
	}

	/**
	 * Select item. Be sure to call {@code onItemsUpdated} when new items are
	 * added to the map.
	 *
	 * @param selectedItem
	 *            the selected item
	 * @param delayed
	 *            the delay
	 */
	public void selectItem(SightMarkerItem selectedItem, boolean delayed) {
		if (selectedItem != null) {
			removeSelectedItems();
			mCurrentSelectedItems.add(selectedItem);
			if (delayed) {
				addSelectedMarkerDelayed(selectedItem, ITEM_RETRIEVAL_DURATION);
			} else {
				addSelectedMarker(selectedItem);
			}
		}
	}
	
	public void onItemsUpdated(List<SightMarkerItem> newItems){
		//TODO
	}

	/* **************************************************************************** */
    /* *********************** OnBeforeClusterRenderedListener ******************** */
    /* **************************************************************************** */

	@Override
	public void onBeforeClusterRendered(Cluster<SightMarkerItem> cluster,
			MarkerOptions markerOptions) {
		if (!mCurrentSelectedItems.isEmpty()){
			Collection<SightMarkerItem> clusterItems = cluster.getItems();
			for(SightMarkerItem selectedItem: mCurrentSelectedItems){
				if(clusterItems.contains(selectedItem)){
					hideSelectedMarkerInCluster(selectedItem);
				}
			}
		
		}
	}

	@Override
	public void onBeforeClusterItemRendered(SightMarkerItem item,
			MarkerOptions markerOptions) {
		if(mCurrentSelectedItems.contains(item)){
			unclusterSelectedMarker(item);
		}
	}

	/* **************************************************************************** */
    /* ************************************* Utility API ************************** */
    /* **************************************************************************** */

	private Marker addSelectedMarker(SightMarkerItem selectedItem) {
		if(!mCurrentSelectedItems.contains(selectedItem)){
			throw new IllegalStateException("Cannot add selected marker for non-selected item");
		}
		Marker selectedMarker = mGoogleMap.addMarker(selectedItem.getMarkerOptions().icon(
				BitmapDescriptorFactory.fromResource(CategoryUtils
						.getCategorySelectedMarkerResId(selectedItem
								.getCategory()))));
		if(mCurrentSelectedMarkersMap.get(selectedItem)!=null){
			mCurrentSelectedMarkersMap.get(selectedItem).remove();
		}
		mCurrentSelectedMarkersMap.put(selectedItem, selectedMarker);
		return selectedMarker;
	}

	private void addSelectedMarkerDelayed(final SightMarkerItem item, int delay) {
		mRootView.postDelayed(new Runnable() {

			@Override
			public void run() {
				addSelectedMarker(item);
			}
		}, delay);
	}

	private void hideSelectedMarkerInCluster(SightMarkerItem selectedItem) {
		Marker selectedMarker = mCurrentSelectedMarkersMap.get(selectedItem);
		if (selectedMarker != null) {
			selectedMarker.remove();
			mCurrentSelectedMarkersMap.remove(selectedItem);
		}
		mCurrentSelectedItemsClustered.add(selectedItem);
	}

	private void unclusterSelectedMarker(SightMarkerItem item) {
		if (mCurrentSelectedItemsClustered.contains(item)) {
			//TODO replace the delay with onUpdate() listener
			addSelectedMarkerDelayed(item, CLUSTER_ANIMATION_DURATION);
			mCurrentSelectedItemsClustered.remove(item);
		}
	}
	
	private void markSavedSelectedItem(SightMarkerItem savedSelectedItem) {
		if (!mCurrentSelectedItemsClustered.contains(savedSelectedItem)) {
			addSelectedMarkerDelayed(savedSelectedItem,
					ITEM_RETRIEVAL_DURATION);
		}
	}
}
