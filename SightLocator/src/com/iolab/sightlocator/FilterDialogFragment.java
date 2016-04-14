package com.iolab.sightlocator;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ListView;

public class FilterDialogFragment extends DialogFragment{

	public interface FilterDialogListener {
		public void onFilterDialogPositiveClick(DialogFragment dialog, List<Category> newCategories, List<Category> oldCategories);
		public void onFilterDialogNegativeClick(DialogFragment dialog);
	}
	
	FilterDialogListener mListener;
	
	public void SetDialogListener(FilterDialogListener filterDialogListener) {
		mListener = filterDialogListener;
	}

	public Dialog onCreateDialog(Bundle savedInstanceState){
		// Where we track the selected items
		final boolean[] tmpCheckedItems;
		tmpCheckedItems = new boolean[Appl.categoriesValues.size()];
		final List<Category> oldCategories = CategoryUtils.getSelectedMarkerCategories();
		System.arraycopy(Appl.selectedCategories, 0, tmpCheckedItems, 0, Appl.selectedCategories.length);

		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

		// Set the dialog title
		dialogBuilder.setTitle(R.string.dialog_title);
		
		// Specify the list array, the items to be selected by default (null for none),
		// and the listener through which to receive callback when items are selected
		dialogBuilder.setMultiChoiceItems(R.array.marker_category_display, Appl.selectedCategories, new DialogInterface.OnMultiChoiceClickListener() {
				
			@SuppressLint("NewApi")
			@Override
			public void onClick(DialogInterface dialog, int which,boolean isChecked) {

				final ListView listView = ((AlertDialog) dialog).getListView();

				// if checked category "ALL"
				if (which == 0) {
					// change the list
					Appl.selectedCategories[0] = isChecked;
					// uncheck all other checkboxes
					for (int j = 1; j < Appl.selectedCategories.length; j++) {
						Appl.selectedCategories[j] = false;
						listView.setItemChecked(j, false);
					}
				// if checked other categories
				} else {
					Appl.selectedCategories[which] = isChecked;
					// uncheck category "ALL"
					Appl.selectedCategories[0] = false;
					listView.setItemChecked(0, false);

					// count how many categories are checked
					int categoriesCount = 0;
					for (int i = 1; i < Appl.selectedCategories.length; i++) {
						if (Appl.selectedCategories[i] == true) {
							categoriesCount++;
						}
					}

                    // if all categories (except "All") are checked (or unchecked)-
                    // then check "ALL" and uncheck all others
                    if ((categoriesCount == Appl.selectedCategories.length - 1)
                            || (categoriesCount == 0)) {
                        Appl.selectedCategories[0] = true;
                        for (int j = 1; j < Appl.selectedCategories.length; j++) {
                            Appl.selectedCategories[j] = false;
                            listView.setItemChecked(j, false);
                        }

                        listView.smoothScrollToPosition(0);
                    }

				}
			}
		});
			
		// Set the action buttons
		dialogBuilder.setPositiveButton(R.string.dialog_ok_button, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// Send the positive button event back to the host activity
					List<Category> newCategories = CategoryUtils.getSelectedMarkerCategories();
					mListener.onFilterDialogPositiveClick(
							FilterDialogFragment.this, newCategories, oldCategories);
				}
			});
			
		dialogBuilder.setNegativeButton(R.string.dialog_cancel_button, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// Send the negative button event back to the host activity
					System.arraycopy(tmpCheckedItems, 0, Appl.selectedCategories, 0, Appl.selectedCategories.length);
					mListener.onFilterDialogNegativeClick(FilterDialogFragment.this);
				}
			});
		
		// Create the AlertDialog object and return it
		return dialogBuilder.create();
	}

	@Override
	public void onCancel(DialogInterface dialog){
		mListener.onFilterDialogNegativeClick(FilterDialogFragment.this);
	}
	
////////////////   LEGACY CODE to work with checkboxes and checkboxes_layout.xml	////////////////
	
//	private ScrollView getScrollView() {
//	ScrollView scr = (ScrollView) getDialog().findViewById(R.id.scrollViewFilter);
//	return scr;
//}
//
//private CheckBox setCheckBox(View checkBoxView, int id, final String name) {
//	CheckBox checkBox = (CheckBox) checkBoxView.findViewById(id);
//	checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//
//		@Override
//		public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
//		}
//	});
//	
//	checkBox.setText(name);
//	return checkBox;
//}

