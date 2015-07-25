package cx.it.nullpo.nm9.engine.game;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import cx.it.nullpo.nm9.engine.common.Copyable;
import cx.it.nullpo.nm9.engine.common.NRandom;
import cx.it.nullpo.nm9.engine.definition.Game;
import cx.it.nullpo.nm9.engine.definition.Level;
import cx.it.nullpo.nm9.engine.definition.Pattern;
import cx.it.nullpo.nm9.engine.definition.Speed;
import cx.it.nullpo.nm9.engine.definition.Wallkick;
import cx.it.nullpo.nm9.engine.definition.XYPair;
import cx.it.nullpo.nm9.engine.event.output.PieceMovedEvent;
import cx.it.nullpo.nm9.engine.event.output.PieceUpdateEvent;
import cx.it.nullpo.nm9.engine.event.output.PlayerDeathEvent;
import cx.it.nullpo.nm9.engine.event.output.SnapshotUpdateEvent;
import cx.it.nullpo.nm9.engine.script.ScriptHost;

/**
 * Game logic for each player. This is where most actions take place.
 * @author NullNoname
 */
public class GamePlay implements Serializable, Copyable {
	private static final long serialVersionUID = -1202115515249179269L;

	/**
	 * The GameEngine that owns this GamePlay
	 */
	private GameEngine engine;

	/**
	 * Random number generator for this GamePlay
	 */
	private NRandom random;

	/**
	 * Player ID
	 */
	private int playerID;

	/**
	 * Contains various variables
	 */
	private GameSnapshot snap;

	/**
	 * Current game state
	 */
	private String gameState;

	/**
	 * Deepest Y position the current piece reached
	 */
	private int nowPieceDeepestY;

	/**
	 * Old piece object
	 */
	private RuntimePiece oldPieceObject;

	/**
	 * Old piece positions
	 */
	private int oldPieceX, oldPieceY;

	/**
	 * Old piece state
	 */
	private int oldPieceState;

	/**
	 * Old ghost piece Y position
	 */
	private int oldPieceBottomY;

	/**
	 * true if Hold is used (used for piece spawn)
	 */
	private boolean holdSpawn;

	/**
	 * Previous piece object
	 */
	private RuntimePiece prevPieceObject;

	/**
	 * Previous piece position
	 */
	private int prevPieceX, prevPieceY;

	/**
	 * Previous piece state
	 */
	private int prevPieceState;

	/**
	 * Cleared block's Y position set (used by classic stackfall)
	 */
	private Set<Integer> eraseYSet;

	/**
	 * Duration of ARE (set before entering ARE state)
	 */
	private long areValue;

	/**
	 * Initial Rotation used in the current frame
	 */
	private boolean irsUsed;

	/**
	 * true if at least 1 pattern has activated
	 */
	private boolean patternActivated;

	public GamePlay() {
	}

	public GamePlay(GameEngine engine, NRandom random) {
		this.engine = engine;
		this.random = new NRandom(random);
		this.snap = new GameSnapshot();
		init();
	}

	public GamePlay(GameEngine engine, NRandom random, int playerID) {
		this.engine = engine;
		this.random = random;
		this.playerID = playerID;
		init();
	}

	public GamePlay(Copyable src) {
		copy(src);
	}

	public void copy(Copyable src) {
		GamePlay s = (GamePlay)src;
		this.engine = s.engine;	// Should be handled by GameEngine's copy method
		this.snap = new GameSnapshot(s.snap, false);
		this.snap.field = null;	// Should be handled by GameEngine's copy method
		this.random = new NRandom(s.random);
		this.gameState = s.gameState;
		this.nowPieceDeepestY = s.nowPieceDeepestY;
		this.oldPieceObject = (s.oldPieceObject == null) ? null : new RuntimePiece(s.oldPieceObject);
		this.oldPieceX = s.oldPieceX;
		this.oldPieceY = s.oldPieceY;
		this.oldPieceState = s.oldPieceState;
		this.oldPieceBottomY = s.oldPieceBottomY;
		this.holdSpawn = s.holdSpawn;
		this.eraseYSet = (s.eraseYSet == null) ? null : new HashSet<Integer>(s.eraseYSet);
		this.areValue = s.areValue;
		this.irsUsed = s.irsUsed;
		this.patternActivated = s.patternActivated;
	}

