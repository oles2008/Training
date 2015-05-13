package com.iolab.sightlocator;

import static com.iolab.sightlocator.SightsDatabaseOpenHelper.COLUMN_LATITUDE;
import static com.iolab.sightlocator.SightsDatabaseOpenHelper.COLUMN_LONGITUDE;
import static com.iolab.sightlocator.SightsDatabaseOpenHelper.SIGHT_DESCRIPTION;
import static com.iolab.sightlocator.SightsDatabaseOpenHelper.TABLE_NAME;
import static com.iolab.sightlocator.SightsDatabaseOpenHelper.COLUMN_SIGHT_IMAGE_PATH;
import static com.iolab.sightlocator.SightsDatabaseOpenHelper.COLUMN_ID;

import java.io.File;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class GetTextOnMarkerClickAction implements ServiceAction, Parcelable{

	private long mMarkerClickCounter = -1;
	private long mMapClickCounter = -1;
	private long mClusterClickCounter = -1;
	private LatLng mPosition;
	private int mID = -1;
	
	public GetTextOnMarkerClickAction(Bundle inputBundle) {
		mPosition  = new LatLng(inputBundle.getDouble(Tags.POSITION_LAT),
								inputBundle.getDouble(Tags.POSITION_LNG));
		mMarkerClickCounter = inputBundle.getInt(Tags.ON_MARKER_CLICK_COUNTER);
		mMapClickCounter = inputBundle.getInt(Tags.ON_MAP_CLICK_COUNTER);
		mClusterClickCounter = inputBundle.getInt(Tags.ON_CLUSTER_CLICK_COUNTER);
		mID = inputBundle.getInt(Tags.COMMON_PARENT_ID,-1);
	}
	
	private GetTextOnMarkerClickAction(Parcel parcel){
		this(parcel.readBundle());
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		Bundle bundle = new Bundle();
		bundle.putDouble(Tags.POSITION_LAT, mPosition.latitude);
		bundle.putDouble(Tags.POSITION_LNG, mPosition.longitude);
		bundle.putLong(Tags.ON_CLUSTER_CLICK_COUNTER, mClusterClickCounter);
		bundle.putLong(Tags.ON_MAP_CLICK_COUNTER, mMapClickCounter);
		bundle.putLong(Tags.ON_MARKER_CLICK_COUNTER, mMarkerClickCounter);
		bundle.putInt(Tags.COMMON_PARENT_ID, mID);
		
		dest.writeBundle(bundle);
		
	}

	public static final Parcelable.Creator<GetTextOnMarkerClickAction> CREATOR = new Parcelable.Creator<GetTextOnMarkerClickAction>() {
		public GetTextOnMarkerClickAction createFromParcel(Parcel in) {
			return new GetTextOnMarkerClickAction(in);
		}

		public GetTextOnMarkerClickAction[] newArray(int size) {
			return new GetTextOnMarkerClickAction[size];
		}
	};
	public Cursor getCursor(){
		Cursor cursor = null;
		if(mPosition != null){
			cursor = Appl.sightsDatabaseOpenHelper.getReadableDatabase()
					.query(TABLE_NAME,
							new String[] { COLUMN_LATITUDE,
									COLUMN_LONGITUDE,
									COLUMN_SIGHT_IMAGE_PATH,
									SIGHT_DESCRIPTION + "en"},
								"(" + COLUMN_LATITUDE + " = "
									+ mPosition.latitude + " AND "
									+ COLUMN_LONGITUDE + " = "
									+ mPosition.longitude + ")",
									null, null, null, null);
		};
		if(mID != -1){
			cursor = Appl.sightsDatabaseOpenHelper.getReadableDatabase()
					.query(TABLE_NAME,
							new String[] { COLUMN_LATITUDE,
									COLUMN_LONGITUDE,
									COLUMN_SIGHT_IMAGE_PATH,
									SIGHT_DESCRIPTION + "en"},
								"(" + COLUMN_ID + " = "
									+ mID + ")",
									null, null, null, null);
		};

		return cursor;
	};
	
	@Override
	public void runInService() {
		
		Cursor cursor = getCursor();

		String sightDescription = null;
		String pathToImage = null;

		if(cursor == null){
			return;
		}
		
		if (cursor.moveToFirst()) {
			pathToImage = cursor.getString(2);
			sightDescription = cursor.getString(3);
		}

		if (pathToImage !=null && pathToImage.startsWith("/")){
			pathToImage = pathToImage.substring(1, pathToImage.length());
		}
		
		if (pathToImage == null || pathToImage.isEmpty()){
			pathToImage = Tags.ONE_PIXEL_JPEG;
		}else{
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED))
			{
				Log.d("Mytag","External storage:"+ Environment.getExternalStorageState());
			
			
				String destinationPath = Environment
						.getExternalStorageDirectory().getPath()
						+ "/"
						+ Appl.appContext.getPackageName()
						+ "/"
						+ Tags.PATH_TO_IMAGES_IN_ASSETS + pathToImage;
				Utils.copyFromAssets(Tags.PATH_TO_IMAGES_IN_ASSETS
						+ pathToImage, destinationPath);
				pathToImage = destinationPath;
				
			} else
			{
				Log.d("Mytag","Internal storage:");
				
				
				String destinationPath = Appl.appContext.getCacheDir() + "/"
						+ Tags.PATH_TO_IMAGES_IN_ASSETS + pathToImage;
				Utils.copyFromAssets(Tags.PATH_TO_IMAGES_IN_ASSETS
						+ pathToImage, destinationPath);
				pathToImage = destinationPath;
				
				Log.d("Mytag","Destination:"+ destinationPath);
			}
		}
//		Log.d("MSG","runInService pathToImage > " + pathToImage);
		
		Bundle resultData = new Bundle();
		resultData.putString(Tags.SIGHT_DESCRIPTION, sightDescription);
		resultData.putLong(Tags.ON_MARKER_CLICK_COUNTER, mMarkerClickCounter);
		resultData.putString(Tags.PATH_TO_IMAGE, pathToImage);
		Appl.receiver.send(0, resultData);
	}
	
}
