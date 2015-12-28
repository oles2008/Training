package com.iolab.sightlocator;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

/**
 * The class which provides the drawable for the selected cluster.
 * 
 * @author Oles Potiatynyk
 */
public class SelectedClusterRenderer extends
		DefaultClusterRenderer<SightMarkerItem> {

	public SelectedClusterRenderer(GoogleMap map, ClusterManager<SightMarkerItem> clusterManager) {
		super(Appl.appContext, map, clusterManager);
	}

	@Override
	protected LayerDrawable makeClusterBackground() {
		mColoredCircleBackground = new ShapeDrawable(new OvalShape());
		ShapeDrawable outline = new ShapeDrawable(new OvalShape());
		outline.getPaint().setColor(
				Appl.appContext.getResources().getColor(
						R.color.selected_cluster_outline));
		LayerDrawable background = new LayerDrawable(new Drawable[] { outline,
				mColoredCircleBackground });
		int strokeWidth = Appl.appContext.getResources().getDimensionPixelSize(
				R.dimen.selected_cluster_outline);
		background.setLayerInset(1, strokeWidth, strokeWidth, strokeWidth,
				strokeWidth);
		return background;
	}

	@Override
	public void onBeforeClusterRendered(
			Cluster<SightMarkerItem> selectedCluster,
			MarkerOptions markerOptions) {
		super.onBeforeClusterRendered(selectedCluster, markerOptions);
	}
}
