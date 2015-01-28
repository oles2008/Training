package com.iolab.sightlocator;

import android.content.Context;
import android.content.Intent;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
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
        // Draw a single person.
        // Set the info window to show their name.
        markerOptions.title(item.title).snippet(item.snippet);
    }

}
