package com.iolab.sightlocator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.maps.model.LatLngBounds;
import com.iolab.sightlocator.Appl.ViewUpdateListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

public class LanguagesDialogFragment extends DialogFragment implements ViewUpdateListener {
	int mSelectedItem = -1;
	int mItemId = -1;
	int mFlag= -1;
	String[] arrayOfLanguages; //input for builder.setSingleChoiceItems
	List<String> itemAvailableLanguages;
	LanguagesDialogListener mListener; //we have created listener for dialog buttons
	
	//class constructor
	LanguagesDialogFragment(int id) {
		mItemId = id;
	}
	
	//added 12/13/15
	private void showLanguagesDialog(int itemId) {
	if(mFlag == -1){
	LanguagesDialogFragment dialogLangs = new LanguagesDialogFragment(itemId);
	dialogLangs.itemAvailableLanguages = this.itemAvailableLanguages;
	dialogLangs.mFlag = 1;
	dialogLangs.setArrayOfLanguages();
	dialogLangs.show(getFragmentManager(), "ALanguagesDialogFragment");
		}
	}
	
	public void onUpdateView(Bundle bundle) {
		if (bundle.containsKey(Tags.AVAILABLE_LANGUAGES)) {
			itemAvailableLanguages = bundle
					.getStringArrayList(Tags.AVAILABLE_LANGUAGES);
		} else {
			itemAvailableLanguages = new ArrayList<String>();
		}
		setArrayOfLanguages();
		//added 12/13/15
		showLanguagesDialog(mItemId);
		dismiss();
		//((BaseAdapter) ((AlertDialog) getDialog()).getListView().getAdapter()).notifyDataSetChanged();
	}
	
	public void onAttach(Activity activity){
		super.onAttach(activity);
		try {
			mListener = (LanguagesDialogListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement LanguagesDialogListener");
		}
	}

	//The method define what list of languages will be input for dialog builder
	private void setArrayOfLanguages() {
		if (itemAvailableLanguages != null && !itemAvailableLanguages.isEmpty()) {
			arrayOfLanguages = itemAvailableLanguages
					.toArray(new String[itemAvailableLanguages.size()]);
		} else {
			String[] techArray = new String[Appl.appContext.getResources().getStringArray(
					R.array.content_language_abbr).length];
			for (int i = 0; i < Appl.appContext.getResources().getStringArray(
					R.array.content_language_abbr).length; i++) {
				Locale locale = new Locale(Appl.appContext.getResources().getStringArray(
						R.array.content_language_abbr)[i]);
				techArray[i] = locale.getDisplayLanguage();
			}
			arrayOfLanguages = techArray;
		}
	}

	public Dialog onCreateDialog(Bundle savedInstanceState) {
		//start action
		if(mFlag == -1){
		Intent intent = new Intent(getActivity(), SightsIntentService.class);
		intent.putExtra(SightsIntentService.ACTION,
				new GetAvailableContentLanguagesAction(mItemId));
		getActivity().startService(intent);
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
	
	@Override
	public void onResume() {
		super.onResume();
		Appl.subscribeForViewUpdates(this);
	}
	
    @Override
	public void onPause() {
		super.onPause();
		Appl.unsubscribeFromViewUpdates(this);
	}
}