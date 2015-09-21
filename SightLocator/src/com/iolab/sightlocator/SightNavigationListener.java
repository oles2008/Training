package com.iolab.sightlocator;

/**
 * A listener interface for navigation between items.
 */
public interface SightNavigationListener { 
	
	/**
	 * Called when navigation to the indicated item is performed.
	 * 
	 * @param item the item to which navigation is performed
	 */
	void onNavigation(SightMarkerItem item);
}
