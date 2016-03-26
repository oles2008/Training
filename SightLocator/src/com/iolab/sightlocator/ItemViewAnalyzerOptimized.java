package com.iolab.sightlocator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemViewAnalyzerOptimized {

	public static int findCommonParent(List<int[]> parentIDsList, int ignoreLevelPercentage) {
		int analysisId = -1;
		int numberOfItems = parentIDsList.size();
		
		// general hierarchy analysis. NOT FINISHED
		Map<Integer, Integer> parentOccurrenceMap = new HashMap<Integer, Integer>();
		for(int[] parentIDs: parentIDsList) {
			for(int parentID: parentIDs) {
				addOccurence(parentID, parentOccurrenceMap);
			}
		}
		
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
	
	private static void addOccurence(int parentID, Map<Integer, Integer> occurenceMap) {
		if(!occurenceMap.containsKey(parentID)) {
			occurenceMap.put(parentID, 0);
		} else {
			int currentValue = occurenceMap.get(parentID);
			occurenceMap.put(parentID, ++currentValue);
		}
	}
}
