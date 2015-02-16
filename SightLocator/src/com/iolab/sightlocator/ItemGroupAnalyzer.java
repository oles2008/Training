package com.iolab.sightlocator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.util.Log;

public class ItemGroupAnalyzer {
	
	public static int findCommonParent(List<int[]> list, int percentageToIgnore){
		return findCommonParent(list, percentageToIgnore, null);
	}
	
	public static List<ClusterGroup> split(List<int[]> list, int maxNumberToSplitInto){
		List<ClusterGroup> result = new ArrayList<ItemGroupAnalyzer.ClusterGroup>();
		
		List<Integer> listOfAll = new ArrayList<Integer>();
		for(int i=0;i<list.size();i++){
			listOfAll.add(i);
		}
		
		//the objects that do not have any closer parents than the "common parent"
		for(int i=0;i<list.size();i++){
			if(list.get(i).length==0){
				List<Integer> oneElementList = new ArrayList<Integer>();
				oneElementList.add(i);
				result.add(new ClusterGroup(-1, oneElementList));
			}
		}
		
		
		//if there are too many separate markers, return
		if(result.size()>maxNumberToSplitInto){
			result.clear();
			result.add(new ClusterGroup(-1, listOfAll));
			return result;
		}

		List<int[]> copyOfList = new ArrayList<int[]>();
		copyOfList.addAll(list);
		List<Integer> finalListOfPositionsInArrays = new ArrayList<Integer>();
		int commonParent = findCommonParent(copyOfList, 0, finalListOfPositionsInArrays);
		
		Log.d("MyLogs", "common parent: "+commonParent);
		
		//resetting the copyOfList
		copyOfList.clear();
		copyOfList.addAll(list);
		
//		//adding positions of the objects which do not have a more precise 
//		//location than the found commonParent. We are inserting their previous positions in the 
//		//initial list, i.e. before the removal of elements with empty location
//		//Therefore, we are temporarily putting the removed empty arrays back
//		for(int i: listOfEmpty){
//			copyOfList.add(i, new int[]{});
//		}
		for(int i=0;i<copyOfList.size();i++){
			int[] array = copyOfList.get(i);
			if(array.length>0 && array[array.length-1]==commonParent){
				List<Integer> oneElementList = new ArrayList<Integer>();
				oneElementList.add(i);
				result.add(new ClusterGroup(commonParent, oneElementList));
			}
		}
		
		//if there are too many separate markers, return
		if(result.size()>maxNumberToSplitInto){
			result.clear();
			result.add(new ClusterGroup(-1, listOfAll));
			return result;
		}
		
		ignoreParentsAboveCommon(copyOfList, finalListOfPositionsInArrays, commonParent);

		//this list will become smaller after every findCommonParent(), which removes all empty arrays
		List<int[]> anotherCopyOfList = new ArrayList<int[]>();
		anotherCopyOfList.addAll(copyOfList);
		
		while(result.size()<maxNumberToSplitInto && anotherCopyOfList.size()>0){
			anotherCopyOfList.clear();
			anotherCopyOfList.addAll(copyOfList);
			//apart from finding common parent, this will remove all empty arrays from anotherCopyOfList
			commonParent = findCommonParent(anotherCopyOfList, (int)(100*(1-(double)1/(double)(maxNumberToSplitInto-result.size()))), finalListOfPositionsInArrays);
//			Log.d("MyLogs", "results size: "+result.size());
//			Log.d("MyLogs", "maxNumberToSplitInto-results size: "+maxNumberToSplitInto-);
//			Log.d("MyLogs", "common parent: "+commonParent+" for: ");
//			for(int[] p: anotherCopyOfList){
//				String par="["+p[0];
//				for(int j=1;j<p.length;j++){
//					par+=","+p[j];
//				}
//				par+="]";
//				Log.d("MyLogs", ""+par);
//			}
			if(commonParent==-1 && anotherCopyOfList.size()>0){
				result.clear();
				result.add(new ClusterGroup(-1, listOfAll));
//				Log.d("MyLogs", "returning at 2");
				return result;
			}else{
				//adds the positions of items from the indicated parent, and replaces
				//the corresponding arrays in the copyOfList with empty arrays
//				Log.d("MyLogs", "separated array:");
//				List<Integer> p = separateArraysFromIndicatedParent(copyOfList, commonParent);
//					String par="["+p.get(0);
//					for(int j=1;j<p.size();j++){
//						par+=","+p.get(j);
//					}
//					par+="]";
//					Log.d("MyLogs", ""+par);
//					Log.d("MyLogs", "what remained: ");
//					for(int[] pa: copyOfList){
//						if(pa.length==0){
//							Log.d("MyLogs","[]");
//							continue;
//						}
//						par="["+pa[0];
//						for(int j=1;j<pa.length;j++){
//							par+=","+pa[j];
//						}
//						par+="]";
//						Log.d("MyLogs", ""+par);
//					}
				result.add(new ClusterGroup(commonParent, separateArraysFromIndicatedParent(copyOfList, commonParent)));
			}
			//just to see how many non-empty arrays remain
			anotherCopyOfList.clear();
			anotherCopyOfList.addAll(copyOfList);
			for(int i=0;i<anotherCopyOfList.size();i++){
				if(anotherCopyOfList.get(i).length==0){
					anotherCopyOfList.remove(anotherCopyOfList.get(i));
//					Log.d("MyLogs", "removing empty array "+i+", size: "+anotherCopyOfList.size());
					i--;
				}
			}
		}
		
		
		if(anotherCopyOfList.size()>0){
			//the items could not be split into a less or equal to maxNumberToSplitInto
			result.clear();
			result.add(new ClusterGroup(-1, listOfAll));
//			Log.d("MyLogs", "returning at 3, size: "+anotherCopyOfList.size());
		}
		//returning either the result with "listOfAll", or all the split groups
		return result;
	}
	
