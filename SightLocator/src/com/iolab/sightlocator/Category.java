package com.iolab.sightlocator;

public class Category {

	private String mString;
	
	public Category(String string) {
		mString = string.toLowerCase().trim();
	}
	
	@Override
	public String toString() {
		return mString.toLowerCase().trim();
	}
	
	public boolean equals(){
		return false;
	}
	
	public boolean isItemBelongsToThisCategory(SightMarkerItem item){
		if (item.getCategory()
				.toLowerCase()
				.trim()
				.contains(mString)){
			return true;
		}
		return false;
	}
}
