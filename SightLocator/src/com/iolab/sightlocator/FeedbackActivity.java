package com.iolab.sightlocator;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class FeedbackActivity extends Activity {
	
	public static final String EMAIL_ADDRESS = "info.uguide@gmail.com";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feedback_activity);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		Button sendFeedbackButton = (Button) findViewById(R.id.send_feedback_btn);
		sendFeedbackButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Uri uri = Uri.parse("mailto:"
						+ EMAIL_ADDRESS
						+ "?subject="
						+ getResources().getString(
								R.string.feedback_email_subject));
				Intent feedbackEmailIntent = new Intent(Intent.ACTION_SENDTO);
				feedbackEmailIntent.setData(uri);
				startActivity(Intent
						.createChooser(
								feedbackEmailIntent,
								getResources()
										.getString(
												R.string.feedback_email_intent_resolver_message)));
			}
		});
	}
}
