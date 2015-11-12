package com.iolab.sightlocator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.util.Log;

/**
 * @author 
 *
 */
public class CategoryUtils {
	
	private CategoryUtils(){}

	public static int getCategoryMarkerResId(String category) {
		return 0;
	}
	
	public static int getCategorySelectedMarkerResId(String category) {
		return R.drawable.orange_dot_marker_selected;
	}
	
	/**
	 * @return Array list with marker categories from string resources
	 */
	public static ArrayList<String> getMarkerCategories(){
		List<String> categoriesList = Arrays.asList(
				Appl.appContext.getResources()
								.getStringArray(
										R.array.marker_category));
		return new ArrayList<String>(categoriesList);
	}
	
	public static ArrayList<String> getSelectedMarkerCategories(){
		ArrayList<String> selectedMarkerCategories = new ArrayList<String>();
		for (int i=0; i<Appl.selectedCategories.length; i++) {
			if (Appl.selectedCategories[i]) {
				selectedMarkerCategories.add(getMarkerCategories().get(i));
			}
		}
		Log.d("Marker", selectedMarkerCategories.toString());
		return selectedMarkerCategories;
	}
}
