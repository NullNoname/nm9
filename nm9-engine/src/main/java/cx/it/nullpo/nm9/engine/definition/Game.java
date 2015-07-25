package cx.it.nullpo.nm9.engine.definition;

import java.io.Serializable;
import java.util.ArrayList;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import cx.it.nullpo.nm9.engine.common.Copyable;
import cx.it.nullpo.nm9.engine.common.NamedObject;

/**
 * The definition of the game that the players play.
 * @author NullNoname
 */
@Root
public class Game implements NamedObject, Serializable, Copyable {
	private static final long serialVersionUID = -3626227072710685701L;

	/**
	 * The internal name of this game.
	 */
	@Attribute
	private String name;

	/**
	 * The script file name.
	 */
	@Attribute(required=false)
	private String scriptFileName;

	/**
	 * Script type (usually text/javascript)
	 */
	@Attribute(required=false)
	private String scriptType;

	/**
	 * Number of fields. 2 or more equals multiplayer.
	 */
	@Attribute(required=false)
	private int numFields = 1;

	/**
	 * Number of players per field. 2 or more enables co-op.
	 */
	@Attribute(required=false)
	private int numPlayersPerField = 1;

	/**
	 * This game's configulations.
	 */
	@Element
	private Config config = new Config();

	/**
	 * List of blocks that can appear in this game.
	 */
	@ElementList
	private ArrayList<Block> blocks = new ArrayList<Block>();

	/**
	 * List of pieces that can appear in this game.
	 */
	@ElementList
	private ArrayList<Piece> pieces = new ArrayList<Piece>();

	/**
	 * List of patterns.
	 */
	@ElementList
	private ArrayList<Pattern> patterns = new ArrayList<Pattern>();

	/**
	 * List of speed settings.
	 */
	@ElementList
	private ArrayList<Speed> speeds = new ArrayList<Speed>();

	/**
	 * List of level settings.
	 */
	@ElementList
	private ArrayList<Level> levels = new ArrayList<Level>();

	/**
	 * Action order of the move phase.
	 */
	@ElementList
	private ArrayList<String> movePhase = new ArrayList<String>();

	public Game() {
	}

	public Game(String name) {
		this.name = name;
	}

	public Game(Copyable src) {
		copy(src);
	}

	public void copy(Copyable src) {
		Game s = (Game)src;
		this.name = s.name;
		this.scriptFileName = s.scriptFileName;
		this.scriptType = s.scriptType;
		this.numFields = s.numFields;
		this.numPlayersPerField = s.numPlayersPerField;
		this.config = new Config(s.config);
		this.blocks = new ArrayList<Block>();
		for(Block blk: s.blocks) {
			this.blocks.add(new Block(blk));
		}
		this.pieces = new ArrayList<Piece>();
		for(Piece p: s.pieces) {
			this.pieces.add(new Piece(p));
		}
		this.patterns = new ArrayList<Pattern>();
		for(Pattern p: s.patterns) {
			this.patterns.add(new Pattern(p));
		}
		this.speeds = new ArrayList<Speed>();
		for(Speed spd: s.speeds) {
			this.speeds.add(new Speed(spd));
		}
		this.levels = new ArrayList<Level>();
		for(Level lv: s.levels) {
			this.levels.add(new Level(lv));
		}
		this.movePhase = new ArrayList<String>(s.movePhase);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getScriptFileName() {
		return scriptFileName;
	}

	public void setScriptFileName(String scriptFileName) {
		this.scriptFileName = scriptFileName;
	}

	public String getScriptType() {
		return scriptType;
	}

	public void setScriptType(String scriptType) {
		this.scriptType = scriptType;
	}

	public int getNumFields() {
		return numFields;
	}

	public void setNumFields(int numFields) {
		this.numFields = numFields;
	}

	public int getNumPlayersPerField() {
		return numPlayersPerField;
	}

	public void setNumPlayersPerField(int numPlayersPerField) {
		this.numPlayersPerField = numPlayersPerField;
	}

	public Config getConfig() {
		return config;
	}

	public void setConfig(Config config) {
		this.config = config;
	}

	public ArrayList<Block> getBlocks() {
		return blocks;
	}

	public void setBlocks(ArrayList<Block> blocks) {
		this.blocks = blocks;
	}

	public ArrayList<Piece> getPieces() {
		return pieces;
	}

	public void setPieces(ArrayList<Piece> pieces) {
		this.pieces = pieces;
	}

	public ArrayList<Pattern> getPatterns() {
		return patterns;
	}

	public void setPatterns(ArrayList<Pattern> patterns) {
		this.patterns = patterns;
	}

	public ArrayList<Speed> getSpeeds() {
		return speeds;
	}

	public void setSpeeds(ArrayList<Speed> speeds) {
		this.speeds = speeds;
	}

	public ArrayList<Level> getLevels() {
		return levels;
	}

	public void setLevels(ArrayList<Level> levels) {
		this.levels = levels;
	}

	public ArrayList<String> getMovePhase() {
		return movePhase;
	}

	public void setMovePhase(ArrayList<String> movePhase) {
		this.movePhase = movePhase;
	}
}
