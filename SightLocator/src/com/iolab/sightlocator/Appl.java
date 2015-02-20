package com.iolab.sightlocator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.ClusterManager.OnClusterClickListener;
import com.google.maps.android.clustering.ClusterManager.OnClusterItemClickListener;

public class Appl extends Application{
	
	static Context appContext;
	// list to store checked checkboxes from option-Filter menu
//	public static List<String> checkedItems;
	public static boolean[] checkedItems;
	
	//all the following listeners should perform their callback methods
	//in the UI thread
	public static List<OnMarkerClickListener> onMarkerClickListeners = new ArrayList<OnMarkerClickListener>();
	public static List<ClusterManager.OnClusterItemClickListener<SightMarkerItem>> onClusterItemClickListeners = new ArrayList<OnClusterItemClickListener<SightMarkerItem>>();
	public static List<OnClusterClickListener<SightMarkerItem>> onClusterClickListeners = new ArrayList<OnClusterClickListener<SightMarkerItem>>();
	public static List<OnMapClickListener> onMapClickListeners = new ArrayList<OnMapClickListener>();
	public static List<OnMapLongClickListener> onMapLongClickListeners = new ArrayList<OnMapLongClickListener>();
	public static List<ViewUpdateListener> viewUpdateListeners = new ArrayList<ViewUpdateListener>();
	public static SightsDatabaseOpenHelper sightsDatabaseOpenHelper;
	public static List<OnMarkerCategoryUpdateListener> onMarkerCategoryUpdateListeners = new ArrayList<OnMarkerCategoryUpdateListener>();
	
	@Override
	public void onCreate() {
		super.onCreate();
		appContext = getApplicationContext();
		sightsDatabaseOpenHelper = new SightsDatabaseOpenHelper(appContext, 1);
		
		// Initialise the list where checkboxes from Option-Filter menu are stored
//		checkedItems = new ArrayList<String>();
		// set first checkbox "All" pre-checked
//		checkedItems.add(getResources().getString(R.string.action_filter_all));
		
		//boolean array
		checkedItems = new boolean[(getResources().getStringArray(R.array.marker_category)).length]; 
		Arrays.fill(checkedItems, Boolean.FALSE);
		checkedItems[0] = true;

	}
	
	/**
	 * The ResultReceiver will be used to send data from background services to
	 * the UI thread in a {@link Bundle}. When the result is received on the UI thread, all the
	 * viewUpdateListeners will have their views updated depending on the content of the Bundle.
	 */
	public static final ResultReceiver receiver = new ResultReceiver(
			new Handler()) {

		@Override
		protected void onReceiveResult(final int resultCode, final Bundle resultData) {
			notifyViewUpdates(resultData);
		}
	};
	
	public static void subscribeForClusterItemClickUpdates(OnClusterItemClickListener<SightMarkerItem> onClusterItemClickListener){
		onClusterItemClickListeners.add(onClusterItemClickListener);
	}
	
	public static void unsubscribeFromClusterItemClickUpdates(OnClusterItemClickListener<SightMarkerItem> onClusterItemClickListener){
		onClusterItemClickListeners.remove(onClusterItemClickListener);
	}

	public static void notifyClusterItemClickUpdates(SightMarkerItem item){
		for(OnClusterItemClickListener<SightMarkerItem> listener: onClusterItemClickListeners){
			listener.onClusterItemClick(item);
		}
	}

	
	public static void subscribeForClusterClickUpdates(OnClusterClickListener<SightMarkerItem> onClusterClickListener){
		onClusterClickListeners.add(onClusterClickListener);
	}
	
	public static void unsubscribeFromClusterClickUpdates(OnClusterClickListener<SightMarkerItem> onClusterClickListener){
		onClusterClickListeners.remove(onClusterClickListener);
	}
	
	public static void notifyClusterClickUpdates(Cluster<SightMarkerItem> cluster){
		for(OnClusterClickListener<SightMarkerItem> listener: onClusterClickListeners){
			listener.onClusterClick(cluster);
		}
	}
	
	
	@Deprecated
	public static void subscribeForMarkerClickUpdates(OnMarkerClickListener onMarkerClickListener){
		onMarkerClickListeners.add(onMarkerClickListener);
	}
	
	@Deprecated
	public static void unsubscribeFromMarkerClickUpdates(OnMarkerClickListener onMarkerClickListener){
		onMarkerClickListeners.remove(onMarkerClickListener);
	}

	
	public static void subscribeForMapClickUpdates(OnMapClickListener onMapClickListener){
		onMapClickListeners.add(onMapClickListener);
	}
	
	public static void unsubscribeFromMapClickUpdates(OnMapClickListener onMapClickListener){
		onMapClickListeners.remove(onMapClickListener);
	}
	
	public static void notifyMapClickUpdates(LatLng arg0){
		for(OnMapClickListener listener: onMapClickListeners){
			listener.onMapClick(arg0);
		}
	}
	
	
	public static void subscribeForMapLongClickUpdates(OnMapLongClickListener onMapLongClickListener){
		onMapLongClickListeners.add(onMapLongClickListener);
	}
	
	public static void unsubscribeFromMapLongClickUpdates(OnMapLongClickListener onMapLongClickListener){
		onMapLongClickListeners.remove(onMapLongClickListener);
	}

	public static void notifyLongMapClickUpdates(LatLng arg0){
		for(OnMapLongClickListener listener: onMapLongClickListeners){
			listener.onMapLongClick(arg0);
		}
	}
	
	
	public static void subscribeForViewUpdates(ViewUpdateListener viewUpdateListener){
		viewUpdateListeners.add(viewUpdateListener);
	}
	
	public static void unsubscribeFromViewUpdates(ViewUpdateListener viewUpdateListener){
		viewUpdateListeners.remove(viewUpdateListener);
	}

	public static void notifyViewUpdates(Bundle bundle){
		for(ViewUpdateListener listener: viewUpdateListeners){
			listener.onUpdateView(bundle);
		}
	}

	
	public static void subscribeForMarkerCategoryUpdates(OnMarkerCategoryUpdateListener onMarkerCategoryUpdateListener){
		onMarkerCategoryUpdateListeners.add(onMarkerCategoryUpdateListener);
	}
	
	public static void unsubscribeFromMarkerCategoryUpdates(OnMarkerCategoryUpdateListener onMarkerCategoryUpdateListener){
		onMarkerCategoryUpdateListeners.remove(onMarkerCategoryUpdateListener);
	}

	public static void notifyMarkerCategoryUpdates(){
		for(OnMarkerCategoryUpdateListener listener: onMarkerCategoryUpdateListeners){
			listener.onMarkerCategoryChosen();
		}
	}
	
	interface ViewUpdateListener {
		void onUpdateView(Bundle bundle);
	}
}
