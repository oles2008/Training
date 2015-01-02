package com.iolab.sightlocator;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import static com.iolab.sightlocator.SightsDatabaseOpenHelper.TABLE_NAME;
import static com.iolab.sightlocator.SightsDatabaseOpenHelper.COLUMN_LATITUDE;
import static com.iolab.sightlocator.SightsDatabaseOpenHelper.COLUMN_LONGITUDE;
import static com.iolab.sightlocator.SightsDatabaseOpenHelper.COLUMN_SIGHT_STATUS;
import static com.iolab.sightlocator.SightsDatabaseOpenHelper.COLUMNS_LOCATION_LEVEL;
import static com.iolab.sightlocator.SightsDatabaseOpenHelper.SIGHT_ADDRESS;
import static com.iolab.sightlocator.SightsDatabaseOpenHelper.SIGHT_NAME;

import com.google.android.gms.maps.model.LatLngBounds;

public class GetMarkersOnCameraUpdateAction implements ServiceAction,
		Parcelable {
	
	private LatLngBounds latLngBounds;
	
	public GetMarkersOnCameraUpdateAction(LatLngBounds latLngBounds) {
		this.latLngBounds = latLngBounds;
	}
	
	private GetMarkersOnCameraUpdateAction(Parcel parcel) {
		this.latLngBounds = parcel.readParcelable(LatLngBounds.class.getClassLoader());
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(latLngBounds, flags);
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
		//Log.d("MyLogs", "Hi from service");
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
								+ latLngBounds.northeast.latitude + " AND "
								+ latLngBounds.southwest.latitude + ") AND ("
								+ COLUMN_LONGITUDE + " BETWEEN "
								+ latLngBounds.northeast.longitude + " AND "
								+ latLngBounds.southwest.longitude + ")", null,
						null, null, null);
		
	}

}
