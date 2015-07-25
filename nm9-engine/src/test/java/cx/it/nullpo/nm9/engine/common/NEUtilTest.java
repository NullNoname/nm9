package cx.it.nullpo.nm9.engine.common;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.regex.Pattern;

import org.junit.Test;

import cx.it.nullpo.nm9.engine.definition.XYPair;

public class NEUtilTest {

	@Test
	public void testGetIntByRegexCharSequenceInt() {
		String s;
		int e;
		int v;

		s = "1";
		e = 1;
		v = NEUtil.getIntByRegex(s, 0);
		assertTrue("Tested with '" + s + "', expected " + e + ", but got " + v + " instead", v == e);

		s = "12345";
		e = 12345;
		v = NEUtil.getIntByRegex(s, 0);
		assertTrue("Tested with '" + s + "', expected " + e + ", but got " + v + " instead", v == e);

		s = "-12345";
		e = -12345;
		v = NEUtil.getIntByRegex(s, 0);
		assertTrue("Tested with '" + s + "', expected " + e + ", but got " + v + " instead", v == e);

		s = "with 8 bag";
		e = 8;
		v = NEUtil.getIntByRegex(s, 0);
		assertTrue("Tested with '" + s + "', expected " + e + ", but got " + v + " instead", v == e);

		s = "7 bag";
		e = 7;
		v = NEUtil.getIntByRegex(s, 0);
		assertTrue("Tested with '" + s + "', expected " + e + ", but got " + v + " instead", v == e);

		s = "-99 history";
		e = -99;
		v = NEUtil.getIntByRegex(s, 0);
		assertTrue("Tested with '" + s + "', expected " + e + ", but got " + v + " instead", v == e);

		s = "1 2 3";
		e = 1;
		v = NEUtil.getIntByRegex(s, 0);
		assertTrue("Tested with '" + s + "', expected " + e + ", but got " + v + " instead", v == e);
	}

	@Test
	public void testGetIntByRegexCharSequenceIntPattern() {
		Pattern p = Pattern.compile("with -?[0-9]+ history");
		String s;
		int e;
		int v;

		s = "with 10 history";
		e = 10;
		v = NEUtil.getIntByRegex(s, 0, p);
		assertTrue("Tested with '" + s + "', expected " + e + ", but got " + v + " instead", v == e);

		s = "with 6 history";
		e = 6;
		v = NEUtil.getIntByRegex(s, 0, p);
		assertTrue("Tested with '" + s + "', expected " + e + ", but got " + v + " instead", v == e);

		s = "with -99 history";
		e = -99;
		v = NEUtil.getIntByRegex(s, 0, p);
		assertTrue("Tested with '" + s + "', expected " + e + ", but got " + v + " instead", v == e);

		s = "bag of S,Z with 5 history 4 rolls";
		e = 5;
		v = NEUtil.getIntByRegex(s, 0, p);
		assertTrue("Tested with '" + s + "', expected " + e + ", but got " + v + " instead", v == e);
	}

	@Test
	public void testHasCommonItem() {
		ArrayList<String> list1 = new ArrayList<String>();
		list1.add("foo");
		list1.add("bar");

		ArrayList<String> list2 = new ArrayList<String>();
		list2.add("bar");

		ArrayList<String> list3 = new ArrayList<String>();
		list3.add("nullpo");

		assertTrue("hasCommonItem couldn't find same object between two lists", NEUtil.hasCommonItem(list1, list2));
		assertFalse("hasCommonItem says list1 and list3 has common item(s), but it's not", NEUtil.hasCommonItem(list1, list3));

		ArrayList<XYPair> xyList1 = new ArrayList<XYPair>();
		xyList1.add(new XYPair(0, 1));
		xyList1.add(new XYPair(1, 1));
		xyList1.add(new XYPair(2, 2));

		ArrayList<XYPair> xyList2 = new ArrayList<XYPair>();
		xyList2.add(new XYPair(2, 2));

		ArrayList<XYPair> xyList3 = new ArrayList<XYPair>();
		xyList3.add(new XYPair(3, 1));

		assertTrue("hasCommonItem couldn't find same object between two XYPair lists", NEUtil.hasCommonItem(xyList1, xyList2));
		assertFalse("hasCommonItem says xyList1 and xyList3 has common item(s), but it's not", NEUtil.hasCommonItem(xyList1, xyList3));
	}
}
