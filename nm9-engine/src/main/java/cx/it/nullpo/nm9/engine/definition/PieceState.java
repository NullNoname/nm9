package cx.it.nullpo.nm9.engine.definition;

import java.io.Serializable;
import java.util.ArrayList;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import cx.it.nullpo.nm9.engine.common.Copyable;

/**
 * The definition of a State of a Piece. It has X and Y positions of each blocks.
 * @author NullNoname
 */
@Root
public class PieceState implements Serializable, Copyable {
	private static final long serialVersionUID = 4026425566520531005L;

	/**
	 * X and Y positions for each blocks.
	 */
	@ElementList(inline=true)
	private ArrayList<XYPair> blockPosList = new ArrayList<XYPair>();

	/**
	 * Spawn offset (X)
	 */
	@Attribute(required=false)
	private int spawnOffsetX;

	/**
	 * Spawn offset (Y)
	 */
	@Attribute(required=false)
	private int spawnOffsetY;

	public PieceState() {
	}

	public PieceState(ArrayList<XYPair> blockPosList) {
		this.blockPosList = blockPosList;
	}

	public PieceState(Copyable src) {
		copy(src);
	}

	public void copy(Copyable src) {
		PieceState s = (PieceState)src;
		this.blockPosList = new ArrayList<XYPair>();
		for(XYPair xyPair: s.blockPosList) {
			this.blockPosList.add(new XYPair(xyPair));
		}
		this.spawnOffsetX = s.spawnOffsetX;
		this.spawnOffsetY = s.spawnOffsetY;
	}

	public ArrayList<XYPair> getBlockPosList() {
		return blockPosList;
	}

	public void setBlockPosList(ArrayList<XYPair> list) {
		this.blockPosList = list;
	}

	public int getSpawnOffsetX() {
		return spawnOffsetX;
	}

	public void setSpawnOffsetX(int spawnOffsetX) {
		this.spawnOffsetX = spawnOffsetX;
	}

	public int getSpawnOffsetY() {
		return spawnOffsetY;
	}

	public void setSpawnOffsetY(int spawnOffsetY) {
		this.spawnOffsetY = spawnOffsetY;
	}
}
