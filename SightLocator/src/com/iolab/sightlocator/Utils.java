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
import android.widget.ImageView;

public class Utils {

	/**
	 * @param args
	 */
	public static boolean copyFromAssets(String pathInAssets,
			String destinationPath) {
		File dir = new File(destinationPath.substring(0,
				destinationPath.lastIndexOf("/")));
		if (!dir.exists()) {
			dir.mkdirs();
		}

		File f = new File(destinationPath);
		byte[] buffer = null;
		InputStream is = null;
		FileOutputStream fos = null;
		
		if (f.length() == 0) {
			try {
				is = Appl.appContext.getAssets().open(pathInAssets);
				int size = is.available();
				buffer = new byte[size];
				is.read(buffer);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			} finally {
				try {
					if (is != null) {
						is.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			}

			try {
				fos = new FileOutputStream(f);
				fos.write(buffer);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			} finally {
				try {
					if (fos != null) {
						fos.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			}
		}
		
		// have to do this check twice as sometimes empty files are copied (length == 0) 
		if (f.length() == 0) {
			try {
				is = Appl.appContext.getAssets().open(pathInAssets);
				int size = is.available();
				buffer = new byte[size];
				is.read(buffer);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			} finally {
				try {
					if (is != null) {
						is.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			}

			try {
				fos = new FileOutputStream(f);
				fos.write(buffer);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			} finally {
				try {
					if (fos != null) {
						fos.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			}
		}
		Log.d("Mytag","Size:"+f.length());
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
