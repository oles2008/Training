package com.iolab.sightlocator;

import java.util.Collection;
import java.util.List;

/**
 * The class representing a single navigation action.
 * 
 * @author Oles Potiatynyk
 */
public class DestinationEndPoint {
	
	private int mID;
	private Collection<SightMarkerItem> mClusteredItems;
	private List<String> mCategories;
	private String mLanguage;
	
	
	public DestinationEndPoint(int id, Collection<SightMarkerItem> clusteredItems, List<String> categories, String language){
		mID = id;
		mClusteredItems = clusteredItems;
		mCategories = categories;
		mLanguage = language;
	}
	
	public DestinationEndPoint(int id){
		this(id, null, null, null);
	}
	
	public DestinationEndPoint(int id, Collection<SightMarkerItem> clusteredItems){
		this(id, clusteredItems, null, null);
	}
	
	public int getID() {
		return mID;
	}
	
	public Collection<SightMarkerItem> getClusteredItems() {
		return mClusteredItems;
	}
	
	public void setClusteredItems(Collection<SightMarkerItem> clusteredItems) {
		mClusteredItems = clusteredItems;
	}
	
	public List<String> getCategories() {
		return mCategories;
	}
	
	public void setCategories(List<String> categories) {
		mCategories = categories;
	}
	
	public String getLanguage() {
		return mLanguage;
	}
	
	public void setLanguage(String language) {
		mLanguage = language;
	}
}
