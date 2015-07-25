package cx.it.nullpo.nm9.engine.definition;

import java.io.Serializable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import cx.it.nullpo.nm9.engine.common.Copyable;

/**
 * Game config section
 * @author NullNoname
 */
@Root
public class Config implements Serializable, Copyable {
	private static final long serialVersionUID = -8875067587179110793L;

	/**
	 * Localization file name
	 */
	@Element(required=false)
	private String langFileName;

	/**
	 * Displayed title of this game
	 */
	@Element(required=false)
	private String gameName;

	/**
	 * Description of this game
	 */
	@Element(required=false)
	private String description;

	/**
	 * Game type (Tetromino, Avalanche, etc...)
	 */
	@Element(required=false)
	private String gameType;

	/**
	 * Duration of 1 frame in nanoseconds
	 */
	@Element(required=false)
	private long frameTime = 16666666;

	/**
	 * Allow sideways DAS override
	 */
	@Element(required=false)
	private boolean allowOverrideDAS = true;

	/**
	 * Allow sideways ARR override
	 */
	@Element(required=false)
	private boolean allowOverrideARR = true;

	/**
	 * Field width
	 */
	@Element(required=false)
	private int fieldWidth = 10;

	/**
	 * Field height (Visible area)
	 */
	@Element(required=false)
	private int fieldHeight = 20;

	/**
	 * Field hidden height ("Buffer Area")
	 */
	@Element(required=false)
	private int fieldHiddenHeight = 20;

	/**
	 * Randomizer setting in Blackjack language
	 */
	@Element(required=false)
	private String randomizerBlackjack;

	/**
	 * Number of piece preview
	 */
	@Element(required=false)
	private int piecePreview = 6;

	/**
	 * Pieces spawn inside of playfield
	 */
	@Element(required=false)
	private boolean spawnBelowSkyline = false;

	/**
	 * Maximum number of push-up attempts when piece spawn fails
	 */
	@Element(required=false)
	private int spawnPushUp = 0;

	/**
	 * Ready->Go duration
	 */
	@Element(required=false)
	private long readyTime = 60;

	/**
	 * Stack fall type
	 */
	@Element(required=false)
	private String stackFallType = "Connect";

	/**
	 * Check for stack fall first
	 */
	@Element(required=false)
	private boolean alwaysCheckStackFall = false;

	/**
	 * Allow initial rotation (when no blocks are cleared)
	 */
	@Element(required=false)
	private boolean allowIRS = true;

	/**
	 * Allow initial rotation (with at least 1 block cleared)
	 */
	@Element(required=false)
	private boolean allowIRSAfterClear = true;

	/**
	 * Allow initial hold (when no blocks are cleared)
	 */
	@Element(required=false)
	private boolean allowIHS = true;

	/**
	 * Allow initial hold (with at least 1 block cleared)
	 */
	@Element(required=false)
	private boolean allowIHSAfterClear = true;

	public Config() {
	}

	public Config(Copyable src) {
		copy(src);
	}

	public void copy(Copyable src) {
		Config s = (Config)src;
		this.langFileName = s.langFileName;
		this.gameName = s.gameName;
		this.description = s.description;
		this.frameTime = s.frameTime;
		this.allowOverrideDAS = s.allowOverrideDAS;
		this.allowOverrideARR = s.allowOverrideARR;
		this.fieldWidth = s.fieldWidth;
		this.fieldHeight = s.fieldHeight;
		this.fieldHiddenHeight = s.fieldHiddenHeight;
		this.randomizerBlackjack = s.randomizerBlackjack;
		this.piecePreview = s.piecePreview;
		this.spawnBelowSkyline = s.spawnBelowSkyline;
		this.spawnPushUp = s.spawnPushUp;
		this.readyTime = s.readyTime;
		this.stackFallType = s.stackFallType;
		this.alwaysCheckStackFall = s.alwaysCheckStackFall;
		this.allowIRS = s.allowIRS;
		this.allowIRSAfterClear = s.allowIRSAfterClear;
		this.allowIHS = s.allowIHS;
		this.allowIHSAfterClear = s.allowIHSAfterClear;
	}

