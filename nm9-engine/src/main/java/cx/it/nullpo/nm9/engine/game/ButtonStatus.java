package cx.it.nullpo.nm9.engine.game;

import java.io.Serializable;

import cx.it.nullpo.nm9.engine.common.Copyable;

/**
 * This class stores the player's button input status.
 * @author NullNoname
 */
public class ButtonStatus implements Serializable, Copyable {
	private static final long serialVersionUID = -8270082496542928402L;

	public static final int ID_HARDDROP = 0;
	public static final int ID_SOFTDROP = 1;
	public static final int ID_LEFT = 2;
	public static final int ID_RIGHT = 3;
	public static final int ID_ROTATECCW = 4;
	public static final int ID_ROTATECW = 5;
	public static final int ID_ROTATE180 = 6;
	public static final int ID_FARLEFT = 7;
	public static final int ID_FARRIGHT = 8;
	public static final int ID_HOLD = 9;
	public static final int ID_ITEM = 10;

	public static final int MAX_BUTTONS = 11;

	private boolean[] pressedArray;
	private long[] duration;
	private long[] lastPushTime;
	private long[] lastHeldTime;
	private long durationLR;
	private int slide;

	public ButtonStatus() {
		init();
	}

	public ButtonStatus(Copyable src) {
		copy(src);
	}

	public void init() {
		this.pressedArray = new boolean[MAX_BUTTONS];
		this.duration = new long[MAX_BUTTONS];
		this.lastPushTime = new long[MAX_BUTTONS];
		this.lastHeldTime = new long[MAX_BUTTONS];
		this.durationLR = 0;
		this.slide = 0;
	}

	public void copy(Copyable src) {
		ButtonStatus s = (ButtonStatus)src;

		this.pressedArray = new boolean[MAX_BUTTONS];
		this.duration = new long[MAX_BUTTONS];
		this.lastPushTime = new long[MAX_BUTTONS];
		this.lastHeldTime = new long[MAX_BUTTONS];
		this.durationLR = s.durationLR;
		this.slide = s.slide;

		for(int i = 0; i < MAX_BUTTONS; i++) {
			this.pressedArray[i] = s.pressedArray[i];
			this.duration[i] = s.duration[i];
			this.lastPushTime[i] = s.lastPushTime[i];
			this.lastHeldTime[i] = s.lastHeldTime[i];
		}
	}

	public void setPressed(int button, boolean pressed, long internalTime) {
		pressedArray[button] = pressed;
		if(pressed) lastPushTime[button] = internalTime;
	}

	public boolean isPressed(int button) {
		return pressedArray[button];
	}

	public boolean isPressed(int button, long das) {
		if(!pressedArray[button]) return false;
		if(duration[button] == 1) return true;
		if(duration[button] >= das) return true;
		return false;
	}

	public boolean isPressed(int button, long das, long arr) {
		if(!pressedArray[button]) return false;
		if(duration[button] == 1) return true;
		if(duration[button] >= das) {
			long d = duration[button] - das;
			if(d % arr == 0) return true;
		}
		return false;
	}

	public boolean isPushed(int button) {
		return pressedArray[button] && (duration[button] == 1);
	}

	public long getDuration(int button) {
		return duration[button];
	}

	public long getLastPushTime(int button) {
		return lastPushTime[button];
	}

	public long getLastHeldTime(int button) {
		return lastHeldTime[button];
	}

	public void update(long internalTime) {
		for(int i = 0; i < MAX_BUTTONS; i++) {
			if(pressedArray[i]) {
				duration[i]++;
				lastHeldTime[i] = internalTime;
			} else {
				duration[i] = 0;
			}
		}

		if(isPressed(ID_LEFT) && isPressed(ID_RIGHT)) {
			if(getLastPushTime(ID_LEFT) == getLastPushTime(ID_RIGHT)) {
				slide = 0;
				durationLR = 0;
			} else if(getLastPushTime(ID_LEFT) > getLastPushTime(ID_RIGHT)) {
				if(slide != -1) {
					slide = -1;
					durationLR = 1;
				} else {
					durationLR++;
				}
			} else if(getLastPushTime(ID_LEFT) < getLastPushTime(ID_RIGHT)) {
				if(slide != 1) {
					slide = 1;
					durationLR = 1;
				} else {
					durationLR++;
				}
			}
		} else if(isPressed(ID_LEFT)) {
			if(slide != -1) {
				slide = -1;
				durationLR = 1;
			} else {
				durationLR++;
			}
		} else if(isPressed(ID_RIGHT)) {
			if(slide != 1) {
				slide = 1;
				durationLR = 1;
			} else {
				durationLR++;
			}
		} else {
			slide = 0;
			durationLR = 0;
		}
	}

	public boolean isHardDrop() {
		return isPushed(ID_HARDDROP);
	}

	public boolean isSoftDrop() {
		return isPressed(ID_SOFTDROP);
	}

	public boolean isLeft(long das, long arr) {
		if(slide != -1) return false;
		if(durationLR == 1) return true;
		if(durationLR >= das) {
			long d = durationLR - das;
			if(d % arr == 0) return true;
		}
		return false;
	}

	public boolean isRight(long das, long arr) {
		if(slide != 1) return false;
		if(durationLR == 1) return true;
		if(durationLR >= das) {
			long d = durationLR - das;
			if(d % arr == 0) return true;
		}
		return false;
	}

	public int getSlideButtonStatus(long das, long arr) {
		if(slide == -1)
			return (isLeft(das, arr) ? -1 : 0);
		else if(slide == 1)
			return (isRight(das, arr) ? 1 : 0);

		return 0;
	}
}
