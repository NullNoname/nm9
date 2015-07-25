package cx.it.nullpo.nm9.engine.game;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import cx.it.nullpo.nm9.engine.common.Copyable;
import cx.it.nullpo.nm9.engine.common.NRandom;
import cx.it.nullpo.nm9.engine.definition.Game;
import cx.it.nullpo.nm9.engine.event.input.ButtonEvent;
import cx.it.nullpo.nm9.engine.event.input.NMInputEvent;
import cx.it.nullpo.nm9.engine.event.listeners.DummySyscallListener;
import cx.it.nullpo.nm9.engine.event.listeners.NMOutputListener;
import cx.it.nullpo.nm9.engine.event.listeners.NMSyscallListener;
import cx.it.nullpo.nm9.engine.event.output.LogEvent;
import cx.it.nullpo.nm9.engine.event.output.NMOutputEvent;
import cx.it.nullpo.nm9.engine.script.ScriptHost;

/**
 * Top-level game manager of NullpoMino Engine.
 * @author NullNoname
 */
public class GameManager implements Serializable, Copyable {
	private static final long serialVersionUID = -2334973777629966706L;

	/**
	 * Major version number
	 */
	public static final int VERSION_MAJOR = 9;

	/**
	 * Minor version number (9.*.*)
	 */
	public static final float VERSION_MINOR = 0.1f;

	/**
	 * The game definition (in-use)
	 */
	private Game gameDef;

	/**
	 * The backup of game definition.
	 * This exists because the current one (gameDef) can be modified on the fly by scripts.
	 */
	private Game gameDefBackup;

	/**
	 * The game engines
	 */
	private GameEngine[] engines;

	/**
	 * Random number generator for this GameManager
	 */
	private NRandom random;

	/**
	 * Global internal timer
	 */
	private long internalTimer;

	/**
	 * Game loop time
	 */
	private long loopTime;

	/**
	 * Number of frames remaining to process
	 */
	private long remainingUpdateFrames;

	/**
	 * Game engine is running
	 */
	private volatile boolean running;

	/**
	 * Output event listener list (get null'd at the end of update)
	 */
	private transient List<NMOutputListener> listenerList;

	/**
	 * Script host (get null'd at the end of update)
	 */
	private transient ScriptHost scriptHost;

	/**
	 * System call handler (get null'd at the end of update)
	 */
	private transient NMSyscallListener syscallListener;

	public GameManager() {
	}

	public GameManager(Game gameDef) {
		this(gameDef, System.nanoTime());
	}

	public GameManager(Game gameDef, long randSeed) {
		this.gameDefBackup = gameDef;
		this.gameDef = new Game(gameDefBackup);
		init(randSeed);
	}

	public GameManager(Game gameDef, NRandom rand) {
		this.gameDefBackup = gameDef;
		this.gameDef = new Game(gameDefBackup);
		init(rand.getSeed());
	}

	public GameManager(Copyable src) {
		copy(src);
	}

	public void copy(Copyable src) {
		GameManager s = (GameManager)src;
		this.gameDef = new Game(s.gameDef);
		this.gameDefBackup = new Game(s.gameDefBackup);
		this.random = new NRandom(s.random);
		this.internalTimer = s.internalTimer;
		this.loopTime = s.loopTime;
		this.remainingUpdateFrames = s.remainingUpdateFrames;

		this.engines = new GameEngine[s.engines.length];
		for(int i = 0; i < s.engines.length; i++) {
			this.engines[i] = new GameEngine(s.engines[i]);
			this.engines[i].setManager(this);
		}
	}

	public void init(long randSeed) {
		random = new NRandom(randSeed);
		internalTimer = 0;

		engines = new GameEngine[gameDef.getNumFields()];
		for(int i = 0; i < engines.length; i++) {
			engines[i] = new GameEngine(this, new NRandom(random), i);
		}

		loopTime = 0;
		remainingUpdateFrames = 0;
	}

	public Game getGameDefBackup() {
		return gameDefBackup;
	}

	public Game getGameDef() {
		return gameDef;
	}

	public void setGameDef(Game gameDef) {
		this.gameDef = gameDef;
	}

	public GameEngine[] getEngines() {
		return engines;
	}

	public void setEngines(GameEngine[] engines) {
		this.engines = engines;
	}

	public GameEngine getEngine(int id) {
		return engines[id];
	}

	public GamePlay getGamePlay(int engineID, int playerID) {
		return engines[engineID].getPlayer(playerID);
	}

	public GameSnapshot getSnapshot(int engineID, int playerID) {
		return getGamePlay(engineID, playerID).getSnapshot();
	}

	public NRandom getRandom() {
		return random;
	}

	public void setRandom(NRandom random) {
		this.random = random;
	}

	public boolean isRunning() {
		return running;
	}

	public long getRemainingUpdateFrames() {
		return remainingUpdateFrames;
	}

	public long getInternalTimer() {
		return internalTimer;
	}

