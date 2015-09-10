package com.iolab.sightlocator;

import java.util.ArrayList;
import java.util.Collection;
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
	
	/* **************************************************************************** */
    /* ******************** NonHierarchicalDistanceBasedAlgorithm ***************** */
    /* **************************************************************************** */

	@Override
	public Set<Cluster<SightMarkerItem>> getClusters(double zoom){
		Set<? extends Cluster<SightMarkerItem>> clusterSet = super.getClusters(zoom);
		//to make it impossible for clusters to be split even under a large scale
		Set<? extends Cluster<SightMarkerItem>> clustersBiggerZoom = super.getClusters(zoom+1);
		Set<Cluster<SightMarkerItem>> resultSet = new HashSet<Cluster<SightMarkerItem>>();
		for(Cluster<SightMarkerItem> cluster: clusterSet){
			if(shouldClusterBeSplit(cluster, clustersBiggerZoom)){
				Log.d("MyLogs", "cluster "+printCluster(cluster)+" split");
				resultSet.addAll(splitClusterAccordingToHierarchy(cluster));
			}else{
				Log.d("MyLogs", "cluster "+printCluster(cluster)+" not split");
				resultSet.add(cluster);
			}
		}
		return resultSet;
//		boolean isTheSame = true;
//		for (Cluster<SightMarkerItem> cluster : clusterSet) {
//			for (Cluster<SightMarkerItem> clusterFromBiggerZoom : clusterSetBiggerZoom) {
//				Set<SightMarkerItem> intersection = new HashSet<SightMarkerItem>(
//						clusterFromBiggerZoom.getItems());
//				intersection.retainAll(cluster.getItems());
//				if (!intersection.isEmpty()
//						&& !clusterFromBiggerZoom.getItems().containsAll(
//								cluster.getItems())) {
//					isTheSame = false;
//				}
//			}
//		}
//		Log.d("MyLogs", "isTheSame: "+isTheSame);
//		if (isTheSame) {
//			return clusterSet;
//		}
//		//the end
//		
//		List<Cluster<SightMarkerItem>> initialClusterList = new ArrayList<Cluster<SightMarkerItem>>(clusterSet);
//		clusterSet.clear();
//		for(Cluster<SightMarkerItem> cluster: initialClusterList){
//			List<int[]> parentIDsArrays = new ArrayList<int[]>();
//			List<SightMarkerItem> clusterItems = new ArrayList<SightMarkerItem>(cluster.getItems());
//			for(int i=0;i<clusterItems.size();i++){
//				SightMarkerItem item = clusterItems.get(i);
//				parentIDsArrays.add(item.getParentIDs());
//			}
//			List<ClusterGroup> clusterGroups = ItemGroupAnalyzer.split(parentIDsArrays, 3);
//			for(ClusterGroup clusterGroup: clusterGroups){
//				Set<SightMarkerItem> items = new HashSet<SightMarkerItem>();
//				double averageLat=0;
//				double averageLong=0;
//				for(int index: clusterGroup.getListOfElements()){
//					SightMarkerItem item = clusterItems.get(index); 
//					items.add(item);
//					averageLat += item.getLatitude();
//					averageLong += item.getLongitude();
//				}
//				if(items.size()==0){
//					continue;
//				}
//				averageLat /= items.size();
//				averageLong /= items.size();
//				StaticCluster<SightMarkerItem> newCluster = new StaticCluster<SightMarkerItem>( new LatLng(averageLat, averageLong));
//				for(SightMarkerItem item: items){
//					newCluster.add(item);
//				}
//				//TODO use the newCluster's common parent
//				clusterSet.add(newCluster);
//			}
//		}
//		return clusterSet;
	}
	
	/* **************************************************************************** */
    /* ******************************** Utility API ******************************* */
    /* **************************************************************************** */

	private boolean shouldClusterBeSplit(Cluster<SightMarkerItem> cluster,
			Set<? extends Cluster<SightMarkerItem>> clustersBiggerZoom) {
		for (Cluster<SightMarkerItem> clusterBiggerZoom : clustersBiggerZoom) {
			if (clusterBiggerZoom.getItems().containsAll(cluster.getItems())) {
				return false;
			}
		}
		return true;
	}

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
