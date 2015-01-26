package com.iolab.sightlocator;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class BaseActivity extends Activity{
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
		case R.id.action_about:
			showAbout();
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
		Toast.makeText(getApplicationContext(), "clicked Help menu", Toast.LENGTH_SHORT).show();
	}
	
	public void onPause(){
		super.onPause();
	}


}
