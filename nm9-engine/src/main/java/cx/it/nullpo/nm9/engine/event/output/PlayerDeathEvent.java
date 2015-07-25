package cx.it.nullpo.nm9.engine.event.output;

import cx.it.nullpo.nm9.engine.game.GameManager;
import cx.it.nullpo.nm9.engine.game.GameSnapshot;

/**
 * An event that signals player's death
 * @author NullNoname
 */
public class PlayerDeathEvent extends SnapshotUpdateEvent {
	private static final long serialVersionUID = -2722350951336450719L;

	public PlayerDeathEvent() {
		super();
	}

	public PlayerDeathEvent(GameManager manager, GameSnapshot snapshot) {
		super(manager, snapshot);
	}
}
