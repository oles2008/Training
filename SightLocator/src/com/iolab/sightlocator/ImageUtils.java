package com.iolab.sightlocator;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

/**
 * This class contains utility methods for working with images. The instance
 * should be connected to a concrete instance of the UI component where it is
 * used.
 */
public class ImageUtils {

	private Context mContext;

	public ImageUtils(Context context) {
		mContext = context;
	}

	/**
	 * Loads the image from the indicated source.
	 *
	 * @param imageView
	 *            the {@link ImageView}
	 * @param imageSource
	 *            the image source, one of {@code Tags.IMAGE_BLANK},
	 *            {@code Tags.IMAGE_FROM_CACHE}, or
	 *            {@code Tags.IMAGE_FROM_ASSET}
	 * @param imageUri
	 *            the path to the image, depending on the source
	 */
	public void loadImage(final ImageView imageView, final String imageUri,
			final String imageSource) {
		imageView.post(new Runnable() {
			
			@Override
			public void run() {
				loadBitmap(imageUri, imageSource, imageView, imageView.getWidth(),
						imageView.getWidth());
			}
		});
	}

	private void loadBitmap(String imageUri, String imageSource,
			ImageView imageView, int reqWidth, int reqHeight) {
		if (isPreviousTaskCancelled(imageUri, imageView)) {
			if (imageUri != null) {
				DecodeImageAsyncTask asyncTask = new DecodeImageAsyncTask(
						imageView, imageUri, imageSource, reqWidth, reqHeight);
				imageView.setImageDrawable(new AsyncDrawable(mContext
						.getResources(), BitmapFactory.decodeResource(
						mContext.getResources(), R.drawable.bubble_shadow),
						asyncTask));
				asyncTask.execute();
			} else {
				imageView.setImageResource(R.drawable.bubble_shadow);
			}
		}
	}

	private boolean isPreviousTaskCancelled(String imagePath,
			ImageView imageView) {

		if (imageView != null) {
			Drawable currentDrawable = imageView.getDrawable();
			if (currentDrawable != null
					&& currentDrawable instanceof AsyncDrawable) {
				DecodeImageAsyncTask currentAsyncTask = ((AsyncDrawable) currentDrawable)
						.getAsyncTask();
				if (currentAsyncTask == null) {
					return true;
				}
				if (currentAsyncTask.getPath() != imagePath) {
					currentAsyncTask.cancel(true);
				} else {
					return false;
				}
			}
		}
		return true;
	}

}
