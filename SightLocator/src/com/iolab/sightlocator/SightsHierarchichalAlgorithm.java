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

	@Override
	public Set<Cluster<SightMarkerItem>> getClusters(double zoom){
		Set<Cluster<SightMarkerItem>> clusterSet = (Set<Cluster<SightMarkerItem>>) super.getClusters(zoom+1);
		List<Cluster<SightMarkerItem>> initialClusterList = new ArrayList<Cluster<SightMarkerItem>>(clusterSet);
		clusterSet.clear();
		for(Cluster<SightMarkerItem> cluster: initialClusterList){
			List<int[]> parentIDsArrays = new ArrayList<int[]>();
			List<SightMarkerItem> clusterItems = new ArrayList<SightMarkerItem>(cluster.getItems());
			for(int i=0;i<clusterItems.size();i++){
				SightMarkerItem item = clusterItems.get(i);
				parentIDsArrays.add(item.getParentIDs());
			}
			List<ClusterGroup> clusterGroups = ItemGroupAnalyzer.split(parentIDsArrays, 3);
			for(ClusterGroup clusterGroup: clusterGroups){
				Set<SightMarkerItem> items = new HashSet<SightMarkerItem>();
				double averageLat=0;
				double averageLong=0;
				for(int index: clusterGroup.getListOfElements()){
					SightMarkerItem item = clusterItems.get(index); 
					items.add(item);
					averageLat += item.getLatitude();
					averageLong += item.getLongitude();
				}
				if(items.size()==0){
					continue;
				}
				averageLat /= items.size();
				averageLong /= items.size();
				StaticCluster<SightMarkerItem> newCluster = new StaticCluster<SightMarkerItem>( new LatLng(averageLat, averageLong));
				for(SightMarkerItem item: items){
					newCluster.add(item);
				}
				//TODO use the newCluster's common parent
				clusterSet.add(newCluster);
			}
		}
		return clusterSet;
	}
}
