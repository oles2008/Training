package com.iolab.sightlocator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SightsDatabaseOpenHelper extends SQLiteOpenHelper {
	
	public static final String DATABASE_NAME = "sights.db";
	public static final String TABLE_NAME = "sights";
	private static final int DATABASE_VERSION = 1;
	
	//column names
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_LONGITUDE = "longitude";
	public static final String COLUMN_LATITUDE = "latitude";
	public static final String[] COLUMNS_LOCATION_LEVEL = { "level0", "level1",
			"level2", "level3", "level4" };
	public static final String COLUMN_SIGHT_IMAGE_PATH = "sight_image_path";
	public static final String COLUMN_SIGHT_STATUS = "status";
	
	//column name stubs
	public static final String SIGHT_NAME = "name_";
	public static final String SIGHT_ADDRESS = "address_";
	public static final String SIGHT_DESCRIPTION = "description_";
	 
	private static String DATABASE_CREATE = null;
	
	/**
	 * Gets the database create statement.
	 *
	 * @param languages the standard Java language tags
	 * @return the database create statement
	 */
	private static String getDatabaseCreateStatement(List<String> languages){
		String createStatement = "create table " 
					+ TABLE_NAME + "(" 
					+ COLUMN_ID
					+ " integer primary key autoincrement, " 
					+ COLUMN_LONGITUDE
					+ " real, " 
					+ COLUMN_LATITUDE 
					+ " real, " 
					+ COLUMN_SIGHT_IMAGE_PATH
					+ " text, "
					+ COLUMN_SIGHT_STATUS
					+ " integer, ";
		for(String locationLevelColumn: COLUMNS_LOCATION_LEVEL){
			createStatement += locationLevelColumn + " text, ";
		}
		
		int size = languages.size();
		for(int i=0;i<size-1;i++){
			createStatement += SIGHT_NAME + languages.get(i) + " text, ";
			createStatement += SIGHT_ADDRESS + languages.get(i) + " text, ";
			createStatement += SIGHT_DESCRIPTION + languages.get(i) + " text, ";
		}
		
			createStatement += SIGHT_NAME + languages.get(size-1) + " text, ";
			createStatement += SIGHT_ADDRESS + languages.get(size-1) + " text, ";
			createStatement += SIGHT_DESCRIPTION + languages.get(size-1) + " text)";
		
		return createStatement;
	}
	
	private static String getDatabaseInsertStatement(String[] columnNames, String[] values){
		String insertStatement = "";
		if(columnNames== null || values == null || columnNames.length!=values.length){
			return insertStatement;
		}
		insertStatement = "INSERT INTO "+TABLE_NAME + " (";
		for(int i=0;i<columnNames.length;i++){
			insertStatement += columnNames[i] + ",";
		}
			insertStatement += columnNames[columnNames.length-1];
		insertStatement += ") VALUES (";
		for(int i=0;i<values.length;i++){
			insertStatement += values[i] + ",";
		}
			insertStatement += values[values.length-1] + ")";
		return insertStatement;
	}
	
	private static List<String> getLanguageTagsList(String ... tags){
		return Arrays.asList(tags);
	}

	public SightsDatabaseOpenHelper(Context context, int version) {
		super(context, DATABASE_NAME, null, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(getDatabaseCreateStatement(getLanguageTagsList("en","uk")));
		db.beginTransaction();
		try{
			db.execSQL(getDatabaseInsertStatement(
					new String[] { 	COLUMN_LATITUDE,
									COLUMN_LONGITUDE,
									SIGHT_NAME + "en",
									COLUMN_SIGHT_IMAGE_PATH,
									SIGHT_DESCRIPTION + "en" },
					new String[] {
							"49.839860",
							"23.993669",
							"\'Main Railway Station\'",
							"railway.jpg",
							"\'The Main railway station in Lviv, Ukraine also known as Lviv-Main. It is one of the most notable pieces of Art Nouveau architecture in former Galicia. The station was opened to the public in 1904, and celebrated its centennial anniversary on 26 March 2004. On a monthly basis, the terminal handles over 1.2 million passengers and moves 16 thousand tons of freight.\'" }));
			db.execSQL(getDatabaseInsertStatement(
					new String[] { 	COLUMN_LATITUDE,
									COLUMN_LONGITUDE,
									SIGHT_NAME + "en",
									COLUMN_SIGHT_IMAGE_PATH, 
									SIGHT_DESCRIPTION + "en"},
					new String[] {
							"49.8367019",
							"24.0048451",
							"\'Church of Sts. Olha and Elizabeth, Lviv\'",
							"elis.jpg",
							"\'The Church of Sts. Olha and Elizabeth in Lviv, Ukraine, is located between the citys main rail station and the Old Town. It was originally built as the Roman Catholic Church of St. Elizabeth and today serves as the Greek Catholic Church of Sts. Olha and Elizabeth. The church was built by the Latin Archbishop of Lviv Saint Joseph Bilczewski in the years 1903&#8211;1911 as a parish church for the dynamically developing western suburb. It was designed by Polish architect Teodor Talowski.\'" }));
			db.setTransactionSuccessful(); 
		}finally{
			db.endTransaction();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
