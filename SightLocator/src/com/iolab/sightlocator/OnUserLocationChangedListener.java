package com.iolab.sightlocator;

import android.location.Location;
import android.location.LocationManager;
import android.widget.Toast;

import com.google.android.gms.maps.LocationSource.OnLocationChangedListener;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving onUserLocationChanged events.
 * The class that is interested in processing a onUserLocationChanged
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addOnUserLocationChangedListener<code> method. When
 * the onUserLocationChanged event occurs, that object's appropriate
 * method is invoked.
 *
 * @see OnUserLocationChangedEvent
 */
public class OnUserLocationChangedListener implements OnLocationChangedListener{

	/**
	 * The Interface NewLocationUser.
	 */
	public interface NewLocationUser{
		
		/**
		 * Make use of new location.
		 *
		 * @param location the location
		 */
		void onUserLocationChanged(Location location);
	}
	
	/**
	 * Instantiates a new OnUserLocationChangedListener.
	 *
	 * @param newLocationUser the new location user
	 */
	public OnUserLocationChangedListener(NewLocationUser newLocationUser){
		this.newLocationUser = newLocationUser;
	}
	
	/** The object that will makeUseOfNewLocation(). */
	private NewLocationUser newLocationUser;
	
	/** The current best location. */
	private Location currentBestLocation;

	// Called when a new location is found by one of the location providers.
	@Override
	public void onLocationChanged(Location location) {
		if(isBetterLocation(location, currentBestLocation)){
			newLocationUser.onUserLocationChanged(location);
			currentBestLocation = location;
		}
		
	}
	
	/**
	 * Checks if is the new location is better than the current one and is the one that should be used.
	 *
	 * @param location the new location
	 * @param currentBestLocation the current best location
	 * @return true, if is the new location is better and should be used
	 */
	private boolean isBetterLocation(Location location, Location currentBestLocation){
		int significantDifference = 30000;
		if (currentBestLocation == null) {
	        // A new location is always better than no location
	        return true;
	    }

		//find out how much newer the current location is
		long timeDifference = location.getTime() - currentBestLocation.getTime();
		
		//if the new location is significantly newer, we should take it into account even if it's from a
		//less reliable source
		if(timeDifference>significantDifference){
			return true;
		}
		
		//if it's older, it's probably worse
		if(timeDifference<0){
			return false;
		}
		
		float accuracyDifference = location.getAccuracy() - currentBestLocation.getAccuracy();
		if(accuracyDifference>0){
			return true;
		}

		String newLocationProvider = location.getProvider();
		
		//GPS provider is better
		if(newLocationProvider.equals(LocationManager.GPS_PROVIDER) && (accuracyDifference > -30)){
			return true;
		}
		
		//in other cases, it's worse
		return false;

	}
}
