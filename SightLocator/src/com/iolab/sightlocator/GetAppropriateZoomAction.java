package com.iolab.sightlocator;

import static com.iolab.sightlocator.SightsDatabaseOpenHelper.COLUMNS_LOCATION_LEVEL;
import static com.iolab.sightlocator.SightsDatabaseOpenHelper.COLUMN_ID;
import static com.iolab.sightlocator.SightsDatabaseOpenHelper.COLUMN_LATITUDE;
import static com.iolab.sightlocator.SightsDatabaseOpenHelper.COLUMN_LONGITUDE;
import static com.iolab.sightlocator.SightsDatabaseOpenHelper.TABLE_NAME;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.util.Pair;

/**
 * The class encapsulating the algorithm for calculating the appropriate zoom level for given user location.
 */
public class GetAppropriateZoomAction implements ServiceAction, Parcelable {
	
	/**
	 * If the distance from the user's location to the group item is not larger
	 * then this group's width multiplied by this contant.
	 */
	private final static int DISTANCE_TO_AREA_WIDTH_FACTOR = 3;
	
	private Location mLocation;
	private float mInitialZoom;
	
	//not to be saved in Parcel
    private Location mClosestLocation;
    private int mClosestLocationId;
    private float mDistanceToClosestLocation;
	
	public GetAppropriateZoomAction(Location location, float initialZoom) {
		mLocation = location;
		mInitialZoom = initialZoom;
	}
	
	/* **************************************************************************** */
    /* ******************************* Parcelable ********************************* */
    /* **************************************************************************** */

	public GetAppropriateZoomAction(Parcel in) {
		mLocation = in.readParcelable(Location.class.getClassLoader());
		mInitialZoom = in.readFloat();
	}

	@Override
	public int describeContents() {
		// Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(mLocation, flags);
		dest.writeFloat(mInitialZoom);
	}
	
	public static final Parcelable.Creator<GetAppropriateZoomAction> CREATOR = new Parcelable.Creator<GetAppropriateZoomAction>() {
		public GetAppropriateZoomAction createFromParcel(Parcel in) {
			return new GetAppropriateZoomAction(in);
		}

		public GetAppropriateZoomAction[] newArray(int size) {
			return new GetAppropriateZoomAction[size];
		}
	};
	
	/* **************************************************************************** */
    /* *************************** ServiceAction ********************************** */
    /* **************************************************************************** */

	@Override
	public void runInService() {
		Cursor allItems = getAllItemsCursor();
		initClosestLocation(allItems);
		Pair<Location, Integer> locationOfFarthestChildAndRelevantParentId = getRelevantParentIDAndLocationOfFarthestChild();
		Location furthestLocation = locationOfFarthestChildAndRelevantParentId.first;
		int relevantParentId = locationOfFarthestChildAndRelevantParentId.second;
		
		Bundle result = new Bundle();
		result.putParcelable(Tags.LOCATION_FOR_APPROPRIATE_ZOOM, furthestLocation);
		result.putInt(Tags.RELEVANT_PARENT_ID, relevantParentId);
		Appl.receiver.send(0, result);
	}
	
	/* **************************************************************************** */
    /* **************************** Utility API *********************************** */
    /* **************************************************************************** */
	
	/**
	 * Gets the {@link Cursor} containing all the items among which th.
	 *
	 * @return the {@link Cursor} containing all the children of the given item
	 *         with their IDs and coordinates (the latter if present)
	 */
	private Cursor getAllItemsCursor() {
		Cursor cursor = Appl.sightsDatabaseOpenHelper.getReadableDatabase()
				.query(TABLE_NAME,
						new String[] { COLUMN_ID, COLUMN_LATITUDE,
								COLUMN_LONGITUDE }, null, null, null, null,
						null);
		return cursor;
	}
	
	/**
	 * Find closest item and the distance to it, initializes the closest item ID and its location.
	 *
	 * @param allItemsCursor
	 *            the {@link Cursor} containing all the items among which we are
	 *            going to find the closest one. The cursor should contain the
	 *            items' ID and coordinates (the latter if present)
	 */
	private void initClosestLocation(Cursor allItemsCursor) {
		int closestID = 0;
		float minDistance = Float.MAX_VALUE;
		Location closestItemLocation = new Location("");
		while (allItemsCursor.moveToNext()) {
			boolean hasCoordinates = !allItemsCursor.isNull(allItemsCursor.getColumnIndex(COLUMN_LATITUDE)) 
					&& !allItemsCursor.isNull(allItemsCursor.getColumnIndex(COLUMN_LONGITUDE));
			if(hasCoordinates) {
				double latitude = allItemsCursor.getDouble(allItemsCursor.getColumnIndex(COLUMN_LATITUDE));
				double longitude = allItemsCursor.getDouble(allItemsCursor.getColumnIndex(COLUMN_LONGITUDE));
				closestItemLocation.setLatitude(latitude);
				closestItemLocation.setLongitude(longitude);
				float distance = mLocation.distanceTo(closestItemLocation);
				if(distance < minDistance) {
					minDistance = distance;
					closestID =  allItemsCursor.getInt(allItemsCursor.getColumnIndex(COLUMN_ID));
				}
			}
		}
		mClosestLocation = closestItemLocation;
		mClosestLocationId = closestID;
		mDistanceToClosestLocation = minDistance;
	}
	
