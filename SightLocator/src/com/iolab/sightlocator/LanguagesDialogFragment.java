package com.iolab.sightlocator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

public class LanguagesDialogFragment extends DialogFragment {
	int mSelectedItem = -1;
	int mItemId = -1;
	String[] arrayOfLanguages; //input for builder.setSingleChoiceItems
	String[] availableLanguages;//goes from Action
	LanguagesDialogListener mListener; //we have created listener for dialog buttons
	
	//class constructor
	LanguagesDialogFragment(String[] languages){
		availableLanguages = languages;
	}
	
	LanguagesDialogFragment(){
		availableLanguages = null;
	}
		
	public void onAttach(Activity activity){
		super.onAttach(activity);
		try {
			mListener = (LanguagesDialogListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement LanguagesDialogListener");
		}
	}

	//List of abbreviations is transformed into long names for dialog builder
	//If array  from action is empty then list of languages from strings will be taken
	private void setArrayOfLanguages() {
		if (availableLanguages != null && availableLanguages.length != 0) {
			arrayOfLanguages = Language.getDisplayLanguagesFromAbbrArray(availableLanguages);
		} else {
			arrayOfLanguages = Language.getDisplayLanguagesFromAbbrArray(Appl.appContext.getResources().getStringArray(
					R.array.content_language_abbr));
		}
	}
	
	private void setLanguageIntoPreference(String langToSet){
		SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(getString(R.string.content_language),langToSet);
		editor.commit();						
	}
	
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		//run action
//		Intent intent = new Intent(getActivity(), SightsIntentService.class);
//		intent.putExtra(SightsIntentService.ACTION,
//				new GetAvailableContentLanguagesAction(mItemId));
//		getActivity().startService(intent);
//		//prepare input for builder
		setArrayOfLanguages();
		//create dialog using builder
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
				setLanguageIntoPreference(arrayOfLanguages[mSelectedItem]);
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