package cx.it.nullpo.nm9.engine.common;

/**
 * A runtime exception that thrown when the specified named object cannot be found.
 * @author NullNoname
 */
public class NamedObjectNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 2604542062013640822L;

	public NamedObjectNotFoundException() {
	}

	public NamedObjectNotFoundException(String message) {
		super(message);
	}

	public NamedObjectNotFoundException(Throwable cause) {
		super(cause);
	}

	public NamedObjectNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
