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
						new String[] { COLUMN_LATITUDE, COLUMN_LONGITUDE,
								SIGHT_NAME+"en", SIGHT_ADDRESS+"en", COLUMN_SIGHT_STATUS,
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
								+ latLngBounds.northeast.longitude + ")", null,
						null, null, null);
//		Log.d("MyLogs", "Where statement: "+"(" + COLUMN_LATITUDE + " BETWEEN "
//								+ latLngBounds.northeast.latitude + " AND "
//								+ latLngBounds.southwest.latitude + ") AND ("
//								+ COLUMN_LONGITUDE + " BETWEEN "
//								+ latLngBounds.southwest.longitude + " AND "
//								+ latLngBounds.northeast.longitude + ")");
//		Log.d("MyLogs", "cursor size: "+cursor.getCount());
		ArrayList<MarkerOptions> markerOptionsList = new ArrayList<MarkerOptions>();
		if (cursor.moveToFirst()) {
			LatLng position = new LatLng(cursor.getDouble(0),
					cursor.getDouble(1));
			markerOptionsList.add(new MarkerOptions().position(position)
					.title(cursor.getString(2)).snippet(cursor.getString(3)));
		}
		while (cursor.moveToNext()) {
			LatLng position = new LatLng(cursor.getDouble(0),
					cursor.getDouble(1));
			markerOptionsList.add(new MarkerOptions().position(position)
					.title(cursor.getString(2)).snippet(cursor.getString(3)));
		}
		Bundle resultData = new Bundle();
		resultData.putParcelableArrayList(Tags.MARKERS, markerOptionsList);
		resultData.putLong(Tags.ON_CAMERA_CHANGE_CALL_INDEX, viewUpdateCallIndex);
		Appl.receiver.send(0, resultData);
	}

}
