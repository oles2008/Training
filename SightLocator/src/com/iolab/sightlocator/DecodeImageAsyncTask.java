package com.iolab.sightlocator;

import java.lang.ref.WeakReference;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public class DecodeImageAsyncTask extends AsyncTask<Void, Void, Bitmap> {
	
	private WeakReference<ImageView> mImageViewRef;
	private String mImagePath;
	private int mWidth;
	private int mHeight;
	
	public DecodeImageAsyncTask(ImageView imageView, String imagePath, int width, int height) {
		mImageViewRef = new WeakReference<ImageView>(imageView);
		mImagePath = imagePath;
		mWidth = width;
		mHeight = height;
	}
	
	public String getPath() {
		return mImagePath;
	}

	@Override
	protected Bitmap doInBackground(Void... params) {
		return decodeBitmapFromPath(mWidth, mHeight);
	}
	
	@Override
	protected void onPostExecute(Bitmap bitmap){
		ImageView imageView = mImageViewRef.get();
		if(imageView!=null ){
			Drawable currentDrawable = imageView.getDrawable();
			if(currentDrawable instanceof AsyncDrawable){
				if(((AsyncDrawable) currentDrawable).getAsyncTask() == this){
					imageView.setImageBitmap(bitmap);
				}
			}
		}
	}
	
	private Bitmap decodeBitmapFromPath(int width, int height) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(mImagePath, options);
		options.inSampleSize = getInSampleSize(options, width, height);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(mImagePath, options);
	}

	private int getInSampleSize(BitmapFactory.Options options, int newWidth, int newHeight) {
		int inSampleSize = 1;
		while((newWidth < options.outWidth/inSampleSize) && (newHeight < options.outHeight/inSampleSize)){
			inSampleSize *= 2;
		}
		return inSampleSize;
	}
}
