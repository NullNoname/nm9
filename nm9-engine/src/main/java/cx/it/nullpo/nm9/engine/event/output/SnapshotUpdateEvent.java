package cx.it.nullpo.nm9.engine.event.output;

import cx.it.nullpo.nm9.engine.game.GameManager;
import cx.it.nullpo.nm9.engine.game.GameSnapshot;

/**
 * An event that caused by an update of game snapshot
 * @author NullNoname
 */
public class SnapshotUpdateEvent extends NMOutputEvent {
	private static final long serialVersionUID = -8423767289564586903L;

	protected long remainingUpdateFrames;

	protected GameManager manager;
	protected GameSnapshot snapshot;

	public SnapshotUpdateEvent() {
	}

	public SnapshotUpdateEvent(GameManager manager, GameSnapshot snapshot) {
		this.manager = manager;
		this.snapshot = snapshot;
		this.remainingUpdateFrames = manager.getRemainingUpdateFrames();
	}

	public GameManager getManager() {
		return manager;
	}

	public GameSnapshot getSnapshot() {
		return snapshot;
	}
}
