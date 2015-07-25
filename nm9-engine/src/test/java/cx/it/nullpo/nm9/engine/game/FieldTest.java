package cx.it.nullpo.nm9.engine.game;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import cx.it.nullpo.nm9.engine.definition.Block;
import cx.it.nullpo.nm9.engine.definition.XYPair;

public class FieldTest {

	@Test
	public void testGetCoordType() {
		Field f = new Field(10, 20, 20);
		int ct;

		ct = f.getCoordType(0, 19);
		assertEquals("X:0,Y:19 is expected to COORD_NORMAL. ", Field.COORD_NORMAL, ct);
		ct = f.getCoordType(0, 0);
		assertEquals("X:0,Y:0 is expected to COORD_NORMAL. ", Field.COORD_NORMAL, ct);
		ct = f.getCoordType(10, 0);
		assertEquals("X:10,Y:0 is expected to COORD_WALL. ", Field.COORD_WALL, ct);
		ct = f.getCoordType(0, -1);
		assertEquals("X:0,Y:-1 is expected to COORD_HIDDEN. ", Field.COORD_HIDDEN, ct);
		ct = f.getCoordType(0, -20);
		assertEquals("X:0,Y:-20 is expected to COORD_HIDDEN. ", Field.COORD_HIDDEN, ct);
		ct = f.getCoordType(0, -21);
		assertEquals("X:0,Y:-21 is expected to COORD_VANISH. ", Field.COORD_VANISH, ct);
	}

	@Test
	public void testGetHighestBlockYInt() {
		Field f = new Field(10, 20, 20);
		int y, expected;

		// Empty field
		y = f.getHighestBlockY();
		expected = f.getHeight();
		assertEquals("Expected " + expected + " as a result for an empty field, but got " + y + " instead. ", expected, y);

		// Put a block in 5,5 and 5,10.
		// It has no valid block definition, but the method to test doesn't care about it.
		f.setBlockE(5, 5, new RuntimeBlock());
		f.setBlockE(5, 10, new RuntimeBlock());

		// Test for empty column (X:0)
		y = f.getHighestBlockY(0);
		expected = 20;
		assertEquals("X:0 has no blocks and expected " + expected + " as a result, but got " + y + " instead. ", expected, y);
		// Test for non-empty column (X:5)
		y = f.getHighestBlockY(5);
		expected = 5;
		assertEquals("X:5 has a block and expected " + expected + " as a result, but got " + y + " instead. ", expected, y);
	}

	@Test
	public void testGetLowestBlockYInt() {
		Field f = new Field(10, 20, 20);

		// Put a block in 5,5 and 5,10.
		// It has no valid block definition, but the method to test doesn't care about it.
		f.setBlockE(5, 5, new RuntimeBlock());
		f.setBlockE(5, 10, new RuntimeBlock());

		int y, expected;

		// Test for empty column (X:0)
		y = f.getLowestBlockY(0);
		expected = -21;
		assertEquals("X:0 has no blocks and expected " + expected + " as a result, but got " + y + " instead. ", expected, y);
		// Test for non-empty column (X:5)
		y = f.getLowestBlockY(5);
		expected = 10;
		assertEquals("X:5 has a block and expected " + expected + " as a result, but got " + y + " instead. ", expected, y);
	}

	@Test
	public void testMatchHorizontal() {
		Field f = new Field(10, 20, 20);
		Block blkDef = new Block("MyBlock", "Red");

		// Tag-less match test
		// Setup a line of 5 blocks in x positions 2-7
		for(int i = 2; i < 7; i++) {
			f.setBlockE(i, 19, new RuntimeBlock(blkDef));
		}

		List<XYPair> hitList = f.matchHorizontal(2, 19, null);
		assertEquals("Expected 5 hits but got " + hitList.size() + " instead. ", 5, hitList.size());

		// Tag match test
		ArrayList<String> tags1 = new ArrayList<String>();
		tags1.add("mytag");
		ArrayList<String> tags2 = new ArrayList<String>();
		tags2.add("mytag");

		Block blkDef2 = new Block("MyBlock2", "Orange", tags1);
		for(int i = 0; i < 10; i++) {
			f.setBlockE(i, 18, new RuntimeBlock(blkDef2));
		}

		List<XYPair> hitList2 = f.matchHorizontal(2, 18, tags2);
		assertEquals("Expected 10 hits for tag-match but got " + hitList2.size() + " instead. ", 10, hitList2.size());

		// No-match-test
		List<XYPair> hitList3 = f.matchHorizontal(0, 17, null);
		assertEquals("Expected 0 hits for no-match-test but got " + hitList3.size() + " instead. ", 0, hitList3.size());
	}

