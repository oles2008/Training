package com.iolab.sightlocator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

public class FilterDialogFragment extends DialogFragment{

	public interface FilterDialogListener {
		public void onFilterDialogPositiveClick(DialogFragment dialog);
		public void onFilterDialogNegativeClick(DialogFragment dialog);
	}
	
	FilterDialogListener mListener;
	
	public void onAttach(Activity activity){
		super.onAttach(activity);
		try {
			mListener = (FilterDialogListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement FilterDialogListener");
		}
	}
	
	public Dialog onCreateDialog(Bundle savedInstanceState){
		// Where we track the selected items
		final boolean[] tmpCheckedItems;
		tmpCheckedItems = new boolean[getResources().getInteger(R.integer.marker_category_length)];
		System.arraycopy(Appl.checkedItems, 0, tmpCheckedItems, 0, Appl.checkedItems.length);

		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
		
//		dialogBuilder.setMessage(R.string.dialog_message);
			
		// Set the dialog title
		dialogBuilder.setTitle(R.string.dialog_title);
		
		// Specify the list array, the items to be selected by default (null for none),
		// and the listener through which to receive callback when items are selected
		dialogBuilder.setMultiChoiceItems(R.array.marker_category, Appl.checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				}
			});
			
		// Set the action buttons
		dialogBuilder.setPositiveButton(R.string.dialog_ok_button, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// Send the positive button event back to the host activity
					mListener.onFilterDialogPositiveClick(FilterDialogFragment.this);
				}
			});
			
		dialogBuilder.setNegativeButton(R.string.dialog_cancel_button, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// Send the negative button event back to the host activity
					mListener.onFilterDialogNegativeClick(FilterDialogFragment.this);
					dialog.cancel();
					System.arraycopy(tmpCheckedItems, 0, Appl.checkedItems, 0, Appl.checkedItems.length);
				}
			});
		
		// Create the AlertDialog object and return it
		return dialogBuilder.create();
	}
}
