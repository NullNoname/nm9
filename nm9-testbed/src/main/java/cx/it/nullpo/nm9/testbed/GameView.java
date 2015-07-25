package cx.it.nullpo.nm9.testbed;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JComponent;

import cx.it.nullpo.nm9.engine.event.listeners.NMOutputListener;
import cx.it.nullpo.nm9.engine.event.output.NMOutputEvent;
import cx.it.nullpo.nm9.engine.event.output.PieceMovedEvent;
import cx.it.nullpo.nm9.engine.event.output.SnapshotUpdateEvent;
import cx.it.nullpo.nm9.engine.game.Field;
import cx.it.nullpo.nm9.engine.game.RuntimePiece;

public class GameView extends JComponent implements NMOutputListener, Runnable {
	private static final long serialVersionUID = 2525834672337063187L;

	protected Field field;
	protected volatile Field fieldPending;
	protected RuntimePiece nowPieceObject;
	protected volatile RuntimePiece nowPieceObjectPending;
	protected int nowPieceX, nowPieceY, nowPieceState, nowPieceBottomY;
	protected volatile int nowPieceXPending, nowPieceYPending, nowPieceStatePending, nowPieceBottomYPending;

	public Thread paintThread;
	public volatile boolean running;
	protected volatile boolean needRepaint;
	protected Graphics2D dbGraphics;
	protected Image dbImage;

	public void onNMOutputEvent(NMOutputEvent event) {
		if(event instanceof SnapshotUpdateEvent) {
			SnapshotUpdateEvent ev = (SnapshotUpdateEvent)event;
			fieldPending = new Field(ev.getSnapshot().getField());
			RuntimePiece p = ev.getSnapshot().getNowPieceObject();
			nowPieceObjectPending = (p == null) ? null : new RuntimePiece(p);
			nowPieceXPending = ev.getSnapshot().getNowPieceX();
			nowPieceYPending = ev.getSnapshot().getNowPieceY();
			nowPieceStatePending = ev.getSnapshot().getNowPieceState();
			nowPieceBottomYPending = ev.getSnapshot().getNowPieceBottomY();
			needRepaint = true;
			if(paintThread != null) paintThread.interrupt();
		} else if(event instanceof PieceMovedEvent) {
			PieceMovedEvent ev = (PieceMovedEvent)event;
			RuntimePiece p = ev.getPieceObject();
			nowPieceObjectPending = (p == null) ? null : new RuntimePiece(p);
			nowPieceXPending = ev.getNewPieceX();
			nowPieceYPending = ev.getNewPieceY();
			nowPieceStatePending = ev.getNewPieceState();
			nowPieceBottomYPending = ev.getNewPieceBottomY();
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
		field = fieldPending;
		nowPieceObject = nowPieceObjectPending;
		nowPieceX = nowPieceXPending;
		nowPieceY = nowPieceYPending;
		nowPieceState = nowPieceStatePending;
		nowPieceBottomY = nowPieceBottomYPending;

		if(field != null) {
			int x = getRecommendedFieldX(field.getWidth(), field.getHeight());
			int y = getRecommendedFieldY(field.getWidth(), field.getHeight());
			int blkSize = getRecommendedBlockSize(field.getWidth(), field.getHeight());

			g.setColor(Color.black);
			g.setClip(x, y, blkSize * field.getWidth(), blkSize * field.getHeight());
			g.fillRect(x, y, blkSize * field.getWidth(), blkSize * field.getHeight());

			ViewUtil.drawField(g, x, y, blkSize, field);

			if(nowPieceObject != null) {
				ViewUtil.drawPiece(g, x + (nowPieceX * blkSize), y + (nowPieceBottomY * blkSize), 0, blkSize,
						ViewUtil.BLOCK_VIEW_GHOST, nowPieceObject, nowPieceState);

				ViewUtil.drawPiece(g, x + (nowPieceX * blkSize), y + (nowPieceY * blkSize), 0, blkSize,
						ViewUtil.BLOCK_VIEW_PIECE, nowPieceObject, nowPieceState);
			}
		}

		g.setClip(null);
	}

	public int getRecommendedFieldX(int fldWidth, int fldHeight) {
		int blkSize = getRecommendedBlockSize(fldWidth, fldHeight);
		int fldTotalWidth = fldWidth * blkSize;
		return (getWidth() - fldTotalWidth) / 2;
	}

	public int getRecommendedFieldY(int fldWidth, int fldHeight) {
		/*
		int blkSize = getRecommendedBlockSize(fldWidth, fldHeight);
		int fldTotalHeight = fldHeight * blkSize;
		return (getHeight() - fldTotalHeight) / 2;
		*/
		return 0;
	}

	public int getRecommendedBlockSize(int fldWidth, int fldHeight) {
		return Math.min(getWidth() / fldWidth, getHeight() / fldHeight);
	}
}
