package cx.it.nullpo.nm9.engine.definition;

import java.io.Serializable;
import java.util.ArrayList;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import cx.it.nullpo.nm9.engine.common.Copyable;
import cx.it.nullpo.nm9.engine.common.NamedObject;

/**
 * The definition of a Piece that players can move and rotate.
 * @author NullNoname
 */
@Root
public class Piece implements NamedObject, Serializable, Copyable {
	private static final long serialVersionUID = -2977558937826697685L;

	/**
	 * Name of this piece. Must be unique.
	 */
	@Attribute
	private String name;

	/**
	 * Default state of this piece. State is Direction or Orientation. Setting it to -1 will randomize the default state.
	 */
	@Attribute(required=false)
	private int defaultState;

	/**
	 * Number of blocks in this piece. 0 to set automatically.
	 */
	@Attribute(required=false)
	private int numBlocks;

	/**
	 * true to randomize what block will be included
	 */
	@Attribute(required=false)
	private boolean randomizeBlock;

	/**
	 * Connection type (can be None, SameBlock, or All)
	 */
	@Attribute(required=false)
	private String connectionType = "SameBlock";

	/**
	 * A list of Blocks (by name) that included in this Piece.
	 */
	@ElementList
	private ArrayList<String> blocks = new ArrayList<String>();

	/**
	 * A list of this Piece's states.
	 */
	@ElementList
	private ArrayList<PieceState> states = new ArrayList<PieceState>();

	/**
	 * Wallkick settings for this piece.
	 */
	@ElementList(required=false)
	private ArrayList<Wallkick> wallkicks = new ArrayList<Wallkick>();

	/**
	 * List of optional tags for this Piece.
	 */
	@ElementList(required=false)
	private ArrayList<String> tags = new ArrayList<String>();

	public Piece() {
	}

	public Piece(Copyable src) {
		copy(src);
	}

	public Piece(String name) {
		this.name = name;
	}

	public void copy(Copyable src) {
		Piece s = (Piece)src;
		this.name = s.name;
		this.defaultState = s.defaultState;
		this.numBlocks = s.numBlocks;
		this.randomizeBlock = s.randomizeBlock;
		this.connectionType = s.connectionType;
		this.blocks = new ArrayList<String>(s.blocks);
		this.states = new ArrayList<PieceState>();
		for(PieceState ps: s.states) {
			this.states.add(new PieceState(ps));
		}
		this.wallkicks = new ArrayList<Wallkick>();
		for(Wallkick wk: s.wallkicks) {
			this.wallkicks.add(new Wallkick(wk));
		}
		this.tags = new ArrayList<String>(s.tags);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getDefaultState() {
		return defaultState;
	}

	public void setDefaultState(int defaultState) {
		this.defaultState = defaultState;
	}

	public int getNumBlocks() {
		return numBlocks;
	}

	public void setNumBlocks(int numBlocks) {
		this.numBlocks = numBlocks;
	}

	public boolean isRandomizeBlock() {
		return randomizeBlock;
	}

	public void setRandomizeBlock(boolean randomizeBlock) {
		this.randomizeBlock = randomizeBlock;
	}

	public String getConnectionType() {
		return connectionType;
	}

	public void setConnectionType(String connectionType) {
		this.connectionType = connectionType;
	}

	public ArrayList<String> getBlocks() {
		return blocks;
	}

	public void setBlocks(ArrayList<String> blocks) {
		this.blocks = blocks;
	}

	public ArrayList<PieceState> getStates() {
		return states;
	}

	public void setStates(ArrayList<PieceState> states) {
		this.states = states;
	}

	public ArrayList<Wallkick> getWallkicks() {
		return wallkicks;
	}

	public void setWallkicks(ArrayList<Wallkick> wallkicks) {
		this.wallkicks = wallkicks;
	}

	public ArrayList<String> getTags() {
		return tags;
	}

	public void setTags(ArrayList<String> tags) {
		this.tags = tags;
	}

	public Wallkick getWallkick(int stateFrom, int stateTo) {
		for(Wallkick kick: wallkicks) {
			if(stateFrom == kick.getStateFrom() && stateTo == kick.getStateTo())
				return kick;
		}
		return null;
	}
}
