package com.iolab.sightlocator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.algo.NonHierarchicalDistanceBasedAlgorithm;
import com.google.maps.android.clustering.algo.StaticCluster;
import com.iolab.sightlocator.ItemGroupAnalyzer.ClusterGroup;

public class SightsHierarchichalAlgorithm extends
		NonHierarchicalDistanceBasedAlgorithm<SightMarkerItem> {
	
	private static final double ZOOM_OFFSET = 1.5;
	
	/* **************************************************************************** */
    /* ******************** NonHierarchicalDistanceBasedAlgorithm ***************** */
    /* **************************************************************************** */

	@Override
	public Set<Cluster<SightMarkerItem>> getClusters(double zoom){
		Appl.Tic();
		Set<? extends Cluster<SightMarkerItem>> clusterSet = super.getClusters(zoom+ZOOM_OFFSET);

		Appl.Toc("-- getClusters after super: ");
		
		//to make it impossible for clusters to be split even under a large scale
		Set<? extends Cluster<SightMarkerItem>> clustersBiggerZoom = super.getClusters(zoom+ZOOM_OFFSET+1);

		Appl.Toc("-- getClusters after super+1: ");

		Set<Cluster<SightMarkerItem>> resultSet = new HashSet<Cluster<SightMarkerItem>>();
		for(Cluster<SightMarkerItem> cluster: clusterSet){
			Set<Cluster<SightMarkerItem>> splitClusters = splitClusterAccordingToHierarchy(cluster);
			
			if(isBiggerZoomClusteringFiner(splitClusters, clustersBiggerZoom)){
				resultSet.addAll(splitClusters);
			} else {
				resultSet.add(cluster);
			}
		}
		Appl.Toc("-- getClusters finish (background) duration: ");
		return resultSet;
	}

	
	/* **************************************************************************** */
    /* ******************************** Utility API ******************************* */
    /* **************************************************************************** */


	private Set<Cluster<SightMarkerItem>> splitClusterAccordingToHierarchy(
			Cluster<SightMarkerItem> initialCluster) {

		Set<Cluster<SightMarkerItem>> clusterSet = new HashSet<Cluster<SightMarkerItem>>();

		List<int[]> parentIDsArrays = new ArrayList<int[]>();
		List<SightMarkerItem> clusterItems = new ArrayList<SightMarkerItem>(
				initialCluster.getItems());
		for (int i = 0; i < clusterItems.size(); i++) {
			SightMarkerItem item = clusterItems.get(i);
			parentIDsArrays.add(item.getParentIDs());
		}
		List<ClusterGroup> clusterGroups = ItemGroupAnalyzer.split(
				parentIDsArrays, 3);
		for (ClusterGroup clusterGroup : clusterGroups) {
			Set<SightMarkerItem> items = new HashSet<SightMarkerItem>();
			double averageLat = 0;
			double averageLong = 0;
			for (int index : clusterGroup.getListOfElements()) {
				SightMarkerItem item = clusterItems.get(index);
				items.add(item);
				averageLat += item.getLatitude();
				averageLong += item.getLongitude();
			}
			if (items.size() == 0) {
				continue;
			}
			averageLat /= items.size();
			averageLong /= items.size();
			StaticCluster<SightMarkerItem> newCluster = new StaticCluster<SightMarkerItem>(
					new LatLng(averageLat, averageLong));
			for (SightMarkerItem item : items) {
				newCluster.add(item);
			}
			// TODO use the newCluster's common parent
			clusterSet.add(newCluster);
		}
		return clusterSet;
	}
	
	/**
	 * Determines whether the clustering under bigger zoom is finer than the given split cluster.
	 * 
	 * @param splitClusters the clusters that appeared after splitting the original one
	 * @param clustersBiggerZoom the set of clusters obtained by the distance-based algorithm at a bigger zoom
	 */
	private boolean isBiggerZoomClusteringFiner(Set<Cluster<SightMarkerItem>> splitClusters, Set<? extends Cluster<SightMarkerItem>> clustersBiggerZoom){
		for(Cluster<SightMarkerItem> clusterBiggerZoom: clustersBiggerZoom){
			for(Cluster<SightMarkerItem> splitCluster: splitClusters){
				if(splitCluster.getItems().containsAll(clusterBiggerZoom.getItems())){
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Used for debugging.
	 * 
	 * @param the {@link Cluster} to be printed.
	 */
	private String printCluster(Cluster<SightMarkerItem> cluster){
		if(cluster == null){
			return null;
		}
		String result = "[";
		for(SightMarkerItem item: cluster.getItems()){
			result += item.getTitle();
		}
		result += "]";
		return result;
	}

}
