package cx.it.nullpo.nm9.engine.game;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import cx.it.nullpo.nm9.engine.common.Copyable;
import cx.it.nullpo.nm9.engine.common.NEUtil;
import cx.it.nullpo.nm9.engine.common.NRandom;
import cx.it.nullpo.nm9.engine.definition.Game;
import cx.it.nullpo.nm9.engine.definition.Piece;

/**
 * A piece queue that uses Blackjack language to specify the behavior
 * @author NullNoname
 */
public class BJPieceQueue implements PieceQueue, Serializable, Copyable {
	private static final long serialVersionUID = 715596824008504531L;

	private Game gameDef;
	private Blackjack bj;
	private NRandom random;
	private int nextSize = 6;
	private LinkedList<RuntimePiece> nextQueue = new LinkedList<RuntimePiece>();
	private LinkedList<RuntimePiece> piecePool = new LinkedList<RuntimePiece>();
	private LinkedList<String> history = new LinkedList<String>();
	private int pieceCount = 0;

	public BJPieceQueue() {
	}

	public BJPieceQueue(Game gameDef, NRandom random) {
		this.gameDef = gameDef;
		this.random = random;

		String strBJ = gameDef.getConfig().getRandomizerBlackjack();
		if(strBJ == null || strBJ.length() == 0) {
			// Create a default Blackjack setting if none specified in game config
			strBJ = "";
			for(Piece p: gameDef.getPieces()) {
				strBJ += p.getName() + ",";
			}
			if(strBJ.endsWith(",")) {
				strBJ = strBJ.substring(0, strBJ.length()-1);
			}
		}
		this.bj = new Blackjack(strBJ);

		this.nextSize = gameDef.getConfig().getPiecePreview();
		init();
	}

	public BJPieceQueue(Game gameDef, NRandom random, Blackjack bj) {
		this.gameDef = gameDef;
		this.random = random;
		this.bj = bj;
		this.nextSize = gameDef.getConfig().getPiecePreview();
		init();
	}

	public BJPieceQueue(Game gameDef, NRandom random, Blackjack bj, int nextSize) {
		this.gameDef = gameDef;
		this.random = random;
		this.bj = bj;
		this.nextSize = nextSize;
		init();
	}

	public BJPieceQueue(Copyable src) {
		copy(src);
	}

	public void copy(Copyable src) {
		BJPieceQueue s = (BJPieceQueue)src;
		this.gameDef = new Game(s.gameDef);
		this.bj = new Blackjack(s.bj);
		this.random = new NRandom(s.random);
		this.nextSize = s.nextSize;
		this.nextQueue = new LinkedList<RuntimePiece>();
		for(RuntimePiece p: s.nextQueue) {
			this.nextQueue.add(new RuntimePiece(p));
		}
		this.piecePool = new LinkedList<RuntimePiece>();
		for(RuntimePiece p: s.piecePool) {
			this.piecePool.add(new RuntimePiece(p));
		}
		this.history = new LinkedList<String>(s.history);
		this.pieceCount = s.pieceCount;
	}

	public void init() {
		pieceCount = 0;
		random.reset();
		nextQueue.clear();
		piecePool.clear();

		// History init
		List<String> initHistoryList = bj.getInitHistoryList();
		history.clear();

		if(initHistoryList.size() > bj.getHistorySize()) {
			LinkedList<String> tempHistory = new LinkedList<String>(initHistoryList);

			while((history.size() < bj.getHistorySize()) && (tempHistory.size() >= 1)) {
				String pieceName = tempHistory.remove(random.nextInt(tempHistory.size()));
				history.add(pieceName);
			}
		} else {
			history.addAll(initHistoryList);
		}
	}

	public RuntimePiece pop() {
		fillNextQueue();
		return nextQueue.removeFirst();
	}

	public RuntimePiece peek(int n) {
		fillNextQueue();
		return nextQueue.get(n);
	}

	public int getNextQueueSize() {
		return nextSize;
	}

	public int getPieceCount() {
		return pieceCount;
	}

	protected void fillNextQueue() {
		while(nextQueue.size() < nextSize) {
			if(piecePool.size() == 0) {
				populatePool();
			}
			nextQueue.add(piecePool.poll());
			pieceCount++;
		}
	}

	protected void populatePool() {
		// Generate pool
		LinkedList<String> pieces = generatePool();

		// Create actual RuntimePiece by using names
		for(int i = 0; i < pieces.size(); i++) {
			String name = pieces.get(i);
			Piece pieceDef = (Piece)NEUtil.getNamedObjectE(name, gameDef.getPieces());
			RuntimePiece piece = new RuntimePiece(gameDef, pieceDef, random);
			piecePool.add(piece);
		}
	}

	protected LinkedList<String> generatePool() {
		LinkedList<String> pieces = new LinkedList<String>();
		LinkedList<String> bag = new LinkedList<String>();

		if(bj.isSequence()) {
			// Sequence
			pieces.addAll(bj.getPieceList());
		} else if(bj.getHistorySize() > 0) {
			// History
			int rolls = 0;
			while(bj.getMaxRolls() <= 0 || rolls < bj.getMaxRolls()) {
				String name;
				do {
					name = bj.getPieceList().get(random.nextInt(bj.getPieceList().size()));
				} while ((pieceCount == 0) && (bj.getFirstList().size() > 0) && (!bj.getFirstList().contains(name)));

				if(!history.contains(name)) {
					history.addFirst(name);
					while(history.size() > bj.getHistorySize()) history.removeLast();
					pieces.add(name);
					break;
				} else {
					rolls++;
				}
			}
		} else {
			// Bag
			bag.addAll(bj.getPieceList());

			while(bag.size() < bj.getBagSize()) {
				String randName = bj.getPieceList().get(random.nextInt(bj.getPieceList().size()));
				bag.add(randName);
			}

			if(pieceCount == 0) {
				int pos;
				String name;
				do {
					pos = random.nextInt(bag.size());
					name = bag.get(pos);
				} while ((bj.getFirstList().size() > 0) && (!bj.getFirstList().contains(name)));
				pieces.add(bag.remove(pos));
			}

			while((bag.size() > 0) && (pieces.size() < bj.getBagSize())) {
				int pos = random.nextInt(bag.size());
				pieces.add(bag.remove(pos));
			}
		}

		return pieces;
	}
}
