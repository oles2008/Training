package com.iolab.sightlocator;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

public class LanguagesDialogFragment extends DialogFragment{
	int selectedItem = -1;
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
	
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    builder.setTitle(R.string.languages_dialog_title)
	           .setSingleChoiceItems(R.array.apps_language,-1, new DialogInterface.OnClickListener() {
	        	   
	               public void onClick(DialogInterface dialog, int which) {
	               // The 'which' argument contains the index position
	               // of the selected item
	            	   selectedItem = which;
	           }
	     
	    });
	    builder.setPositiveButton(R.string.dialog_ok_button, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Set into SharedPreferences variable selected in dialog language
				if(selectedItem != -1){
				SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = sharedPref.edit();
				editor.putInt(getString(R.string.application_language), selectedItem);
				editor.commit();
				selectedItem = -1;
				//Log.w("ihor",Integer.toString(sharedPref.getInt(getString(R.string.application_language),0)));
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