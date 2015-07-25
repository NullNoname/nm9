package cx.it.nullpo.nm9.engine.common;

/**
 * Interface for objects that can be copied with copy(Copyable) method.
 * @author NullNoname
 */
public interface Copyable {
	/**
	 * Copy from other source
	 * @param src Copy source
	 */
	public void copy(Copyable src);
}
