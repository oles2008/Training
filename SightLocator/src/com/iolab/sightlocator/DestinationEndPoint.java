package com.iolab.sightlocator;

import java.util.List;

/**
 * The class representing a single navigation action.
 * 
 * @author Oles Potiatynyk
 */
public class DestinationEndPoint {
	
	private int mID;
	private List<String> mCategories;
	private String mLanguage;
	
	public DestinationEndPoint(int id, List<String> categories, String language){
		mID = id;
		mCategories = categories;
		mLanguage = language;
	}
	
	public DestinationEndPoint(int id){
		this(id, null, null);
	}
	
	public int getID() {
		return mID;
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
