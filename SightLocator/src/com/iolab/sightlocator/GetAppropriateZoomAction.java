package com.iolab.sightlocator;

import android.location.Location;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * The class encapsulating the algorithm for calculating the appropriate zoom level for given user location.
 */
public class GetAppropriateZoomAction implements ServiceAction, Parcelable {
	
	Location mLocation;
	float mInitialZoom;
	
	public GetAppropriateZoomAction(Location location, float initialZoom) {
		mLocation = location;
		mInitialZoom = initialZoom;
	}
	
	/* **************************************************************************** */
    /* ******************************* Parcelable ********************************* */
    /* **************************************************************************** */

	public GetAppropriateZoomAction(Parcel in) {
		mLocation = in.readParcelable(Location.class.getClassLoader());
		mInitialZoom = in.readFloat();
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(mLocation, flags);
		dest.writeFloat(mInitialZoom);
	}
	
	public static final Parcelable.Creator<GetAppropriateZoomAction> CREATOR = new Parcelable.Creator<GetAppropriateZoomAction>() {
		public GetAppropriateZoomAction createFromParcel(Parcel in) {
			return new GetAppropriateZoomAction(in);
		}

		public GetAppropriateZoomAction[] newArray(int size) {
			return new GetAppropriateZoomAction[size];
		}
	};
	
	/* **************************************************************************** */
    /* *************************** ServiceAction ********************************** */
    /* **************************************************************************** */

	@Override
	public void runInService() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Bundle result = new Bundle();
		result.putFloat(Tags.APPROPRIATE_ZOOM, 17);
		Appl.receiver.send(0, result);
	}

}
