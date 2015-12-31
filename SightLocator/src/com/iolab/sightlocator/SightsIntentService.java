package com.iolab.sightlocator;

import android.app.IntentService;
import android.content.Intent;

public class SightsIntentService extends IntentService {
	
	public final static String ACTION = "action";
	
	public SightsIntentService(){
		super("SightsIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		ServiceAction serviceAction = intent.getExtras().getParcelable(ACTION);
		if(serviceAction!=null){
			serviceAction.runInService();
		}
	}

}
