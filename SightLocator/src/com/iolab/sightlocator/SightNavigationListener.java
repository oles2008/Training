package com.iolab.sightlocator;

import java.util.Collection;

/**
 * A listener interface for navigation between items.
 */
public interface SightNavigationListener { 
	
	/**
	 * Called when navigation to the indicated items is performed.
	 * 
	 * @param items the items to which navigation is performed
	 */
	void onNavigation(Collection<SightMarkerItem> items);
}