	/**
	 * Finds arrays from indicated parent in the list, replaces them with empty arrays, and adds
	 * their positions in the initial list to the Integer list to be returned.
	 * 
	 * @param list
	 *            the list
	 * @param parent
	 *            the parent
	 * @return the Integer list of positions of arrays from the indicated parent in the initial list
	 */
	private static List<Integer> separateArraysFromIndicatedParent(List<int[]> list, int parent){
		List<Integer> arraysFromIndicatedParentIndices = new ArrayList<Integer>();
		for(int i=0;i<list.size();i++){
			int[] array = list.get(i);
			if(array.length==0){
				continue;
			}
			if(isParent(list, parent, array[0])==1){
				arraysFromIndicatedParentIndices.add(i);
			}
		}
		for(int i=0;i<list.size();i++){
			if(arraysFromIndicatedParentIndices.contains(i)){
				list.remove(i);
				list.add(i, new int[]{});
			}
		}
		return arraysFromIndicatedParentIndices;
	}

	/**
	 * "Cuts off" all the parents above the known common parent.
	 *
	 * @param list the list
	 * @param finalListOfPositionsInArrays the final list of positions in arrays
	 * @param commonParent the common parent
	 */
	private static void ignoreParentsAboveCommon(List<int[]> list, List<Integer> finalListOfPositionsInArrays, int commonParent){
		if(commonParent==-1){
			return;
		}
		for(int i=0;i<list.size();i++){
			int[] initialArray = list.get(i);
			if(initialArray.length==0){
				continue;
			}
			int positionInInitialArray = finalListOfPositionsInArrays.get(i);
			int[] trimmedArray=null;
			if(initialArray[positionInInitialArray]!=commonParent){
				trimmedArray = Arrays.copyOfRange(initialArray, positionInInitialArray, initialArray.length);
			}else{
				trimmedArray = Arrays.copyOfRange(initialArray, positionInInitialArray, initialArray.length);
			}
			list.set(i, trimmedArray);
		}
	}
	
