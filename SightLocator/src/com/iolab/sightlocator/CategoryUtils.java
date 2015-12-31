package com.iolab.sightlocator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
	
//	/**
//	 * Create an ArrayList with all marker's categories.
//	 *
//	 * @return the marker categories
//	 */
//	public static ArrayList<String> getMarkerCategories(){
//		List<String> categoriesList = Arrays.asList(
//				Appl.appContext.getResources()
//								.getStringArray(
//										R.array.marker_category));
//		return new ArrayList<String>(categoriesList);
//	}
 
	/**
	 * Create an ArrayList with the selected by user marker's categories.
	 *
	 * @return the ArrayList with selected marker categories
	 */
	public static ArrayList<Category> getSelectedMarkerCategories(){
		ArrayList<Category> selectedMarkerCategories = new ArrayList<Category>();
		for (int i=0; i<Appl.selectedCategories.length; i++) {
			if (Appl.selectedCategories[i]) {
				selectedMarkerCategories.add(new Category(Appl.categoriesValues.get(i).toLowerCase()));
			}
		}
		return selectedMarkerCategories;
	}
	
	/**
	 * Checks if the {@link SightMarkerItem} belongs to the given categories.
	 *
	 * @param categories the categories
	 * @param item the item
	 * @return true, if is item belongs to at least one of the categories
	 */
	public static boolean isItemInCategories(Collection<Category> categories, SightMarkerItem item) {
		for (Category category : categories) {
			if (category.isItemBelongsToThisCategory(item)){
				return true;
			}
		}
		return false;
	}
}
