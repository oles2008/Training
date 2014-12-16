package com.iolab.sightlocator;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class TouchEventListenerFrameLayout extends FrameLayout {

	boolean mMapIsTouched = false;

	public TouchEventListenerFrameLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mMapIsTouched = true;
			break;

		case MotionEvent.ACTION_UP:
			mMapIsTouched = false;
			break;
		}

		return super.dispatchTouchEvent(ev);
	}

}
