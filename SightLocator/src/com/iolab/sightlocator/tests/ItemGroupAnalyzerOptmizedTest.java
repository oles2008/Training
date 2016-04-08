package com.iolab.sightlocator.tests;

import java.util.ArrayList;
import java.util.List;

import com.iolab.sightlocator.ItemGroupAnalyzerOptimized;

import junit.framework.TestCase;

public class ItemGroupAnalyzerOptmizedTest extends TestCase {
	public void testFindCommonParent() {
		List<int[]> list = new ArrayList<int[]>();
		list.add(new int[]{5,1,3});
		list.add(new int[]{5,1,3});
		list.add(new int[]{5});
		assertEquals(5, ItemGroupAnalyzerOptimized.findCommonParent(list, 10));
		list = new ArrayList<int[]>();
		list.add(new int[]{5,1,3});
		list.add(new int[]{5,1,2});
		list.add(new int[]{4});
		assertEquals(-1, ItemGroupAnalyzerOptimized.findCommonParent(list, 10));
		list = new ArrayList<int[]>();
		list.add(new int[]{5,1,3,6});
		list.add(new int[]{5,1,3,7});
		list.add(new int[]{5,4});
		assertEquals(3, ItemGroupAnalyzerOptimized.findCommonParent(list, 40));
		list = new ArrayList<int[]>();
		list.add(new int[]{6,5,1,3});
		list.add(new int[]{});
		list.add(new int[]{6,5,4});
		list.add(new int[]{6,5,2});
		assertEquals(5, ItemGroupAnalyzerOptimized.findCommonParent(list, 27));
		list = new ArrayList<int[]>();
		list.add(new int[]{5,1,7,3});
		list.add(new int[]{5,2,4});
		list.add(new int[]{});
		list.add(new int[]{6,8});
		list.add(new int[]{5,9});
		list.add(new int[]{5,9,10});
		assertEquals(-1, ItemGroupAnalyzerOptimized.findCommonParent(list, 10));
		list = new ArrayList<int[]>();
		list.add(new int[]{5,1,7,3});
		list.add(new int[]{5,10,15});
		list.add(new int[]{});
		list.add(new int[]{5,6,4});
		list.add(new int[]{5,6,8});
		list.add(new int[]{5,6,9});
		assertEquals(5, ItemGroupAnalyzerOptimized.findCommonParent(list, 25));
		list = new ArrayList<int[]>();
		list.add(new int[]{7,8,11});
		list.add(new int[]{7,8,11});
		list.add(new int[]{7,8,10});
		list.add(new int[]{7,8,10});
		list.add(new int[]{7,8,9});
		list.add(new int[]{7,8,11});
		assertEquals(8, ItemGroupAnalyzerOptimized.findCommonParent(list, 0));
	}
}