	public void init() {
		snap.engineID = engine.getEngineID();
		snap.playerID = playerID;
		snap.buttonStatus = new ButtonStatus();
		snap.pieceQueue = new BJPieceQueue(getGameDef(), new NRandom(random));
		snap.gameDef = engine.getGameDef();
		snap.field = engine.getField();
		gameState = "ready";
	}

	public GameEngine getEngine() {
		return engine;
	}

	public void setEngine(GameEngine engine) {
		this.engine = engine;
	}

	public Game getGameDef() {
		return engine.getGameDef();
	}

	public ScriptHost getScriptHost() {
		return engine.getScriptHost();
	}

	public GameManager getManager() {
		return engine.getManager();
	}

	public Field getField() {
		return engine.getField();
	}

	public NRandom getRandom() {
		return random;
	}

	public void setRandom(NRandom random) {
		this.random = random;
	}

	public int getPlayerID() {
		return playerID;
	}

	public void setPlayerID(int playerID) {
		this.playerID = playerID;
	}

	public String getGameState() {
		return gameState;
	}

	public void setGameState(String gameState) {
		this.gameState = gameState;
	}

	public GameSnapshot getSnapshot() {
		return snap;
	}

	public void setSnapshot(GameSnapshot snap) {
		this.snap = snap;
	}

	public ButtonStatus getButtonStatus() {
		return snap.getButtonStatus();
	}

	public Speed getSpeedDef() {
		// TODO: Get the speed definition
		return getGameDef().getSpeeds().get(0);
	}

	public Level getLevelDef() {
		// TODO: Get the level definition
		return getGameDef().getLevels().get(0);
	}

	public void update(long remainingUpdateFrames) {
		getButtonStatus().update(getManager().getInternalTimer());
		getScriptHost().callNull("gameUpdateStart", this, remainingUpdateFrames);

		if(gameState.equals("ready"))
			gsReady();
		else if(gameState.equals("move"))
			gsMove();
		else if(gameState.equals("erase"))
			gsErase();
		else if(gameState.equals("stackfall"))
			gsStackFall();
		else if(gameState.equals("are"))
			gsARE();
		else if(gameState.length() > 0)
			getScriptHost().callNull("gsCustom", this, gameState);

		getScriptHost().callNull("gameUpdateEnd", this, remainingUpdateFrames);
	}

	public void gsReady() {
		if(getScriptHost().callBoolean("gsReadyStart", this)) return;

		snap.readyTimer++;
		if(snap.readyTimer >= getGameDef().getConfig().getReadyTime()) {
			gameState = "move";
			getScriptHost().callNull("gsReadyGameStart", this);
		}

		getScriptHost().callNull("gsReadyEnd", this);
	}

	public boolean moveSlide(int m) {
		if(snap.nowPieceObject == null) return false;
		if(!snap.nowPieceObject.fieldCollision(getField(), snap.nowPieceX + m, snap.nowPieceY, snap.nowPieceState)) {
			snap.nowPieceX = snap.nowPieceX + m;
			snap.updateNowPieceBottomY();
			snap.nowPieceLockDelay = 0;
			return true;
		}
		return false;
	}

	public boolean moveDown(boolean softDrop) {
		if(snap.nowPieceObject == null) return false;
		if(!snap.nowPieceObject.fieldCollision(getField(), snap.nowPieceX, snap.nowPieceY + 1, snap.nowPieceState)) {
			snap.nowPieceY = snap.nowPieceY + 1;
			snap.nowPieceLockDelay = 0;
			if(snap.nowPieceY > nowPieceDeepestY) {
				nowPieceDeepestY = snap.nowPieceY;
			}
			return true;
		}
		return false;
	}

