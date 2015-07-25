package cx.it.nullpo.nm9.engine.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cx.it.nullpo.nm9.engine.common.Copyable;
import cx.it.nullpo.nm9.engine.common.NEUtil;

/**
 * <b>Blackjack</b> implementation for NullpoMino.
 * <br /><b>Blackjack</b> is a language to specify randomizers, first proposed by Lardarse and expanded to
 * cover history-style randomizers by Tepples.
 * <br /><a href="http://harddrop.com/wiki/Blackjack">Read more...</a>
 * <br />
 * <br /><b>Extension: Definition of the initial state of the history</b>
 * <br />To define the initial state of the history, add "<i>list</i> as inithistory" before anything else.
 * <br />Example: Z,S,Z,S as inithistory I,J,L,O,S,T,Z with 4 history 6 rolls first I,J,L,T
 * @author Lardarse (design)
 * @author Tepples (design)
 * @author NullNoname (implementation for NullpoMino)
 */
public class Blackjack implements Serializable, Copyable {
	private static final long serialVersionUID = 4606580407479146354L;

	static private final Pattern PATTERN_SIZE_BAG = Pattern.compile("-?[0-9]+ bag of ");
	static private final Pattern PATTERN_HISTORY = Pattern.compile("with -?[0-9]+ history");
	static private final Pattern PATTERN_ROLLS = Pattern.compile("-?[0-9]+ rolls");
	static private final Pattern PATTERN_FIRST = Pattern.compile("first \\S+");
	static private final Pattern PATTERN_EXT_INITHISTORY = Pattern.compile("\\S+\\ as inithistory");
	static private final String STRING_NO_SIZE_BAG = "bag of ";
	static private final String STRING_SEQUENCE = "sequence of ";

	private int bagSize;
	private int historySize;
	private int maxRolls;
	private boolean sequence;
	private List<String> pieceList = new ArrayList<String>();
	private List<String> pieceKindList = new ArrayList<String>();
	private List<String> firstList = new ArrayList<String>();
	private List<String> firstKindList = new ArrayList<String>();
	private List<String> initHistoryList = new ArrayList<String>();
	private List<String> initHistoryKindList = new ArrayList<String>();

	public Blackjack() {
	}

	public Blackjack(String src) {
		parse(src);
	}

	public Blackjack(Copyable src) {
		copy(src);
	}

	public void copy(Copyable src) {
		Blackjack s = (Blackjack)src;
		this.bagSize = s.bagSize;
		this.historySize = s.historySize;
		this.maxRolls = s.maxRolls;
		this.sequence = s.sequence;
		this.pieceList = new ArrayList<String>(s.pieceList);
		this.pieceKindList = new ArrayList<String>(s.pieceKindList);
		this.firstList = new ArrayList<String>(s.firstList);
		this.firstKindList = new ArrayList<String>(s.firstKindList);
		this.initHistoryList = new ArrayList<String>(s.initHistoryList);
		this.initHistoryKindList = new ArrayList<String>(s.initHistoryKindList);
	}

