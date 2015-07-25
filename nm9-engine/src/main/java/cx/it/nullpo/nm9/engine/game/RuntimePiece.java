package cx.it.nullpo.nm9.engine.game;

import java.io.Serializable;
import java.util.ArrayList;

import cx.it.nullpo.nm9.engine.common.Copyable;
import cx.it.nullpo.nm9.engine.common.NRandom;
import cx.it.nullpo.nm9.engine.definition.Game;
import cx.it.nullpo.nm9.engine.definition.Piece;
import cx.it.nullpo.nm9.engine.definition.PieceState;
import cx.it.nullpo.nm9.engine.definition.XYPair;

/**
 * The onscreen piece
 * @author NullNoname
 */
public class RuntimePiece implements Serializable, Copyable {
	private static final long serialVersionUID = -2744397138078475043L;

	/**
	 * Definition of the piece
	 */
	private Piece pieceDef;

	/**
	 * Blocks in the piece
	 */
	private RuntimeBlock[] blocks;

	public RuntimePiece() {
	}

	public RuntimePiece(Game gameDef, Piece pieceDef, NRandom random) {
		this.pieceDef = pieceDef;

		ArrayList<String> blkNameList = new ArrayList<String>();
		if(pieceDef.isRandomizeBlock()) {
			// Randomize the block list
			while(blkNameList.size() < pieceDef.getBlocks().size()) {
				blkNameList.add(pieceDef.getBlocks().get(random.nextInt(pieceDef.getBlocks().size())));
			}
		} else {
			blkNameList.addAll(pieceDef.getBlocks());
		}

		// Set the number of blocks
		int numBlocks = pieceDef.getNumBlocks();
		if(numBlocks <= 0) {
			int n = blkNameList.size();
			for(PieceState state: pieceDef.getStates()) {
				n = Math.min(n, state.getBlockPosList().size());
			}
			numBlocks = n;
		}

		blocks = new RuntimeBlock[numBlocks];
		for(int i = 0; i < numBlocks; i++) {
			String blkName = blkNameList.get(i);
			blocks[i] = new RuntimeBlock(gameDef, blkName);
		}
	}

	public RuntimePiece(Copyable src) {
		copy(src);
	}

	public void copy(Copyable src) {
		RuntimePiece s = (RuntimePiece)src;
		this.pieceDef = new Piece(s.pieceDef);
		this.blocks = new RuntimeBlock[s.blocks.length];
		for(int i = 0; i < s.blocks.length; i++) {
			this.blocks[i] = new RuntimeBlock(s.blocks[i]);
		}
	}

	public int getNumBlocks() {
		if(blocks == null) return -1;
		return blocks.length;
	}

	public int getNumStates() {
		if(pieceDef == null) return -1;
		return pieceDef.getStates().size();
	}

	public XYPair getBlockPosition(int stateID, int blockID) {
		return pieceDef.getStates().get(stateID).getBlockPosList().get(blockID);
	}

	public int getBlockPositionX(int stateID, int blockID) {
		return getBlockPosition(stateID, blockID).getX();
	}

	public int getBlockPositionY(int stateID, int blockID) {
		return getBlockPosition(stateID, blockID).getY();
	}

	public boolean isBlockConnectUp(int stateID, int blockID) {
		if(pieceDef.getConnectionType().equals("None")) return false;
		boolean sameBlockRequired = pieceDef.getConnectionType().equals("SameBlock");

		int x = getBlockPositionX(stateID, blockID);
		int y = getBlockPositionY(stateID, blockID);
		RuntimeBlock b = getRuntimeBlock(blockID);
		for(int i = 0; i < blocks.length; i++) {
			if(i != blockID) {
				int x2 = getBlockPositionX(stateID, i);
				int y2 = getBlockPositionY(stateID, i);
				RuntimeBlock b2 = getRuntimeBlock(i);

				if(x2 == x && y2 == y - 1 && (!sameBlockRequired || b.getBlockDef().getName().equals(b2.getBlockDef().getName())))
					return true;
			}
		}

		return false;
	}

