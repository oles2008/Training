package com.iolab.sightlocator;

import java.util.Collection;

import android.location.Location;

public interface AbstractMap {

	/**
	 * Moves camera to the indicated item.
	 * 
	 * @param item
	 *            the item to which the camera should move
	 */
	void moveCameraTo(SightMarkerItem item);

	/**
	 * Moves camera to the indicated items.
	 *
	 * @param items
	 *            the items to which the camera should move and show them all
	 * @param padding
	 *            the additional padding; if 0, some of the items will be on the
	 *            map's edge
	 */
	void moveCameraTo(Collection<SightMarkerItem> items, int padding);
	
	/**
	 * Moves camera to the include the indicated locations.
	 *
	 * @param locations
	 *            the {@link Location}s to be included
	 * @param padding
	 *            the additional padding; if 0, some of the items will be on the
	 *            map's edge
	 */
	public void moveCameraToLocations(Collection<Location> locations, int padding);
}
