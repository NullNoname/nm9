package cx.it.nullpo.nm9.engine.event.listeners;

/**
 * A dummy implementation of NMSyscallListener
 * @author NullNoname
 */
public class DummySyscallListener implements NMSyscallListener {
	public Object onNMSyscall(String cmd, Object... param) {
		return null;
	}
}