	private static int findCommonParent(List<int[]> list, int percentageToIgnore, List<Integer> finalListOfPositionsInArrays){
		Log.d("MyLogs", "looking for common parent, ignoring "+percentageToIgnore);
		List<Integer> listOfBiggest = new ArrayList<Integer>(list.size());
		List<Integer> positionsOfEmpty = new ArrayList<Integer>();
		for(int i=0;i<list.size();i++){
			if(list.get(i).length==0){
				positionsOfEmpty.add(i);
				list.remove(list.get(i));
				i--;
				continue;
			}
			listOfBiggest.add(list.get(i)[0]);
		}
//		Log.d("MyLogs", "initial listOfBiggest:");
//		for(int i: listOfBiggest){
//			Log.d("MyLogs", "    "+i);
//		}
		List<Integer> listOfPositionsInArrays = new ArrayList<Integer>(listOfBiggest.size());
		for(int i=0;i<listOfBiggest.size();i++){
			listOfPositionsInArrays.add(i, 0);
		}
		int maxPosition = getMaximalElementPosition(list, listOfBiggest, percentageToIgnore);
		if(maxPosition==-1){
			return -1;
		}
		int currentCommonParent = list.get(maxPosition)[listOfPositionsInArrays.get(maxPosition)];
		while(maxPosition!=-1 && listOfPositionsInArrays.get(maxPosition)+1!=list.get(maxPosition).length){
			currentCommonParent = list.get(maxPosition)[listOfPositionsInArrays.get(maxPosition)];
//			Log.d("MyLogs", "maxPosition: "+maxPosition+", in array: "+listOfPositionsInArrays.get(maxPosition));
			listOfPositionsInArrays.set(maxPosition,listOfPositionsInArrays.get(maxPosition)+1);
			listOfBiggest.set(maxPosition,list.get(maxPosition)[listOfPositionsInArrays.get(maxPosition)]);
			maxPosition = getMaximalElementPosition(list, listOfBiggest, percentageToIgnore);
		}
		if(finalListOfPositionsInArrays!=null){
			finalListOfPositionsInArrays.clear();
			finalListOfPositionsInArrays.addAll(listOfPositionsInArrays);
			for(int i:positionsOfEmpty){
				finalListOfPositionsInArrays.add(i,-1);
			}
		}
		return currentCommonParent;
	}
	
	/**
	 * Gets the maximal element's position.
	 *
	 * @param list            the list of parent arrays
	 * @param listOfMax            the list of maximal elements of each parent array
	 * @param percentage            the percentage of elements in the list that can be ignored
	 * @return the maximal element's position, or -1, if such element cannot be
	 *         found for the necessary percentage of objects
	 */
	public static int getMaximalElementPosition(List<int[]> list, List<Integer> listOfMax, int percentage){
		if(listOfMax.size()==0){
			return -1;
		}
//		Log.d("MyLogs", "listOfMax:");
//		for(int i: listOfMax){
//			Log.d("MyLogs", "    "+i);
//		}
		for(int i=0;i<listOfMax.size();i++){
			int pos = getMaximalElementPositionStartingFromGivenPosition(list, listOfMax, i, percentage);
//			Log.d("MyLogs", "maxElPosition starting from "+i+": "+pos);
			if(pos >-1){
				return pos;
			}
		}
		return -1;
	}
	
	/**
	 * Gets the maximal element's position starting from the indicated position.
	 *
	 * @param list            the list of parent arrays
	 * @param listOfMax            the list of maximal elements of each parent array
	 * @param startPosition the start position
	 * @param percentage            the percentage of elements in the list that can be ignored
	 * @return the maximal element's position, or -1, if such element cannot be
	 *         found for the necessary percentage of objects
	 */
	private static int getMaximalElementPositionStartingFromGivenPosition(List<int[]> list, List<Integer> listOfMax, int startPosition, int percentage){
		int positionOfMax=startPosition;
		int numberOfOther=0;
		for(int i=0;i<listOfMax.size();i++){
			int isParent = isParent(list, listOfMax.get(i), listOfMax.get(positionOfMax));
			if(isParent==1){
				positionOfMax=i;
			}else if (isParent==0){
				numberOfOther++;
			}
		}
		if(((double) numberOfOther/(double) listOfMax.size())*100<=percentage){
			return positionOfMax;
		}else{
			return -1;
		}
	}
	
