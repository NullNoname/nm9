package cx.it.nullpo.nm9.engine.event.input;

/**
 * A responce event for an asynchronous system call
 * @author NullNoname
 */
public class AsyncSyscallResponceEvent extends NMInputEvent {
	private static final long serialVersionUID = 8026535905148598945L;

	/**
	 * System call identify number (destination of this responce)
	 */
	protected long callID;

	/**
	 * Responce data
	 */
	protected Object[] responce;

	public AsyncSyscallResponceEvent() {
		this.callID = -1;
		this.responce = new Object[0];
	}

	public AsyncSyscallResponceEvent(int callID, Object... responce) {
		this.callID = -1;
		this.responce = responce;
	}

	public long getCallID() {
		return callID;
	}

	public Object[] getResponce() {
		return responce;
	}
}