	public boolean isBlockConnectDown(int stateID, int blockID) {
		if(pieceDef.getConnectionType().equals("None")) return false;
		boolean sameBlockRequired = pieceDef.getConnectionType().equals("SameBlock");

		int x = getBlockPositionX(stateID, blockID);
		int y = getBlockPositionY(stateID, blockID);
		RuntimeBlock b = getRuntimeBlock(blockID);
		for(int i = 0; i < blocks.length; i++) {
			if(i != blockID) {
				int x2 = getBlockPositionX(stateID, i);
				int y2 = getBlockPositionY(stateID, i);
				RuntimeBlock b2 = getRuntimeBlock(i);

				if(x2 == x && y2 == y + 1 && (!sameBlockRequired || b.getBlockDef().getName().equals(b2.getBlockDef().getName())))
					return true;
			}
		}

		return false;
	}

	public boolean isBlockConnectLeft(int stateID, int blockID) {
		if(pieceDef.getConnectionType().equals("None")) return false;
		boolean sameBlockRequired = pieceDef.getConnectionType().equals("SameBlock");

		int x = getBlockPositionX(stateID, blockID);
		int y = getBlockPositionY(stateID, blockID);
		RuntimeBlock b = getRuntimeBlock(blockID);
		for(int i = 0; i < blocks.length; i++) {
			if(i != blockID) {
				int x2 = getBlockPositionX(stateID, i);
				int y2 = getBlockPositionY(stateID, i);
				RuntimeBlock b2 = getRuntimeBlock(i);

				if(x2 == x - 1 && y2 == y && (!sameBlockRequired || b.getBlockDef().getName().equals(b2.getBlockDef().getName())))
					return true;
			}
		}

		return false;
	}

	public boolean isBlockConnectRight(int stateID, int blockID) {
		if(pieceDef.getConnectionType().equals("None")) return false;
		boolean sameBlockRequired = pieceDef.getConnectionType().equals("SameBlock");

		int x = getBlockPositionX(stateID, blockID);
		int y = getBlockPositionY(stateID, blockID);
		RuntimeBlock b = getRuntimeBlock(blockID);
		for(int i = 0; i < blocks.length; i++) {
			if(i != blockID) {
				int x2 = getBlockPositionX(stateID, i);
				int y2 = getBlockPositionY(stateID, i);
				RuntimeBlock b2 = getRuntimeBlock(i);

				if(x2 == x + 1 && y2 == y && (!sameBlockRequired || b.getBlockDef().getName().equals(b2.getBlockDef().getName())))
					return true;
			}
		}

		return false;
	}

	public Piece getPieceDef() {
		return pieceDef;
	}

	public void setPieceDef(Piece pieceDef) {
		this.pieceDef = pieceDef;
	}

	public RuntimeBlock[] getBlocks() {
		return blocks;
	}

	public void setBlocks(RuntimeBlock[] blocks) {
		this.blocks = blocks;
	}

	public RuntimeBlock getRuntimeBlock(int blockID) {
		return blocks[blockID];
	}

	public void setRuntimeBlock(int blockID, RuntimeBlock b) {
		blocks[blockID] = b;
	}

	public int getMinBlockPositionX(int stateID) {
		int m = Integer.MAX_VALUE;
		for(int i = 0; i < getNumBlocks(); i++) {
			m = Math.min(m, getBlockPositionX(stateID, i));
		}
		return m;
	}

	public int getMaxBlockPositionX(int stateID) {
		int m = Integer.MIN_VALUE;
		for(int i = 0; i < getNumBlocks(); i++) {
			m = Math.max(m, getBlockPositionX(stateID, i));
		}
		return m;
	}

	public int getMinBlockPositionY(int stateID) {
		int m = Integer.MAX_VALUE;
		for(int i = 0; i < getNumBlocks(); i++) {
			m = Math.min(m, getBlockPositionY(stateID, i));
		}
		return m;
	}

