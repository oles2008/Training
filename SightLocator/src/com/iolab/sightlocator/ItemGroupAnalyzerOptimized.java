package com.iolab.sightlocator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemGroupAnalyzerOptimized {

	public static int findCommonParent(List<int[]> parentIDsList, int ignoreLevelPercentage) {
		int analysisId = -1;
		int numberOfItems = parentIDsList.size();
		
		// general hierarchy analysis. NOT FINISHED
		Map<Integer, Integer> parentOccurrenceMap = new HashMap<Integer, Integer>();
//		for(int[] parentIDs: parentIDsList) {
//			for(int parentID: parentIDs) {
//				addOccurence(parentID, parentOccurrenceMap);
//			}
//		}
		
		// hierarchy level based algorithm
		// no optimization of proper level start yet
		boolean continueCond = true;
		int level = 1;
		while(continueCond){
			// build occurrence map for level = "level" (starting from highest)
			for(int[] parentIDs: parentIDsList) {
				// check if hierarchy is deep enough and add parent to accurrence map
				if(parentIDs.length >= level){
					addOccurence(parentIDs[parentIDs.length - level], parentOccurrenceMap);
				}
			}
			
			// get the parentID which passes ignore level with smallest overhead
			double overhead = 100;
			int levelOptId = -1;
			for(int parentId : parentOccurrenceMap.keySet()){
				// get frequency of parentId
				double freq = parentOccurrenceMap.get(parentId) / (double)numberOfItems * 100;
				
				// check if freq passes level and has smaller overhead
				if(freq > ignoreLevelPercentage && freq - ignoreLevelPercentage < overhead){
					overhead = freq - ignoreLevelPercentage;
					levelOptId = parentId;
				}
			}
			
			// setup analysis id
			if(levelOptId != -1){
				analysisId = levelOptId;
			} else {
				continueCond = false;
			}
			
			// go to the next level
			level++;
		}
		
		return analysisId;
	}
	
	public static int findCommonParent2(List<int[]> parentIDsList, int ignoreLevelPercentage) {
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
