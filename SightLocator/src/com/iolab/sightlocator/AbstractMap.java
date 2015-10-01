package com.iolab.sightlocator;

import java.util.Collection;

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
	 */
	void moveCameraTo(Collection<SightMarkerItem> items);
}