package cx.it.nullpo.nm9.engine.event.output;

/**
 * Event for logging message from NullpoMino engine.
 * @author NullNoname
 */
public class LogEvent extends NMOutputEvent {
	private static final long serialVersionUID = -5177341522180259760L;

	public static final int TRACE = 0;
	public static final int DEBUG = 1;
	public static final int INFO = 2;
	public static final int WARN = 3;
	public static final int ERROR = 4;
	public static final int FATAL = 5;
	public static final String[] LEVEL_NAMES = {"TRACE", "DEBUG", "INFO", "WARN", "ERROR", "FATAL"};

	protected int level;
	protected String tag;
	protected String message;
	protected Throwable t;

	public LogEvent() {
		super();
	}

	public LogEvent(int level, Object tag, Object message) {
		super();
		this.level = level;
		this.tag = tag.toString();
		this.message = message.toString();
	}

	public LogEvent(int level, Object tag, Object message, Throwable t) {
		super();
		this.level = level;
		this.tag = tag.toString();
		this.message = message.toString();
		this.t = t;
	}

	public String getLevelName() {
		return ((level < 0) || (level >= LEVEL_NAMES.length)) ? "UNKNOWN" : LEVEL_NAMES[level];
	}

	public int getLevel() {
		return level;
	}

	public String getTag() {
		return (tag != null) ? tag : "null";
	}

	public String getMessage() {
		return (message != null) ? message : "null";
	}

	public Throwable getThrowable() {
		return t;
	}
}
