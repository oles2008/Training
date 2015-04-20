package com.iolab.sightlocator;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import static com.iolab.sightlocator.SightsDatabaseOpenHelper.TABLE_NAME;
import static com.iolab.sightlocator.SightsDatabaseOpenHelper.COLUMN_LATITUDE;
import static com.iolab.sightlocator.SightsDatabaseOpenHelper.COLUMN_LONGITUDE;
import static com.iolab.sightlocator.SightsDatabaseOpenHelper.COLUMN_SIGHT_STATUS;
import static com.iolab.sightlocator.SightsDatabaseOpenHelper.COLUMNS_LOCATION_LEVEL;
import static com.iolab.sightlocator.SightsDatabaseOpenHelper.SIGHT_ADDRESS;
import static com.iolab.sightlocator.SightsDatabaseOpenHelper.SIGHT_NAME;
import com.iolab.sightlocator.ItemGroupAnalyzer;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class GetMarkersOnCameraUpdateAction implements ServiceAction,
		Parcelable {
	
	private LatLngBounds latLngBounds;
	private long viewUpdateCallIndex;
	
	public GetMarkersOnCameraUpdateAction(LatLngBounds latLngBounds, long viewUpdateCallIndex) {
		this.latLngBounds = latLngBounds;
		this.viewUpdateCallIndex = viewUpdateCallIndex;
	}
	
	private GetMarkersOnCameraUpdateAction(Parcel parcel) {
		this.latLngBounds = parcel.readParcelable(LatLngBounds.class.getClassLoader());
		this.viewUpdateCallIndex = parcel.readLong();
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(latLngBounds, flags);
		dest.writeLong(viewUpdateCallIndex);
	}
	
	public static final Parcelable.Creator<GetMarkersOnCameraUpdateAction> CREATOR = new Parcelable.Creator<GetMarkersOnCameraUpdateAction>() {
		public GetMarkersOnCameraUpdateAction createFromParcel(Parcel in) {
			return new GetMarkersOnCameraUpdateAction(in);
		}

		public GetMarkersOnCameraUpdateAction[] newArray(int size) {
			return new GetMarkersOnCameraUpdateAction[size];
		}
	};

	@Override
	public void runInService() {
		Log.d("MyLogs", "Starting runUnService(), updateViewCallIndex: "+this.viewUpdateCallIndex);
		Cursor cursor = Appl.sightsDatabaseOpenHelper.getReadableDatabase()
				.query(TABLE_NAME,
						new String[] { COLUMN_LATITUDE,
								COLUMN_LONGITUDE,
								SIGHT_NAME+"en",
								SIGHT_ADDRESS+"en",
								COLUMN_SIGHT_STATUS,
								COLUMNS_LOCATION_LEVEL[0],
								COLUMNS_LOCATION_LEVEL[1],
								COLUMNS_LOCATION_LEVEL[2],
								COLUMNS_LOCATION_LEVEL[3],
								COLUMNS_LOCATION_LEVEL[4] },
							"(" + COLUMN_LATITUDE + " BETWEEN "
								+ latLngBounds.southwest.latitude + " AND "
								+ latLngBounds.northeast.latitude + ") AND ("
								+ COLUMN_LONGITUDE + " BETWEEN "
								+ latLngBounds.southwest.longitude + " AND "
								+ latLngBounds.northeast.longitude + ")",
								null, null, null, null);
//		Log.d("MyLogs", "Where statement: "+"(" + COLUMN_LATITUDE + " BETWEEN "
//								+ latLngBounds.northeast.latitude + " AND "
//								+ latLngBounds.southwest.latitude + ") AND ("
//								+ COLUMN_LONGITUDE + " BETWEEN "
//								+ latLngBounds.southwest.longitude + " AND "
//								+ latLngBounds.northeast.longitude + ")");
//		Log.d("MyLogs", "cursor size: "+cursor.getCount());
//		ArrayList<MarkerOptions> markerOptionsList = new ArrayList<MarkerOptions>();
		ArrayList<SightMarkerItem> SightMarkerItemList = new ArrayList<SightMarkerItem>();
		if (cursor.moveToFirst()) {
			LatLng position = new LatLng(cursor.getDouble(0),
										cursor.getDouble(1));
			int[] parentIDs = {cursor.getInt(5),cursor.getInt(6),cursor.getInt(7),cursor.getInt(8),cursor.getInt(9)}; //the last argument for SightMarkerItemList
			//markerOptionsList.add(new MarkerOptions()
			SightMarkerItemList.add(new SightMarkerItem(position,cursor.getString(2),
														cursor.getString(3),null,parentIDs));
//			
//										.position(position)
//										.title(cursor.getString(2))
//										.snippet(cursor.getString(3)));
		}
		List<int[]> ListOfArrays = new ArrayList<int[]>(); //done on 19/04/15
		while (cursor.moveToNext()) {
			LatLng position = new LatLng(cursor.getDouble(0),
										cursor.getDouble(1));
			
			int[] parentIDs = {cursor.getInt(5),cursor.getInt(6),cursor.getInt(7),cursor.getInt(8),cursor.getInt(9)}; //the last argument for SightMarkerItemList
			SightMarkerItemList.add(new SightMarkerItem(position,cursor.getString(2),
														cursor.getString(3),null,parentIDs));
			ListOfArrays.add(parentIDs); ////done on 19/04/15
		}
		
		//TODO define List of arrays for findCommonParent(List<int[]> list, int percentageToIgnore)
		//      input parameter as in SightMarkerItemList
		
		//TODO define common parent
		
		Bundle resultData = new Bundle();
		resultData.putParcelableArrayList(Tags.MARKERS, SightMarkerItemList);
		resultData.putLong(Tags.ON_CAMERA_CHANGE_CALL_INDEX, viewUpdateCallIndex);
		
		//TODO resultData.putInt() put common parent into bundle
		resultData.putInt("CommonParentId",ItemGroupAnalyzer.findCommonParent(ListOfArrays,80));////done on 19/04/15
		Appl.receiver.send(0, resultData);
	}

}
