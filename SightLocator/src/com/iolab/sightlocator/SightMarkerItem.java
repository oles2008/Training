package com.iolab.sightlocator;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterItem;

public class SightMarkerItem implements ClusterItem, Parcelable {
	
	private LatLng position;
	public String title;
	public String snippet;
	public String color;
	
	public SightMarkerItem(LatLng position, String title, String snippet, String color) {
		this.position = position;
		this.title = title;
		this.snippet = snippet;
		this.color = color;
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
		this.snippet=array[1];
		this.color=array[2];
		
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
	
	public String getTitle() {
		return title;
	}
	
	public String getSnippet() {
		return snippet;
	}
	
	public String getColor() {
		return color;
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
		String[] array = {this.title, this.snippet, this.color};
		parcel.writeStringArray(array);
		parcel.writeParcelable(position, flags);
	}

	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public int hashCode() {
		return position.hashCode()+title.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SightMarkerItem)) {
			return false;
		}
		SightMarkerItem item = (SightMarkerItem) obj;
		return this.position.equals(item.getPosition())
				&& this.title.equals(item.getTitle());
	}

}