	/**
	 * Finds the most distant child location from the user's location, and the
	 * distance to it to the item in the {@link Cursor} which is the most
	 * distant one from the user's location.
	 *
	 * @param allItemsCursor
	 *            the {@link Cursor} containing all the items among which we are
	 *            going to find the closest one. The cursor should contain the
	 *            items' ID and coordinates (the latter if present)
	 * @return the farthest location, and the distance to it
	 */
	private Pair<Location, Float> findFarthestChildLocationAndDistanceToIt(Cursor allItemsCursor) {
		float maxDistance = 0;
		Location itemLocation = new Location(mLocation);
		Location farthestLocation = new Location(mClosestLocation);
		while (allItemsCursor.moveToNext()) {
			boolean hasCoordinates = !allItemsCursor.isNull(allItemsCursor.getColumnIndex(COLUMN_LATITUDE)) 
					&& !allItemsCursor.isNull(allItemsCursor.getColumnIndex(COLUMN_LONGITUDE));
			if(hasCoordinates) {
				double latitude = allItemsCursor.getDouble(allItemsCursor.getColumnIndex(COLUMN_LATITUDE));
				double longitude = allItemsCursor.getDouble(allItemsCursor.getColumnIndex(COLUMN_LONGITUDE));
				itemLocation.setLatitude(latitude);
				itemLocation.setLongitude(longitude);
				float distance = mLocation.distanceTo(itemLocation);
				if(distance > maxDistance) {
					maxDistance = distance;
					farthestLocation.setLatitude(latitude);
					farthestLocation.setLongitude(longitude);
				}
			}
		}
		return new Pair<Location, Float>(farthestLocation, maxDistance);
	}

	/**
	 * Find all children of the given parent.
	 *
	 * @param parentID
	 *            the parent id
	 * @return the {@link Cursor} containing all the children of the given item
	 *         with their IDs and coordinates (the latter if present)
	 */
	private Cursor findAllChildrenOf(int parentID) {
		Cursor cursor = Appl.sightsDatabaseOpenHelper.getReadableDatabase()
				.query(TABLE_NAME,
						new String[] { COLUMN_ID, COLUMN_LATITUDE,
								COLUMN_LONGITUDE },
						getFindAllChildrenWhereClause(parentID), null, null,
						null, null);
		return cursor;
	}
	
	/**
	 * Gets the SQL where clause for the query to find all children of the given parent.
	 *
	 * @param parentID the parent id
	 * @return the where clause
	 */
	private String getFindAllChildrenWhereClause(int parentID) {
		String whereClause = "(" + COLUMNS_LOCATION_LEVEL[0] + "=" + parentID + ")";
		for(int i=1;i<COLUMNS_LOCATION_LEVEL.length;i++) {
			whereClause += " OR (" + COLUMNS_LOCATION_LEVEL[i] + "=" + parentID + ")";
		}
		return whereClause;
	}
	
	/**
	 * Gets the parent IDs of the given element.
	 *
	 * @param childID the child ID
	 * @return the IDs of the parent element
	 */
	private int[] getParentIDsOf(int childID) {
		Cursor cursor = Appl.sightsDatabaseOpenHelper.getReadableDatabase()
				.query(TABLE_NAME,
						COLUMNS_LOCATION_LEVEL,
						COLUMN_ID + "="+childID, null, null,
						null, null);
		if(cursor.moveToFirst()){
			int[] parents = DatabaseHelper.getParentArrayFromCursor(cursor);
			return parents;
		}
		return null;
	}
	
	private Pair<Location, Integer> getRelevantParentIDAndLocationOfFarthestChild() {
		int[] parents = getParentIDsOf(mClosestLocationId);
		Location furthestChildLocation = new Location(mClosestLocation);
		int relevantParentId = mClosestLocationId;
		for(int i=parents.length-1;i>=0;i--) {
			Pair<Location, Float> currentFurthestChildLocationAndDistance = findFarthestChildLocationAndDistanceToIt(findAllChildrenOf(parents[i]));
			if((currentFurthestChildLocationAndDistance.second - mDistanceToClosestLocation)*DISTANCE_TO_AREA_WIDTH_FACTOR > mDistanceToClosestLocation) {
				//it's large enough and close enough
				return new Pair<Location, Integer>(currentFurthestChildLocationAndDistance.first, parents[i]);
			} else {
				furthestChildLocation = currentFurthestChildLocationAndDistance.first;
				relevantParentId = parents[i];
			}
		}
		//None of the parents is very large
		return new Pair<Location, Integer>(furthestChildLocation, relevantParentId);
	}
	
	
}
