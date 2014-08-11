package zephyr.text;

import java.util.ArrayList;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import zephyr.text.exceptions.NoMatchesFoundException;
import zephyr.ui.components.TextArea;

/**
 * @author Fuad Saud
 * 
 */
public class Finder {

	public static final byte FIND_NEXT = 1;

	public static final byte FIND_PREVIOUS = 2;

	public static final int WHOLE_WORD = 1 << 1;

	public static final int MATCH_CASE = 1 << 2;

	public static final int REGULAR_EXPRESSION = 1 << 3;

	private final TextArea textArea;

	private String search;

	private ArrayList<Integer> results;

	private int modifiers;

	private int currentIndex = 0;

	private boolean wrapEnd = false;

	private boolean wrapBegin = false;

	private boolean updated;

	private NoMatchesFoundException lastException;

	public Finder(TextArea textArea) {
		this.textArea = textArea;
		if (textArea == null) {
			throw new IllegalArgumentException("Parameter cannot be null");
		}

		this.updated = false;

		textArea.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void changedUpdate(DocumentEvent e) {
				updated = false;
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				updated = false;
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				updated = false;

			}
		});
	}

	public void find(String search, byte direction, int modifiers) throws NoMatchesFoundException {
		textArea.requestFocus();
		if (this.search == null || !this.search.equals(search) || this.modifiers != modifiers
				|| !updated || lastException != null) {
			this.search = search;
			this.modifiers = modifiers;
			TextDocument doc = textArea.getTextDocument();
			results = doc.find(search, wholeWord(), matchCase());
			this.updated = true;
			this.currentIndex = 0;
			if (results.size() == 0) {

				throw lastException = new NoMatchesFoundException();
			}

			lastException = null;
		}

		if (direction == FIND_PREVIOUS) {
			findPrevious();
		} else {
			findNext();
		}

	}

	private void findNext() {
		int caretPos = textArea.getCaretPosition();
		if (wrapEnd) {
			caretPos = 0;
			wrapEnd = false;
			wrapBegin = true;
		}

		caretPos = caretPos - search.length() + 1;

		// Finds the next result to be selected
		for (int i = 0; i < results.size(); i++) {
			if (currentIndex == results.size() - 1) {
				currentIndex = 0;
			}

			if (results.get(i) >= caretPos) {
				currentIndex = i;
				textArea.setCaretPosition(results.get(currentIndex));
				if (i != 0) {
					wrapBegin = false;
				}
				break;
			} else if (i == results.size() - 1) {
				// If there's no more matches ahead, return to begin.
				wrapEnd = true;
				wrapBegin = false;
				findNext();
				return;
			}
		}
		caretPos = textArea.getCaretPosition();
		textArea.setSelectionStart(caretPos);
		textArea.setSelectionEnd(caretPos + search.length());
	}

	private void findPrevious() {
		int caretPos = textArea.getCaretPosition();
		if (wrapBegin) {
			caretPos = textArea.getText().length();
			wrapBegin = false;
			wrapEnd = true;
		}

		boolean shouldInvertSelectionCaretPos = true;
		for (int i = results.size() - 1; i >= 0; i--) {
			if (currentIndex == 0) {
				currentIndex = results.size() - 1;
			}

			String selectedText = textArea.getSelectedText();

			if (shouldInvertSelectionCaretPos && selectedText != null
					&& selectedText.equals(search)
					&& caretPos == results.get(currentIndex) + search.length()) {
				caretPos = caretPos - search.length();
				shouldInvertSelectionCaretPos = false;
			}

			if (results.get(i) < caretPos) {
				currentIndex = i;
				textArea.setCaretPosition(results.get(currentIndex));
				if (i != results.size() - 1) {
					wrapEnd = false;
				}
				break;
			} else if (i == 0) {
				// If there's no more matches back, return to end.
				wrapBegin = true;
				wrapEnd = false;
				findPrevious();
				return;
			}
		}
		caretPos = textArea.getCaretPosition();
		textArea.setSelectionStart(caretPos);
		textArea.setSelectionEnd(caretPos + search.length());
	}

	private boolean matchCase() {
		return (this.modifiers & MATCH_CASE) != 0;
	}

	private boolean wholeWord() {
		return (this.modifiers & WHOLE_WORD) != 0;
	}
}
