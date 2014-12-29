package com.iolab.sightlocator;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;

import android.content.Context;
import android.os.Bundle;

public class Appl {
	
	static Context appContext;
	static List<OnMarkerClickListener> onMarkerClickListeners = new ArrayList<GoogleMap.OnMarkerClickListener>();
	static List<OnMapClickListener> onMapClickListeners = new ArrayList<OnMapClickListener>();
	static List<OnMapLongClickListener> onMapLongClickListeners = new ArrayList<OnMapLongClickListener>();
	static List<ViewUpdateListener> viewUpdateListeners = new ArrayList<ViewUpdateListener>();
	
	public static void subscribeForMarkerClickUpdates(OnMarkerClickListener onMarkerClickListener){
		onMarkerClickListeners.add(onMarkerClickListener);
	}
	
	public static void unsubscribeFromMarkerClickUpdates(OnMarkerClickListener onMarkerClickListener){
		onMarkerClickListeners.remove(onMarkerClickListener);
	}
	
	public static void subscribeForMapClickUpdates(OnMapClickListener onMapClickListener){
		onMapClickListeners.add(onMapClickListener);
	}
	
	public static void unsubscribeFromMapClickUpdates(OnMapClickListener onMapClickListener){
		onMapClickListeners.remove(onMapClickListener);
	}
	
	public static void subscribeForMapLongClickUpdates(OnMapLongClickListener onMapLongClickListener){
		onMapLongClickListeners.add(onMapLongClickListener);
	}
	
	public static void unsubscribeFromMapLongClickUpdates(OnMapLongClickListener onMapLongClickListener){
		onMapLongClickListeners.remove(onMapLongClickListener);
	}
	
	public static void subscribeForViewUpdates(ViewUpdateListener viewUpdateListener){
		viewUpdateListeners.add(viewUpdateListener);
	}
	
	public static void unsubscribeFromViewUpdates(ViewUpdateListener viewUpdateListener){
		viewUpdateListeners.remove(viewUpdateListener);
	}
	
	interface ViewUpdateListener {
		void onUpdateView(Bundle bundle);
	}
}
