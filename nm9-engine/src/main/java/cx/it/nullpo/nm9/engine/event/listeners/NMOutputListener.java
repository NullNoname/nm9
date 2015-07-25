package cx.it.nullpo.nm9.engine.event.listeners;

import cx.it.nullpo.nm9.engine.event.output.NMOutputEvent;

/**
 * Listener interface for NMOutputEvent
 * @author NullNoname
 */
public interface NMOutputListener {
	/**
	 * Receives NMOutputEvent from NullpoMino engine.
	 * @param event The event
	 */
	public void onNMOutputEvent(NMOutputEvent event);
}
