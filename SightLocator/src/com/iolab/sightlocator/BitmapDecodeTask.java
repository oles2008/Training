package com.iolab.sightlocator;

import java.lang.ref.WeakReference;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.AsyncTask;
import android.widget.ImageView;

/**
 * A subclass of {@link AsyncTask} designed for displaying bitmaps efficiently in the application.
 */
public class BitmapDecodeTask extends AsyncTask<String, Void, Bitmap> {
	private WeakReference<ImageView> mImageViewRef;
	private String mImagePath;
	private int mNewWidth;
	private int mNewHeight;
	
	public BitmapDecodeTask(ImageView imageView, String path, int newWidth, int newHeight){
		mImageViewRef = new WeakReference<ImageView>(imageView);
		mImagePath = path;
		mNewWidth = newWidth;
		mNewHeight = newHeight;
	}

	@Override
	protected Bitmap doInBackground(String... params) {
		return decodeBitmapFromPath(mNewWidth, mNewHeight);
	}
	
	@Override
	protected void onPostExecute(Bitmap bitmap) {
		ImageView imageView = mImageViewRef.get();
		if(imageView!=null){
			imageView.setImageBitmap(bitmap);
		}
	}
	
	private Bitmap decodeBitmapFromPath(int newWidth, int newHeight){
		BitmapFactory.Options options = new Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(mImagePath, options);
		options.inSampleSize = calculateInSampleSize(options, newWidth, newHeight);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(mImagePath, options);
	}
	
	private int calculateInSampleSize(BitmapFactory.Options options, int newWidth, int newHeight) {
		int inSampleSize = 1;
		while((options.outWidth/inSampleSize > newWidth) && (options.outHeight/inSampleSize > newHeight)){
			inSampleSize *= 2;
		}
		return inSampleSize;
	}

}
