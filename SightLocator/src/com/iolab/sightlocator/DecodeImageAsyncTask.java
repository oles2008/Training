package com.iolab.sightlocator;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

public class DecodeImageAsyncTask extends AsyncTask<Void, Void, Bitmap> {
	
	private WeakReference<ImageView> mImageViewRef;
	private String mImagePath;
	private String mImageSource;
	private int mWidth;
	private int mHeight;
	
	public DecodeImageAsyncTask(ImageView imageView, String imagePath, String imageSource, int width, int height) {
		mImageViewRef = new WeakReference<ImageView>(imageView);
		mImagePath = imagePath;
		mImageSource = imageSource;
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
	protected void onPostExecute(Bitmap bitmap) {
		ImageView imageView = mImageViewRef.get();
		if (imageView != null && bitmap != null) {
			Drawable currentDrawable = imageView.getDrawable();
			if (currentDrawable != null
					&& currentDrawable instanceof AsyncDrawable) {
				if (((AsyncDrawable) currentDrawable).getAsyncTask() == this) {
					imageView.setImageBitmap(bitmap);
				}
			}
		}
	}
	
	private Bitmap decodeBitmapFromPath(int width, int height) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		if(mImageSource == null || mImageSource.equals(Tags.IMAGE_BLANK) || mImageSource.isEmpty()) {
			return null;
		}
		
		// image from storage
		if(mImageSource.equals(Tags.IMAGE_FROM_CACHE)){
			BitmapFactory.decodeFile(mImagePath, options);
			options.inSampleSize = getInSampleSize(options, width, height);
			options.inJustDecodeBounds = false;
			return BitmapFactory.decodeFile(mImagePath, options);		
		}

		// image from assets
		if(mImageSource.equals(Tags.IMAGE_FROM_ASSET)){
			try{
			    // get input stream
			    InputStream ims = Appl.appContext.getAssets().open(mImagePath);
				BitmapFactory.decodeStream(ims, null, options);
				options.inSampleSize = getInSampleSize(options, width, height);
				options.inJustDecodeBounds = false;
				return BitmapFactory.decodeStream(ims, null, options);
			}
			catch(IOException ex) {
				return null;
			}
		}
		return null;
	}

	private int getInSampleSize(BitmapFactory.Options options, int newWidth, int newHeight) {
		int inSampleSize = 1;
		while((newWidth < options.outWidth/inSampleSize) && (newHeight < options.outHeight/inSampleSize)){
			inSampleSize *= 2;
		}
		return inSampleSize;
	}
}