	public void gsMove() {
		if(getScriptHost().callBoolean("gsMoveStart", this)) return;

		boolean repeat = false;
		do {
			for(String phaseName: getGameDef().getMovePhase()) {
				if(phaseName.equals("hold"))
					phaseMoveHold();
				else if(phaseName.equals("spawn"))
					phaseMoveSpawn();
				else if(phaseName.equals("rotate"))
					phaseMoveRotate();
				else if(phaseName.equals("slide"))
					phaseMoveSlide();
				else if(phaseName.equals("soft"))
					phaseMoveSoft();
				else if(phaseName.equals("hard"))
					phaseMoveHard();
				else if(phaseName.equals("gravity"))
					phaseMoveGravity();
				else if(phaseName.equals("lock"))
					phaseMoveLock();
				else
					getScriptHost().callNull("gsMoveCustomPhase", this, phaseName);

				if(!gameState.equals("move")) {
					getScriptHost().callNull("gsMoveTransition", this, gameState);
					return;
				} else if(holdSpawn) {
					repeat = true;
					holdSpawn = false;
					break;
				}
			}
		} while (repeat);

		// End of frame
		irsUsed = false;

		// Event publish
		if(snap.nowPieceObject == null) {
			// Current piece has been locked
		} else if(snap.nowPieceObject != oldPieceObject) {
			oldPieceObject = snap.nowPieceObject;
			getManager().publishEvent( new PieceUpdateEvent(getManager(), getSnapshot()) );
		} else {
			if((snap.nowPieceX != oldPieceX) || (snap.nowPieceY != oldPieceY) || (snap.nowPieceState != oldPieceState)
			 || (snap.nowPieceBottomY != oldPieceBottomY))
			{
				getManager().publishEvent(
					new PieceMovedEvent(
							getManager().getRemainingUpdateFrames(),
							getSnapshot(),
							oldPieceX,
							oldPieceY,
							oldPieceState,
							oldPieceBottomY)
					);
			}
			oldPieceX = snap.nowPieceX;
			oldPieceY = snap.nowPieceY;
			oldPieceState = snap.nowPieceState;
			oldPieceBottomY = snap.nowPieceBottomY;
		}

		getScriptHost().callNull("gsMoveEnd", this);
	}

	public void pieceSpawn() {
		// variables
		snap.nowPieceGravity = 0;
		snap.nowPieceLockDelay = 0;
		nowPieceDeepestY = -getField().getHiddenHeight();

		// state
		int defaultState = snap.nowPieceObject.getPieceDef().getDefaultState();
		snap.nowPieceState = defaultState;

		// x
		int spawnOffsetX = snap.nowPieceObject.getPieceDef().getStates().get(snap.nowPieceState).getSpawnOffsetX();
		snap.nowPieceX = ((getField().getWidth() - snap.nowPieceObject.getWidth(snap.nowPieceState)) / 2) + spawnOffsetX;

		// y
		int spawnOffsetY = snap.nowPieceObject.getPieceDef().getStates().get(snap.nowPieceState).getSpawnOffsetY();
		if(getGameDef().getConfig().isSpawnBelowSkyline()) {
			snap.nowPieceY = spawnOffsetY;
		} else {
			//snap.nowPieceY = -snap.nowPieceObject.getHeight(snap.nowPieceState) + spawnOffsetY;
			snap.nowPieceY = -snap.nowPieceObject.getMaxBlockPositionY(snap.nowPieceState) - 1 + spawnOffsetY;
		}

		// push up
		int pushUpCount = 0;
		while((pushUpCount < getGameDef().getConfig().getSpawnPushUp()) &&
		      (snap.nowPieceObject.fieldCollision(getField(), snap.nowPieceX, snap.nowPieceY, snap.nowPieceState) == true))
		{
			snap.nowPieceY--;
			pushUpCount++;
		}

		// IRS
		boolean allowIRS = getGameDef().getConfig().isAllowIRS();
		if(patternActivated) allowIRS = getGameDef().getConfig().isAllowIRSAfterClear();

		if(allowIRS) {
			int rot = 0;
			if(getButtonStatus().isPressed(ButtonStatus.ID_ROTATECCW)) rot = -1;
			if(getButtonStatus().isPressed(ButtonStatus.ID_ROTATECW)) rot = 1;
			if(getButtonStatus().isPressed(ButtonStatus.ID_ROTATE180)) rot = 2;
			if(rot != 0) {
				int afterState = snap.nowPieceObject.getRotateAfterState(snap.nowPieceState, rot);

				if(!snap.nowPieceObject.fieldCollision(getField(), snap.nowPieceX, snap.nowPieceY, afterState)) {
					snap.nowPieceState = afterState;
					irsUsed = true;
				}
			}
		}

		// Ghost position
		snap.updateNowPieceBottomY();

		// game over
		if(snap.nowPieceObject.fieldCollision(getField(), snap.nowPieceX, snap.nowPieceY, snap.nowPieceState) == true) {
			// TODO: game over
			gameState = "dead";
			snap.nowPieceObject.fieldPut(getField(), snap.nowPieceX, snap.nowPieceY, snap.nowPieceState, 0, 0);
			getManager().publishEvent( new PlayerDeathEvent(getManager(), getSnapshot()) );
		}

		oldPieceX = snap.nowPieceX;
		oldPieceY = snap.nowPieceY;
		oldPieceState = snap.nowPieceState;
		oldPieceBottomY = snap.nowPieceBottomY;
		patternActivated = false;
	}

