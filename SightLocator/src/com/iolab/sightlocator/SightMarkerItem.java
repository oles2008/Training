package com.iolab.sightlocator;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

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
	private String category;
	private int id;
	private int[] parentIDs;
	//not to be saved in Parcel
	private Cluster<SightMarkerItem> mCluster;
	
	public SightMarkerItem(int id) {
		this.id = id;
	}
	
	public SightMarkerItem(LatLng position, 
	                       String title, 
	                       String address, 
	                       String snippet, 
	                       String imageURI, 
	                       String category, 
	                       int id, 
	                       int[] parentIDs) {
		this.position = position;
		this.title = title;
		this.snippet = snippet;
		this.imageURI = imageURI;
		this.category = category;
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
		this.title=array[0];
		this.address=array[1];
		this.snippet=array[2];
		this.imageURI=array[3];
		this.category=array[4];
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
	
	public String getCategory() {
		return category;
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
		String[] array = {this.title, this.address, this.snippet, this.imageURI, this.category};
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

}
