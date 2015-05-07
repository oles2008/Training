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
		Log.d("MyLogs", "onBeforeClusterItemRendered, "+item.title);
        // Draw a single person.
        // Set the info window to show their name.
        markerOptions.title(item.title).snippet(item.snippet).icon(BitmapDescriptorFactory
				.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
//        if(sightsMapFragment.currentSelectedMarker!=null){
//        	SightMarkerItem selectedItem = new SightMarkerItem(sightsMapFragment.currentSelectedMarker);
//        	if(item.equals(selectedItem)){
//        		sightsMapFragment.currentSelectedMarker = gMap
//						.addMarker(selectedItem
//								.getMarkerOptions()
//								.icon(BitmapDescriptorFactory
//										.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
//        	}
//        }
    }
	
	@Override
    protected void onBeforeClusterRendered(Cluster<SightMarkerItem> cluster, MarkerOptions markerOptions) {
        // Draw multiple people.
        // Note: this method runs on the UI thread. Don't spend too much time in here (like in this example).
		Log.d("MyLogs", "onBeforeClusterItemRendered, "+cluster.getSize());
		super.onBeforeClusterRendered(cluster, markerOptions);
//        if(sightsMapFragment.currentSelectedMarker!=null){
//        	SightMarkerItem selectedItem = new SightMarkerItem(sightsMapFragment.currentSelectedMarker);
//        	if(cluster.getItems().contains(selectedItem)){
//        		sightsMapFragment.currentSelectedMarker.remove();
//        	}
//        }
    }
	
	@Override
	protected boolean shouldRenderAsCluster(Cluster<SightMarkerItem> cluster) {
		return cluster.getSize() > 1;
	}

}