	public void phaseMoveSpawn() {
		if(getScriptHost().callBoolean("phaseMoveSpawnStart", this)) return;

		if(snap.nowPieceObject == null) {
			snap.nowPieceObject = snap.pieceQueue.pop();
			pieceSpawn();
		}

		getScriptHost().callNull("phaseMoveSpawnEnd", this);
	}

	public void phaseMoveHold() {
		if(getScriptHost().callBoolean("phaseMoveHoldStart", this)) return;

		if(!snap.holdPieceUsed) {
			if(snap.nowPieceObject == null) {
				// IHS
				boolean ihsAllowed = getGameDef().getConfig().isAllowIHS();
				if(patternActivated) ihsAllowed = getGameDef().getConfig().isAllowIHSAfterClear();
				boolean pressed = getButtonStatus().isPressed(ButtonStatus.ID_HOLD);

				if(ihsAllowed && pressed) {
					if(snap.holdPieceObject != null) {
						snap.nowPieceObject = snap.holdPieceObject;
						snap.holdPieceObject = snap.pieceQueue.pop();
					} else {
						snap.holdPieceObject = snap.pieceQueue.pop();
						snap.nowPieceObject = snap.pieceQueue.pop();
					}
					snap.holdPieceUsed = true;
					pieceSpawn();
				}
			} else {
				// Normal Hold
				boolean pressed = getButtonStatus().isPushed(ButtonStatus.ID_HOLD);

				if(pressed) {
					if(snap.holdPieceObject != null) {
						RuntimePiece p = snap.nowPieceObject;
						snap.nowPieceObject = snap.holdPieceObject;
						snap.holdPieceObject = p;
					} else {
						snap.holdPieceObject = snap.nowPieceObject;
						snap.nowPieceObject = snap.pieceQueue.pop();
					}
					snap.holdPieceUsed = true;
					pieceSpawn();
				}
			}
		}

		getScriptHost().callNull("phaseMoveHoldEnd", this);
	}

	public void phaseMoveRotate() {
		if(getScriptHost().callBoolean("phaseMoveRotateStart", this)) return;

		if(snap.nowPieceObject != null && !irsUsed) {
			// TODO: Priority setting for multi button input
			int rot = 0;
			if(getButtonStatus().isPushed(ButtonStatus.ID_ROTATECCW)) rot = -1;
			if(getButtonStatus().isPushed(ButtonStatus.ID_ROTATECW)) rot = 1;
			if(getButtonStatus().isPushed(ButtonStatus.ID_ROTATE180)) rot = 2;

			if(rot != 0) {
				int afterState = snap.nowPieceObject.getRotateAfterState(snap.nowPieceState, rot);

				if(!snap.nowPieceObject.fieldCollision(getField(), snap.nowPieceX, snap.nowPieceY, afterState)) {
					snap.nowPieceState = afterState;
					snap.updateNowPieceBottomY();
					snap.nowPieceLockDelay = 0;
				} else {
					Wallkick kick = snap.nowPieceObject.getPieceDef().getWallkick(snap.nowPieceState, afterState);
					if(kick != null) {
						if(kick.getType().equals("Offsets")) {
							for(XYPair xy: kick.getOffsetList()) {
								int newX = snap.nowPieceX + xy.getX();
								int newY = snap.nowPieceY + xy.getY();

								if(!snap.nowPieceObject.fieldCollision(getField(), newX, newY, afterState)) {
									snap.nowPieceState = afterState;
									snap.nowPieceX = newX;
									snap.nowPieceY = newY;
									snap.updateNowPieceBottomY();
									snap.nowPieceLockDelay = 0;
									break;
								}
							}
						}
					}
				}
			}
		}

		getScriptHost().callNull("phaseMoveRotateEnd", this);
	}

