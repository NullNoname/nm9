package cx.it.nullpo.nm9.engine.definition;

import java.io.Serializable;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

import cx.it.nullpo.nm9.engine.common.Copyable;

/**
 * An object that represents the pair of X and Y values.
 * @author NullNoname
 */
@Root
public class XYPair implements Serializable, Copyable {
	private static final long serialVersionUID = -180175217588641543L;

	/**
	 * The X value
	 */
	@Attribute
	public int x;

	/**
	 * The Y value
	 */
	@Attribute
	public int y;

	public XYPair() {
	}

	public XYPair(int x) {
		this.x = x;
	}

	public XYPair(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public XYPair(Copyable src) {
		copy(src);
	}

	public void copy(Copyable src) {
		XYPair s = (XYPair)src;
		this.x = s.x;
		this.y = s.y;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		XYPair other = (XYPair) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
}
