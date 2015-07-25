package cx.it.nullpo.nm9.engine.event.output;

/**
 * An event for an asynchronous system call
 * @author NullNoname
 */
public class AsyncSyscallEvent extends NMOutputEvent {
	private static final long serialVersionUID = 3320412732912560246L;

	/**
	 * System call identify number.
	 * Start from 0, and should be inclemented by 1 for subsequent calls for syscalls that require responce.
	 * Should be set to -1 if no responce is required.
	 */
	protected long callID;

	/**
	 * Command string
	 */
	protected String cmd;

	/**
	 * Params
	 */
	protected Object[] params;

	public AsyncSyscallEvent() {
		this.callID = -1;
	}

	public AsyncSyscallEvent(String cmd, Object... params) {
		this.callID = -1;
		this.cmd = cmd;
		this.params = params;
	}

	public AsyncSyscallEvent(int callID, String cmd, Object... params) {
		this.callID = callID;
		this.cmd = cmd;
		this.params = params;
	}

	public long getCallID() {
		return callID;
	}

	public String getCmd() {
		return cmd;
	}

	public Object[] getParams() {
		return params;
	}
}
