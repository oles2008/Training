package com.iolab.sightlocator;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

public class LanguagesDialogFragment extends DialogFragment {

	public interface LanguagesDialogListener {
		public void onFilterDialogPositiveClick(String languageTag);
		public void onFilterDialogNegativeClick();
	}
	
	int mSelectedItem = -1;
	int mItemId = -1;
	String mCurrentLanguage;
	String[] arrayOfLanguages; //input for builder.setSingleChoiceItems
	String[] availableLanguages;//goes from Action
	String[] langsForSharedPreference;
	LanguagesDialogListener mLanguageDialogListener;
	
	
	//class constructor
	LanguagesDialogFragment(String[] languages, LanguagesDialogListener langDialogListener, String currentLang){
		availableLanguages = languages;
		mLanguageDialogListener = langDialogListener;
		mCurrentLanguage = currentLang;
	}
	
	LanguagesDialogFragment(LanguagesDialogListener langDialogListener, String currentLang){
		availableLanguages = null;
		mLanguageDialogListener = langDialogListener;
		mCurrentLanguage = currentLang;
	}
	
	//List of abbreviations is transformed into long names for dialog builder
	//If array  from action is empty then list of languages from strings will be taken
	private void setArrayOfLanguages() {
		if (availableLanguages != null && availableLanguages.length != 0) {
			arrayOfLanguages = Language.getDisplayLanguagesFromAbbrArray(availableLanguages);
			langsForSharedPreference = availableLanguages;
		} else {
			arrayOfLanguages = Language.getDisplayLanguagesFromAbbrArray(Appl.appContext.getResources().getStringArray(
					R.array.content_language_abbr));
			langsForSharedPreference = Appl.appContext.getResources().getStringArray(
					R.array.content_language_abbr);
		}
	}
	
	private int getLanguagePosition(String languageTag){
		for(int i=0;i<langsForSharedPreference.length;i++) {
			if(languageTag.equals(langsForSharedPreference[i])){
				return i;
			}
		}
		return -1;
	}
	
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		//prepare input for builder
		setArrayOfLanguages();
		mSelectedItem = getLanguagePosition(mCurrentLanguage);
		//create dialog using builder
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    builder.setTitle(R.string.content_lang_dialog_title)
	    	   .setSingleChoiceItems(arrayOfLanguages,mSelectedItem, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int which) {
	               // The 'which' argument contains the index position of the selected item
	            	   mSelectedItem = which;
	           }
	    });
		builder.setPositiveButton(R.string.dialog_ok_button, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Set into SharedPreferences variable selected in
				// dialog language
				String lang = null;
				if (mSelectedItem != getLanguagePosition(mCurrentLanguage)) {
					lang = langsForSharedPreference[mSelectedItem];
				}
				mLanguageDialogListener.onFilterDialogPositiveClick(lang);
			}
		});
	    builder.setNegativeButton(R.string.dialog_cancel_button, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mLanguageDialogListener.onFilterDialogNegativeClick();
			}
		});
	    return builder.create();
	}	
}