	public int getMaxBlockPositionY(int stateID) {
		int m = Integer.MIN_VALUE;
		for(int i = 0; i < getNumBlocks(); i++) {
			m = Math.max(m, getBlockPositionY(stateID, i));
		}
		return m;
	}

	public int getWidth(int stateID) {
		return (getMaxBlockPositionX(stateID) - getMinBlockPositionX(stateID)) + 1;
	}

	public int getWidth() {
		int m = 0;
		for(int i = 0; i < getNumStates(); i++) {
			m = Math.max(m, getWidth(i));
		}
		return m;
	}

	public int getHeight(int stateID) {
		return (getMaxBlockPositionY(stateID) - getMinBlockPositionY(stateID)) + 1;
	}

	public int getHeight() {
		int m = 0;
		for(int i = 0; i < getNumStates(); i++) {
			m = Math.max(m, getHeight(i));
		}
		return m;
	}

	public boolean fieldCollision(Field fld, int x, int y, int stateID) {
		for(int i = 0; i < blocks.length; i++) {
			XYPair blkPos = getBlockPosition(stateID, i);
			if(!fld.isEmpty(x + blkPos.getX(), y + blkPos.getY())) {
				return true;
			}
		}
		return false;
	}

	public boolean fieldFullLockout(Field fld, int x, int y, int stateID) {
		for(int i = 0; i < blocks.length; i++) {
			XYPair blkPos = getBlockPosition(stateID, i);
			int coordType = fld.getCoordType(x + blkPos.getX(), y + blkPos.getY());
			if(coordType == Field.COORD_NORMAL || coordType == Field.COORD_WALL)
				return false;
		}
		return true;
	}

	public boolean fieldPartialLockout(Field fld, int x, int y, int stateID) {
		for(int i = 0; i < blocks.length; i++) {
			XYPair blkPos = getBlockPosition(stateID, i);
			int coordType = fld.getCoordType(x + blkPos.getX(), y + blkPos.getY());
			if(coordType == Field.COORD_HIDDEN || coordType == Field.COORD_VANISH)
				return true;
		}
		return false;
	}

	public int fieldPut(Field fld, int x, int y, int stateID, long internalTime, long gameTime) {
		int putCount = 0;
		for(int i = 0; i < blocks.length; i++) {
			RuntimeBlock blk = new RuntimeBlock(blocks[i].getBlockDef());
			blk.setPlaceInternalTime(internalTime);
			blk.setPlaceGameTime(gameTime);
			blk.setConnectUp(isBlockConnectUp(stateID, i));
			blk.setConnectDown(isBlockConnectDown(stateID, i));
			blk.setConnectLeft(isBlockConnectLeft(stateID, i));
			blk.setConnectRight(isBlockConnectRight(stateID, i));

			XYPair blkPos = getBlockPosition(stateID, i);
			if(fld.setBlock(x + blkPos.getX(), y + blkPos.getY(), blk))
				putCount++;
		}
		return putCount;
	}

	public int fieldBottom(Field fld, int x, int y, int stateID) {
		int y2 = y;
		while(!fieldCollision(fld, x, y2, stateID)) y2++;
		return y2 - 1;
	}

	public int fieldFarLeft(Field fld, int x, int y, int stateID) {
		int x2 = x;
		while(!fieldCollision(fld, x2, y, stateID)) x2--;
		return x2 + 1;
	}

	public int fieldFarRight(Field fld, int x, int y, int stateID) {
		int x2 = x;
		while(!fieldCollision(fld, x2, y, stateID)) x2++;
		return x2 - 1;
	}

	public int getRotateAfterState(int beforeState, int rot) {
		int afterState = beforeState + rot;
		if(afterState < 0) {
			afterState = getNumStates() - 1;
		} else if(afterState >= getNumStates()) {
			afterState = afterState - getNumStates();
		}
		return afterState;
	}
}
