package com.iolab.sightlocator;

public class Category {

	private String mCategoryString;
	
	public Category(String string) {
		mCategoryString = string.toLowerCase().trim();
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

		//KOSTYL if Item has no category assigned
		if (item.getCategory() == null
				|| item.getCategory().isEmpty()) {
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
}