	public void phaseMoveSlide() {
		if(getScriptHost().callBoolean("phaseMoveSlideStart", this)) return;

		// TODO: Priority setting for multi button input
		int move = getButtonStatus().getSlideButtonStatus(getSpeedDef().getDas(), getSpeedDef().getArr());

		if((snap.nowPieceObject != null) && (move != 0)) {
			moveSlide(move);
		}

		getScriptHost().callNull("phaseMoveSlideEnd", this);
	}

	public void phaseMoveSoft() {
		if(getScriptHost().callBoolean("phaseMoveSoftStart", this)) return;

		if((snap.nowPieceObject != null) && getButtonStatus().isSoftDrop()) {
			moveDown(true);
		}

		getScriptHost().callNull("phaseMoveSoftEnd", this);
	}

	public void phaseMoveHard() {
		if(getScriptHost().callBoolean("phaseMoveHardStart", this)) return;

		if((snap.nowPieceObject != null) && getButtonStatus().isHardDrop()) {
			snap.nowPieceY = snap.nowPieceObject.fieldBottom(getField(), snap.nowPieceX, snap.nowPieceY, snap.nowPieceState);
			snap.nowPieceLockDelay = getSpeedDef().getLockDelay();
		}

		getScriptHost().callNull("phaseMoveHardEnd", this);
	}

	public void phaseMoveGravity() {
		if(getScriptHost().callBoolean("phaseMoveGravityStart", this)) return;

		if(snap.nowPieceObject != null) {
			if(!snap.nowPieceObject.fieldCollision(getField(), snap.nowPieceX, snap.nowPieceY + 1, snap.nowPieceState)) {
				snap.nowPieceGravity += getSpeedDef().getGravity();

				while(snap.nowPieceGravity >= getSpeedDef().getDenominator()) {
					snap.nowPieceGravity -= getSpeedDef().getDenominator();

					if(!moveDown(false)) {
						break;
					}
				}
			}
		}

		getScriptHost().callNull("phaseMoveGravityEnd", this);
	}

	public void phaseMoveLock() {
		if(getScriptHost().callBoolean("phaseMoveLockStart", this)) return;

		if(snap.nowPieceObject != null) {
			if(snap.nowPieceObject.fieldCollision(getField(), snap.nowPieceX, snap.nowPieceY + 1, snap.nowPieceState)) {
				snap.nowPieceLockDelay++;

				if(snap.nowPieceLockDelay >= getSpeedDef().getLockDelay()) {
					// Lock piece
					irsUsed = false;
					snap.holdPieceUsed = false;
					snap.nowPieceObject.fieldPut(getField(), snap.nowPieceX, snap.nowPieceY, snap.nowPieceState, 0, 0);

					boolean fullLockOut = snap.nowPieceObject.fieldFullLockout(getField(), snap.nowPieceX, snap.nowPieceY, snap.nowPieceState);
					snap.nowPieceObject = null;

					prevPieceObject = snap.nowPieceObject;
					prevPieceX = snap.nowPieceX;
					prevPieceY = snap.nowPieceY;
					prevPieceState = snap.nowPieceState;

					if(fullLockOut) {
						// TODO: game over
						gameState = "dead";
						getManager().publishEvent( new PlayerDeathEvent(getManager(), getSnapshot()) );
					} else if(getGameDef().getConfig().isAlwaysCheckStackFall()) {
						// Always check for stack fall first
						gameState = "stackfall";
						snap.stackFallTimer = 0;
					} else if(doPattern(prevPieceObject, prevPieceX, prevPieceY, prevPieceState) > 0) {
						// Line Clears
						gameState = "erase";
						snap.lineTimer = 0;
						patternActivated = true;
					} else if(getSpeedDef().getAre() > 0) {
						// ARE
						snap.areTimer = 0;
						areValue = getSpeedDef().getAre();
						gameState = "are";
						getManager().publishEvent( new SnapshotUpdateEvent(getManager(), getSnapshot()) );
					} else {
						// No ARE
					}
				}
			}
		}

		getScriptHost().callNull("phaseMoveLockEnd", this);
	}

