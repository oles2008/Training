package com.iolab.sightlocator;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

public class LanguagesDialogFragment extends DialogFragment{
	int mSelectedItem = -1;
	String[] arrayOfLanguages; //input for builder.setSingleChoiceItems
	int mItemId;
	LanguagesDialogListener mListener; //we have created listener for dialog buttons
	
	LanguagesDialogFragment(int itemId){
		mItemId = itemId;
	};

	public void onAttach(Activity activity){
		super.onAttach(activity);
		try {
			mListener = (LanguagesDialogListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement LanguagesDialogListener");
		}
	}

	public Dialog onCreateDialog(Bundle savedInstanceState) {
		List<String> itemAvailableLanguages = new ArrayList<String>();
		String sqlQueryParticle = "";
		//here we prepare sqlQueryParticle
		for(int i=0;i<getResources().getStringArray(R.array.content_language_abbr).length;i++){
			sqlQueryParticle = sqlQueryParticle +
					"CASE WHEN " + SightsDatabaseOpenHelper.SIGHT_DESCRIPTION +
					getResources().getStringArray(R.array.content_language_abbr)[i] + " IS NOT NULL AND " + SightsDatabaseOpenHelper.SIGHT_DESCRIPTION +
					getResources().getStringArray(R.array.content_language_abbr)[i] + " <> \"\" THEN \"" + getResources().getStringArray(R.array.content_language_abbr)[i] +
					"\" ELSE NULL END,";
		};
		sqlQueryParticle = sqlQueryParticle.substring(0,sqlQueryParticle.length()-1);
		//here we prepare input for cursors query
		String sqlQuery = "SELECT " + sqlQueryParticle + " FROM " + SightsDatabaseOpenHelper.TABLE_NAME +
							" WHERE " + SightsDatabaseOpenHelper.COLUMN_ID + " = " + mItemId;
		//The cursor returns list of available languages for defined item
		Cursor cursor = Appl.sightsDatabaseOpenHelper.getReadableDatabase().rawQuery(sqlQuery, null);
		//here we transform cursors result into itemAvailableLanguages ArrayList
		if(cursor.getCount() == 1){
			cursor.moveToFirst();
			for(int i=0;i<cursor.getColumnCount();i++){
				if (!cursor.isNull(i)){itemAvailableLanguages.add(cursor.getString(i));}
			}
		}
		cursor.close();
		//here we define what list of languages will be input for dialog builder
		if(itemAvailableLanguages.isEmpty()){
			arrayOfLanguages = getResources().getStringArray(R.array.content_language);
			}
		else{
			arrayOfLanguages = itemAvailableLanguages.toArray(new String[itemAvailableLanguages.size()]);
			}
		//here we create dialog using builder
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    builder.setTitle(R.string.content_lang_dialog_title)
	    	   .setSingleChoiceItems(arrayOfLanguages,-1, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int which) {
	               // The 'which' argument contains the index position of the selected item
	            	   mSelectedItem = which;
	           } 
	    });
	    builder.setPositiveButton(R.string.dialog_ok_button, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Set into SharedPreferences variable selected in dialog language
				if(mSelectedItem != -1){
				SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = sharedPref.edit();
				editor.putString(getString(R.string.content_language),arrayOfLanguages[mSelectedItem]);
				editor.commit();				
				}
				mListener.onLanguagesDialogPositiveClick(LanguagesDialogFragment.this);
			}
		});
	    builder.setNegativeButton(R.string.dialog_cancel_button, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Send the negative button event back to the host activity
				mListener.onLanguagesDialogNegativeClick(LanguagesDialogFragment.this);
				dialog.cancel();
			}
		});
	    return builder.create();
	}
	public interface LanguagesDialogListener {
		public void onLanguagesDialogPositiveClick(DialogFragment dialog);
		public void onLanguagesDialogNegativeClick(DialogFragment dialog);
	}
}