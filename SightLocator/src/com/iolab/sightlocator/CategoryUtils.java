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
		return R.drawable.orange_map_marker;
	}
	
	public static int getCategorySelectedMarkerResId(String category) {
		return R.drawable.orange_map_marker_selected;
	}
	
	/**
	 * Create an ArrayList with all marker's categories.
	 *
	 * @return the marker categories
	 */
	public static ArrayList<String> getMarkerCategories(){
		List<String> categoriesList = Arrays.asList(
				Appl.appContext.getResources()
								.getStringArray(
										R.array.marker_category));
		return new ArrayList<String>(categoriesList);
	}
	
 
	/**
	 * Create an ArrayList with the selected by user marker's categories.
	 *
	 * @return the ArrayList with selected marker categories
	 */
	public static ArrayList<String> getSelectedMarkerCategories(){
		ArrayList<String> selectedMarkerCategories = new ArrayList<String>();
		for (int i=0; i<Appl.selectedCategories.length; i++) {
			if (Appl.selectedCategories[i]) {
				selectedMarkerCategories.add(getMarkerCategories().get(i).toLowerCase());
			}
		}
		Log.d("Marker", selectedMarkerCategories.toString());
		return selectedMarkerCategories;
	}
}
