package com.iolab.sightlocator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

public class Utils {

	/**
	 * @param args
	 */
	public static boolean copyFromAssets(String pathInAssets, String destinationPath) {
		File dir = new File(destinationPath.substring(0,
				destinationPath.lastIndexOf("/")));
		if (!dir.exists()) {
			dir.mkdirs();
		}
		
		File file = new File(destinationPath);
		byte[] buffer = null;
		InputStream assetStream = null;
		FileOutputStream outputStream = null;
		
		if (file.length() == 0) {
			Log.d("asset", "asset does not exists");
		}
		else {
			Log.d("asset", "asset exists");
		}
			
		// read asset into input buffer
		try {
			assetStream = Appl.appContext.getAssets().open(pathInAssets);
			buffer = new byte[assetStream.available()];
			assetStream.read(buffer);
		} catch (Exception e) {
				e.printStackTrace();
				return false;
		} finally {
			if (assetStream != null) {
				try {
					assetStream.close();
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			}
		}

		// save asset into destination file
		try {
			outputStream = new FileOutputStream(file);
			outputStream.write(buffer);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Resize bitmap to New Width (set by method argument) by New
	 * Height(calculated according aspect ratio)
	 * 
	 * @param imageView
	 *            the Image View where the bitmap is placed
	 * @return the resized image bitmap
	 */
	public static Bitmap resizeBitmap(ImageView imageView, int newWidth) {
		// get the bitmap from Image View
		Drawable drawable = imageView.getDrawable();
		
		if (drawable == null) {
			return null;
		}
		
		Bitmap bitmapFromImageView = ((BitmapDrawable) drawable).getBitmap();
		// get Width and Height
		int originalWidth = bitmapFromImageView.getWidth();
		int originalHeight = bitmapFromImageView.getHeight();
		// find the proportion (aspect ratio)
		float scale = (float) newWidth / originalWidth;
		// find new Height keeping aspect ratio
		int newHeight = (int) Math.round(originalHeight * scale);
		// get new resized Bitmap
		Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmapFromImageView,
				newWidth, newHeight, true);
		return resizedBitmap;
	}
	
	public static void changeImageFragmentToOnePixel(String uri, Resources res, FragmentManager fragmentManager) {
		ImageView imageView = getImageView(fragmentManager);

		Drawable drawable = res.getDrawable(R.drawable.one_pixel);
		imageView.setImageDrawable(drawable);
		imageView.setTag(R.string.imageview_tag_uri, uri);
	}

	public static ImageView getImageView(FragmentManager fragmentManager) {
		Fragment fragment = fragmentManager.findFragmentById(
				R.id.text_fragment);
		ImageView imageView = (ImageView) fragment.getView().findViewById(
				R.id.imageView);

		return imageView;
	}

}
