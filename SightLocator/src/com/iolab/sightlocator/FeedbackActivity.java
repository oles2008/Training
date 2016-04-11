package com.iolab.sightlocator;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class FeedbackActivity extends Activity {
	
	private static final String NO_EMAIL_APP_DIALOG_ACTIVE = "no_email_app_dialog_active";
	
	private boolean mNoEmailDialogActive = false;
	
	public static final String EMAIL_ADDRESS = "info.uguide@gmail.com";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feedback_activity);
		if(savedInstanceState != null) {
			mNoEmailDialogActive = savedInstanceState.getBoolean(NO_EMAIL_APP_DIALOG_ACTIVE, false);
			if(mNoEmailDialogActive) {
				showNoEmailAppDialog();
			}
		}
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
				if(feedbackEmailIntent.resolveActivity(getPackageManager()) != null) {
					startActivity(Intent
						.createChooser(
								feedbackEmailIntent,
								getResources()
										.getString(
												R.string.feedback_email_intent_resolver_message)));
				} else {
					showNoEmailAppDialog();
				}
			}
		});
	}
	
	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putBoolean(NO_EMAIL_APP_DIALOG_ACTIVE, mNoEmailDialogActive);
		super.onSaveInstanceState(savedInstanceState);
	}
	
	@SuppressLint("NewApi")
	private void showNoEmailAppDialog() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setMessage(R.string.feedback_no_email_app_message)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						mNoEmailDialogActive = false;
						dialog.cancel();
					}
				});
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			alertDialogBuilder.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface dialog) {
					mNoEmailDialogActive = false;

				}
			});
		}
		alertDialogBuilder.create().show();
		mNoEmailDialogActive = true;
	}
}
