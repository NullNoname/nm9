package cx.it.nullpo.nm9.engine.script;

/**
 * A runtime exception that will be thrown when specified function doesn't exist
 * @author NullNoname
 */
public class NoSuchFunctionException extends InvalidFunctionException {
	private static final long serialVersionUID = 8663276070764757315L;

	public NoSuchFunctionException() {
	}

	public NoSuchFunctionException(String message) {
		super(message);
	}

	public NoSuchFunctionException(Throwable cause) {
		super(cause);
	}

	public NoSuchFunctionException(String message, Throwable cause) {
		super(message, cause);
	}
}
