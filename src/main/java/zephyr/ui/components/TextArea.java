package zephyr.ui.components;

import static zephyr.Zephyr.PREFERENCES;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextArea;

import zephyr.text.TextDocument;

/**
 * 
 * @see JTextArea
 * 
 * @author Fuad Saud
 * 
 */
public class TextArea extends JTextArea {

	/**
	 * Serial version ID.
	 */
	private static final long serialVersionUID = 2233198146436091347L;

	public TextArea() {
		this(new TextDocument());
	}

	public TextArea(TextDocument document) {
		super(document);

		init();
	}

	public void createLineAbove() {
		setCaretPosition(getTextDocument().createLineAbove(getCaretPosition()));
	}

	public void createLineBelow() {
		setCaretPosition(getTextDocument().createLineBelow(getCaretPosition()));
	}

	public void duplicateLines() {
		int start = getSelectionStart();
		int end = getSelectionEnd();

		getTextDocument().duplicateLines(start, end);

		setSelectionStart(start);
		setSelectionEnd(end);
	}

	/**
	 * This methods works just like {@link JTextArea#getDocument()}, but casts
	 * the return type to {@link TextDocument}.
	 * 
	 * @return the casted document
	 */
	public TextDocument getTextDocument() {
		return (TextDocument) super.getDocument();
	}

	public void init() {
		setTabSize(PREFERENCES.tabSize());
		setLineWrap(PREFERENCES.lineWrap());
		setWrapStyleWord(true);
		setFont(new Font("Courier New", Font.PLAIN, PREFERENCES.fontSize()));
		setForeground(Color.BLACK);
		setBackground(PREFERENCES.backgroundColor());

		addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
				defineAction(e);
			}

			@Override
			public void keyReleased(KeyEvent e) {

			}

			@Override
			public void keyTyped(KeyEvent e) {

			}
		});
	}

	public void joinLines(String pattern) {

		getTextDocument().joinLines(getSelectionStart(), getSelectionEnd(), pattern);
	}

	public void moveLineDown() {
		int[] moveLineDown = getTextDocument().moveLineDown(getCaretPosition());
		if (moveLineDown[0] != moveLineDown[1]) {
			setSelectionStart(moveLineDown[0]);
			setSelectionEnd(moveLineDown[1]);
		}

	}

	public void moveLineUp() {
		int[] moveLineUp = getTextDocument().moveLineUp(getCaretPosition());
		if (moveLineUp[0] != moveLineUp[1]) {
			setSelectionStart(moveLineUp[0]);
			setSelectionEnd(moveLineUp[1]);
		}

	}

	public void selectLine() {
		int[] selectLine = getTextDocument().selectLine(getCaretPosition());
		setSelectionStart(selectLine[0]);
		setSelectionEnd(selectLine[1]);
	}

	public void selectLineText() {
		int[] selectLineText = getTextDocument().selectLineText(getCaretPosition());

		setSelectionStart(selectLineText[0]);
		setSelectionEnd(selectLineText[1]);
	}

	public void toLowerCase() {
		int start = getSelectionStart();
		int end = getSelectionEnd();

		getTextDocument().toLowerCase(start, end);

		setSelectionStart(start);
		setSelectionEnd(end);
	}

	public void toUpperCase() {
		int start = getSelectionStart();
		int end = getSelectionEnd();

		getTextDocument().toUpperCase(start, end);

		setSelectionStart(start);
		setSelectionEnd(end);
	}

	private void defineAction(KeyEvent e) {
		if (e.isControlDown() && e.isShiftDown()) {
			// --------------------------------------- CONTROL + SHIFT
			switch (e.getKeyCode()) {

			case KeyEvent.VK_ENTER:
				createLineAbove();
				break;

			// case KeyEvent.VK_UP:
			// moveLineUp();
			// break;
			//
			// case KeyEvent.VK_DOWN:
			// moveLineDown();
			// break;

			}
		} else if (e.isControlDown() && e.isAltDown()) {
			// --------------------------------------- CONTROL + ALT

			switch (e.getKeyCode()) {

			// case KeyEvent.VK_UP:
			// toUpperCase();
			// break;
			//
			// case KeyEvent.VK_DOWN:
			// toLowerCase();
			// break;

			}
		} else if (e.isControlDown()) {
			// --------------------------------------- CONTROL
			switch (e.getKeyCode()) {

			case KeyEvent.VK_ENTER:
				createLineBelow();
				break;

			// case KeyEvent.VK_D:
			// duplicateLines();
			// break;
			}

		} else if (e.isAltDown()) {
			// --------------------------------------- ALT
			switch (e.getKeyCode()) {

			}
		}
	}
}
