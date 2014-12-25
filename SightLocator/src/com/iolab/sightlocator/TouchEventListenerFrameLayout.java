package com.iolab.sightlocator;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class TouchEventListenerFrameLayout extends FrameLayout {

	OnMapTouchedListener onMapTouchedListener;
	

	public TouchEventListenerFrameLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public void registerOnMapTouchListener(OnMapTouchedListener onMapTouchedListener){
		this.onMapTouchedListener = onMapTouchedListener;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			onMapTouchedListener.onMapTouched();
			break;

		case MotionEvent.ACTION_UP:
//			mMapIsTouched = false;
			break;
		}

		return super.dispatchTouchEvent(ev);
	}
	
	interface OnMapTouchedListener{
		void onMapTouched();
	}

}
