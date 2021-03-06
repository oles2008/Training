package com.iolab.sightlocator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * The class representing a single navigation action.
 * 
 * @author Oles Potiatynyk
 */
public class DestinationEndPoint implements Parcelable {
	
	private int mID;
	private Collection<SightMarkerItem> mClusteredItems = new ArrayList<SightMarkerItem>();
	private List<Category> mCategories = new ArrayList<Category>();
	private String mLanguage;
	
	
	public DestinationEndPoint(int id, Collection<SightMarkerItem> clusteredItems, List<Category> categories, String language){
		mID = id;
		mClusteredItems = clusteredItems;
		if(mClusteredItems == null) {
			mClusteredItems = new ArrayList<SightMarkerItem>();
		}
		mCategories = categories;
		if(mCategories == null) {
			mCategories = new ArrayList<Category>();
		}
		mLanguage = language;
	}
	
	public DestinationEndPoint(Parcel src){
		mID = src.readInt();
		mClusteredItems = src.readArrayList(SightMarkerItem.class.getClassLoader());
		if(mClusteredItems == null) {
			mClusteredItems = new ArrayList<SightMarkerItem>();
		}
		mCategories = src.readArrayList(Category.class.getClassLoader());
		if(mCategories == null) {
			mCategories = new ArrayList<Category>();
		}
		mLanguage = src.readString();
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
		if(mClusteredItems == null){
			mClusteredItems = new ArrayList<SightMarkerItem>();
		}
	}
	
	public List<Category> getCategories() {
		return mCategories;
	}
	
	public void setCategories(List<Category> categories) {
		mCategories = categories;
		if(categories == null) {
			mCategories = new ArrayList<Category>();
		}
	}
	
	public String getLanguage() {
		return mLanguage;
	}
	
	public void setLanguage(String language) {
		mLanguage = language;
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("ID: ");
		result.append(getID());
		result.append(", categories: [");
		if (getCategories() != null) {
			for (Category category : getCategories()) {
				result.append(category.toString());
				result.append(", ");
			}
			result.replace(result.length() - 1, result.length(), "]");
		}
		return result.toString();
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(mID);
		dest.writeList(new ArrayList<SightMarkerItem>(mClusteredItems));
		dest.writeList(mCategories);
		dest.writeString(mLanguage);
	}
	
	public static final Parcelable.Creator<DestinationEndPoint> CREATOR = new Parcelable.Creator<DestinationEndPoint>() {
		public DestinationEndPoint createFromParcel(Parcel in) {
			return new DestinationEndPoint(in);
		}

		public DestinationEndPoint[] newArray(int size) {
			return new DestinationEndPoint[size];
		}
	};
}
