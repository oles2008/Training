package com.iolab.sightlocator;

import android.os.Parcel;
import android.os.Parcelable;

public class Category implements Parcelable {

	public static final String CATEGORY_ALL = "all";
	
	private String mCategoryString;
	private String mCategoryValue;
	
	public Category(String string) {
		mCategoryString = string.toLowerCase().trim();
	}
	
	public Category(Parcel in) {
		this(in.readString());
	}
	
	@Override
	public String toString() {
		return mCategoryString.toLowerCase().trim();
	}

	@Override
	public boolean equals(Object other){
		if (!(other instanceof Category)) {
			return false;
		}
		return toString()
				.equals(other.toString());
	}
	
	public boolean isItemBelongsToThisCategory(SightMarkerItem item){
		//TODO KOSTYL
		if (item.getCategories() == null) {
			return true;
		}

		//KOSTYL "All"
		if (mCategoryString.equals(CATEGORY_ALL)) {
			return true;
		}
		
		//KOSTYL "industry" - "industrial"
		if (mCategoryString.equals("industry")
				&& item.getCategories().containsKey("industrial")){
			return true;
		}
		
		if (item.getCategories().containsKey(mCategoryString)){
			return true;
		}
		return false;
	}
	
	public static final Parcelable.Creator<Category> CREATOR = new Parcelable.Creator<Category>() {
		public Category createFromParcel(Parcel in) {
			return new Category(in);
		}

		public Category[] newArray(int size) {
			return new Category[size];
		}
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mCategoryString);
		
	}
}
