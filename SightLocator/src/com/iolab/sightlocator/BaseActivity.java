package com.iolab.sightlocator;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class BaseActivity extends Activity implements FilterDialogFragment.FilterDialogListener{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	public void onResume(){
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		
		case R.id.action_settings:
			showSettings();
			return true;
		
		case R.id.action_help:
			showHelp();
			return true;

//		case R.id.action_quick_help:
//			showHelp();
//			return true;

		case R.id.action_about:
			showAbout();
			return true;

//		case R.id.action_quick_about:
//			showAbout();
//			return true;

		case R.id.action_filter:
			showFilterDialog();
			return true;

		case R.id.action_quick_filter:
			showFilterDialog();
			return true;
			

		
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void showAbout() {
		Toast.makeText(getApplicationContext(), "clicked About menu", Toast.LENGTH_SHORT).show();
	}

	private void showSettings(){
		Toast.makeText(getApplicationContext(), "clicked Settings menu", Toast.LENGTH_SHORT).show();
	}
	
	private void showHelp(){
		Intent intent =  new Intent(this, DisplayHelpTextActivity.class);
		startActivity(intent);
	}

	public void showFilterDialog() {
		// Create an instance of the dialog fragment and show it
		DialogFragment dialog = new FilterDialogFragment();
		dialog.show(getFragmentManager(), "FilterDialogFragment");
	}

	// The dialog fragment receives a reference to this Activity through the
	// Fragment.onAttach() callback, which it uses to call the following methods
	// defined by the NoticeDialogFragment.NoticeDialogListener interface
	@Override
	public void onFilterDialogPositiveClick(DialogFragment dialog) {
	}

	@Override
	public void onFilterDialogNegativeClick(DialogFragment dialog) {
		Appl.notifyMarkerCategoryUpdates();
	}
	
	public void showLanguagesDialog() {
		// Create an instance of the dialog fragment and show it
		DialogFragment dialogLangs = new LanguagesDialogFragment();
		dialogLangs.show(getFragmentManager(), "LanguagesDialogFragment");
	}
	
	public void onPause(){
		super.onPause();
	}

}
