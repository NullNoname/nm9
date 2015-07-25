package cx.it.nullpo.nm9.testbed;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import cx.it.nullpo.nm9.engine.definition.Game;
import cx.it.nullpo.nm9.engine.event.input.ButtonEvent;
import cx.it.nullpo.nm9.engine.event.input.NMInputEvent;
import cx.it.nullpo.nm9.engine.event.listeners.NMOutputListener;
import cx.it.nullpo.nm9.engine.game.ButtonStatus;
import cx.it.nullpo.nm9.engine.game.GameManager;
import cx.it.nullpo.nm9.engine.script.RhinoScriptHost;
import cx.it.nullpo.nm9.engine.script.ScriptHost;

public class NullpoMinoTestbed extends JFrame implements Runnable {
	private static final long serialVersionUID = 4290970389745095120L;

	protected GameView gameView;
	protected PieceView holdView;
	protected PieceView[] pieceViewArray;
	protected GameManager gameManager;
	protected List<NMOutputListener> outputListenerList = Collections.synchronizedList(new ArrayList<NMOutputListener>());
	protected List<NMInputEvent> inputEventQueue = Collections.synchronizedList(new ArrayList<NMInputEvent>());
	protected Thread gameThread;
	protected volatile boolean running;
	protected Game gameDef;
	protected ScriptHost scriptHost;

	public NullpoMinoTestbed() {
		this(new String[0]);
	}

	public NullpoMinoTestbed(String[] args) {
		super();

		// Load game definition file
		try {
			Serializer serializer = new Persister();
			String mode = "sample";
			if(args.length >= 1) mode = args[0];
			URL url = Game.class.getClassLoader().getResource("cx/it/nullpo/nm9/engine/res/mode/" + mode + ".xml");
			gameDef = serializer.read(Game.class, url.openStream());
		} catch (Exception e) {
			System.err.println("Cannot load game definition file");
			e.printStackTrace();
			return;
		}

		// Create GameManager
		try {
			gameManager = new GameManager(gameDef, System.currentTimeMillis());
		} catch (Exception e) {
			System.err.println("Failed to create GameManager");
			e.printStackTrace();
			return;
		}

		// Setup GUI things
		setTitle("NullpoMino Testbed");
		setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));

		JPanel pHold = new JPanel(new BorderLayout());
		add(pHold);
		holdView = new PieceView(-1, 16);
		holdView.setOpaque(true);
		holdView.setPreferredSize(new Dimension(4*16, 4*16));
		holdView.setVisible(true);
		holdView.setFocusable(false);
		outputListenerList.add(holdView);
		pHold.add(holdView, BorderLayout.NORTH);

		gameView = new GameView();
		gameView.setOpaque(true);
		gameView.setPreferredSize(new Dimension(gameDef.getConfig().getFieldWidth() * 20, gameDef.getConfig().getFieldHeight() * 20));
		gameView.setVisible(true);
		gameView.setFocusable(true);
		gameView.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				processKeyEvent(e, true);
			}
			@Override
			public void keyReleased(KeyEvent e) {
				processKeyEvent(e, false);
			}

			public void processKeyEvent(KeyEvent e, boolean pressed) {
				int id = -1;

				switch(e.getKeyCode()) {
				case KeyEvent.VK_UP:    id = ButtonStatus.ID_HARDDROP; break;
				case KeyEvent.VK_DOWN:  id = ButtonStatus.ID_SOFTDROP; break;
				case KeyEvent.VK_LEFT:  id = ButtonStatus.ID_LEFT; break;
				case KeyEvent.VK_RIGHT: id = ButtonStatus.ID_RIGHT; break;
				case KeyEvent.VK_A:     id = ButtonStatus.ID_ROTATECW; break;
				case KeyEvent.VK_S:     id = ButtonStatus.ID_ROTATECCW; break;
				case KeyEvent.VK_D:     id = ButtonStatus.ID_ROTATECW; break;
				case KeyEvent.VK_Z:     id = ButtonStatus.ID_ROTATE180; break;
				case KeyEvent.VK_X:     id = ButtonStatus.ID_HOLD; break;
				case KeyEvent.VK_C:     id = ButtonStatus.ID_ITEM; break;
				case KeyEvent.VK_ESCAPE:
					dispose();
					break;
				}

				if(id >= 0) {
					ButtonEvent be = new ButtonEvent(0, 0, id, pressed);
					inputEventQueue.add(be);
				}
			}
		});
		outputListenerList.add(gameView);
		add(gameView);

		JPanel pNext = new JPanel(new BorderLayout());
		add(pNext);
		JPanel pNextSub = new JPanel();
		pNextSub.setLayout(new BoxLayout(pNextSub, BoxLayout.Y_AXIS));
		pNext.add(pNextSub, BorderLayout.NORTH);

		pieceViewArray = new PieceView[gameDef.getConfig().getPiecePreview()];
		for(int i = 0; i < pieceViewArray.length; i++) {
			PieceView pv = new PieceView(i, 16);
			pv.setOpaque(true);
			pv.setPreferredSize(new Dimension(4*16, 4*16));
			pv.setVisible(true);
			pv.setFocusable(false);
			outputListenerList.add(pv);
			pNextSub.add(pv);
			pieceViewArray[i] = pv;
		}

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
			}

			@Override
			public void windowClosed(WindowEvent e) {
				running = false;
				gameView.stopThread();
				holdView.stopThread();
				for(int i = 0; i < pieceViewArray.length; i++)
					pieceViewArray[i].stopThread();
			}
		});

		setVisible(true);
		pack();
		gameView.requestFocus();

		gameThread = new Thread(this, "Game Thread");
		gameThread.setDaemon(true);
		running = true;
		gameThread.start();

		gameView.startThread();
		holdView.startThread();
		for(int i = 0; i < pieceViewArray.length; i++)
			pieceViewArray[i].startThread();
	}

	public void run() {
		// Load game script file
		try {
			String scriptFileName = gameDef.getScriptFileName();
			URL url = Game.class.getClassLoader().getResource("cx/it/nullpo/nm9/engine/res/mode/" + scriptFileName);
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

			if(gameDef.getScriptType().equals("text/javascript")) {
				scriptHost = new RhinoScriptHost();
				scriptHost.evalReader(in, scriptFileName, 1);
				scriptHost.callNull("init");
			}
		} catch (Exception e) {
			e.printStackTrace();
			scriptHost = null;
		}

		// Run game
		long prevTime = System.nanoTime();

		while(running) {
			long nowTime = System.nanoTime();
			long runTime = nowTime - prevTime;
			prevTime = nowTime;
			gameManager.update(runTime, inputEventQueue, outputListenerList, scriptHost, null);

			try {
				Thread.sleep(1);
			} catch (Exception e) {}
		}

		// Cleanup
		if(scriptHost != null) {
			scriptHost.callNull("shutdown");
			scriptHost.shutdown();
		}
	}

	public static void main(String[] args) {
		NullpoMinoTestbed.class.getSimpleName();

		new NullpoMinoTestbed(args);
	}
}
