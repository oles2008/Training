package com.iolab.sightlocator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.util.Log;

public class ItemGroupAnalyzer {

	
	public static int findCommonParent(List<int[]> list, int percentage){
		List<Integer> listOfBiggest = new ArrayList<Integer>(list.size());
		for(int i=0;i<list.size();i++){
			if(list.get(i).length==0){
				list.remove(list.get(i));
				i--;
				continue;
			}
			listOfBiggest.add(list.get(i)[0]);
		}
		Log.d("MyLogs", "initial listOfBiggest:");
		for(int i: listOfBiggest){
			Log.d("MyLogs", "    "+i);
		}
		List<Integer> listOfPositionsInArrays = new ArrayList<Integer>(listOfBiggest.size());
		for(int i=0;i<listOfBiggest.size();i++){
			listOfPositionsInArrays.add(i, 0);
		}
		int maxPosition = getMaximalElementPosition(list, listOfBiggest, percentage);
		if(maxPosition==-1){
			return -1;
		}
		int currentCommonParent = list.get(maxPosition)[listOfPositionsInArrays.get(maxPosition)];
		while(maxPosition!=-1 && listOfPositionsInArrays.get(maxPosition)+1!=list.get(maxPosition).length){
			currentCommonParent = list.get(maxPosition)[listOfPositionsInArrays.get(maxPosition)];
			Log.d("MyLogs", "maxPosition: "+maxPosition+", in array: "+listOfPositionsInArrays.get(maxPosition));
			listOfPositionsInArrays.set(maxPosition,listOfPositionsInArrays.get(maxPosition)+1);
			listOfBiggest.set(maxPosition,list.get(maxPosition)[listOfPositionsInArrays.get(maxPosition)]);
			maxPosition = getMaximalElementPosition(list, listOfBiggest, percentage);
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
		Log.d("MyLogs", "listOfMax:");
		for(int i: listOfMax){
			Log.d("MyLogs", "    "+i);
		}
		for(int i=0;i<listOfMax.size();i++){
			int pos = getMaximalElementPositionStartingFromGivenPosition(list, listOfMax, i, percentage);
			Log.d("MyLogs", "maxElPosition starting from "+i+": "+pos);
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
}
