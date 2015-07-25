package cx.it.nullpo.nm9.engine.game;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.reflect.ConstructorUtils;

import cx.it.nullpo.nm9.engine.common.Copyable;
import cx.it.nullpo.nm9.engine.definition.Game;

/**
 * Container for every data required for rendering the game screen
 * @author NullNoname
 */
public class GameSnapshot implements Serializable, Copyable {
	private static final long serialVersionUID = 384279139862519964L;

	/**
	 * Game engine ID
	 */
	protected int engineID;

	/**
	 * Player ID
	 */
	protected int playerID;

	/**
	 * The game definition
	 */
	protected Game gameDef;

	/**
	 * Field
	 */
	protected Field field;

	/**
	 * Map for various variable maps
	 */
	protected Map<String, Object> mapVariables;

	/**
	 * Button status
	 */
	protected ButtonStatus buttonStatus;

	/**
	 * Next piece queue
	 */
	protected PieceQueue pieceQueue;

	/**
	 * Current piece object
	 */
	protected RuntimePiece nowPieceObject;

	/**
	 * Current piece X position
	 */
	protected int nowPieceX;

	/**
	 * Current piece Y position
	 */
	protected int nowPieceY;

	/**
	 * Current piece state (direction)
	 */
	protected int nowPieceState;

	/**
	 * Current piece ghost Y position
	 */
	protected int nowPieceBottomY;

	/**
	 * Current piece gravity value (when this value exceeds the denominator, the piece will move 1 space down)
	 */
	protected long nowPieceGravity;

	/**
	 * Current piece lock delay value
	 */
	protected long nowPieceLockDelay;

	/**
	 * Hold piece object
	 */
	protected RuntimePiece holdPieceObject;

	/**
	 * Hold used flag
	 */
	protected boolean holdPieceUsed;

	/**
	 * Ready->Go timer
	 */
	protected long readyTimer;

	/**
	 * Line clear timer
	 */
	protected long lineTimer;

	/**
	 * Stack fall timer
	 */
	protected long stackFallTimer;

	/**
	 * ARE timer
	 */
	protected long areTimer;

	public GameSnapshot() {
	}

	public GameSnapshot(Copyable src) {
		copy(src);
	}

	public GameSnapshot(Copyable src, boolean isCopyField) {
		copy(src, isCopyField);
	}

	public void copy(Copyable src) {
		copy(src, true);
	}

	public void copy(Copyable src, boolean isCopyField) {
		GameSnapshot s = (GameSnapshot)src;

		this.gameDef = new Game(s.gameDef);

		if(isCopyField) this.field = new Field(s.field);
		else this.field = s.field;

		try {
			this.pieceQueue = (PieceQueue)ConstructorUtils.invokeConstructor(Copyable.class, s.pieceQueue);
		} catch (Throwable e) {
			this.pieceQueue = SerializationUtils.clone(s.pieceQueue);
		}

		this.mapVariables = new HashMap<String, Object>();
		this.buttonStatus = new ButtonStatus(s.buttonStatus);
		this.nowPieceObject = (s.nowPieceObject == null) ? null : new RuntimePiece(s.nowPieceObject);
		this.nowPieceX = s.nowPieceX;
		this.nowPieceY = s.nowPieceY;
		this.nowPieceBottomY = s.nowPieceBottomY;
		this.nowPieceState = s.nowPieceState;
		this.nowPieceGravity = s.nowPieceGravity;
		this.nowPieceLockDelay = s.nowPieceLockDelay;
		this.holdPieceObject = (s.holdPieceObject == null) ? null : new RuntimePiece(s.holdPieceObject);
		this.holdPieceUsed = s.holdPieceUsed;
		this.readyTimer = s.readyTimer;
		this.lineTimer = s.lineTimer;
		this.stackFallTimer = s.stackFallTimer;
		this.areTimer = s.areTimer;
	}

	public int getEngineID() {
		return engineID;
	}

	public void setEngineID(int engineID) {
		this.engineID = engineID;
	}

	public int getPlayerID() {
		return playerID;
	}

	public void setPlayerID(int playerID) {
		this.playerID = playerID;
	}

	public Game getGameDef() {
		return gameDef;
	}

	public void setGameDef(Game gameDef) {
		this.gameDef = gameDef;
	}

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}

	public Map<String, Object> getMapVariables() {
		return mapVariables;
	}

	public void setMapVariables(Map<String, Object> mapVariables) {
		this.mapVariables = mapVariables;
	}

	public ButtonStatus getButtonStatus() {
		return buttonStatus;
	}

	public void setButtonStatus(ButtonStatus buttonStatus) {
		this.buttonStatus = buttonStatus;
	}

	public PieceQueue getPieceQueue() {
		return pieceQueue;
	}

	public void setPieceQueue(PieceQueue pieceQueue) {
		this.pieceQueue = pieceQueue;
	}

	public RuntimePiece getNowPieceObject() {
		return nowPieceObject;
	}

	public void setNowPieceObject(RuntimePiece nowPieceObject) {
		this.nowPieceObject = nowPieceObject;
	}

	public int getNowPieceX() {
		return nowPieceX;
	}

	public void setNowPieceX(int nowPieceX) {
		this.nowPieceX = nowPieceX;
	}

	public int getNowPieceY() {
		return nowPieceY;
	}

	public void setNowPieceY(int nowPieceY) {
		this.nowPieceY = nowPieceY;
	}

	public int getNowPieceBottomY() {
		return nowPieceBottomY;
	}

	public void setNowPieceBottomY(int nowPieceBottomY) {
		this.nowPieceBottomY = nowPieceBottomY;
	}

	public void updateNowPieceBottomY() {
		if(nowPieceObject != null) {
			nowPieceBottomY = nowPieceObject.fieldBottom(field, nowPieceX, nowPieceY, nowPieceState);
		}
	}

	public int getNowPieceState() {
		return nowPieceState;
	}

	public void setNowPieceState(int nowPieceState) {
		this.nowPieceState = nowPieceState;
	}

	public long getNowPieceGravity() {
		return nowPieceGravity;
	}

	public void setNowPieceGravity(long nowPieceGravity) {
		this.nowPieceGravity = nowPieceGravity;
	}

	public long getNowPieceLockDelay() {
		return nowPieceLockDelay;
	}

	public void setNowPieceLockDelay(long nowPieceLockDelay) {
		this.nowPieceLockDelay = nowPieceLockDelay;
	}

	public RuntimePiece getHoldPieceObject() {
		return holdPieceObject;
	}

	public void setHoldPieceObject(RuntimePiece holdPieceObject) {
		this.holdPieceObject = holdPieceObject;
	}

	public boolean isHoldPieceUsed() {
		return holdPieceUsed;
	}

	public void setHoldPieceUsed(boolean holdPieceUsed) {
		this.holdPieceUsed = holdPieceUsed;
	}

	public long getReadyTimer() {
		return readyTimer;
	}

	public void setReadyTimer(long readyTimer) {
		this.readyTimer = readyTimer;
	}

	public long getLineTimer() {
		return lineTimer;
	}

	public void setLineTimer(long lineTimer) {
		this.lineTimer = lineTimer;
	}

	public long getStackFallTimer() {
		return stackFallTimer;
	}

	public void setStackFallTimer(long stackFallTimer) {
		this.stackFallTimer = stackFallTimer;
	}

	public long getARETimer() {
		return areTimer;
	}

	public void setARETimer(long areTimer) {
		this.areTimer = areTimer;
	}
}
