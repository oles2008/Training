package com.iolab.sightlocator.tests;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.iolab.sightlocator.ItemGroupAnalyzer;

import junit.framework.TestCase;

public class ItemGroupAnalyzerTest extends TestCase {
	
	public void setUp() throws Exception {
		super.setUp();
	}

	public void testFindCommonParent() {
		List<int[]> list = new ArrayList<int[]>();
		list.add(new int[]{5,1,3});
		list.add(new int[]{3});
		list.add(new int[]{1});
		assertEquals(5, ItemGroupAnalyzer.findCommonParent(list, 10));
		list = new ArrayList<int[]>();
		list.add(new int[]{5,1,3});
		list.add(new int[]{3});
		list.add(new int[]{4});
		assertEquals(-1, ItemGroupAnalyzer.findCommonParent(list, 10));
		list = new ArrayList<int[]>();
		list.add(new int[]{5,1,3});
		list.add(new int[]{3});
		list.add(new int[]{4});
		assertEquals(1, ItemGroupAnalyzer.findCommonParent(list, 40));
		list = new ArrayList<int[]>();
		list.add(new int[]{5,1,3});
		list.add(new int[]{});
		list.add(new int[]{6,5,4});
		list.add(new int[]{4});
		assertEquals(5, ItemGroupAnalyzer.findCommonParent(list, 10));
		list = new ArrayList<int[]>();
		list.add(new int[]{5,1,7,3});
		list.add(new int[]{5,5,5});
		list.add(new int[]{});
		list.add(new int[]{6,4});
		list.add(new int[]{4});
		list.add(new int[]{1,6});
		assertEquals(5, ItemGroupAnalyzer.findCommonParent(list, 10));
		list = new ArrayList<int[]>();
		list.add(new int[]{5,1,7,3});
		list.add(new int[]{5,10,15});
		list.add(new int[]{});
		list.add(new int[]{6,4});
		list.add(new int[]{4});
		list.add(new int[]{1,6});
		assertEquals(-1, ItemGroupAnalyzer.isParent(list, 4, 1));
		assertEquals(1, ItemGroupAnalyzer.isParent(list, 1, 4));
		assertEquals(1, ItemGroupAnalyzer.findCommonParent(list, 25));
	}

	public void testGetMaximalElementPosition() {
		List<int[]> list = new ArrayList<int[]>();
		list.add(new int[]{5,1,3});
		list.add(new int[]{3});
		list.add(new int[]{1});
		List<Integer> listOfMax = new ArrayList<Integer>();
		listOfMax.add(5);
		listOfMax.add(3);
		listOfMax.add(1);
		assertEquals(0, ItemGroupAnalyzer.getMaximalElementPosition(list, listOfMax, 10));
		list = new ArrayList<int[]>();
		list.add(new int[]{5,1,3});
		list.add(new int[]{3});
		list.add(new int[]{4});
		listOfMax = new ArrayList<Integer>();
		listOfMax.add(5);
		listOfMax.add(3);
		listOfMax.add(4);
		assertEquals(-1, ItemGroupAnalyzer.getMaximalElementPosition(list, listOfMax, 10));
		list = new ArrayList<int[]>();
		list.add(new int[]{5,1,3});
		list.add(new int[]{3});
		list.add(new int[]{4});
		listOfMax = new ArrayList<Integer>();
		listOfMax.add(5);
		listOfMax.add(3);
		listOfMax.add(4);
		assertEquals(0, ItemGroupAnalyzer.getMaximalElementPosition(list, listOfMax, 40));
		list = new ArrayList<int[]>();
		list.add(new int[]{5,1,3});
		list.add(new int[]{});
		list.add(new int[]{6,5,4});
		list.add(new int[]{4});
		listOfMax = new ArrayList<Integer>();
		listOfMax.add(5);
		listOfMax.add(6);
		listOfMax.add(4);
		assertEquals(1, ItemGroupAnalyzer.getMaximalElementPosition(list, listOfMax, 10));
		list = new ArrayList<int[]>();
		list.add(new int[]{5,1,7,3});
		list.add(new int[]{5,5,5});
		list.add(new int[]{6,4});
		list.add(new int[]{4});
		list.add(new int[]{1,6});
		listOfMax = new ArrayList<Integer>();
		listOfMax.add(5);
		listOfMax.add(5);
		listOfMax.add(6);
		listOfMax.add(4);
		listOfMax.add(1);
		assertTrue(ItemGroupAnalyzer.isParent(list, 1, 6)==1);
		assertTrue(ItemGroupAnalyzer.isParent(list, 5, 6)==1);
		assertEquals(1, ItemGroupAnalyzer.getMaximalElementPosition(list, listOfMax, 10));
	}
	
	public void testSplit() {
		List<int[]> list = new ArrayList<int[]>();
		list.add(new int[]{5,1,7,3});
		list.add(new int[]{5,10,15});
		list.add(new int[]{});
		list.add(new int[]{6,4});
		list.add(new int[]{4});
		list.add(new int[]{1,6});
		List<ItemGroupAnalyzer.ClusterGroup> clusterGroups = ItemGroupAnalyzer.split(list, 3);
		assertEquals(-1, clusterGroups.get(0).getParent());
		assertEquals(1, clusterGroups.get(1).getParent());
		assertEquals(10, clusterGroups.get(2).getParent());
		assertEquals(3, clusterGroups.size());
	}

}
