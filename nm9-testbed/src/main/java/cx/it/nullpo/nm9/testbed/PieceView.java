package cx.it.nullpo.nm9.testbed;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JComponent;

import cx.it.nullpo.nm9.engine.event.listeners.NMOutputListener;
import cx.it.nullpo.nm9.engine.event.output.NMOutputEvent;
import cx.it.nullpo.nm9.engine.event.output.SnapshotUpdateEvent;
import cx.it.nullpo.nm9.engine.game.RuntimePiece;

public class PieceView extends JComponent implements Runnable, NMOutputListener {
	private static final long serialVersionUID = 1459657212409403914L;

	protected int previewNumber = 0;	// -1:Hold 0+:Next
	protected int blockSize = 0;

	protected RuntimePiece pieceObject;
	protected volatile RuntimePiece pieceObjectPending;

	public Thread paintThread;
	public volatile boolean running;
	protected volatile boolean needRepaint;
	protected Graphics2D dbGraphics;
	protected Image dbImage;

	public PieceView() {
	}

	public PieceView(int previewNumber) {
		this.previewNumber = previewNumber;
	}

	public PieceView(int previewNumber, int blockSize) {
		this.previewNumber = previewNumber;
		this.blockSize = blockSize;
	}

	public void onNMOutputEvent(NMOutputEvent event) {
		if(event instanceof SnapshotUpdateEvent) {
			SnapshotUpdateEvent ev = (SnapshotUpdateEvent)event;

			RuntimePiece p = null;
			if(previewNumber >= 0)
				p = ev.getSnapshot().getPieceQueue().peek(previewNumber);
			else
				p = ev.getSnapshot().getHoldPieceObject();

			pieceObjectPending = (p == null) ? null : new RuntimePiece(p);

			needRepaint = true;
			if(paintThread != null) paintThread.interrupt();
		}
	}

	public void startThread() {
		paintThread = new Thread(this, "GameView Paint Thread");
		paintThread.setDaemon(true);
		running = true;
		paintThread.start();
	}

	public void stopThread() {
		running = false;
		if(paintThread != null) paintThread.interrupt();
	}

	public void run() {
		while(running) {
			if(needRepaint) {
				needRepaint = false;
				renderScreen();
				paintScreen();
			}

			try {
				Thread.sleep(1000);
			} catch (Exception e) {}
		}
	}

	public void paintScreen() {
		try {
			Graphics2D g = (Graphics2D)getGraphics();
			if(g != null && dbImage != null) {
				g.drawImage(dbImage, 0, 0, null);
			}
			Toolkit.getDefaultToolkit().sync();
			if(g != null) {
				g.dispose();
			}
		} catch (Exception e) {
		}
	}

	public void renderScreen() {
		if(dbImage == null) {
			dbImage = createImage(getWidth(), getHeight());
			if(dbImage == null) {
				return;
			} else {
				dbGraphics = (Graphics2D)dbImage.getGraphics();
			}
		}

		if(dbGraphics != null)
			drawScreen(dbGraphics);
	}

	public void drawScreen(Graphics2D g) {
		pieceObject = pieceObjectPending;

		g.setColor(Color.black);
		g.fillRect(0, 0, getWidth(), getHeight());

		if(pieceObject != null) {
			int blkSize = (blockSize <= 0) ? getRecommendedBlockSize(pieceObject) : blockSize;
			int x = getRecommendedPieceX(pieceObject, blkSize);
			int y = getRecommendedPieceY(pieceObject, blkSize);
			ViewUtil.drawPiece(g, x, y, 0, blkSize, ViewUtil.BLOCK_VIEW_PIECE, pieceObject, 0);
		}
	}

	public int getRecommendedPieceX(RuntimePiece piece) {
		int blkSize = getRecommendedBlockSize(piece);
		return getRecommendedPieceX(piece, blkSize);
	}

	public int getRecommendedPieceX(RuntimePiece piece, int size) {
		int min = piece.getMinBlockPositionX(0);
		int totalWidth = size * piece.getWidth(0);
		return (-min * size) + ((getWidth() - totalWidth) / 2);
	}

	public int getRecommendedPieceY(RuntimePiece piece) {
		int blkSize = getRecommendedBlockSize(piece);
		return getRecommendedPieceY(piece, blkSize);
	}

	public int getRecommendedPieceY(RuntimePiece piece, int size) {
		int min = piece.getMinBlockPositionY(0);
		int totalHeight = size * piece.getHeight(0);
		return (-min * size) + ((getHeight() - totalHeight) / 2);
	}

	public int getRecommendedBlockSize(RuntimePiece piece) {
		return Math.min(getWidth() / piece.getWidth(0), getHeight() / piece.getHeight(0));
	}
}
