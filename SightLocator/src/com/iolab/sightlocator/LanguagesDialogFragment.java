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
	String mStr="";
	public interface LanguagesDialogListener {
		public void onLanguagesDialogPositiveClick(DialogFragment dialog);
		public void onLanguagesDialogNegativeClick(DialogFragment dialog);
	}
	
	LanguagesDialogListener mListener; //we have created listener for dialog buttons
	
	public void onAttach(Activity activity){
		super.onAttach(activity);
		try {
			mListener = (LanguagesDialogListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement LanguagesDialogListener");
		}
	}

	public Dialog onCreateDialog(Bundle savedInstanceState) {
		List<String> itemAvailableLanguages = new ArrayList<String>();//lang
		CharSequence[] arrayOfLanguages;//lang
		Cursor cursor = Appl.sightsDatabaseOpenHelper.getReadableDatabase(). //lang
				rawQuery( //lang
		"SELECT CASE WHEN DESCRIPTION_EN IS NOT NULL AND DESCRIPTION_EN <> '' THEN 'en' ELSE NULL END,"+ //lang
		" CASE WHEN DESCRIPTION_UK IS NOT NULL AND DESCRIPTION_EN <> '' THEN 'uk' ELSE NULL END"+ //lang
				" FROM " + SightsDatabaseOpenHelper.TABLE_NAME + //lang
				" WHERE " + SightsDatabaseOpenHelper.COLUMN_ID + " = 11", null);  //lang change hardcoded id to once from bandle
		
		if(cursor.getCount() == 1){  //lang
			cursor.moveToFirst();
			for(int i=0;i<cursor.getColumnCount();i++){
				if (!cursor.isNull(i)){itemAvailableLanguages.add(cursor.getString(i));}
			}  //lang
		}  //lang
		cursor.close();//lang
		
		if(itemAvailableLanguages.isEmpty()){
			arrayOfLanguages = getResources().getStringArray(R.array.content_language);}//lang
		else{
			arrayOfLanguages = (CharSequence[]) itemAvailableLanguages.toArray(new String[itemAvailableLanguages.size()]);}//lang
			//Log.w("ihor",itemAvailableLanguages.toArray(new String[itemAvailableLanguages.size()]).getClass().getName());}//lang
		
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    builder.setTitle(R.string.content_lang_dialog_title)
	    	   .setSingleChoiceItems(arrayOfLanguages,-1, new DialogInterface.OnClickListener() {
	        	   
	               public void onClick(DialogInterface dialog, int which) {
	               // The 'which' argument contains the index position
	               // of the selected item
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
				editor.putInt(getString(R.string.content_language), mSelectedItem);
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
}