	public void parse(String src) {
		// Local variables
		String s = src.trim();
		boolean nonSizedBag = false;
		Matcher initHistMatcher = PATTERN_EXT_INITHISTORY.matcher(s);
		Matcher bagSizeMatcher = PATTERN_SIZE_BAG.matcher(s);
		Matcher historyMatcher = PATTERN_HISTORY.matcher(s);
		Matcher rollsMatcher = PATTERN_ROLLS.matcher(s);
		Matcher firstMatcher = PATTERN_FIRST.matcher(s);

		// Init global variables
		bagSize = 1;
		historySize = 0;
		maxRolls = 0;
		sequence = false;
		pieceList.clear();
		pieceKindList.clear();
		firstList.clear();
		firstKindList.clear();

		if(s.startsWith(STRING_SEQUENCE)) {
			// Sequence
			sequence = true;
		} else {
			// Init history list
			if(initHistMatcher.find()) {
				String g = initHistMatcher.group();
				g = g.substring(0, g.length()-15);
				String[] initHists = g.split(",");
				for(int i = 0; i < initHists.length; i++) {
					String pieceName = initHists[i];
					initHistoryList.add(pieceName);
					if(!initHistoryKindList.contains(pieceName)) {
						initHistoryKindList.add(pieceName);
					}
				}
			}

			// Check for bag size
			if(bagSizeMatcher.find()) {
				bagSize = NEUtil.getIntByRegex(bagSizeMatcher.group(), 0);
			}
			// Non-sized bag
			else if(s.startsWith(STRING_NO_SIZE_BAG)) {
				nonSizedBag = true;
			}

			// Check for history
			if(historyMatcher.find()) {
				historySize = NEUtil.getIntByRegex(historyMatcher.group(), 0);
			}

			// Check for rolls
			if(rollsMatcher.find()) {
				maxRolls = NEUtil.getIntByRegex(rollsMatcher.group(), 0);
			}

			// Check for first
			if(firstMatcher.find()) {
				String g = firstMatcher.group().substring(6).trim();
				String[] firsts = g.split(",");
				for(int i = 0; i < firsts.length; i++) {
					String pieceName = firsts[i];
					firstList.add(pieceName);
					if(!firstKindList.contains(pieceName)) {
						firstKindList.add(pieceName);
					}
				}
			}
		}

		// Cleanup, then create piece list
		String s2 = s;
		if(sequence) {
			s2 = s2.substring(STRING_SEQUENCE.length());
		} else {
			s2 = PATTERN_EXT_INITHISTORY.matcher(s2).replaceFirst("");
			s2 = s2.startsWith(STRING_NO_SIZE_BAG) ? s2.substring(STRING_NO_SIZE_BAG.length()) : s2;
			s2 = PATTERN_SIZE_BAG.matcher(s2).replaceFirst("");
			s2 = PATTERN_HISTORY.matcher(s2).replaceFirst("");
			s2 = PATTERN_ROLLS.matcher(s2).replaceFirst("");
			s2 = PATTERN_FIRST.matcher(s2).replaceFirst("");
		}
		s2 = s2.trim();

		String[] pieces = s2.split(",");
		for(int i = 0; i < pieces.length; i++) {
			String pieceName = pieces[i];
			pieceList.add(pieceName);
			if(!pieceKindList.contains(pieceName)) {
				pieceKindList.add(pieceName);
			}
		}

		if(nonSizedBag) {
			bagSize = pieceList.size();
		}
	}

	public String getText() {
		String s = "";

		// Sequence
		if(sequence) {
			s = "sequence of ";
			for(int i = 0; i < pieceList.size(); i++) {
				if(i != 0) s += ",";
				s += pieceList.get(i);
			}
			return s;
		}

		// inithistory
		if(initHistoryList.size() >= 1) {
			for(int i = 0; i < initHistoryList.size(); i++) {
				if(i != 0) s += ",";
				s += initHistoryList.get(i);
			}
			s += " as inithistory ";
		}

		// bag of
		if(bagSize == pieceList.size()) {
			s += "bag of ";
		} else if(bagSize >= 2) {
			s += bagSize + " bag of ";
		}

		// Piece list
		for(int i = 0; i < pieceList.size(); i++) {
			if(i != 0) s += ",";
			s += pieceList.get(i);
		}

		// with n history
		if(historySize >= 1) {
			s += " with " + historySize + " history";
		}

		// n rolls
		if(maxRolls >= 1) {
			s += " " + maxRolls + " rolls";
		}

		// First pieces
		if(firstKindList.size() >= 1) {
			s += " first ";
			for(int i = 0; i < firstKindList.size(); i++) {
				if(i != 0) s += ",";
				s += firstKindList.get(i);
			}
		}

		return s;
	}

	public int getBagSize() {
		return bagSize;
	}

	public void setBagSize(int bagSize) {
		this.bagSize = bagSize;
	}

	public int getHistorySize() {
		return historySize;
	}

	public void setHistorySize(int historySize) {
		this.historySize = historySize;
	}

	public int getMaxRolls() {
		return maxRolls;
	}

	public void setMaxRolls(int maxRolls) {
		this.maxRolls = maxRolls;
	}

	public boolean isSequence() {
		return sequence;
	}

	public void setSequence(boolean sequence) {
		this.sequence = sequence;
	}

	public List<String> getPieceList() {
		return pieceList;
	}

	public void setPieceList(List<String> pieceList) {
		this.pieceList = pieceList;
	}

	public List<String> getPieceKindList() {
		return pieceKindList;
	}

	public void setPieceKindList(List<String> pieceKindList) {
		this.pieceKindList = pieceKindList;
	}

	public List<String> getFirstList() {
		return firstList;
	}

	public void setFirstList(List<String> firstList) {
		this.firstList = firstList;
	}

	public List<String> getFirstKindList() {
		return firstKindList;
	}

	public void setFirstKindList(List<String> firstKindList) {
		this.firstKindList = firstKindList;
	}

	public List<String> getInitHistoryList() {
		return initHistoryList;
	}

	public void setInitHistoryList(List<String> initHistoryList) {
		this.initHistoryList = initHistoryList;
	}

	public List<String> getInitHistoryKindList() {
		return initHistoryKindList;
	}

	public void setInitHistoryKindList(List<String> initHistoryKindList) {
		this.initHistoryKindList = initHistoryKindList;
	}
}
