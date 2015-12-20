package com.iolab.sightlocator;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

public class SightsDatabaseOpenHelper extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "sights.db";
	public static final String TABLE_NAME = "sights";
	private static final int DATABASE_VERSION = 1;

	// column names
	public static final String COLUMN_ID 				= "_id";
	public static final String COLUMN_LONGITUDE 		= "longitude";
	public static final String COLUMN_LATITUDE 			= "latitude";
	public static final String COLUMN_SIGHT_IMAGE_PATH 	= "sight_image_path";
	public static final String COLUMN_SIGHT_STATUS 		= "status";
	public static final String[] COLUMNS_LOCATION_LEVEL = { "level0", 
															"level1",
															"level2",
															"level3",
															"level4" };

	// column name stubs
	public static final String SIGHT_NAME 				= "name_";
	public static final String SIGHT_ADDRESS 			= "address_";
	public static final String SIGHT_DESCRIPTION 		= "description_";
	public static final String MARKER_CATEGORY 			= "category";

	public SightsDatabaseOpenHelper(Context context, int version) {
		super(context, DATABASE_NAME, null, version);
		copyDatabaseFromAssets();
	}

	// onCreate and onUpgrade should be used if DB creation/upgrade is handled from code
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}

	private void copyDatabaseFromAssets() {
		Utils.copyFromAssets(DATABASE_NAME, 
				Environment.getDataDirectory()
				+ "/data/" 
				+ Appl.appContext.getPackageName() 
				+ "/databases/"
				+ DATABASE_NAME);
	}
}
