package com.iolab.sightlocator;

import android.os.Bundle;
import android.widget.TextView;

public class DisplayHelpTextActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help_text);

		TextView textView = (TextView) findViewById(R.id.textView_help_content);
		textView.setTextSize(24);
	}
}
