package cx.it.nullpo.nm9.engine.common;

import java.io.Serializable;
import java.util.Random;

/**
 * XORShift random number generator
 * <br /><a href="http://www.javamex.com/tutorials/random_numbers/xorshift.shtml">Original source</a>
 * @author NullNoname
 */
public class NRandom implements Serializable, Copyable {
	private static final long serialVersionUID = -5124030739374236171L;

	private long seed;
	private long now;

	public NRandom() {
		setSeed(System.nanoTime());
	}

	public NRandom(Random r) {
		this.seed = 0;
		while(this.seed == 0) this.seed = r.nextLong();
		this.now = seed;
	}

	public NRandom(long seed) {
		setSeed(seed);
	}

	public NRandom(Copyable src) {
		copy(src);
	}

	public void copy(Copyable src) {
		NRandom s = (NRandom)src;
		this.seed = s.seed;
		this.now = s.now;
	}

	public long nextLong() {
		now ^= (now << 21);
		now ^= (now >>> 35);
		now ^= (now << 4);
		return now;
	}

	public long nextLong(long max) {
		if(max == 0) return 0;
		return Math.abs(nextLong() % max);
	}

	public int nextInt() {
		return (int)nextLong();
	}

	public int nextInt(int max) {
		return (int)Math.abs(nextInt() % max);
	}

	public long getSeed() {
		return seed;
	}

	public void setSeed(long seed) {
		if(seed == 0) seed = 1;
		this.seed = seed;
	}

	public long getNow() {
		return now;
	}

	public void setNow(long now) {
		this.now = now;
	}

	public void reSeed(long seed) {
		if(seed == 0) seed = 1;
		this.seed = seed;
		this.now = seed;
	}

	public void reset() {
		this.now = this.seed;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (now ^ (now >>> 32));
		result = prime * result + (int) (seed ^ (seed >>> 32));
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
		NRandom other = (NRandom) obj;
		if (now != other.now)
			return false;
		if (seed != other.seed)
			return false;
		return true;
	}
}
