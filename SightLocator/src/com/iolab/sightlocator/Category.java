package com.iolab.sightlocator;

import android.os.Parcel;
import android.os.Parcelable;

public class Category implements Parcelable {

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
		if (item.getCategory() == null) {
			return true;
		}

		//KOSTYL "All"
		if (mCategoryString.equals("all")) {
			return true;
		}
		
		//KOSTYL "industry" - "industrial"
		if (mCategoryString.equals("industry")
				&& item.getCategory()
						.toLowerCase()
						.trim()
						.contains("industrial")){
			return true;
		}
		
		if (item.getCategory()
				.toLowerCase()
				.trim()
				.contains(mCategoryString)){
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