	@Test
	public void testMatchFloodFill() {
		Field f = new Field(10, 20, 20);

		// Setup block definitions
		ArrayList<String> tagsDefRed = new ArrayList<String>();
		tagsDefRed.add("Red");
		Block blkDefRed = new Block("MyBlock1", "Red", tagsDefRed);
		ArrayList<String> tagsDefGreen = new ArrayList<String>();
		tagsDefGreen.add("Green");
		Block blkDefGreen = new Block("MyBlock2", "Green", tagsDefGreen);

		// Setup field
		for(int i = 0; i < 5; i++) {
			f.setBlockE(i + 5, 16, new RuntimeBlock(blkDefGreen));
			f.setBlockE(i + 5, 17, new RuntimeBlock(blkDefGreen));
			f.setBlockE(i, 18, new RuntimeBlock(blkDefRed));
			f.setBlockE(i, 19, new RuntimeBlock(blkDefRed));
		}

		// Tag-less match test
		List<XYPair> hitListTagLess1 = f.matchFloodFill(0, 18, null, false, false);
		assertEquals("Four-directional flood fill test failed. ", 10, hitListTagLess1.size());
		List<XYPair> hitListTagLess2 = f.matchFloodFill(0, 18, null, false, true);
		assertEquals("Eight-directional flood fill test failed. ", 20, hitListTagLess2.size());

		// Tag match test
		ArrayList<String> tagsSearchGreen = new ArrayList<String>();
		tagsSearchGreen.add("Green");
		ArrayList<String> tagsSearchMulti = new ArrayList<String>();
		tagsSearchMulti.add("Red");
		tagsSearchMulti.add("Green");

		List<XYPair> hitListSingleTag1 = f.matchFloodFill(5, 17, tagsSearchGreen, false, false);
		assertEquals("Single tag four-directional flood fill test failed. ", 10, hitListSingleTag1.size());
		List<XYPair> hitListSingleTag2 = f.matchFloodFill(5, 17, tagsSearchGreen, false, true);
		assertEquals("Single tag eight-directional flood fill test failed. ", 10, hitListSingleTag2.size());

		List<XYPair> hitListMultiTag1 = f.matchFloodFill(5, 17, tagsSearchMulti, false, false);
		assertEquals("Multi tag four-directional flood fill test failed. ", 10, hitListMultiTag1.size());
		List<XYPair> hitListMultiTag2 = f.matchFloodFill(5, 17, tagsSearchMulti, false, true);
		assertEquals("Multi tag eight-directional flood fill test failed. ", 20, hitListMultiTag2.size());
	}

	@Test
	public void testCheckGravityFloodFill() {
		Field f = new Field(10, 20, 20);

		// Setup block definitions
		ArrayList<String> tagsDefRed = new ArrayList<String>();
		tagsDefRed.add("Red");
		Block blkDefRed = new Block("MyBlock1", "Red", tagsDefRed);
		ArrayList<String> tagsDefGreen = new ArrayList<String>();
		tagsDefGreen.add("Green");
		Block blkDefGreen = new Block("MyBlock2", "Green", tagsDefGreen);

		// Setup field
		// 16:....
		// 17:.RR.
		// 18:.RR.
		// 19:....
		f.setBlockE(1, 17, new RuntimeBlock(blkDefRed));
		f.setBlockE(2, 17, new RuntimeBlock(blkDefRed));
		f.setBlockE(1, 18, new RuntimeBlock(blkDefRed));
		f.setBlockE(2, 18, new RuntimeBlock(blkDefRed));
		assertTrue("This formation should be able to fall one space down, but checkGravityFloodFill says no. ",
				   f.checkGravityFloodFill(1, 18, null, false, false));

		// Setup field
		// 16:....
		// 17:RRRR
		// 18:.G..
		// 19:....
		f.init();
		f.setBlockE(0, 17, new RuntimeBlock(blkDefRed));
		f.setBlockE(1, 17, new RuntimeBlock(blkDefRed));
		f.setBlockE(2, 17, new RuntimeBlock(blkDefRed));
		f.setBlockE(3, 17, new RuntimeBlock(blkDefRed));
		f.setBlockE(1, 18, new RuntimeBlock(blkDefGreen));
		assertFalse("This formation should not be able to fall, but checkGravityFloodFill says something else.",
					f.checkGravityFloodFill(0, 17, tagsDefRed, false, false));

	}

