package cx.it.nullpo.nm9.testbed;

import java.awt.Color;
import java.awt.Graphics2D;

import cx.it.nullpo.nm9.engine.game.Field;
import cx.it.nullpo.nm9.engine.game.RuntimeBlock;
import cx.it.nullpo.nm9.engine.game.RuntimePiece;

public class ViewUtil {
	public static final int BLOCK_VIEW_FIELD = 0;
	public static final int BLOCK_VIEW_PIECE = 1;
	public static final int BLOCK_VIEW_GHOST = 2;

	public static void drawField(Graphics2D g, int x, int y, int size, Field fld) {
		for(int i = fld.getHeight() - 1; i >= fld.getHighestBlockY(); i--) {
			for(int j = 0; j < fld.getWidth(); j++) {
				drawBlock(g, x + (j * size), y + (i * size), 0, size, BLOCK_VIEW_FIELD, fld.getBlock(j, i));
			}
		}
	}

	public static void drawPiece(Graphics2D g, int x, int y, float subY, int size, int viewType, RuntimePiece piece, int state) {
		for(int i = 0; i < piece.getNumBlocks(); i++) {
			int x2 = piece.getBlockPositionX(state, i);
			int y2 = piece.getBlockPositionY(state, i);
			RuntimeBlock b = new RuntimeBlock(piece.getRuntimeBlock(i));
			b.setConnectUp(piece.isBlockConnectUp(state, i));
			b.setConnectDown(piece.isBlockConnectDown(state, i));
			b.setConnectLeft(piece.isBlockConnectLeft(state, i));
			b.setConnectRight(piece.isBlockConnectRight(state, i));
			drawBlock(g, x + (x2 * size), y + (y2 * size), subY, size, viewType, b);
		}
	}

	public static void drawBlock(Graphics2D g, int x, int y, float subY, int size, int viewType, RuntimeBlock blk) {
		if(blk == null) {
			return;
		}

		int y2 = (int)(y + (subY * size));

		if(viewType == BLOCK_VIEW_GHOST) {
			g.setColor(Color.black);
			g.fillRect(x, y2, size, size);
		}

		final String colorName = blk.getBlockDef().getColor();
		if(colorName.equalsIgnoreCase("red"))
			g.setColor(Color.red);
		else if(colorName.equalsIgnoreCase("orange"))
			g.setColor(Color.orange);
		else if(colorName.equalsIgnoreCase("yellow"))
			g.setColor(Color.yellow);
		else if(colorName.equalsIgnoreCase("green"))
			g.setColor(Color.green);
		else if(colorName.equalsIgnoreCase("cyan"))
			g.setColor(Color.cyan);
		else if(colorName.equalsIgnoreCase("blue"))
			g.setColor(Color.blue);
		else if(colorName.equalsIgnoreCase("purple"))
			g.setColor(Color.magenta);
		else if(colorName.equalsIgnoreCase("magenta"))
			g.setColor(Color.magenta);
		else if(colorName.equalsIgnoreCase("gray"))
			g.setColor(Color.gray);
		else if(colorName.equalsIgnoreCase("white"))
			g.setColor(Color.white);

		if(viewType == BLOCK_VIEW_GHOST) {
			//g.drawRect(x, y2, size-1, size-1);
		} else {
			g.fillRect(x, y2, size, size);
			g.setColor(Color.white);
		}

		int size2 = size - 1;
		if(!blk.isConnectUp())    g.drawLine(x        , y2        , x + size2, y2        );
		if(!blk.isConnectDown())  g.drawLine(x        , y2 + size2, x + size2, y2 + size2);
		if(!blk.isConnectLeft())  g.drawLine(x        , y2        , x        , y2 + size2);
		if(!blk.isConnectRight()) g.drawLine(x + size2, y2        , x + size2, y2 + size2);

	}
}
