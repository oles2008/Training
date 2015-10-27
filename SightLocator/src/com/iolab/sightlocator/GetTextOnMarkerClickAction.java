package com.iolab.sightlocator;

import static com.iolab.sightlocator.SightsDatabaseOpenHelper.COLUMN_LATITUDE;
import static com.iolab.sightlocator.SightsDatabaseOpenHelper.COLUMN_LONGITUDE;
import static com.iolab.sightlocator.SightsDatabaseOpenHelper.SIGHT_DESCRIPTION;
import static com.iolab.sightlocator.SightsDatabaseOpenHelper.TABLE_NAME;
import static com.iolab.sightlocator.SightsDatabaseOpenHelper.COLUMN_SIGHT_IMAGE_PATH;
import static com.iolab.sightlocator.SightsDatabaseOpenHelper.COLUMN_ID;
import static com.iolab.sightlocator.SightsDatabaseOpenHelper.COLUMN_SIGHT_STATUS;
import static com.iolab.sightlocator.SightsDatabaseOpenHelper.SIGHT_ADDRESS;
import static com.iolab.sightlocator.SightsDatabaseOpenHelper.SIGHT_NAME;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
	private ArrayList<SightMarkerItem> mClusterItems = null;
	private boolean mShowOnMap = false;
	
	public GetTextOnMarkerClickAction(Bundle inputBundle) {
		if (inputBundle.containsKey(Tags.POSITION_LAT)
				&& inputBundle.containsKey(Tags.POSITION_LNG)) {
			mPosition = new LatLng(inputBundle.getDouble(Tags.POSITION_LAT),
					inputBundle.getDouble(Tags.POSITION_LNG));
		}
		mMarkerClickCounter = inputBundle.getInt(Tags.ON_MARKER_CLICK_COUNTER);
		mMapClickCounter = inputBundle.getInt(Tags.ON_MAP_CLICK_COUNTER);
		mClusterClickCounter = inputBundle.getInt(Tags.ON_CLUSTER_CLICK_COUNTER);
		mID = inputBundle.getInt(Tags.ID,-1);
//		inputBundle.setClassLoader(SightMarkerItem.class.getClassLoader());
		mClusterItems = inputBundle.getParcelableArrayList(Tags.SIGHT_ITEM_LIST);
		mShowOnMap = inputBundle.getBoolean(Tags.SHOW_ON_MAP, false);
	}
	
	private GetTextOnMarkerClickAction(Parcel parcel){
		this(parcel.readBundle(SightMarkerItem.class.getClassLoader()));
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		Bundle bundle = new Bundle();
		if (mPosition != null) {
			bundle.putDouble(Tags.POSITION_LAT, mPosition.latitude);
			bundle.putDouble(Tags.POSITION_LNG, mPosition.longitude);
		}
		bundle.putLong(Tags.ON_CLUSTER_CLICK_COUNTER, mClusterClickCounter);
		bundle.putLong(Tags.ON_MAP_CLICK_COUNTER, mMapClickCounter);
		bundle.putLong(Tags.ON_MARKER_CLICK_COUNTER, mMarkerClickCounter);
		bundle.putInt(Tags.ID, mID);
		bundle.putParcelableArrayList(Tags.SIGHT_ITEM_LIST, mClusterItems);
		bundle.putBoolean(Tags.SHOW_ON_MAP, mShowOnMap);
		
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
									SIGHT_DESCRIPTION + "en", SIGHT_NAME + "en", SIGHT_ADDRESS+"en"},
								"(" + COLUMN_LATITUDE + " = "
									+ mPosition.latitude + " AND "
									+ COLUMN_LONGITUDE + " = "
									+ mPosition.longitude + ")",
									null, null, null, null);
		}
		//mind that if we use ID we also look if it has children
		else if (mID != -1){//TODO
			cursor = Appl.sightsDatabaseOpenHelper.getReadableDatabase()
					.query(TABLE_NAME,
							new String[] { COLUMN_LATITUDE,
									COLUMN_LONGITUDE,
									COLUMN_SIGHT_IMAGE_PATH,
									SIGHT_DESCRIPTION + "en", SIGHT_NAME + "en", SIGHT_ADDRESS+"en"},
								"(" + COLUMN_ID + " = "
									+ mID + ")",
									null, null, null, null);
		};

		return cursor;
	};
	
	private Cursor getMultipleItemsCursor() {
		Cursor cursor = null;
		String whereClause = null;
		if (mClusterItems != null && !mClusterItems.isEmpty()) {
			whereClause = "("
					+ ((mClusterItems.get(0).getPosition() != null) ? (COLUMN_LATITUDE
							+ " = "
							+ mClusterItems.get(0).getPosition().latitude
							+ " AND " + COLUMN_LONGITUDE + " = " + mClusterItems
							.get(0).getPosition().longitude) : (COLUMN_ID
							+ " = " + mClusterItems.get(0).getID())) + ")";

			for (int i = 1; i < mClusterItems.size(); i++) {
				whereClause += " OR ("
						+ ((mClusterItems.get(i).getPosition() != null) ? (COLUMN_LATITUDE
								+ " = "
								+ mClusterItems.get(i).getPosition().latitude
								+ " AND " + COLUMN_LONGITUDE + " = " + mClusterItems
								.get(i).getPosition().longitude) : (COLUMN_ID
								+ " = " + mClusterItems.get(i).getID())) + ")";
			}
		} else if (mID != -1) {
			whereClause = "("
					+ SightsDatabaseOpenHelper.COLUMNS_LOCATION_LEVEL[0]
					+ " = " + mID + ")";
			for (int i = 1; i < SightsDatabaseOpenHelper.COLUMNS_LOCATION_LEVEL.length; i++) {
				whereClause += " OR " + "("
						+ SightsDatabaseOpenHelper.COLUMNS_LOCATION_LEVEL[i]
						+ " = " + mID + ")";
			}
		}
		if (whereClause != null) {
			cursor = Appl.sightsDatabaseOpenHelper.getReadableDatabase().query(
					TABLE_NAME,
					new String[] { COLUMN_LATITUDE, COLUMN_LONGITUDE,
							COLUMN_SIGHT_IMAGE_PATH, SIGHT_DESCRIPTION + "en",
							SIGHT_NAME + "en", SIGHT_ADDRESS + "en",COLUMN_ID },
					whereClause, null, null, null, null);
		}
		return cursor;
	}
	
	private String getSavedImagePath(String pathToImageFromDatabase) {
		if (pathToImageFromDatabase !=null && pathToImageFromDatabase.startsWith("/")){
			pathToImageFromDatabase = pathToImageFromDatabase.substring(1, pathToImageFromDatabase.length());
		}
		
		if (pathToImageFromDatabase == null || pathToImageFromDatabase.isEmpty()){
			return null;
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
						+ Tags.PATH_TO_IMAGES_IN_ASSETS + pathToImageFromDatabase;
				Utils.copyFromAssets(Tags.PATH_TO_IMAGES_IN_ASSETS
						+ pathToImageFromDatabase, destinationPath);
				return destinationPath;
				
			} else
			{
				Log.d("Mytag","Internal storage:");
				
				
				String destinationPath = Appl.appContext.getCacheDir() + "/"
						+ Tags.PATH_TO_IMAGES_IN_ASSETS + pathToImageFromDatabase;
				Utils.copyFromAssets(Tags.PATH_TO_IMAGES_IN_ASSETS
						+ pathToImageFromDatabase, destinationPath);
				
				Log.d("Mytag","Destination:"+ destinationPath);
				return destinationPath;
			}
		}
	}
	
	@Override
	public void runInService() {
		
		Cursor cursor = getCursor();

		String sightDescription = null;
		String pathToImage = null;
		String sightName = null;
		String sightAddress = null;
		LatLng sightPosition = null;

		if(cursor == null){
			return;
		}
		
		if (cursor.moveToFirst()) {
			pathToImage = cursor.getString(2);
			sightDescription = cursor.getString(3);
			sightName = cursor.getString(4);
			sightAddress = cursor.getString(5);
			if (!cursor.isNull(cursor.getColumnIndex(COLUMN_LATITUDE))
					&& !cursor.isNull(cursor.getColumnIndex(COLUMN_LONGITUDE))) {
				sightPosition = new LatLng(cursor.getDouble(cursor
						.getColumnIndex(COLUMN_LATITUDE)),
						cursor.getDouble(cursor
								.getColumnIndex(COLUMN_LONGITUDE)));
			}
		}

		
		
		Bundle resultData = new Bundle();
		resultData.putInt(Tags.ID, mID);
		resultData.putString(Tags.SIGHT_DESCRIPTION, sightDescription);
		resultData.putLong(Tags.ON_MARKER_CLICK_COUNTER, mMarkerClickCounter);
		resultData.putString(Tags.PATH_TO_IMAGE, getSavedImagePath(pathToImage));
		resultData.putString(Tags.SIGHT_NAME, sightName);
		resultData.putString(Tags.SIGHT_ADDRESS, sightAddress);
		if(sightPosition != null) {
			resultData.putParcelable(Tags.SIGHT_POSITION, sightPosition);
		}
		Appl.receiver.send(0, resultData);
		
		if((mClusterItems!=null && !mClusterItems.isEmpty()) || (mID!=-1)){
			cursor = getMultipleItemsCursor();
			ArrayList<SightMarkerItem> fullItems = new ArrayList<SightMarkerItem>();
			if (cursor.moveToFirst()) {
				// SightMarkerItem(LatLng position, String title, String
				// address, String snippet, String imageURI, String color, int
				// id, int[] parentIDs)
				sightPosition = null;
				if (!cursor.isNull(cursor.getColumnIndex(COLUMN_LATITUDE))
						&& !cursor.isNull(cursor.getColumnIndex(COLUMN_LONGITUDE))) {
					sightPosition = new LatLng(cursor.getDouble(cursor
							.getColumnIndex(COLUMN_LATITUDE)),
							cursor.getDouble(cursor
									.getColumnIndex(COLUMN_LONGITUDE)));
				}
				fullItems
						.add(new SightMarkerItem(sightPosition,
								cursor.getString(4), cursor.getString(5), null,
								getSavedImagePath(cursor.getString(2)), null,
								cursor.getInt(cursor.getColumnIndex(COLUMN_ID)), null));
				while(cursor.moveToNext()){
					sightPosition = null;
					if (!cursor.isNull(cursor.getColumnIndex(COLUMN_LATITUDE))
							&& !cursor.isNull(cursor.getColumnIndex(COLUMN_LONGITUDE))) {
						sightPosition = new LatLng(cursor.getDouble(cursor
								.getColumnIndex(COLUMN_LATITUDE)),
								cursor.getDouble(cursor
										.getColumnIndex(COLUMN_LONGITUDE)));
					}
					fullItems
					.add(new SightMarkerItem(sightPosition,
							cursor.getString(4), cursor.getString(5), null,
							getSavedImagePath(cursor.getString(2)), null,
							cursor.getInt(cursor.getColumnIndex(COLUMN_ID)), null));
				}
				resultData = new Bundle();
				resultData.putParcelableArrayList(Tags.SIGHT_ITEM_LIST, fullItems);
				Appl.receiver.send(0, resultData);
			}
		}
		if(mShowOnMap){
			resultData = new Bundle();
			resultData.putBoolean(Tags.SHOW_ON_MAP, mShowOnMap);
			Appl.receiver.send(0, resultData);
		}
	}
	
}
