package com.iolab.sightlocator;

import static com.iolab.sightlocator.SightsDatabaseOpenHelper.COLUMNS_LOCATION_LEVEL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.database.Cursor;

/**
 * The Class containing utility methods for reading from and writing to the database.
 * 
 * @author Oles Potiatynyk
 */
public class DatabaseHelper {

	public static int[] getParentArrayFromCursor(Cursor cursor) {
		int[] parentIDs;
		List<Integer> parentIDList = new ArrayList<Integer>();
		parentIDList.addAll(Arrays.asList(cursor.getInt(cursor.getColumnIndex(COLUMNS_LOCATION_LEVEL[0])),
				cursor.getInt(cursor.getColumnIndex(COLUMNS_LOCATION_LEVEL[1])),
				cursor.getInt(cursor.getColumnIndex(COLUMNS_LOCATION_LEVEL[2])),
				cursor.getInt(cursor.getColumnIndex(COLUMNS_LOCATION_LEVEL[3])),
				cursor.getInt(cursor.getColumnIndex(COLUMNS_LOCATION_LEVEL[4]))));
		for(int i=0;i<parentIDList.size();){
			if(parentIDList.get(i)==0) {
				parentIDList.remove(i);
				continue;
			}
			i++;
		}
		
		parentIDs = new int[parentIDList.size()];
		for(int i=0;i<parentIDList.size();i++) {
			parentIDs[i] = parentIDList.get(i);
		}
		
		return parentIDs;
		
//		parentIDs = new int[] {
//				cursor.getInt(cursor.getColumnIndex(COLUMNS_LOCATION_LEVEL[0])),
//				cursor.getInt(cursor.getColumnIndex(COLUMNS_LOCATION_LEVEL[1])),
//				cursor.getInt(cursor.getColumnIndex(COLUMNS_LOCATION_LEVEL[2])),
//				cursor.getInt(cursor.getColumnIndex(COLUMNS_LOCATION_LEVEL[3])),
//				cursor.getInt(cursor.getColumnIndex(COLUMNS_LOCATION_LEVEL[4]))};
//		
//		//temporary fix for cases when some parent IDs are empty and are treated as 0
//		
//		//end of temporary fix
//		
//		return parentIDs;
	}
}