	public int doPattern() {
		int count = 0;

		for(Pattern pat: getGameDef().getPatterns()) {
			if(!pat.isManual()) {
				count += doPattern(pat, null, 0, 0, 0);
			}
		}

		return count;
	}

	public int doPattern(RuntimePiece pieceObject, int pieceX, int pieceY, int pieceState) {
		int count = 0;

		for(Pattern pat: getGameDef().getPatterns()) {
			if(!pat.isManual()) {
				count += doPattern(pat, pieceObject, pieceX, pieceY, pieceState);
			}
		}

		return count;
	}

	public int doPattern(Pattern pat, RuntimePiece pieceObject, int pieceX, int pieceY, int pieceState) {
		int count = 0;

		Set<XYPair> set = null;
		if(pieceObject == null) {
			set = getField().createXYPairSetByPattern(pat, getScriptHost());
		} else {
			set = new HashSet<XYPair>();

			for(int i = 0; i < pieceObject.getNumBlocks(); i++) {
				int x = pieceX + pieceObject.getBlockPositionX(pieceState, i);
				int y = pieceY + pieceObject.getBlockPositionY(pieceState, i);
				set.addAll(getField().createXYPairSetByPattern(pat, x, y, getScriptHost()));
			}
		}

		if(set != null && !set.isEmpty()) {
			if("Delete".equals(pat.getAction())) {
				count = set.size();

				for(XYPair xyPair: set) {
					getField().getBlock(xyPair.getX(), xyPair.getY()).setMarkDeletion(true);
				}
			} else if("Connect".equals(pat.getAction())) {
				for(XYPair xyPair: set) {
					RuntimeBlock blk = getField().getBlock(xyPair.getX(), xyPair.getY());
					XYPair xy;

					xy = new XYPair(xyPair.getX(), xyPair.getY() - 1);
					blk.setConnectUp(set.contains(xy));
					xy = new XYPair(xyPair.getX(), xyPair.getY() + 1);
					blk.setConnectDown(set.contains(xy));
					xy = new XYPair(xyPair.getX() - 1, xyPair.getY());
					blk.setConnectLeft(set.contains(xy));
					xy = new XYPair(xyPair.getX() + 1, xyPair.getY());
					blk.setConnectRight(set.contains(xy));
				}
			} else if("None".equals(pat.getAction())) {
				// Nothing
			} else if(StringUtils.startsWith(pat.getAction(), "Script:")) {
				final String action = StringUtils.removeStart(pat.getAction(), "Script:");

				Integer result = (Integer)getScriptHost().callDef(
						"doPatternCustomAction", Integer.valueOf(0),
						action, set, pat, pieceObject, pieceX, pieceY, pieceState
				);
				count = result.intValue();
			} else if(!StringUtils.isEmpty(pat.getAction())) {
				throw new IllegalArgumentException("Unknown pattern action '" + pat.getAction() + "'");
			}
		}

		return count;
	}

