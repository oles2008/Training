package com.iolab.sightlocator;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterItem;

public class SightMarkerItem implements ClusterItem {
	
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

	@Override
	public LatLng getPosition() {
		return position;
	}

}
