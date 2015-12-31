package com.iolab.sightlocator;

import java.lang.ref.WeakReference;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

public class AsyncDrawable extends BitmapDrawable {

	WeakReference<DecodeImageAsyncTask> mAsyncTaskRef;

	public AsyncDrawable(Resources res, Bitmap bitmap,
			DecodeImageAsyncTask asyncTask) {
		super(res, bitmap);
		mAsyncTaskRef = new WeakReference<DecodeImageAsyncTask>(asyncTask);
	}

	public DecodeImageAsyncTask getAsyncTask() {
		return mAsyncTaskRef.get();
	}

}