	/**
	 * Checks if i1 is i2's parent judging from the given list of objects' parents.
	 *
	 * @param list the list
	 * @param i1 the i1
	 * @param i2 the i2
	 * @return 1 if i1 is the parent of i2, -1 if i2 is the parent of i1, 0 if it cannot be determined
	 */
	public static int isParent(List<int[]> list, int i1, int i2){
//		Log.d("MyLogs","Comparing "+i1+" and "+i2);
		int i = isParent(list, i1, i2, new HashSet<IntPair>());
//		Log.d("MyLogs", "Compared "+i1+" and "+i2+": "+i);
		return i;
	}
	
	/**
	 * Checks if i1 is i2's parent judging from the given list of objects' parents.
	 *
	 * @param list the list
	 * @param i1 the i1
	 * @param i2 the i2
	 * @return 1 if i1 is the parent of i2, -1 if i2 is the parent of i1, 0 if it cannot be determined
	 */
	public static int isParent(List<int[]> list, int i1, int i2, Set<IntPair> checkedPairs){
		if(i1==i2){
			return 1;
		}
		if(!checkedPairs.add(new IntPair(i1, i2))){
			return 0;
		}
//		Log.d("MyLogs", "adding ("+i1+"; "+i2+")");
//		Log.d("MyLogs", "set size: "+checkedPairs.size());
		List<int[]> rowsWithI1 = new ArrayList<int[]>();
		List<int[]> rowsWithI2 = new ArrayList<int[]>();
		for(int[] parents: list){
			boolean foundI1=false;
			boolean foundI2=false;
			for(int i=0;i<parents.length;i++){
				if(parents[i]==i1){
					if(foundI2){
						return -1;
					}else{
						foundI1=true;
						rowsWithI1.add(parents);
					}
				}else{
					if(parents[i]==i2){
						if(foundI1){
							return 1;
						}else{
							foundI2=true;
							rowsWithI2.add(parents);
						}
					}
				}
			}
		}
		// if the relationship could not be determined directly (i.e. there
		// exists a row that contains both), try to find it recursively
//		Log.d("MyLogs", "rows with I1 "+i1+":"+rowsWithI1.size());
//		for(int[] p: rowsWithI1){
//			String par="["+p[0];
//			for(int j=1;j<p.length;j++){
//				par+=","+p[j];
//			}
//			par+="]";
//			Log.d("MyLogs", ""+par);
//		}
		for(int[] parents: rowsWithI1){
			boolean foundI1=false;
			for(int i=0;i<parents.length;i++){
				if(!foundI1){
					if(parents[i]!=i1)
						continue;
					else{
						foundI1=true;
						continue;
					}
				}
				if(isParent(list, parents[i], i2, checkedPairs)==1){
					return 1;
				}
			}
		}
		for (int[] parents : rowsWithI2) {
			boolean foundI2=false;
			for (int i = 0; i < parents.length; i++) {
				if(!foundI2){
					if(parents[i]!=i2)
						continue;
					else{
						foundI2=true;
						continue;
					}
				}
				if(isParent(list, parents[i], i1, checkedPairs)==1){
					return -1;
				}
			}
		}
		return 0;
	}
	
	private static class IntPair {
		private int x,y;
		public IntPair(int i1, int i2){
			this.x=i1;
			this.y=i2;
		}
		
		@Override
		public int hashCode(){
			return x+y;
		}
		
		@Override
		public boolean equals(Object o){
			if(!(o instanceof IntPair)){
				return false;
			}
			IntPair other = (IntPair) o;
			return (this.x==other.x && this.y==other.y)||(this.x==other.y && this.y==other.x);
		}
	}
	
	public static class ClusterGroup {
		private int parent;
		private List<Integer> listOfElements;
		
		public ClusterGroup(int parent, List<Integer> listOfElements){
			this.parent=parent;
			this.listOfElements=listOfElements;
		}
		
		public int getParent(){
			return parent;
		}
		
		public List<Integer> getListOfElements(){
			return listOfElements;
		}
	}
}
