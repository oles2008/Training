package com.iolab.sightlocator;

import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.iolab.sightlocator.SightsRenderer.OnBeforeClusterRenderedListener;

public class SelectedMarkerManager implements OnBeforeClusterRenderedListener {

	private static final int CLUSTER_ANIMATION_DURATION = 100;
	private static final String KEY_CURRENT_SELECTED_ITEM = "currentSelectedItem";
	private static final String KEY_IS_CURRENT_SELECTED_ITEM_CLUSTERED = "isCurrentSelectedItemClustered";

	private GoogleMap mGoogleMap;
	private View mRootView;

	/**
	 * Represents the current selected item.
	 */
	private SightMarkerItem mCurrentSelectedItem;

	/**
	 * Keeps the link to the marker representing the selected item.
	 */
	private Marker mCurrentSelectedMarker;

	/**
	 * Indicates whether the currently selected item is hidden in a cluster.
	 */
	private boolean mCurrentSelectedMarkerClustered;

	public SelectedMarkerManager(GoogleMap gMap, Bundle savedInstanceState) {
		mGoogleMap = gMap;
		if (savedInstanceState != null) {
			mCurrentSelectedItem = savedInstanceState
					.getParcelable(KEY_CURRENT_SELECTED_ITEM);
			if (mCurrentSelectedItem != null) {
				mCurrentSelectedMarkerClustered = savedInstanceState
						.getBoolean(KEY_IS_CURRENT_SELECTED_ITEM_CLUSTERED,
								false);
				if (!mCurrentSelectedMarkerClustered) {
					mCurrentSelectedMarker = addSelectedMarker(mCurrentSelectedItem);
				}
			}
		}
	}

	/**
	 * Should be called when saving instance state in order to save selected item's position.
	 * 
	 * @param savedInstanceState the {@link Bundle} for saving the state
	 */
	public void saveSelectedItem(Bundle savedInstanceState) {
		if (mCurrentSelectedItem != null) {
			savedInstanceState.putParcelable(KEY_CURRENT_SELECTED_ITEM, mCurrentSelectedItem);
			savedInstanceState.putBoolean(KEY_IS_CURRENT_SELECTED_ITEM_CLUSTERED,
					mCurrentSelectedMarkerClustered);
		}
	}

	public void removeSelectedItem() {
		if (mCurrentSelectedMarker != null) {
			mCurrentSelectedMarker.remove();
			mCurrentSelectedMarker = null;
			mCurrentSelectedItem = null;
			mCurrentSelectedMarkerClustered = false;
		}
	}

	public void selectItem(SightMarkerItem selectedItem) {
		if (selectedItem != null) {
			removeSelectedItem();
			mCurrentSelectedMarker = addSelectedMarker(selectedItem);
			mCurrentSelectedItem = selectedItem;
		}
	}

	/* **************************************************************************** */
	/*
	 * *********************** OnBeforeClusterRenderedListener
	 * ********************
	 */
	/* **************************************************************************** */

	@Override
	public void onBeforeClusterRendered(Cluster<SightMarkerItem> cluster,
			MarkerOptions markerOptions) {
		if ((mCurrentSelectedItem != null)
				&& cluster.getItems().contains(mCurrentSelectedItem)) {
			hideSelectedMarkerInCluster();
		}
	}

	@Override
	public void onBeforeClusterItemRendered(SightMarkerItem item,
			MarkerOptions markerOptions) {
		unclusterSelectedMarker();
	}

	/* **************************************************************************** */
	/*
	 * ****************************** Utility API
	 * *********************************
	 */
	/* **************************************************************************** */

	private Marker addSelectedMarker(SightMarkerItem selectedItem) {
		return mGoogleMap.addMarker(selectedItem.getMarkerOptions().icon(
				BitmapDescriptorFactory.fromResource(CategoryUtils
						.getCategorySelectedMarkerResId(selectedItem
								.getCategory()))));
	}

	private void hideSelectedMarkerInCluster() {
		if (mCurrentSelectedMarker != null) {
			mCurrentSelectedMarker.remove();
			mCurrentSelectedMarkerClustered = true;
		}
	}

	private void unclusterSelectedMarker() {
		if ((mCurrentSelectedItem != null) && !mCurrentSelectedMarkerClustered) {
			mRootView.postDelayed(new Runnable() {

				@Override
				public void run() {
					if (mCurrentSelectedMarkerClustered
							&& (mCurrentSelectedMarker != null)) {
						mCurrentSelectedMarkerClustered = true;
						mCurrentSelectedMarker = addSelectedMarker(mCurrentSelectedItem);
					}
				}
			}, CLUSTER_ANIMATION_DURATION);

		}

	}
}
