package cx.it.nullpo.nm9.engine.script;

/**
 * A runtime exception that will be thrown when specified function isn't valid
 * @author NullNoname
 */
public class InvalidFunctionException extends RuntimeException {
	private static final long serialVersionUID = 8663276070764757315L;

	public InvalidFunctionException() {
	}

	public InvalidFunctionException(String message) {
		super(message);
	}

	public InvalidFunctionException(Throwable cause) {
		super(cause);
	}

	public InvalidFunctionException(String message, Throwable cause) {
		super(message, cause);
	}
}
