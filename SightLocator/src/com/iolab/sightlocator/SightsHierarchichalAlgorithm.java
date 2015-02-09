package com.iolab.sightlocator;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import android.util.Log;

import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.algo.NonHierarchicalDistanceBasedAlgorithm;

public class SightsHierarchichalAlgorithm extends
		NonHierarchicalDistanceBasedAlgorithm<SightMarkerItem> {

	@Override
	public Set<Cluster<SightMarkerItem>> getClusters(double zoom){
		Set<Cluster<SightMarkerItem>> clusterSet = new HashSet<Cluster<SightMarkerItem>>();
		for(Cluster<SightMarkerItem> cluster: (Set<Cluster<SightMarkerItem>>) super.getClusters(zoom)){
			for(SightMarkerItem item: cluster.getItems()){
				Log.d("MyLogs", "class cast successful: "+item.getTitle());
				
			}
		}
		return clusterSet;
	}
	
	private int findCommonParent(Collection<SightMarkerItem> items){
		for(SightMarkerItem item: items){
			
		}
		return -1;
	}
}
