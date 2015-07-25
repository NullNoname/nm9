package cx.it.nullpo.nm9.engine.game;

import java.io.Serializable;

import cx.it.nullpo.nm9.engine.common.Copyable;

/**
 * An interface of piece queue.
 * @author NullNoname
 */
public interface PieceQueue extends Copyable, Serializable {
	/**
	 * Pop the first piece from the queue.
	 * @return The first RuntimePiece in the queue
	 */
	public RuntimePiece pop();

	/**
	 * Get the specified piece in the queue. Calling this method will not remove the piece from the queue.
	 * @param n Index of the piece
	 * @return RuntimePiece in the queue
	 * @throws ArrayIndexOutOfBoundsException If (n < 0) or (n >= getNextQueueSize())
	 */
	public RuntimePiece peek(int n);

	/**
	 * Get the size of piece queue
	 * @return Size of piece queue
	 */
	public int getNextQueueSize();
}