	@Test
	public void testDoGravityFloodFill() {
		Field f = new Field(10, 20, 20);

		// Setup block definitions
		ArrayList<String> tagsDefRed = new ArrayList<String>();
		tagsDefRed.add("Red");
		Block blkDefRed = new Block("MyBlock1", "Red", tagsDefRed);
		ArrayList<String> tagsDefGreen = new ArrayList<String>();
		tagsDefGreen.add("Green");
		Block blkDefGreen = new Block("MyBlock2", "Green", tagsDefGreen);

		// Setup field
		// 16:.RR. -> .... -> ....
		// 17:.RR. -> .RR. -> ....
		// 18:.... -> .RR. -> .RR.
		// 19:.... -> .... -> .RR.
		f.setBlockE(1, 16, new RuntimeBlock(blkDefRed));
		f.setBlockE(2, 16, new RuntimeBlock(blkDefRed));
		f.setBlockE(1, 17, new RuntimeBlock(blkDefRed));
		f.setBlockE(2, 17, new RuntimeBlock(blkDefRed));

		assertTrue("This formation should be able to fall one space down, but doGravityFloodFill failed. ",
				f.doGravityFloodFill(1, 16, null, false, false));
		assertTrue("(1, 16) is not empty, meaning it didn't fall. ", f.isEmpty(1, 16));
		assertTrue("(2, 16) is not empty, meaning it didn't fall. ", f.isEmpty(2, 16));
		assertFalse("(1, 18) is empty, meaning it didn't fall. ", f.isEmpty(1, 18));
		assertFalse("(2, 18) is empty, meaning it didn't fall. ", f.isEmpty(2, 18));

		assertTrue("This formation should still be able to fall one space down, but doGravityFloodFill no longer work. ",
				f.doGravityFloodFill(1, 17, null, false, false));
		assertFalse("This formation should not be able to fall one space down anymore, but doGravityFloodFill did something wrong. ",
				f.doGravityFloodFill(1, 18, null, false, false));

		// Setup field
		// 16:.RR. -> .... -> ....
		// 17:.... -> .RR. -> ....
		// 18:GG.. -> .... -> .RR.
		// 19:.... -> GG.. -> GG..
		f.init();
		f.setBlockE(1, 16, new RuntimeBlock(blkDefRed));
		f.setBlockE(2, 16, new RuntimeBlock(blkDefRed));
		f.setBlockE(0, 18, new RuntimeBlock(blkDefGreen));
		f.setBlockE(1, 18, new RuntimeBlock(blkDefGreen));

		assertTrue("This formation should be able to fall one space down, but doGravityFloodFill failed. ",
				f.doGravityFloodFill(null, false, false));
		assertTrue("This formation should be able to fall one more space down, but doGravityFloodFill no longer work. ",
				f.doGravityFloodFill(null, false, false));
		assertFalse("This formation should not be able to fall one space down anymore, but doGravityFloodFill did something wrong. ",
				f.doGravityFloodFill(null, false, false));

		assertFalse("(0, 19) is empty, meaning it didn't fall. ", f.isEmpty(0, 19));
		assertFalse("(1, 19) is empty, meaning it didn't fall. ", f.isEmpty(1, 19));
		assertFalse("(1, 18) is empty, meaning it didn't fall. ", f.isEmpty(1, 18));
		assertFalse("(2, 18) is empty, meaning it didn't fall. ", f.isEmpty(2, 18));
	}
}