	public ScriptHost getScriptHost() {
		return scriptHost;
	}

	public NMSyscallListener getSyscallListener() {
		return syscallListener;
	}

	public void logTrace(Object tag, Object message) {
		publishEvent(new LogEvent(LogEvent.TRACE, tag, message));
	}

	public void logTrace(Object tag, Object message, Throwable t) {
		publishEvent(new LogEvent(LogEvent.TRACE, tag, message, t));
	}

	public void logDebug(Object tag, Object message) {
		publishEvent(new LogEvent(LogEvent.DEBUG, tag, message));
	}

	public void logDebug(Object tag, Object message, Throwable t) {
		publishEvent(new LogEvent(LogEvent.DEBUG, tag, message, t));
	}

	public void logInfo(Object tag, Object message) {
		publishEvent(new LogEvent(LogEvent.INFO, tag, message));
	}

	public void logInfo(Object tag, Object message, Throwable t) {
		publishEvent(new LogEvent(LogEvent.INFO, tag, message, t));
	}

	public void logWarn(Object tag, Object message) {
		publishEvent(new LogEvent(LogEvent.WARN, tag, message));
	}

	public void logWarn(Object tag, Object message, Throwable t) {
		publishEvent(new LogEvent(LogEvent.WARN, tag, message, t));
	}

	public void logError(Object tag, Object message) {
		publishEvent(new LogEvent(LogEvent.ERROR, tag, message));
	}

	public void logError(Object tag, Object message, Throwable t) {
		publishEvent(new LogEvent(LogEvent.ERROR, tag, message, t));
	}

	public void logFatal(Object tag, Object message) {
		publishEvent(new LogEvent(LogEvent.FATAL, tag, message));
	}

	public void logFatal(Object tag, Object message, Throwable t) {
		publishEvent(new LogEvent(LogEvent.FATAL, tag, message, t));
	}

	public void publishEvent(final NMOutputEvent event) {
		getScriptHost().callNull("publishEvent", this, event);

		if(listenerList != null) {
			synchronized (listenerList) {
				final Iterator<NMOutputListener> it = listenerList.iterator();

				while(it.hasNext()) {
					final NMOutputListener listener = it.next();
					listener.onNMOutputEvent(event);
				}
			}
		}
	}

	public void processEvent(final List<NMInputEvent> events) {
		if(events == null) return;

		synchronized (events) {
			final Iterator<NMInputEvent> it = events.iterator();

			while(it.hasNext()) {
				final NMInputEvent event = it.next();

				if(event instanceof ButtonEvent) {
					final ButtonEvent input = (ButtonEvent)event;
					getSnapshot(input.getEngineID(),input.getPlayerID()).getButtonStatus().
						setPressed(input.getButtonID(), input.isPressed(), getInternalTimer());
				}
				getScriptHost().callNull("processEvent", this, event);

				it.remove();
			}
		}
	}

	public void updateGame(final List<NMInputEvent> events,
							final List<NMOutputListener> listeners,
							final ScriptHost scriptHost,
							final NMSyscallListener syscallListener)
	{
		if(remainingUpdateFrames > 0) {
			running = true;
			this.listenerList = listeners;
			this.scriptHost = (scriptHost == null) ? new ScriptHost() : scriptHost;
			this.syscallListener = (syscallListener == null) ? new DummySyscallListener() : syscallListener;

			try {
				getScriptHost().begin(this);
				getScriptHost().callNull("updateStart", this);

				while(remainingUpdateFrames > 0) {
					remainingUpdateFrames--;
					getScriptHost().callNull("frameUpdateStart", this, remainingUpdateFrames, internalTimer);

					processEvent(events);
					for(int i = 0; i < engines.length; i++) {
						engines[i].update(remainingUpdateFrames);
					}

					getScriptHost().callNull("frameUpdateEnd", this, remainingUpdateFrames, internalTimer);
					internalTimer++;
				}
			} finally {
				getScriptHost().callNull("updateEnd", this);
				getScriptHost().end(this);

				this.listenerList = null;
				this.scriptHost = null;
				this.syscallListener = null;
				running = false;
			}
		}
	}

	public void updateFrames(long frames,
							  final List<NMInputEvent> events,
							  final List<NMOutputListener> listeners,
							  final ScriptHost scriptHost,
							  final NMSyscallListener syscallListener)
	{
		remainingUpdateFrames += frames;
		updateGame(events, listeners, scriptHost, syscallListener);
	}

	public void update(long runMsec,
						final List<NMInputEvent> events,
						final List<NMOutputListener> listeners,
						final ScriptHost scriptHost,
						final NMSyscallListener syscallListener)
	{
		loopTime += runMsec;

		while(loopTime >= gameDef.getConfig().getFrameTime()) {
			loopTime -= gameDef.getConfig().getFrameTime();
			remainingUpdateFrames++;
		}

		updateGame(events, listeners, scriptHost, syscallListener);
	}
}
