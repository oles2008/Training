package com.iolab.sightlocator;

import static com.iolab.sightlocator.SightsDatabaseOpenHelper.COLUMNS_LOCATION_LEVEL;

import java.util.Arrays;

import android.database.Cursor;

/**
 * The Class containing utility methods for reading from and writing to the database.
 * 
 * @author Oles Potiatynyk
 */
public class DatabaseHelper {

	public static int[] getParentArrayFromCursor(Cursor cursor) {
		int[] parentIDs;
		parentIDs = new int[] {
				cursor.getInt(cursor.getColumnIndex(COLUMNS_LOCATION_LEVEL[0])),
				cursor.getInt(cursor.getColumnIndex(COLUMNS_LOCATION_LEVEL[1])),
				cursor.getInt(cursor.getColumnIndex(COLUMNS_LOCATION_LEVEL[2])),
				cursor.getInt(cursor.getColumnIndex(COLUMNS_LOCATION_LEVEL[3])),
				cursor.getInt(cursor.getColumnIndex(COLUMNS_LOCATION_LEVEL[4]))};
		
		//temporary fix for cases when some parent IDs are empty and are treated as 0
		int positionOfZero = -1;
		for(int i=0; i<parentIDs.length;i++){
			if(parentIDs[i]==0){
				positionOfZero = i;
				break;
			}
		}
		if(positionOfZero!=-1){
			parentIDs = Arrays.copyOfRange(parentIDs, 0, positionOfZero);
		}
		//end of temporary fix
		
		return parentIDs;
	}
}
