package cx.it.nullpo.nm9.engine.event.output;

import cx.it.nullpo.nm9.engine.game.GameSnapshot;
import cx.it.nullpo.nm9.engine.game.RuntimePiece;

/**
 * An event that caused by a successful piece movement
 * @author NullNoname
 */
public class PieceMovedEvent extends NMOutputEvent {
	private static final long serialVersionUID = -3800783518451353377L;

	protected long remainingUpdateFrames;

	/** Piece object */
	protected RuntimePiece pieceObject;

	/** Old piece position and state */
	protected int oldPieceX, oldPieceY, oldPieceState, oldPieceBottomY;

	/** New piece position and state */
	protected int newPieceX, newPieceY, newPieceState, newPieceBottomY;

	public PieceMovedEvent() {
	}

	public PieceMovedEvent(long remainingUpdateFrames, GameSnapshot snap, int oldPieceX, int oldPieceY, int oldPieceState, int oldPieceBottomY) {
		this.remainingUpdateFrames = remainingUpdateFrames;
		this.pieceObject = snap.getNowPieceObject();
		this.oldPieceX = oldPieceX;
		this.oldPieceY = oldPieceY;
		this.oldPieceState = oldPieceState;
		this.oldPieceBottomY = oldPieceBottomY;
		this.newPieceX = snap.getNowPieceX();
		this.newPieceY = snap.getNowPieceY();
		this.newPieceState = snap.getNowPieceState();
		this.newPieceBottomY = snap.getNowPieceBottomY();
	}

	public PieceMovedEvent(long remainingUpdateFrames, RuntimePiece pieceObject, int oldPieceX,
			int oldPieceY, int oldPieceState, int oldPieceBottomY,
			int newPieceX, int newPieceY, int newPieceState, int newPieceBottomY)
	{
		this.remainingUpdateFrames = remainingUpdateFrames;
		this.pieceObject = pieceObject;
		this.oldPieceX = oldPieceX;
		this.oldPieceY = oldPieceY;
		this.oldPieceState = oldPieceState;
		this.oldPieceBottomY = oldPieceBottomY;
		this.newPieceX = newPieceX;
		this.newPieceY = newPieceY;
		this.newPieceState = newPieceState;
		this.newPieceBottomY = newPieceBottomY;
	}

	public RuntimePiece getPieceObject() {
		return pieceObject;
	}

	public int getOldPieceX() {
		return oldPieceX;
	}

	public int getOldPieceY() {
		return oldPieceY;
	}

	public int getOldPieceState() {
		return oldPieceState;
	}

	public int getOldPieceBottomY() {
		return oldPieceBottomY;
	}

	public int getNewPieceX() {
		return newPieceX;
	}

	public int getNewPieceY() {
		return newPieceY;
	}

	public int getNewPieceState() {
		return newPieceState;
	}

	public int getNewPieceBottomY() {
		return newPieceBottomY;
	}

	public int getDifferenceX() {
		return newPieceX - oldPieceX;
	}

	public int getDifferenceY() {
		return newPieceY - oldPieceY;
	}
}
