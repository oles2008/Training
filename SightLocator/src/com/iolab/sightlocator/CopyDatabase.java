package com.iolab.sightlocator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import android.os.Environment;
import android.util.Log;

public class CopyDatabase {

	/**
	 * Copies system files.
	 * Example usage:
	 * CopyDatabase.copyDatabase("data/data/com.iolab.sightlocator/databases/sights.db", "sights.db");
	 * 
	 *
	 * @param currentDBPath the current db path
	 * @param backupDBPath the backup db path
	 * @return true, if successful
	 */
	public static boolean copyDatabase(String currentDBPath, String backupDBPath){
		FileChannel src = null;
		FileChannel dst = null;
		try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
               
                File currentDB = new File(currentDBPath);
                File backupDB = new File(sd, backupDBPath);
                Log.d("MyLogs", "cuurent DB exists: "+(currentDB.exists()));
                Log.d("MyLogs", "backup DB exists: "+(backupDB.exists()));

                if (currentDB.exists()) {
                    src = new FileInputStream(currentDB).getChannel();
                    dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    Log.d("MyLogs", "transferred successfully");
                }
            }
        } catch (Exception e) {
        	return false;
        }
		finally{
			try {
				if(src!=null){
					src.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
            try {
            	if(dst!=null){
            		dst.close();
            	}
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
            Log.d("MyLogs", "closed channels");
		}
		return true;
	}
}
