package com.iolab.sightlocator;

import java.util.Collection;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Build;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class MapImplementationGoogle implements AbstractMap {
	
	private GoogleMap mGMap;
	
	/**
	 * Creates {@link MapImplementationGoogle}.
	 * 
	 * @param parentFragment
	 *            the {@link Fragment} that contains a Google {@link MapFragment}
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	public MapImplementationGoogle(Fragment parentFragment) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			mGMap = ((MapFragment) parentFragment.getChildFragmentManager().findFragmentById(
					R.id.map)).getMap();
		} else {
			mGMap = ((MapFragment) parentFragment.getFragmentManager().findFragmentById(
					R.id.map)).getMap();
		}
	}

	@Override
	public void moveCameraTo(SightMarkerItem item) {
		if(item.getPosition() != null){
			mGMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
					new LatLng(item.getLatitude(),
							item.getLongitude()), 15));
		}

	}

	@Override
	public void moveCameraTo(Collection<SightMarkerItem> items, int padding) {
		LatLngBounds bounds = getBoundsForItems(items);
		if (bounds != null) {
			mGMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds,
					padding));
		} else {
			// Possible behavior when there are no coordinates in the collection
		}
	}
	
	/* **************************************************************************** */
    /* ************************************* Utility API ************************** */
    /* **************************************************************************** */
	
	/**
	 * Gets the {@link LatLngBounds} for items.
	 *
	 * @param items
	 *            the items
	 * @return the bounds for items, if at least one has a position. Otherwise,
	 *         null is returned
	 */
	private LatLngBounds getBoundsForItems(Collection<SightMarkerItem> items) {
		LatLngBounds.Builder builder = new LatLngBounds.Builder();
		boolean hasPoints = false;
		for (SightMarkerItem item : items) {
			LatLng position = item.getPosition();
			if (position != null) {
				hasPoints = true;
				builder.include(position);
			}
		}
		if (hasPoints) {
			return builder.build();
		} else {
			return null;
		}
	}
}
