package com.iolab.sightlocator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.os.Bundle;
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
	private static final int ITEM_ADDITION_DURATION = 100;
	private static final String KEY_CURRENT_SELECTED_ITEMS = "currentSelectedItems";

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
	 * The items currently added on the map. Refers to the same object in
	 * {@link SightsMapFragment}.
	 */
	private Set<SightMarkerItem> mItemsCurrentlyOnMap;

	/**
	 * Instantiates a new {@link SelectedMarkerManager}.
	 *
	 * @param rootView            the root view that contains the {@link MapFragment} on which
	 *            the markers are placed
	 * @param gMap            the map instance
	 * @param itemsCurrentlyOnMap            the items currently added on the map. Refers to the same
	 *            object in {@link SightsMapFragment}. Should be non-null,
	 *            otherwise {@link IllegalArgumentException} will be thrown
	 * @param savedInstanceState            the saved instance state
	 * @throws IllegalArgumentException if {@code itemsCurrentlyOnMap} is null
	 */
	public SelectedMarkerManager(View rootView, GoogleMap gMap, Set<SightMarkerItem> itemsCurrentlyOnMap,
			Bundle savedInstanceState) throws IllegalArgumentException {
		mRootView = rootView;
		mGoogleMap = gMap;
		if(itemsCurrentlyOnMap != null){
			mItemsCurrentlyOnMap = itemsCurrentlyOnMap;
		} else {
			throw new IllegalArgumentException("The set of itemsCurrentlyOnMap cannot be null!");
		}
		if (savedInstanceState != null) {
			mCurrentSelectedItems = savedInstanceState
					.getParcelableArrayList(KEY_CURRENT_SELECTED_ITEMS);
		}
		if(mCurrentSelectedItems == null){
			mCurrentSelectedItems = new ArrayList<SightMarkerItem>();
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
		}
	}

	public void removeSelectedItems() {
		for (Marker selectedMarker : mCurrentSelectedMarkersMap.values()) {
			selectedMarker.remove();
		}
		mCurrentSelectedMarkersMap.clear();
		mCurrentSelectedItems.clear();
	}

	/**
	 * Select item. Be sure to call {@code onItemsUpdated} when new items are
	 * added to the map.
	 *
	 * @param selectedItem            the selected item
	 * @param delay the delay
	 * @param removeSelected whether currently selected items should be removed
	 */
	public void selectItem(SightMarkerItem selectedItem, int delay, boolean removeSelected) {
		if (selectedItem != null) {
			SightMarkerItem real = getItemFromMapForSelectedItem(selectedItem, mItemsCurrentlyOnMap);
			if(real != null){
				selectedItem = real;
			}
			if(removeSelected){
				removeSelectedItems();
			}
			mCurrentSelectedItems.add(selectedItem);
			if (!selectedItem.isClustered()) {
				if (delay > 0) {
					addSelectedMarkerDelayed(selectedItem, delay);
				} else {
					addSelectedMarker(selectedItem);
				}
			} else {
				Marker existingMarker = getExistingSelectedMakerForCluster(selectedItem.getCluster());
				if(existingMarker != null){
					mCurrentSelectedMarkersMap.put(selectedItem, existingMarker);
				} else {
					if (delay > 0) {
						addSelectedClusterDelayed(selectedItem.getCluster(),
								delay);
					} else {
						addSelectedCluster(selectedItem.getCluster());
					}
				}
			}
		}
	}
	
	/**
	 * Select item, de-select currently selected. Be sure to call {@code onItemsUpdated} when new items are
	 * added to the map.
	 *
	 * @param items
	 *            the items to be selected
	 */
	public void selectItems(Collection<SightMarkerItem> items){
		removeSelectedItems();
		if(items != null){
			for(SightMarkerItem item: items){
				selectItem(item, 0, false);
			}
		}
	}
	
	/**
	 * Select item, de-select currently selected. Be sure to call {@code onItemsUpdated} when new items are
	 * added to the map.
	 *
	 * @param selectedItem
	 *            the item to be selected
	 */
	public void selectItem(SightMarkerItem selectedItem){
		selectItem(selectedItem, 0, true);
	}
	
	/**
	 * Should be called when new items are added to the map. If not called,
	 * items that should be selected but were added to the map later, may not be
	 * marker as selected.
	 *
	 * @param newItems
	 *            the new items
	 */
	public void onItemsUpdated(List<SightMarkerItem> newItems) {
		substituteSelectedItemsWithItemsFromMap(mCurrentSelectedItems, mItemsCurrentlyOnMap);
	}

	/* **************************************************************************** */
    /* *********************** OnBeforeClusterRenderedListener ******************** */
    /* **************************************************************************** */

	@Override
	public void onBeforeClusterRendered(Cluster<SightMarkerItem> cluster,
			MarkerOptions markerOptions) {
		if (containsSelectedItems(cluster.getItems())) {
			addSelectedClusterDelayed(cluster, CLUSTER_ANIMATION_DURATION);
		}
	}

	@Override
	public void onBeforeClusterItemRendered(SightMarkerItem item,
			MarkerOptions markerOptions) {
		if (mCurrentSelectedItems.contains(item) && !item.isClustered()) {
			addSelectedMarkerDelayed(item, CLUSTER_ANIMATION_DURATION);
		}
	}

	/* **************************************************************************** */
    /* ************************************* Utility API ************************** */
    /* **************************************************************************** */

	/**
	 * Adds a selected marker for an item which is considered selected. The
	 * selected marker is added immediately if the item is already on the map.
	 * Otherwise, it is added as soon as the item is added.
	 *
	 * @param selectedItem
	 *            the selected item
	 * @return the marker on the map, if it was added immediately, otherwise
	 *         returns {@code null}
	 * @throws IllegalStateException
	 *             if the item is not found among the selected items
	 */
	private Marker addSelectedMarker(SightMarkerItem selectedItem)
			throws IllegalStateException {
		if (!mCurrentSelectedItems.contains(selectedItem)) {
			throw new IllegalStateException(
					"Cannot add selected marker for non-selected item");
		}
		if (mCurrentSelectedMarkersMap.get(selectedItem) != null) {
			mCurrentSelectedMarkersMap.get(selectedItem).remove();
		}
		if (mItemsCurrentlyOnMap.contains(selectedItem)) {
			Marker selectedMarker = mGoogleMap.addMarker(selectedItem
					.getMarkerOptions()
					.icon(BitmapDescriptorFactory.fromResource(CategoryUtils
							.getCategorySelectedMarkerResId(selectedItem
									.getCategory()))));
			mCurrentSelectedMarkersMap.put(selectedItem, selectedMarker);
			return selectedMarker;
		} else {
			return null;
		}
	}

	private void addSelectedMarkerDelayed(final SightMarkerItem item, int delay) {
		mRootView.postDelayed(new Runnable() {

			@Override
			public void run() {
				addSelectedMarker(item);
			}
		}, delay);
	}
	
	/**
	 * Adds a selected marker for a cluster which is considered selected.
	 *
	 * @param selectedCluster
	 *            the selected cluster
	 * @return the marker on the map, if it was added immediately, otherwise
	 *         returns {@code null}
	 * @throws IllegalStateException
	 *             if the item is not found among the selected items
	 */
	private Marker addSelectedCluster(Cluster<SightMarkerItem> selectedCluster)
			throws IllegalStateException {
		Collection<SightMarkerItem> selectedItemsFromCluster = new HashSet<SightMarkerItem>(mCurrentSelectedItems);
		selectedItemsFromCluster.retainAll(selectedCluster.getItems());
		if (selectedItemsFromCluster.isEmpty()) {
			throw new IllegalStateException(
					"Cannot add selected marker for a cluster without selected items");
		}
		if (mItemsCurrentlyOnMap.containsAll(selectedItemsFromCluster)) {
			Marker selectedMarker = mGoogleMap.addMarker(new MarkerOptions()
					.position(selectedCluster.getPosition())
					.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
			for(SightMarkerItem selectedItem: selectedItemsFromCluster){
				if (mCurrentSelectedMarkersMap.get(selectedItem) != null) {
					mCurrentSelectedMarkersMap.get(selectedItem).remove();
				}
				mCurrentSelectedMarkersMap.put(selectedItem, selectedMarker);
			}
			return selectedMarker;
		} else {
			return null;
		}
	}

	private void addSelectedClusterDelayed(final Cluster<SightMarkerItem> cluster, int delay) {
		mRootView.postDelayed(new Runnable() {

			@Override
			public void run() {
				addSelectedCluster(cluster);
			}
		}, delay);
	}
	
	private Marker getExistingSelectedMakerForCluster(Cluster<SightMarkerItem> cluster){
		for(SightMarkerItem item: cluster.getItems()){
			if(mCurrentSelectedMarkersMap.get(item) != null){
				return mCurrentSelectedMarkersMap.get(item);
			}
		}
		return null;
	}
	
	private boolean containsSelectedItems(Collection<SightMarkerItem> collection) {
		if (collection == null || mCurrentSelectedItems == null) {
			return false;
		}
		for (SightMarkerItem selectedItem : mCurrentSelectedItems) {
			if (collection.contains(selectedItem)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * For the given item, finds its equal instance used on the map.
	 *
	 * @param selectedItem
	 *            the selected item
	 * @param realItems
	 *            the real items
	 * @return the item from map for selected item if its present among the
	 *         {@code realItems}, {@code null} otherwise
	 */
	private SightMarkerItem getItemFromMapForSelectedItem(
			SightMarkerItem selectedItem, Collection<SightMarkerItem> realItems) {
		for (SightMarkerItem real : realItems) {
			if (selectedItem.equals(real)) {
				return real;
			}
		}
		return null;
	}

	/**
	 * Substitute selected items with the equal instances used on the map.
	 *
	 * @param selectedItems
	 *            the selected items
	 * @param realItems
	 *            the real items
	 */
	private void substituteSelectedItemsWithItemsFromMap(
			Collection<SightMarkerItem> selectedItems,
			Collection<SightMarkerItem> realItems) {
		Map<SightMarkerItem, SightMarkerItem> selectedToReal = new HashMap<SightMarkerItem, SightMarkerItem>();
		for (SightMarkerItem selected : selectedItems) {
			SightMarkerItem real = getItemFromMapForSelectedItem(selected,
					realItems);
			if (real != null) {
				selectedToReal.put(selected, real);
			}
		}
		selectedItems.removeAll(selectedToReal.keySet());
		selectedItems.addAll(selectedToReal.values());
	}
}
