package com.iolab.sightlocator;

import java.util.ArrayList;
import java.util.Locale;

import com.google.android.gms.maps.model.LatLng;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class GetAvailableContentLanguagesAction implements ServiceAction, Parcelable{
	int mItemId = -1;
	
	public GetAvailableContentLanguagesAction(int id) {
		mItemId = id;
	}
	
	private GetAvailableContentLanguagesAction(Parcel parcel){
		this(parcel.readInt());
	}
	
	public static final Parcelable.Creator<GetAvailableContentLanguagesAction> CREATOR = new Parcelable.Creator<GetAvailableContentLanguagesAction>() {
		public GetAvailableContentLanguagesAction createFromParcel(Parcel in) {
			return new GetAvailableContentLanguagesAction(in);
		}

		public GetAvailableContentLanguagesAction[] newArray(int size) {
			return new GetAvailableContentLanguagesAction[size];
		}
	};
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeInt(mItemId);
	}
	
	private String[] getListOfLanguages() {
	//private ArrayList<String> getListOfLanguages() {
		ArrayList<String> itemAvailableLanguages = new ArrayList<String>();
		String sqlQueryParticle = "";
		// here we prepare sqlQueryParticle
		for (int i = 0; i < Appl.appContext.getResources().getStringArray(
				R.array.content_language_abbr).length; i++) {
			sqlQueryParticle = sqlQueryParticle
					+ "CASE WHEN "
					+ SightsDatabaseOpenHelper.SIGHT_DESCRIPTION
					+ Appl.appContext.getResources().getStringArray(
							R.array.content_language_abbr)[i]
					+ " IS NOT NULL AND "
					+ SightsDatabaseOpenHelper.SIGHT_DESCRIPTION
					+ Appl.appContext.getResources().getStringArray(
							R.array.content_language_abbr)[i]
					+ " <> \"\" THEN \""
					+ Appl.appContext.getResources().getStringArray(
							R.array.content_language_abbr)[i]
					+ "\" ELSE NULL END,";
		}
		;
		sqlQueryParticle = sqlQueryParticle.substring(0,
				sqlQueryParticle.length() - 1);
		// here we prepare input for cursors query
		String sqlQuery = "SELECT " + sqlQueryParticle + " FROM "
				+ SightsDatabaseOpenHelper.TABLE_NAME + " WHERE "
				+ SightsDatabaseOpenHelper.COLUMN_ID + " = " + mItemId;
		// The cursor returns list of available languages for defined item
		Cursor cursor = Appl.sightsDatabaseOpenHelper.getReadableDatabase()
				.rawQuery(sqlQuery, null);
		// here we transform cursors result into itemAvailableLanguages ArrayList
		if (cursor.getCount() == 1) {
			cursor.moveToFirst();
			for (int i = 0; i < cursor.getColumnCount(); i++) {
				if (!cursor.isNull(i)) {
					Locale locale = new Locale(cursor.getString(i));
					itemAvailableLanguages.add(locale.getLanguage());
				}
			}
		}
		cursor.close();
		return itemAvailableLanguages.toArray(new String[itemAvailableLanguages.size()]);
	}

	@Override
	public void runInService() {
		Bundle resultData = new Bundle();
		resultData.putStringArray(Tags.AVAILABLE_LANGUAGES, getListOfLanguages());
		Appl.receiver.send(0, resultData);
		
	}

}
