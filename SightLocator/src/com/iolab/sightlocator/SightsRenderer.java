package com.iolab.sightlocator;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

public class SightsRenderer extends DefaultClusterRenderer<SightMarkerItem> implements OnCameraChangeListener {
	
	private int updateViewCallIndex=0;
	private Context context;
	private GoogleMap gMap;
	private final List<OnBeforeClusterRenderedListener> mOnBeforeClusterRenderedListeners = new ArrayList<SightsRenderer.OnBeforeClusterRenderedListener>();

	public SightsRenderer(Context context, GoogleMap map,
			ClusterManager<SightMarkerItem> clusterManager) {
		super(context, map, clusterManager);
		this.context = context;
		this.gMap = map;
	}

	@Override
	public void onCameraChange(CameraPosition position) {
		LatLngBounds currentMapBounds = gMap.getProjection().getVisibleRegion().latLngBounds;
		Intent intent = new Intent(context, SightsIntentService.class);
		intent.putExtra(SightsIntentService.ACTION,
				new GetMarkersOnCameraUpdateAction(currentMapBounds,
						++updateViewCallIndex));
		intent.putExtra(Tags.ON_CAMERA_CHANGE_CALL_INDEX, updateViewCallIndex);
		context.startService(intent);
	}
	
	@Override
    protected void onBeforeClusterItemRendered(SightMarkerItem item, MarkerOptions markerOptions) {
        // Draw a single person.
        // Set the info window to show their name.
        markerOptions.title(item.getTitle()).snippet(item.getSnippet())
//        	.icon(BitmapDescriptorFactory.defaultMarker(
//        			BitmapDescriptorFactory.HUE_ORANGE));        
        	.icon(BitmapDescriptorFactory.fromResource(
        			CategoryUtils.getCategoryMarkerResId(
        						item.getCategory())));

        item.setCluster(null);
        notifyOnBeforeClusterItemRendered(item, markerOptions);
    }
	
	@Override
    protected void onBeforeClusterRendered(Cluster<SightMarkerItem> cluster, MarkerOptions markerOptions) {
        // Draw multiple people.
        // Note: this method runs on the UI thread. Don't spend too much time in here (like in this example).
		super.onBeforeClusterRendered(cluster, markerOptions);
		for(SightMarkerItem item: cluster.getItems()){
			item.setCluster(cluster);
		}
		notifyOnBeforeClusterRendered(cluster, markerOptions);
    }
	
	@Override
	protected boolean shouldRenderAsCluster(Cluster<SightMarkerItem> cluster) {
		return cluster.getSize() > 1;
	}
	
	void registerOnBeforeClusterRenderedListener(
			OnBeforeClusterRenderedListener onBeforeClusterRenderedListener) {
		mOnBeforeClusterRenderedListeners.add(onBeforeClusterRenderedListener);
	}

	void unregisterOnBeforeClusterRenderedListener(
			OnBeforeClusterRenderedListener onBeforeClusterRenderedListener) {
		mOnBeforeClusterRenderedListeners
				.remove(onBeforeClusterRenderedListener);
	}

	private void notifyOnBeforeClusterRendered(
			Cluster<SightMarkerItem> cluster, MarkerOptions markerOptions) {
		for (OnBeforeClusterRenderedListener onBeforeClusterRenderedListener : mOnBeforeClusterRenderedListeners) {
			onBeforeClusterRenderedListener.onBeforeClusterRendered(cluster,
					markerOptions);
		}
	}
	
	private void notifyOnBeforeClusterItemRendered(SightMarkerItem item,
			MarkerOptions markerOptions) {
		for (OnBeforeClusterRenderedListener onBeforeClusterRenderedListener : mOnBeforeClusterRenderedListeners) {
			onBeforeClusterRenderedListener.onBeforeClusterItemRendered(item,
					markerOptions);
		}
	}

	interface OnBeforeClusterRenderedListener {
		void onBeforeClusterRendered(Cluster<SightMarkerItem> cluster,
				MarkerOptions markerOptions);

		void onBeforeClusterItemRendered(SightMarkerItem item,
				MarkerOptions markerOptions);
	}
}
