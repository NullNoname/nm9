package cx.it.nullpo.nm9.engine.event.listeners;

/**
 * Listener for handling the system calls from the game script
 * @author NullNoname
 */
public interface NMSyscallListener {
	/**
	 * Handles the system call from the game script
	 * @param cmd System call command
	 * @param param The parameters
	 * @return The answer to the system call
	 */
	public Object onNMSyscall(String cmd, Object... param);
}
