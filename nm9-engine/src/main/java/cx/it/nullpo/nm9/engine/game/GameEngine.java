package cx.it.nullpo.nm9.engine.game;

import java.io.Serializable;

import cx.it.nullpo.nm9.engine.common.Copyable;
import cx.it.nullpo.nm9.engine.common.NRandom;
import cx.it.nullpo.nm9.engine.definition.Config;
import cx.it.nullpo.nm9.engine.definition.Game;
import cx.it.nullpo.nm9.engine.script.ScriptHost;

/**
 * Game engine contains a field and 1 or more players.
 * @author NullNoname
 */
public class GameEngine implements Serializable, Copyable {
	private static final long serialVersionUID = -1114493378719949701L;

	/**
	 * GameManager that owns this engine
	 */
	private GameManager manager;

	/**
	 * Random number generator for this GameEngine
	 */
	private NRandom random;

	/**
	 * Game engine ID
	 */
	private int engineID;

	/**
	 * The game field
	 */
	private Field field;

	/**
	 * An array of GamePlay where most actions take place
	 */
	private GamePlay[] players;

	public GameEngine() {
	}

	public GameEngine(GameManager manager, NRandom random) {
		this.manager = manager;
		this.random = new NRandom(random);
		init();
	}

	public GameEngine(GameManager manager, NRandom random, int engineID) {
		this.manager = manager;
		this.random = new NRandom(random);
		this.engineID = engineID;
		init();
	}

	public GameEngine(Copyable src) {
		copy(src);
	}

	public void copy(Copyable src) {
		GameEngine s = (GameEngine)src;
		this.manager = s.manager;	// Should be handled by GameManager's copy method
		this.random = new NRandom(s.random);
		this.engineID = s.engineID;
		this.field = new Field(s.field);

		this.players = new GamePlay[s.players.length];
		for(int i = 0; i < s.players.length; i++) {
			this.players[i] = new GamePlay(s.players[i]);
			this.players[i].setEngine(this);
			this.players[i].getSnapshot().setField(this.field);
		}
	}

	public void init() {
		Config cfg = getGameDef().getConfig();
		field = new Field(cfg.getFieldWidth(), cfg.getFieldHeight(), cfg.getFieldHiddenHeight());

		players = new GamePlay[getGameDef().getNumPlayersPerField()];
		for(int i = 0; i < players.length; i++) {
			players[i] = new GamePlay(this, new NRandom(random));
		}
	}

	public Game getGameDef() {
		return manager.getGameDef();
	}

	public ScriptHost getScriptHost() {
		return manager.getScriptHost();
	}

	public GameManager getManager() {
		return manager;
	}

	public void setManager(GameManager manager) {
		this.manager = manager;
	}

	public Field getField() {
		return field;
	}

	public NRandom getRandom() {
		return random;
	}

	public void setRandom(NRandom random) {
		this.random = random;
	}

	public void setField(Field field) {
		this.field = field;
	}

	public GamePlay[] getPlayers() {
		return players;
	}

	public void setPlayers(GamePlay[] players) {
		this.players = players;
	}

	public GamePlay getPlayer(int playerID) {
		return players[playerID];
	}

	public void setPlayer(int playerID, GamePlay gamePlay) {
		players[playerID] = gamePlay;
	}

	public int getEngineID() {
		return engineID;
	}

	public void setEngineID(int engineID) {
		this.engineID = engineID;
	}

	public void update(long remainingUpdateFrames) {
		getScriptHost().callNull("engineUpdateStart", this, remainingUpdateFrames);

		for(int i = 0; i < players.length; i++) {
			players[i].update(remainingUpdateFrames);
		}

		getScriptHost().callNull("engineUpdateEnd", this, remainingUpdateFrames);
	}
}
