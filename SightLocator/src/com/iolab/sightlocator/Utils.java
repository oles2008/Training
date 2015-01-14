package com.iolab.sightlocator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.util.Log;

public class Utils {

	/**
	 * @param args
	 */
	public static boolean copyFromAssets(String pathInAssets, String destinationPath){
		File dir = new File(destinationPath.substring(0,destinationPath.lastIndexOf("/")));
		if (!dir.exists()) {dir.mkdirs();}
		
		File f = new File(destinationPath);
		byte[] buffer = null;
		InputStream is = null;
		FileOutputStream fos = null;
		
		if (!f.exists())
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
		return true;
	}


}
