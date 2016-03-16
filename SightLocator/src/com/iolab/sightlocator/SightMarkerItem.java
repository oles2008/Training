package com.iolab.sightlocator;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.algo.StaticCluster;

public class SightMarkerItem implements ClusterItem, Parcelable {

	private LatLng position;
	private String title;
	private String snippet;
	private String address;
	private String imageURI;
	private String imageSourceType;
	private String category;
	private Map<String, Double> categories;
	private int id;
	private int[] parentIDs;
	//not to be saved in Parcel
	private Cluster<SightMarkerItem> mCluster;

	final double DEFAULT_PRIORITY = 50;
	
	public SightMarkerItem(int id) {
		this.id = id;
	}
	
	public SightMarkerItem(LatLng position, 
	                       String title, 
	                       String address, 
	                       String snippet, 
	                       String imageURI,
	                       String imageSource, 
	                       String categoryString, 
	                       int id, 
	                       int[] parentIDs) {
		this.position = position;
		this.title = title;
		this.snippet = snippet;
		this.imageURI = imageURI;
		this.imageSourceType = imageSource;
		this.category = categoryString;
		this.categories = parseCategoryString(categoryString);
		this.parentIDs = parentIDs;
		this.id = id;
	}
	
	public SightMarkerItem(LatLng position, 
							String title, 
							String address, 
							int[] parentIDs, 
							String category) {
		this.position = position;
		this.title = title;
		this.category = category;
		this.categories = parseCategoryString(category);
		this.parentIDs = parentIDs;
	}
	
	public SightMarkerItem(LatLng position, 
	                       String title, 
	                       String snippet, 
	                       String color, 
	                       int[] parentIDs) {
		this.position = position;
		this.title = title;
		this.snippet = snippet;
		this.category = color;
		this.categories = parseCategoryString(color);
		this.parentIDs = parentIDs;
	}
	
	public SightMarkerItem(LatLng position, 
	                       String title, 
	                       String snippet, 
	                       String color) {
		this.position = position;
		this.title = title;
		this.snippet = snippet;
		this.category = color;
		this.categories = parseCategoryString(color);
	}
	
	public SightMarkerItem(Marker marker) {
		this.position = marker.getPosition();
		this.title = marker.getTitle();
		this.snippet = marker.getSnippet();
	}
	
	public SightMarkerItem(MarkerOptions markerOptions) {
		this.position = markerOptions.getPosition();
		this.title = markerOptions.getTitle();
		this.snippet = markerOptions.getSnippet();
	}
	
	public SightMarkerItem(Parcel parcel) {
		this.position=parcel.readParcelable(LatLng.class.getClassLoader());
		String[] array = parcel.createStringArray();
		this.title = array[0];
		this.address = array[1];
		this.snippet = array[2];
		this.imageURI = array[3];
		this.imageSourceType = array[4];
		this.category = array[5];
		this.categories = parseCategoryString(array[5]);
		this.parentIDs = parcel.createIntArray();
		this.id = parcel.readInt();
	}
	
	public static final Parcelable.Creator<SightMarkerItem> CREATOR = new Creator<SightMarkerItem>() {
		
		@Override
		public SightMarkerItem[] newArray(int size) {
			return new SightMarkerItem[size];
		}
		
		@Override
		public SightMarkerItem createFromParcel(Parcel source) {
			return new SightMarkerItem(source);
		}
	}; 

	@Override
	public LatLng getPosition() {
		return position;
	}
	
	public void setPosition(LatLng position) {
		this.position = position;
	}
	
	public void setParentIDs(int[] parentIDs) {
		this.parentIDs = parentIDs;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getAddress(){
		return address;
	}
	
	public void setAddress(String address){
		this.address = address;
	}
	
	public String getSnippet() {
		return snippet;
	}
	
	public String getImageURI() {
		return imageURI;
	}

	public String getImageSourceType() {
		return imageSourceType;
	}
	
	public String getCategory() {
		return category;
	}
	
	public Map<String, Double> getCategories(){
		return categories;
	}
	
	public double getItemPriority(){
		double prior = 0;
		for(int i = 0; i < Appl.selectedCategories.length; i++){
			if(Appl.selectedCategories[i]){
				String category = Appl.categoriesValues.get(i);
				
				// get priority if all categories are selected
				if(category.equals(Category.CATEGORY_ALL)){
					for(String itemCategory : categories.keySet()){
						if(categories.containsKey(itemCategory) && 
								Appl.categoryPriorities.containsKey(itemCategory)){
							prior += categories.get(itemCategory)
								  * Appl.categoryPriorities.get(itemCategory);
						}
					}
					return prior;
				}
				
				// update priority for selected category
				if(categories.containsKey(category)){
					if(categories.containsKey(category) && 
							Appl.categoryPriorities.containsKey(category)){
						prior += categories.get(category) * Appl.categoryPriorities.get(category);											
					}
				}
			}
		}
		return prior;
	}
	
	public int getID() {
		return id;
	}
	
	public int[] getParentIDs() {
		return parentIDs;
	}
	
	public double getLatitude() {
		return position.latitude;
	}
	
	public double getLongitude() {
		return position.longitude;
	}
	
	/**
	 * Sets the cluster containing this item.
	 *
	 * @param cluster the new cluster
	 */
	public void setCluster(Cluster<SightMarkerItem> cluster){
		mCluster = cluster;
	}
	
	/**
	 * Sets the category.
	 *
	 * @param category the new category
	 */
	public void setCategory(String category) {
		this.category = category;
		this.categories = parseCategoryString(category);
	}
	
	/**
	 * Gets the cluster which contains this item if the latter is clustered.
	 *
	 * @return the cluster which contains this item if the latter is clustered,
	 *         {@code <code>null</code>} otherwise
	 */
	public Cluster<SightMarkerItem> getCluster() {
		return mCluster;
	}
	
	/**
	 * Checks if this marker clustered.
	 *
	 * @return true, if is clustered
	 */
	public boolean isClustered() {
		return (mCluster != null);
	}
	
	/**
	 * Creates {@link MarkerOptions} for this item's position, title and snippet.
	 *
	 * @return the marker options
	 */
	public MarkerOptions getMarkerOptions() {
		return new MarkerOptions().position(getPosition()).title(getTitle()).snippet(getSnippet());
	}
	
	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeParcelable(position, flags);
		String[] array = {this.title, this.address, this.snippet, this.imageURI, this.imageSourceType, this.category};
		parcel.writeStringArray(array);
		parcel.writeIntArray(parentIDs);
		parcel.writeInt(this.id);
	}

	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public int hashCode() {
		return id;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SightMarkerItem)) {
			return false;
		}
		SightMarkerItem item = (SightMarkerItem) obj;
		return this.id == item.id;
	}
	
	private Map<String, Double> parseCategoryString(String categoryString){
		Map<String, Double> categories = new HashMap<String, Double>();
		if(categoryString != null && !categoryString.isEmpty()){
			String[] items = categoryString.trim().split(Category.SEPARATOR);
			for(String item : items){
				String[] splitItem = item.trim().split(Category.PRIO_SEPARATOR);
				if(splitItem.length == 2){
					categories.put(splitItem[0], Double.parseDouble(splitItem[1]));				
				} else {
					categories.put(splitItem[0], DEFAULT_PRIORITY);
				}
			}
		}
		return categories;
	}
}
