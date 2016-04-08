package com.iolab.sightlocator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemGroupAnalyzerOptimized {

	public static int findCommonParent(List<int[]> parentIDsList, int ignoreLevelPercentage) {
		int commonParentId = -1;
		for(int level=0;;level++) {			
			int mostCommonItemFromLevel = getMostCommonItemFromLevel(parentIDsList, level, ignoreLevelPercentage);
			if(mostCommonItemFromLevel != -1) {
				commonParentId = mostCommonItemFromLevel;
			} else {
				break;
			}
		}
		return commonParentId;
	}
	
	private static int getMostCommonItemFromLevel(List<int[]> parentIDsList, int level, int percentageToIgnore) {
		Map<Integer, Integer> parentOccurrenceMap = new HashMap<Integer, Integer>();
		for(int[] parentIDs: parentIDsList) {
			// check if hierarchy is deep enough and add parent to occurrence map
			if(parentIDs.length > level){
				addOccurence(parentIDs[level], parentOccurrenceMap);
			}
		}
		int mostCommonId = getKeyByMaxValue(parentOccurrenceMap);
		if(mostCommonId == -1) {
			return -1;
		}
		double freq = parentOccurrenceMap.get(mostCommonId) / (double)parentIDsList.size() * 100;
		
		// check if freq passes level
		if(freq >= 100-percentageToIgnore){
			return mostCommonId;
		} else {
			return -1;
		}
	}
	
	private static void addOccurence(int parentID, Map<Integer, Integer> occurenceMap) {
		if(!occurenceMap.containsKey(parentID)) {
			occurenceMap.put(parentID, 1);
		} else {
			int currentValue = occurenceMap.get(parentID);
			occurenceMap.put(parentID, ++currentValue);
		}
	}
	
	private static int getKeyByMaxValue(Map<Integer, Integer> parentOccurrenceMap) {
		int maxKey = -1;
		int maxValue = 0;
		for(int key: parentOccurrenceMap.keySet()) {
			if(parentOccurrenceMap.get(key) > maxValue) {
				maxKey = key;
				maxValue = parentOccurrenceMap.get(key);
			}
		}
		return maxKey;
	}
}
