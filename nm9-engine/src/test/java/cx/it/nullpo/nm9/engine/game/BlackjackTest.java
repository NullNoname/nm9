package cx.it.nullpo.nm9.engine.game;

import static org.junit.Assert.*;

import org.junit.Test;

public class BlackjackTest {

	@Test
	public void testBlackjackString() {
		Blackjack b;

		b = new Blackjack("I,I,J,J,J,L,L,L,O,O,O,S,S,S,T,T,T,Z,Z,Z");
		assertEquals("20 total pieces expected, but " + b.getPieceList().size() + " is reported instead. ",
				20, b.getPieceList().size());
		assertEquals("7 kind of pieces expected, but " + b.getPieceKindList().size() + " kind is reported instead. ",
				7, b.getPieceKindList().size());

		b = new Blackjack("bag of I,L,O,Z,T,J,S");
		assertEquals("7 bag expected, but " + b.getBagSize() + " bag is created instead. ",
				7, b.getBagSize());
		assertEquals("7 kind of pieces expected, but " + b.getPieceKindList().size() + " kind is reported instead. ",
				7, b.getPieceKindList().size());

		b = new Blackjack("3 bag of I,L,O,Z,T,J,S");
		assertEquals("3 bag expected, but " + b.getBagSize() + " bag is created instead. ",
				3, b.getBagSize());

		b = new Blackjack("I,L,O,Z,T,J,S with 4 history");
		assertEquals("4 history expected, but history is set " + b.getHistorySize() + " instead. ",
				4, b.getHistorySize());

		b = new Blackjack("I,L,O,Z,T,J,S with 2 history 10 rolls");
		assertEquals("2 history expected, but history is set " + b.getHistorySize() + " instead. ",
				2, b.getHistorySize());
		assertEquals("10 rolls expected, but rolls is set " + b.getMaxRolls() + " instead. ",
				10, b.getMaxRolls());

		b = new Blackjack("I,J,L,O,S,T,Z with 4 history 6 rolls first I,J,L,T");
		assertEquals("4 history expected, but history is set " + b.getHistorySize() + " instead. ",
				4, b.getHistorySize());
		assertEquals("6 rolls expected, but rolls is set " + b.getMaxRolls() + " instead. ",
				6, b.getMaxRolls());
		assertEquals("4 kinds of first piece expected, but " + b.getFirstKindList().size() + " kind is reported instead. ",
				4, b.getFirstKindList().size());

		b = new Blackjack("I as inithistory I");
		assertEquals("1 inithistory expected, but inithistory size is " + b.getInitHistoryList().size() + " instead. ",
				1, b.getInitHistoryList().size());
		assertEquals("1 kind of pieces expected, but " + b.getPieceKindList().size() + " kind is reported instead. ",
				1, b.getPieceKindList().size());

		b = new Blackjack("Z,S,Z,S as inithistory I,J,L,O,S,T,Z with 4 history 6 rolls first I,J,L,T");
		assertEquals("4 inithistory expected, but inithistory size is " + b.getInitHistoryList().size() + " instead. ",
				4, b.getInitHistoryList().size());
		assertEquals("2 inithistory kind expected, but inithistory kind is " + b.getInitHistoryList().size() + " instead. ",
				2, b.getInitHistoryKindList().size());
		assertEquals("4 history expected, but history is set " + b.getHistorySize() + " instead. ",
				4, b.getHistorySize());
		assertEquals("6 rolls expected, but rolls is set " + b.getMaxRolls() + " instead. ",
				6, b.getMaxRolls());
		assertEquals("4 kinds of first piece expected, but " + b.getFirstKindList().size() + " kind is reported instead. ",
				4, b.getFirstKindList().size());

		b = new Blackjack("sequence of I,I,I,I,I,L,L,L,L,L");
		assertTrue("Not marked as sequence", b.isSequence());
		assertEquals("10 total pieces expected, but " + b.getPieceList().size() + " is reported instead. ",
				10, b.getPieceList().size());
		assertEquals("2 kind of pieces expected, but " + b.getPieceKindList().size() + " kind is reported instead. ",
				2, b.getPieceKindList().size());
	}

}
