package com.iolab.sightlocator;

import static com.iolab.sightlocator.SightsDatabaseOpenHelper.COLUMN_LATITUDE;
import static com.iolab.sightlocator.SightsDatabaseOpenHelper.COLUMN_LONGITUDE;
import static com.iolab.sightlocator.SightsDatabaseOpenHelper.SIGHT_DESCRIPTION;
import static com.iolab.sightlocator.SightsDatabaseOpenHelper.TABLE_NAME;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class GetTextOnMarkerClickAction implements ServiceAction, Parcelable{

	private LatLng position;
	private long viewUpdateCallIndex;
	
	public GetTextOnMarkerClickAction(LatLng position, long viewUpdateCallIndex) {
		this.position = position;
		this.viewUpdateCallIndex = viewUpdateCallIndex;
	}
	
	private GetTextOnMarkerClickAction(Parcel parcel){
		this.position = parcel.readParcelable(LatLng.class.getClassLoader());
		this.viewUpdateCallIndex = parcel.readLong();
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(position, flags);
		dest.writeLong(viewUpdateCallIndex);
		
	}

	public static final Parcelable.Creator<GetTextOnMarkerClickAction> CREATOR = new Parcelable.Creator<GetTextOnMarkerClickAction>() {
		public GetTextOnMarkerClickAction createFromParcel(Parcel in) {
			return new GetTextOnMarkerClickAction(in);
		}

		public GetTextOnMarkerClickAction[] newArray(int size) {
			return new GetTextOnMarkerClickAction[size];
		}
	};

	
	@Override
	public void runInService() {
		Log.d("MSG", "runInService , updateViewCallIndex: " + this.viewUpdateCallIndex);
		
		Cursor cursor = Appl.sightsDatabaseOpenHelper.getReadableDatabase()
				.query(TABLE_NAME,
						new String[] { COLUMN_LATITUDE,
								COLUMN_LONGITUDE,
								SIGHT_DESCRIPTION + "en" },
							"(" + COLUMN_LATITUDE + " = "
								+ position.latitude + " AND "
								+ COLUMN_LONGITUDE + " = "
								+ position.longitude + ")",
								null, null, null, null);

		String sightDescription = null;

		if (cursor.moveToFirst()) {
			sightDescription = cursor.getString(2);
		}
		Log.d("MSG","runInService, String sightDescription : " + sightDescription);

		Bundle resultData = new Bundle();
		resultData.putString(Tags.SIGHT_DESCRIPTION, sightDescription);
		resultData.putLong(Tags.ON_MARKER_CLICK_INDEX, viewUpdateCallIndex);
		Appl.receiver.send(0, resultData);
	}
	
}