//private List<CheckBox> getCheckBoxList(View checkBoxView) {
//	CheckBox checkBoxCafe = setCheckBox(checkBoxView,
//			R.id.checkbox_Cafe,
//			getResources().getString(R.string.action_filter_cafe));
//	CheckBox checkBoxEntertaiment = setCheckBox(checkBoxView,
//			R.id.checkbox_Entertaiment,
//			getResources().getString(R.string.action_filter_entertaiment));
//	CheckBox checkBoxHisory = setCheckBox(checkBoxView,
//			R.id.checkbox_History,
//			getResources().getString(R.string.action_filter_history));
//	CheckBox checkBoxIndustry = setCheckBox(checkBoxView,
//			R.id.checkbox_Industry,
//			getResources().getString(R.string.action_filter_industry));
//	CheckBox checkBoxLiterature = setCheckBox(checkBoxView,
//			R.id.checkbox_Literature,
//			getResources().getString(R.string.action_filter_literature));
//	CheckBox checkBoxMonument = setCheckBox(checkBoxView,
//			R.id.checkbox_Monument,
//			getResources().getString(R.string.action_filter_monument));
//	CheckBox checkBoxReligion = setCheckBox(checkBoxView,
//			R.id.checkbox_Religion,
//			getResources().getString(R.string.action_filter_religion));
//
//	List<CheckBox> checkBoxList = Arrays.asList(checkBoxCafe,
//			checkBoxEntertaiment, checkBoxHisory, checkBoxIndustry,
//			checkBoxLiterature, checkBoxMonument, checkBoxReligion);
//	return checkBoxList;
//} 

//public Dialog onCreateDialog(Bundle savedInstanceState){
//	final List<String> tmpCheckedItems = new ArrayList<String>();
//	tmpCheckedItems.addAll(Appl.checkedItems);
//	Log.d("MSG2"," tmp checkboxes > " + tmpCheckedItems);
//	
//	View checkBoxView = View.inflate(getActivity(), R.layout.filter_menu, null);
//
//	final CheckBox checkBoxAll = setCheckBox(checkBoxView,
//			R.id.checkbox_All,
//			getResources().getString(R.string.action_filter_all));
//	final List<CheckBox> checkBoxList = getCheckBoxList(checkBoxView);
//
//	// Use the Builder class for convenient dialog construction
//	AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
//	
//	// Set the dialog title
//	dialogBuilder.setTitle(R.string.dialog_title);
//
//	dialogBuilder.setView(checkBoxView);
//	
//	if (Appl.checkedItems.contains(checkBoxAll.getText())) {
//		checkBoxAll.setChecked(true);
//	}
//	
//	for (CheckBox checkBox : checkBoxList){
//		if(Appl.checkedItems.contains(checkBox.getText())) {
//			checkBox.setChecked(true);
//		}
//	}
//
//	checkBoxAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//		
//		@Override
//		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//			if (isChecked == true){
//				for (CheckBox checkBox : checkBoxList){
//					checkBox.setChecked(false);
//				}
//				Appl.checkedItems.clear();
//				Appl.checkedItems.add((String) checkBoxAll.getText());
//			} else {
//				Appl.checkedItems.remove((String) checkBoxAll.getText());
//			}
//		}
//	});
//	
//	for (final CheckBox checkBox : checkBoxList){
//		checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//			
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				if (isChecked == true) {
//					// remove checkmark from "All" checkbox
//					checkBoxAll.setChecked(false);
//					
//					// remove "All" from list
//					if (Appl.checkedItems.contains(checkBoxAll.getText())){
//						Appl.checkedItems.remove(checkBoxAll.getText());
//					}
//					
//					// add checkbox name to list
//					Appl.checkedItems.add((String) checkBox.getText());
//					
//					// if all checkboxes are checked - check "All" and uncheck all other  
//					if (Appl.checkedItems.size() == getResources().getInteger(R.integer.action_filter_list_length)) {
//						for (CheckBox checkBox : checkBoxList){
//							checkBox.setChecked(false);
//						}
//						getScrollView().scrollTo(0, 0);
//						checkBoxAll.setChecked(true);
//						Appl.checkedItems.clear();
//						Appl.checkedItems.add((String) checkBoxAll.getText());
//					}
//				} else {
//					// remove checkbox name from the list
//					if (Appl.checkedItems.contains(checkBox.getText())) {
//						Appl.checkedItems.remove(checkBox.getText());
//					}
//				}
//			}
//		});
//	}
//	
//	// Set the action buttons
//	dialogBuilder.setPositiveButton(R.string.dialog_ok_button, new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				// Send the positive button event back to the host activity
//				mListener.onFilterDialogPositiveClick(FilterDialogFragment.this);
//			}
//		});
//		
//	dialogBuilder.setNegativeButton(R.string.dialog_cancel_button, new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				// Send the negative button event back to the host activity
//				mListener.onFilterDialogNegativeClick(FilterDialogFragment.this);
//				Appl.checkedItems.clear();
//				Appl.checkedItems.addAll(tmpCheckedItems);
//				dialog.cancel();
//			}
//		});
//	
//	// Create the AlertDialog object and return it
//	return dialogBuilder.create();
//}
	
}