	public String getLangFileName() {
		return langFileName;
	}

	public void setLangFileName(String langFileName) {
		this.langFileName = langFileName;
	}

	public String getGameName() {
		return gameName;
	}

	public void setGameName(String gameName) {
		this.gameName = gameName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getGameType() {
		return gameType;
	}

	public long getFrameTime() {
		return frameTime;
	}

	public void setFrameTime(long frameTime) {
		this.frameTime = frameTime;
	}

	public void setGameType(String gameType) {
		this.gameType = gameType;
	}

	public boolean isAllowOverrideDAS() {
		return allowOverrideDAS;
	}

	public void setAllowOverrideDAS(boolean allowOverrideDAS) {
		this.allowOverrideDAS = allowOverrideDAS;
	}

	public boolean isAllowOverrideARR() {
		return allowOverrideARR;
	}

	public void setAllowOverrideARR(boolean allowOverrideARR) {
		this.allowOverrideARR = allowOverrideARR;
	}

	public int getFieldWidth() {
		return fieldWidth;
	}

	public void setFieldWidth(int fieldWidth) {
		this.fieldWidth = fieldWidth;
	}

	public int getFieldHeight() {
		return fieldHeight;
	}

	public void setFieldHeight(int fieldHeight) {
		this.fieldHeight = fieldHeight;
	}

	public int getFieldHiddenHeight() {
		return fieldHiddenHeight;
	}

	public void setFieldHiddenHeight(int fieldHiddenHeight) {
		this.fieldHiddenHeight = fieldHiddenHeight;
	}

	public String getRandomizerBlackjack() {
		return randomizerBlackjack;
	}

	public void setRandomizerBlackjack(String randomizerBlackjack) {
		this.randomizerBlackjack = randomizerBlackjack;
	}

	public int getPiecePreview() {
		return piecePreview;
	}

	public void setPiecePreview(int piecePreview) {
		this.piecePreview = piecePreview;
	}

	public boolean isSpawnBelowSkyline() {
		return spawnBelowSkyline;
	}

	public void setSpawnBelowSkyline(boolean spawnBelowSkyline) {
		this.spawnBelowSkyline = spawnBelowSkyline;
	}

	public int getSpawnPushUp() {
		return spawnPushUp;
	}

	public void setSpawnPushUp(int spawnPushUp) {
		this.spawnPushUp = spawnPushUp;
	}

	public long getReadyTime() {
		return readyTime;
	}

	public void setReadyTime(long readyTime) {
		this.readyTime = readyTime;
	}

	public String getStackFallType() {
		return stackFallType;
	}

	public void setStackFallType(String stackFallType) {
		this.stackFallType = stackFallType;
	}

	public boolean isAlwaysCheckStackFall() {
		return alwaysCheckStackFall;
	}

	public void setAlwaysCheckStackFall(boolean alwaysCheckStackFall) {
		this.alwaysCheckStackFall = alwaysCheckStackFall;
	}

	public boolean isAllowIRS() {
		return allowIRS;
	}

	public void setAllowIRS(boolean allowIRS) {
		this.allowIRS = allowIRS;
	}

	public boolean isAllowIRSAfterClear() {
		return allowIRSAfterClear;
	}

	public void setAllowIRSAfterClear(boolean allowIRSAfterClear) {
		this.allowIRSAfterClear = allowIRSAfterClear;
	}

	public boolean isAllowIHS() {
		return allowIHS;
	}

	public void setAllowIHS(boolean allowIHS) {
		this.allowIHS = allowIHS;
	}

	public boolean isAllowIHSAfterClear() {
		return allowIHSAfterClear;
	}

	public void setAllowIHSAfterClear(boolean allowIHSAfterClear) {
		this.allowIHSAfterClear = allowIHSAfterClear;
	}
}
