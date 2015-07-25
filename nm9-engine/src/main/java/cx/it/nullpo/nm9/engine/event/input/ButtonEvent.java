package cx.it.nullpo.nm9.engine.event.input;

/**
 * Button input event
 * @author NullNoname
 */
public class ButtonEvent extends NMInputEvent {
	private static final long serialVersionUID = 2967487467809447968L;

	/** Engine ID */
	protected int engineID;

	/** Player ID */
	protected int playerID;

	/** The button ID */
	protected int buttonID;

	/** true if the button is pressed */
	protected boolean pressed;

	public ButtonEvent() {
	}

	public ButtonEvent(int engineID, int playerID, int buttonID, boolean pressed) {
		this.engineID = engineID;
		this.playerID = playerID;
		this.buttonID = buttonID;
		this.pressed = pressed;
	}

	public int getEngineID() {
		return engineID;
	}

	public int getPlayerID() {
		return playerID;
	}

	public int getButtonID() {
		return buttonID;
	}

	public boolean isPressed() {
		return pressed;
	}
}
