package cx.it.nullpo.nm9.engine.event.output;

import cx.it.nullpo.nm9.engine.game.GameManager;
import cx.it.nullpo.nm9.engine.game.GameSnapshot;
import cx.it.nullpo.nm9.engine.game.RuntimePiece;

public class PieceUpdateEvent extends SnapshotUpdateEvent {
	private static final long serialVersionUID = 4230761948162084471L;

	/** Current piece object */
	protected RuntimePiece nowPieceObject;
	/** Current piece position */
	protected int nowPieceX, nowPieceY;
	/** Current piece state */
	protected int nowPieceState;
	/** Ghost Y position */
	protected int nowPieceBottomY;

	public PieceUpdateEvent() {
	}

	public PieceUpdateEvent(GameManager manager, GameSnapshot snapshot) {
		super(manager, snapshot);
		this.nowPieceObject = snapshot.getNowPieceObject();
		this.nowPieceX = snapshot.getNowPieceX();
		this.nowPieceY = snapshot.getNowPieceY();
		this.nowPieceState = snapshot.getNowPieceState();
		this.nowPieceBottomY = snapshot.getNowPieceBottomY();
	}

	public RuntimePiece getNowPieceObject() {
		return nowPieceObject;
	}

	public int getNowPieceX() {
		return nowPieceX;
	}

	public int getNowPieceY() {
		return nowPieceY;
	}

	public int getNowPieceState() {
		return nowPieceState;
	}

	public int getNowPieceBottomY() {
		return nowPieceBottomY;
	}
}
