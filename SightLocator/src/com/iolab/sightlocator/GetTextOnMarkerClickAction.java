package com.iolab.sightlocator;

import static com.iolab.sightlocator.SightsDatabaseOpenHelper.COLUMN_LATITUDE;
import static com.iolab.sightlocator.SightsDatabaseOpenHelper.COLUMN_LONGITUDE;
import static com.iolab.sightlocator.SightsDatabaseOpenHelper.SIGHT_DESCRIPTION;
import static com.iolab.sightlocator.SightsDatabaseOpenHelper.TABLE_NAME;
import static com.iolab.sightlocator.SightsDatabaseOpenHelper.COLUMN_SIGHT_IMAGE_PATH;

import java.io.File;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class GetTextOnMarkerClickAction implements ServiceAction, Parcelable{

	private LatLng position;
	private long markerClickCounter;
	
	public GetTextOnMarkerClickAction(LatLng position, long markerClickCounter) {
		this.position = position;
		this.markerClickCounter = markerClickCounter;
	}
	
	private GetTextOnMarkerClickAction(Parcel parcel){
		this.position = parcel.readParcelable(LatLng.class.getClassLoader());
		this.markerClickCounter = parcel.readLong();
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	    public static final double MB = 2;
	

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(position, flags);
		dest.writeLong(markerClickCounter);
		
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
		
		Cursor cursor = Appl.sightsDatabaseOpenHelper.getReadableDatabase()
				.query(TABLE_NAME,
						new String[] { COLUMN_LATITUDE,
								COLUMN_LONGITUDE,
								COLUMN_SIGHT_IMAGE_PATH,
								SIGHT_DESCRIPTION + "en"},
							"(" + COLUMN_LATITUDE + " = "
								+ position.latitude + " AND "
								+ COLUMN_LONGITUDE + " = "
								+ position.longitude + ")",
								null, null, null, null);

		String sightDescription = null;
		String pathToImage = null;
		long  megaByteFeeSize = 0;
		

		if (cursor.moveToFirst()) {
			pathToImage = cursor.getString(2);
			sightDescription = cursor.getString(3);
		}

		if (pathToImage !=null && pathToImage.startsWith("/")){
			pathToImage = pathToImage.substring(1, pathToImage.length());
		}
		
		if (pathToImage == null || pathToImage.isEmpty()){
			pathToImage = Tags.ONE_PIXEL_JPEG;
			
			//Log.d("Mytag", "pathToImage:"+ pathToImage);

		} else {
			// if Media is Mounted (SD Card=External Storage) then imaged will
			// be saved at Media (SD Card=External Storage)
			
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
			//	Log.d("Mytag","External storage:" + Environment.getExternalStorageState());

			
				long freeSize = Environment.getExternalStorageDirectory()
						.getFreeSpace();
				
				// One binary megabyte equals 1048576 bytes.
				
				 megaByteFeeSize = freeSize / 1048576;

				//Log.d("Mytag", "External Storage freeSize: " + freeSize);
				//Log.d("Mytag", "External Storage freeSize in Mb: " + megaByteFeeSize);
				// if size of free space at SD Card (External Storage) is at
				// least 2Mb then imaged will be saved at SD Card (External_Storage)
				 
				}else {long freeSize = 0;
				megaByteFeeSize = freeSize / 1048576;
					
				}
				if (megaByteFeeSize > MB) {
					
				//	Log.d("Mytag", "External Storage megaByteFeeSize more than:" + megaByteFeeSize);
					
					String destinationPath = Environment
							.getExternalStorageDirectory().getPath()
							+ "/"
							+ Appl.appContext.getPackageName()
							+ "/"
							+ Tags.PATH_TO_IMAGES_IN_ASSETS + pathToImage;
					Utils.copyFromAssets(Tags.PATH_TO_IMAGES_IN_ASSETS
							+ pathToImage, destinationPath);
					pathToImage = destinationPath;

				
					// else (if size of free space at SD Card (External_Storage) is
					// less than 1Mb then imaged will be saved at Cash (Internal_Storage)
				// if Media is not Mounted then imaged will be saved at Cash (Internal_Storage)
			} else {
		
								
			//	Log.d("Mytag", "getCacheDir:"+ Appl.appContext.getCacheDir());
			//	Log.d("Mytag", "Internal storage:");
				
				String destinationPath = Appl.appContext.getCacheDir() + "/"
						+ Tags.PATH_TO_IMAGES_IN_ASSETS + pathToImage;
				Utils.copyFromAssets(Tags.PATH_TO_IMAGES_IN_ASSETS
						+ pathToImage, destinationPath);
				pathToImage = destinationPath;

				//Log.d("Mytag", "Destination:" + destinationPath);
				}
		
		
			}
		

//		Log.d("MSG","runInService pathToImage > " + pathToImage);
		
		Bundle resultData = new Bundle();
		resultData.putString(Tags.SIGHT_DESCRIPTION, sightDescription);
		resultData.putLong(Tags.ON_MARKER_CLICK_COUNTER, markerClickCounter);
		resultData.putString(Tags.PATH_TO_IMAGE, pathToImage);
		Appl.receiver.send(0, resultData);
	}
	
}
