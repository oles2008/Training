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
					+ COLUMN_ID 				+ " integer primary key autoincrement, " 
					+ COLUMN_LONGITUDE 			+ " real, " 
					+ COLUMN_LATITUDE 			+ " real, " 
					+ COLUMN_SIGHT_IMAGE_PATH	+ " text, "
					+ COLUMN_SIGHT_STATUS		+ " integer, ";
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
							"\'railway.jpg\'",
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
							"\'elis.jpg\'",
							"\'The Church of Sts. Olha and Elizabeth in Lviv, Ukraine, is located between the citys main rail station and the Old Town. It was originally built as the Roman Catholic Church of St. Elizabeth and today serves as the Greek Catholic Church of Sts. Olha and Elizabeth. The church was built by the Latin Archbishop of Lviv Saint Joseph Bilczewski in the years 1903&#8211;1911 as a parish church for the dynamically developing western suburb. It was designed by Polish architect Teodor Talowski.\'" }));
			db.execSQL(getDatabaseInsertStatement(
					new String[] { 	COLUMN_LATITUDE,
									COLUMN_LONGITUDE,
									SIGHT_NAME + "en",
									COLUMN_SIGHT_IMAGE_PATH, 
									SIGHT_DESCRIPTION + "en"},
					new String[] {
							"49.838502",
							"24.012944",
							"\'St. George\'\'s cathedral, Lviv\'",
							"\'saint_george.jpg\'",
							"\'St. George\'\' Cathedral (Ukrainian: Собор святого Юра, translit. Sobor sviatoho Yura) is a baroque-rococo cathedral located in the city of Lviv, the historic capital of western Ukraine. It was constructed between 1744-1760[1] on a hill overlooking the city. This is the third manifestation of a church to inhabit the site since the 13th century, and its prominence has repeatedly made it a target for invaders and vandals. The cathedral also holds a predominant position in Ukrainian religious and cultural terms. During 19th and 20th centuries, the cathedral served as the mother church of the Ukrainian Greek Catholic Church (UGCC) (Eastern Rite Catholic).\'" }));
			db.execSQL(getDatabaseInsertStatement(
					new String[] { 	COLUMN_LATITUDE,
									COLUMN_LONGITUDE,
									SIGHT_NAME + "en",
									COLUMN_SIGHT_IMAGE_PATH, 
									SIGHT_DESCRIPTION + "en"},
					new String[] {
							"49.842086",
							"24.035005",
							"\'Dormition church, Lviv\'",
							"\'dormition_church.jpg\'",
							"\'The Church of the Assumption of the Blessed Virgin Mary in Lviv (commonly known as Uspenska church, or the Wallachian Church) is an Orthodox Church in Lviv, located in the Old Town, in Renaissance style. The current building replaced an earlier church structure, and was built in the period 1591-1629 by Paul Roman, Wojciech Kapinos and Ambrose Przychylny; the bell tower was erected in the years 1571-1578 by Peter Barbon. The Orthodox Church complex is located at ul. Ruska and consists of the Uspensky church, bell tower (the tower Korniakta) and the Chapel of the Three Saints. The founder of the first church was Moldovan hospodar Alexandru Lapusneanu (hence it carries the common name of Vlachs). The second church was erected on the initiative of the Brotherhood of Ouspensky, and the founder of the bell tower and the chapel was Constantine Corniaktos, a Greek merchant. Korniakta Tower is considered one of the most precious monuments of Ukrainian architecture of the sixteenth century Mannerism architectural style.\'" }));
			db.execSQL(getDatabaseInsertStatement(
					new String[] { 	COLUMN_LATITUDE,
									COLUMN_LONGITUDE,
									SIGHT_NAME + "en",
									COLUMN_SIGHT_IMAGE_PATH, 
									SIGHT_DESCRIPTION + "en"},
					new String[] {
							"49.851769",
							"24.030482",
							"\'St. Paraskeva church, Lviv\'",
							"\'paraskeva_church.jpg\'",
							"\'The Church of St. Paraskevi in Lviv (Ukrainian Церква святої Параскеви П\'\'ятниці) - is located at Khmelnytsky 77 B at the foot of the High Castle. The Orthodox Church of St. Paraskeva (or Piatnyci) dates back to the 13th century and is considered to be one of the oldest churches in Lviv.  At the initiative of Moldavian Hospodar Vasile Lupu, in 1645 the church was build in it current shape. Building blocks were hewn sandstone. The single-nave church has a defensive character - its walls are thick and the windows small, also further demonstrated by the loopholes in the upper storeys of the tower. Enclosed is an octagonal apse. The wall bears the coat of arms of Moldavia of the hospodar - the head of an aurochs with the sun, moon and star. In 1885, a fundamental overhaul of the entire building was made. The high, square tower is linked organically with the competent body of the temple. In 1908 he was covered with a new, domed helmet, designed by architect Mykhailo Luzhetskiy and placed at the current helmet tent, then the smaller towers at the corners were added. A decorative element of the tower is an attic with a blind arcade of arches of Romanesque and Renaissance pilasters. In 1987-1990, a new bell tower was built in the courtyard in front of the facade, which was harmonized with architecture of the old church.\'" }));
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
