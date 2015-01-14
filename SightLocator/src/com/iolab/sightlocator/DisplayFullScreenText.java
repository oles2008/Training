package com.iolab.sightlocator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.TextView;

public class DisplayFullScreenText extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		String text = intent.getStringExtra(Tags.EXTRA_TEXT);
		
		ScrollView scroller = new ScrollView(this);
		TextView textView = new TextView(this);
		textView.setText(text);
		textView.setTextSize(24);
		scroller.addView(textView);
		setContentView(scroller);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
}
