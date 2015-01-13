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
				Log.d("MyLogs","exception 1");
				e.printStackTrace();
				return false;
			} finally {
				try {
					if (is != null) {
						is.close();
					}
				} catch (IOException e) {
					Log.d("MyLogs","exception 2");
					e.printStackTrace();
					return false;
				}
			}
		
		try {
			fos = new FileOutputStream(f);
			fos.write(buffer);
			} catch (Exception e) {
			Log.d("MyLogs","exception 3:"+e.toString());
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				Log.d("MyLogs","exception 4");
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}


}