	public void gsErase() {
		if(getScriptHost().callBoolean("gsEraseStart", this)) return;

		if(snap.lineTimer == 0) {
			if(eraseYSet == null) {
				eraseYSet = new HashSet<Integer>();
			} else {
				eraseYSet.clear();
			}

			for(int i = -getField().getHiddenHeight(); i < getField().getHeight(); i++) {
				for(int j = 0; j < getField().getWidth(); j++) {
					RuntimeBlock b = getField().getBlock(j, i);
					if(b != null && b.isMarkDeletion()) {
						// Mark the connected blocks "broken"
						List<XYPair> xyListConnect = getField().matchConnect(j, i, null);
						for(XYPair xyPair: xyListConnect) {
							RuntimeBlock bc = getField().getBlock(xyPair.getX(), xyPair.getY());
							if(bc != null) bc.setBroken(true);
						}

						// Cut the connections of nearby blocks
						RuntimeBlock bn = getField().getBlock(j, i - 1);
						if(bn != null) bn.setConnectDown(false);
						bn = getField().getBlock(j, i + 1);
						if(bn != null) bn.setConnectUp(false);
						bn = getField().getBlock(j - 1, i);
						if(bn != null) bn.setConnectRight(false);
						bn = getField().getBlock(j + 1, i);
						if(bn != null) bn.setConnectLeft(false);

						// Add cleared Y position to eraseYSet (for classic stackfall)
						eraseYSet.add(i);

						// Delete the block
						getField().setBlock(j, i, null);
					}
				}
			}
			getManager().publishEvent( new SnapshotUpdateEvent(getManager(), getSnapshot()) );
		}
		if(snap.lineTimer >= getSpeedDef().getLineDelay()) {
			if(getSpeedDef().getStackFallDelay() > 0) {
				// Smooth stack fall
				gameState = "stackfall";
				snap.stackFallTimer = 0;
			} else {
				// Instant stack fall
				while(doStackFall());

				if(doPattern() > 0) {
					// Cascade
					gameState = "erase";
					snap.lineTimer = 0;
					patternActivated = true;
				} else if(getSpeedDef().getAreLine() > 0) {
					// ARE Line
					snap.areTimer = 0;
					areValue = getSpeedDef().getAreLine();
					gameState = "are";
				} else {
					// Exit
					gameState = "move";
				}
			}
			return;
		}

		snap.lineTimer++;
		getScriptHost().callNull("gsEraseEnd", this);
	}

	public boolean doStackFall() {
		boolean fallFlag = false;

		if(getGameDef().getConfig().getStackFallType().equals("Connect"))
			fallFlag = getField().doGravityConnect(null);
		else if(getGameDef().getConfig().getStackFallType().equals("FloodFill"))
			fallFlag = getField().doGravityFloodFill(null, false, false);
		else if(getGameDef().getConfig().getStackFallType().equals("Classic"))
			fallFlag = getField().doGravityClassic(eraseYSet);
		else if(getGameDef().getConfig().getStackFallType().equals("FreeFall"))
			fallFlag = getField().doGravityFreeFall();
		else if(getGameDef().getConfig().getStackFallType().equals("None"))
			fallFlag = false;
		else if(getGameDef().getConfig().getStackFallType().startsWith("Script:"))
		{
			String fallType = getGameDef().getConfig().getStackFallType().substring(7);
			fallFlag = getScriptHost().callBoolean("doStackFallCustomGravity", this, fallType);
		}
		else
			throw new IllegalArgumentException("Unknown stack fall type:" + getGameDef().getConfig().getStackFallType());

		return fallFlag;
	}

	public void gsStackFall() {
		if(getScriptHost().callBoolean("gsStackFallStart", this)) return;

		if(snap.stackFallTimer < getSpeedDef().getStackFallDelay() - 1) {
			snap.stackFallTimer++;
		} else {
			snap.stackFallTimer = 0;

			boolean fallFlag = doStackFall();

			getManager().publishEvent( new SnapshotUpdateEvent(getManager(), getSnapshot()) );

			if(!fallFlag) {
				if(doPattern() > 0) {
					// Cascade
					gameState = "erase";
					snap.lineTimer = 0;
					patternActivated = true;
				} else if(getSpeedDef().getAreLine() > 0) {
					// ARE Line
					snap.areTimer = 0;
					areValue = getSpeedDef().getAreLine();
					gameState = "are";
				} else {
					// Exit
					gameState = "move";
				}
			}
		}

		getScriptHost().callNull("gsStackFallEnd", this);
	}

	public void gsARE() {
		if(getScriptHost().callBoolean("gsAREStart", this)) return;

		if(snap.areTimer < areValue - 1) {
			snap.areTimer++;
		} else {
			gameState = "move";
		}

		getScriptHost().callNull("gsAREEnd", this);
	}
}
