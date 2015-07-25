package cx.it.nullpo.nm9.engine.definition;

import java.io.Serializable;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

import cx.it.nullpo.nm9.engine.common.Copyable;
import cx.it.nullpo.nm9.engine.common.NamedObject;

/**
 * The definition of a Speed setting. Speed settings contain fall speed, DAS, ARE, etc.
 * @author NullNoname
 */
@Root
public class Speed implements NamedObject, Serializable, Copyable {
	private static final long serialVersionUID = -3949418954177088748L;

	/**
	 * Name of this Speed setting. Must be unique.
	 * Speed setting is referenced by its name and can be used by multiple levels.
	 */
	@Attribute
	private String name;

	/**
	 * Gravity value
	 */
	@Attribute
	private long gravity;

	/**
	 * Gravity denominator
	 */
	@Attribute
	private long denominator;

	/**
	 * ARE (AppeaRancE delay)
	 */
	@Attribute
	private long are;

	/**
	 * ARE (AppeaRancE delay) after a line clear
	 */
	@Attribute
	private long areLine;

	/**
	 * Line clear delay
	 */
	@Attribute
	private long lineDelay;

	/**
	 * Stack fall delay after a line clear (0:Instant)
	 */
	@Attribute
	private long stackFallDelay;

	/**
	 * Lock delay
	 */
	@Attribute
	private long lockDelay;

	/**
	 * Sideways DAS (Delayed Auto Shift)
	 */
	@Attribute
	private long das;

	/**
	 * Sideways ARR (Auto repeat rate)
	 */
	@Attribute
	private long arr;

	/**
	 * Softdrop DAS
	 */
	@Attribute
	private long softDAS;

	/**
	 * Softdrop ARR
	 */
	@Attribute
	private long softARR;

	/**
	 * Softdrop speed change value
	 */
	@Attribute
	private double softValue;

	/**
	 * If false, softdrop will simply add softValue to native speed.
	 * If true, it will add the multiplied native gravity value.
	 */
	@Attribute
	private boolean softIsMultiply;

	public Speed() {
	}

	public Speed(Copyable src) {
		copy(src);
	}

	public Speed(String name) {
		this.name = name;
	}

	public Speed(String name, long gravity, long denominator, long are,
			long areLine, long lineDelay, long stackFallDelay, long lockDelay,
			long das, long arr, long softDAS, long softARR, double softValue,
			boolean softIsMultiply)
	{
		this.name = name;
		this.gravity = gravity;
		this.denominator = denominator;
		this.are = are;
		this.areLine = areLine;
		this.lineDelay = lineDelay;
		this.stackFallDelay = stackFallDelay;
		this.lockDelay = lockDelay;
		this.das = das;
		this.arr = arr;
		this.softDAS = softDAS;
		this.softARR = softARR;
		this.softValue = softValue;
		this.softIsMultiply = softIsMultiply;
	}

	public void copy(Copyable src) {
		Speed s = (Speed)src;
		this.name = s.name;
		this.gravity = s.gravity;
		this.denominator = s.denominator;
		this.are = s.are;
		this.areLine = s.areLine;
		this.lineDelay = s.lineDelay;
		this.stackFallDelay = s.stackFallDelay;
		this.lockDelay = s.lockDelay;
		this.das = s.das;
		this.arr = s.arr;
		this.softDAS = s.softDAS;
		this.softARR = s.softARR;
		this.softValue = s.softValue;
		this.softIsMultiply = s.softIsMultiply;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getGravity() {
		return gravity;
	}

	public void setGravity(long gravity) {
		this.gravity = gravity;
	}

	public long getDenominator() {
		return denominator;
	}

	public void setDenominator(long denominator) {
		this.denominator = denominator;
	}

	public long getAre() {
		return are;
	}

	public void setAre(long are) {
		this.are = are;
	}

	public long getAreLine() {
		return areLine;
	}

	public void setAreLine(long areLine) {
		this.areLine = areLine;
	}

	public long getLineDelay() {
		return lineDelay;
	}

	public void setLineDelay(long lineDelay) {
		this.lineDelay = lineDelay;
	}

	public long getStackFallDelay() {
		return stackFallDelay;
	}

	public void setStackFallDelay(long stackFallDelay) {
		this.stackFallDelay = stackFallDelay;
	}

	public long getLockDelay() {
		return lockDelay;
	}

	public void setLockDelay(long lockDelay) {
		this.lockDelay = lockDelay;
	}

	public long getDas() {
		return das;
	}

	public void setDas(long das) {
		this.das = das;
	}

	public long getArr() {
		return arr;
	}

	public void setArr(long arr) {
		this.arr = arr;
	}

	public long getSoftDAS() {
		return softDAS;
	}

	public void setSoftDAS(long softDAS) {
		this.softDAS = softDAS;
	}

	public long getSoftARR() {
		return softARR;
	}

	public void setSoftARR(long softARR) {
		this.softARR = softARR;
	}

	public double getSoftValue() {
		return softValue;
	}

	public void setSoftValue(double softValue) {
		this.softValue = softValue;
	}

	public boolean isSoftIsMultiply() {
		return softIsMultiply;
	}

	public void setSoftIsMultiply(boolean softIsMultiply) {
		this.softIsMultiply = softIsMultiply;
	}
}
