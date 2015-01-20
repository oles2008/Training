package com.iolab.sightlocator;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class DisplayFullScreenTextActivity extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		String text = intent.getStringExtra(Tags.EXTRA_TEXT);
		String pathToImage = intent.getStringExtra("image");
		
		setContentView(R.layout.full_screen_text);

		ImageView imageView = (ImageView) findViewById(R.id.imageView_full_screen);
		TextView textView = (TextView) findViewById(R.id.textView_info_full_screen);
		
		imageView.setImageURI(Uri.parse(pathToImage));
		Resources res = getResources();
		FragmentManager fragmentManager = getFragmentManager();
		Bitmap resizedBitmap = Utils.resizeBitmap(imageView, 400, res, fragmentManager);
		if (resizedBitmap != null) {
			imageView.setImageBitmap(resizedBitmap);
		}

		textView.setText(text);
		textView.setTextSize(24);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	
}
