package cx.it.nullpo.nm9.engine.game;

import java.io.Serializable;

import cx.it.nullpo.nm9.engine.common.Copyable;
import cx.it.nullpo.nm9.engine.common.NEUtil;
import cx.it.nullpo.nm9.engine.definition.Block;
import cx.it.nullpo.nm9.engine.definition.Game;

/**
 * The onscreen block
 * @author NullNoname
 */
public class RuntimeBlock implements Serializable, Copyable {
	private static final long serialVersionUID = 83475582447676107L;

	/**
	 * Definition of the block
	 */
	private Block blockDef;

	/**
	 * The internal time this block has placed in the field.
	 * -1 if not yet placed, or -2 if it's the part of initial layout.
	 */
	private long placeInternalTime = -1;

	/**
	 * The game time this block has placed in the field.
	 * -1 if not yet placed, or -2 if it's the part of initial layout.
	 */
	private long placeGameTime = -1;

	/**
	 * true if this block is marked for deletion
	 */
	private boolean markDeletion;

	/**
	 * true if this block is checked for something (used internally)
	 */
	private boolean markChecked;

	/**
	 * true if this block is broken (after a line clear)
	 */
	private boolean broken;

	/**
	 * Connection flags
	 */
	private boolean connectUp, connectDown, connectLeft, connectRight;

	public RuntimeBlock() {
	}

	public RuntimeBlock(Block blockDef) {
		this.blockDef = blockDef;
	}

	public RuntimeBlock(Game gameDef, String name) {
		this.blockDef = (Block)NEUtil.getNamedObjectE(name, gameDef.getBlocks());
	}

	public RuntimeBlock(Copyable src) {
		copy(src);
	}

	public void copy(Copyable src) {
		RuntimeBlock s = (RuntimeBlock)src;

		if(s.blockDef == null) this.blockDef = null;
		else this.blockDef = new Block(s.blockDef);

		this.placeInternalTime = s.placeInternalTime;
		this.placeGameTime = s.placeGameTime;
		this.markDeletion = s.markDeletion;
		this.markChecked = s.markChecked;
		this.broken = s.broken;
		this.connectUp = s.connectUp;
		this.connectDown = s.connectDown;
		this.connectLeft = s.connectLeft;
		this.connectRight = s.connectRight;
	}

	public Block getBlockDef() {
		return blockDef;
	}

	public void setBlockDef(Block blockDef) {
		this.blockDef = blockDef;
	}

	public long getPlaceInternalTime() {
		return placeInternalTime;
	}

	public void setPlaceInternalTime(long placeInternalTime) {
		this.placeInternalTime = placeInternalTime;
	}

	public long getPlaceGameTime() {
		return placeGameTime;
	}

	public void setPlaceGameTime(long placeGameTime) {
		this.placeGameTime = placeGameTime;
	}

	public boolean isMarkDeletion() {
		return markDeletion;
	}

	public void setMarkDeletion(boolean markDeletion) {
		this.markDeletion = markDeletion;
	}

	public boolean isMarkChecked() {
		return markChecked;
	}

	public void setMarkChecked(boolean markChecked) {
		this.markChecked = markChecked;
	}

	public boolean isBroken() {
		return broken;
	}

	public void setBroken(boolean broken) {
		this.broken = broken;
	}

	public boolean isConnectUp() {
		return connectUp;
	}

	public void setConnectUp(boolean connectUp) {
		this.connectUp = connectUp;
	}

	public boolean isConnectDown() {
		return connectDown;
	}

	public void setConnectDown(boolean connectDown) {
		this.connectDown = connectDown;
	}

	public boolean isConnectLeft() {
		return connectLeft;
	}

	public void setConnectLeft(boolean connectLeft) {
		this.connectLeft = connectLeft;
	}

	public boolean isConnectRight() {
		return connectRight;
	}

	public void setConnectRight(boolean connectRight) {
		this.connectRight = connectRight;
	}
}
