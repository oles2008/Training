package com.iolab.sightlocator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemViewAnalyzerOptimized {

	public static int findCommonParent(List<int[]> parentIDsList, int percentageToIgnore) {
		int numberOfItems = parentIDsList.size();
		Map<Integer, Integer> parentOccurrenceMap = new HashMap<Integer, Integer>();
		for(int[] parentIDs: parentIDsList) {
			for(int parentID: parentIDs) {
				addOccurence(parentID, parentOccurrenceMap);
			}
		}
		
	}
	
	private static void addOccurence(int parentID, Map<Integer, Integer> occurenceMap) {
		if(!occurenceMap.containsKey(parentID)) {
			occurenceMap.put(parentID, 0);
		} else {
			int currentValue = occurenceMap.get(parentID);
			occurenceMap.put(parentID, ++currentValue);
		}
	}
}
