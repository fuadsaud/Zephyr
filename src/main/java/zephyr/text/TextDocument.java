package zephyr.text;

import static zephyr.Zephyr.STRINGS;

import java.util.ArrayList;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import javax.swing.undo.UndoManager;

/**
 * This class adds some simple features to the {@link PlainDocument} class.
 * 
 * @see PlainDocument
 * @see AbstractDocument
 * @see Document
 * @see DocumentListener
 * 
 * @author Fuad Saud
 * 
 */
public class TextDocument extends PlainDocument {

	/**
	 * Serial version ID.
	 */
	private static final long serialVersionUID = -4676679149064761964L;

	/**
	 * The name of the document.
	 */
	private String name;

	/**
	 * The path of the document on the disk.
	 */
	private String path;

	/**
	 * Indicates whether document is saved.
	 */
	private boolean saved;

	/**
	 * Indicates if the document was already flushed to the disc, independent of
	 * the {@code saved} flag.
	 */
	private boolean savedOnDisk;

	/**
	 * Indicates <code>true</code> when this is a just created blank document.
	 */
	private boolean newDocument;

	/**
	 * Creates a blank document with {@code name} "Untitled" and a null
	 * {@code path}.
	 */
	public TextDocument() {
		this(STRINGS.getString("untitled"), null, null);
		setSavedOnDisk(false);
		this.newDocument = true;
		putProperty("new", true);
	}

	/**
	 * Creates a document with the specified name, path and content
	 * 
	 * @param name
	 *            the document name
	 * @param path
	 *            the document path
	 * @param content
	 *            the content to be set on text area
	 */
	public TextDocument(String name, String path, String content) {
		try {
			insertString(0, content, null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		setName(name);
		setPath(path);

		setSaved(true);
		setSavedOnDisk(true);

		addUndoableEditListener(new UndoManager());
		addDocumentListener(new DocumentListener() {

			@Override
			public void changedUpdate(DocumentEvent e) {
				setSaved(false);
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				setSaved(false);
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				setSaved(false);
			}
		});

	}

	public int createLineAbove(int index) {
		try {
			String text = getText(0, getLength());

			int nextLineFeed = text.lastIndexOf('\n', index - 1);

			if (nextLineFeed == -1) {
				nextLineFeed = text.indexOf('\n');
				if (nextLineFeed == -1) {
					nextLineFeed = getLength();
				}
			}

			insertString(nextLineFeed++, "\n", null);

			return nextLineFeed;
		} catch (BadLocationException e) {
			e.printStackTrace();
			return index;
		}
	}

	public int createLineBelow(int index) {
		try {
			String text = getText(0, getLength());

			int nextLineFeed = text.indexOf('\n', index);

			if (nextLineFeed == -1) {
				nextLineFeed = getLength();
			}

			insertString(nextLineFeed++, "\n", null);

			return nextLineFeed++;
		} catch (BadLocationException e) {
			e.printStackTrace();
			return index;
		}
	}

	public void duplicateLines(int start, int end) {
		try {
			String text = getText(0, getLength());

			int firstLineFeed = text.lastIndexOf('\n', start);

			int secondLineFeed = end;

			if (end != getLength() && text.charAt(end) != 'n') {
				secondLineFeed = text.indexOf('\n', end);

				if (secondLineFeed == -1)
					secondLineFeed = getLength() - 1;
			}

			String dub = getText(firstLineFeed + 1, secondLineFeed - firstLineFeed);

			insertString(secondLineFeed + 1, dub, null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Iterate over the document's content, looking for matches of the given
	 * <code>search</code>.
	 * 
	 * @param search
	 *            a {@link String} to be found on the document's content
	 * @return an {@link ArrayList} containing all first indexes of matches of
	 *         the given <code>search</code>. For example, the following
	 *         content, when searched looking for the string "double" would
	 *         return a list containing [0, 14, 25]:
	 * 
	 *         "double string double int double char"
	 */
	public ArrayList<Integer> find(String search, boolean wholeWord, boolean matchCase) {
		ArrayList<Integer> results = new ArrayList<Integer>();
		String content = null;
		try {
			content = getText(0, getLength());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

		if (content.isEmpty()) {
			return results;
		}

		if (!matchCase) {
			search = search.toLowerCase();
			content = content.toLowerCase();
		}

		int lastIndex = content.lastIndexOf(search);
		if (lastIndex != -1) {
			int i = -1;
			while (i != lastIndex) {
				i = content.indexOf(search, ++i);
				results.add(i);
			}
		}

		ArrayList<Integer> toRemove = new ArrayList<Integer>();

		char[] neutralChars = new char[] { ' ', '\t', '\r', '\n' };

		if (wholeWord) {

			for (Integer result : results) {
				boolean afterNeutralChar = false;
				boolean beforeNeutralChar = false;

				for (char c : neutralChars) {
					if (result != 0 && content.charAt(result - 1) == c) {
						afterNeutralChar = true;
					}
				}

				for (char c : neutralChars) {
					if (result + search.length() != content.length() - 1
							&& content.charAt(result + search.length()) == c) {
						beforeNeutralChar = true;
					}
				}

				if (!((result == 0 || afterNeutralChar) && (result + search.length() == content
						.length() - 1 || beforeNeutralChar))) {
					toRemove.add(result);
				}
			}

			for (Integer remove : toRemove) {
				results.remove(remove);
			}
		}

		return results;
	}

	/**
	 * Returns the name of the document.
	 * 
	 * @return the name of the document
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param newDocument
	 *            the newDocument to set
	 */

	/**
	 * Returns the path of the document.
	 * 
	 * @return the path of the document
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Returns <code>true</code> when this is a just created blank document.
	 * 
	 * @return the <code>newDocument</code> attribute
	 */
	public boolean isNewDocument() {
		return newDocument;
	}

	/**
	 * Returns <code>true</code> if the document is saved; <code>false</code> if
	 * not.
	 * 
	 * @return the saved flag
	 */
	public boolean isSaved() {
		return saved;
	}

	/**
	 * Returns <code>true</code> if the document is saved on the disk;
	 * <code>false</code> if not.
	 * 
	 * @return the savedOnDisk flag
	 */
	public boolean isSavedOnDisk() {
		return savedOnDisk;
	}

	public void joinLines(int start, int end, String pattern) {
		try {
			String text = getText(0, getLength());

			int firstLineFeed = text.lastIndexOf('\n', start);

			int secondLineFeed = end;

			if (end != getLength() && text.charAt(end) != 'n') {
				secondLineFeed = text.indexOf('\n', end);

				if (secondLineFeed == -1)
					secondLineFeed = getLength() - 1;
			}

			int length = secondLineFeed - firstLineFeed - 1;
			String toJoin = getText(++firstLineFeed, length);

			if (toJoin.contains("\n")) {
				toJoin = toJoin.replace("\n", pattern);
				remove(firstLineFeed, length);
				insertString(firstLineFeed, toJoin, null);
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	public int[] moveLineDown(int index) {
		try {
			int caret = index;
			int firstLineLength, firstLineAdjustment = 0;
			String text = getText(0, getLength());

			int secondLineFeed = text.indexOf('\n', caret);
			if (secondLineFeed == -1) {
				return new int[] { index, index };
			}
			int firstLineFeed = text.lastIndexOf('\n', secondLineFeed - 1);

			if (firstLineFeed == -1)
				firstLineFeed++;

			int thirdLineFeed = text.length();

			for (int i = secondLineFeed + 1; i < text.length(); i++) {
				if (text.charAt(i) == '\n') {
					thirdLineFeed = i;
					break;
				}
			}

			String secondLine = text.substring(secondLineFeed, thirdLineFeed);
			remove(secondLineFeed, secondLine.length());
			insertString(firstLineFeed, secondLine, null);

			if (firstLineFeed == 0) {
				firstLineAdjustment = 1;
				remove(0, 1);
				insertString(secondLine.length() - 1, "\n", null);
			}

			firstLineFeed++;
			firstLineLength = secondLineFeed - firstLineFeed;
			secondLineFeed = firstLineFeed + secondLine.length();

			return new int[] { secondLineFeed - firstLineAdjustment,
					secondLineFeed + firstLineLength };

		} catch (BadLocationException e) {
			e.printStackTrace();
			return new int[] { index, index };
		}
	}

	public int[] moveLineUp(int index) {
		try {
			int caret = index;
			String text = getText(0, getLength());

			int secondLineFeed = text.lastIndexOf('\n', caret - 1);

			if (secondLineFeed == -1)
				return new int[] { index, index };

			int firstLineFeed = text.lastIndexOf('\n', secondLineFeed - 1);

			if (firstLineFeed == -1)
				firstLineFeed++;

			int thirdLineFeed = text.length();

			for (int i = caret; i < text.length(); i++) {
				if (text.charAt(i) == '\n') {
					thirdLineFeed = i;
					break;
				}
			}

			String secondLine = text.substring(secondLineFeed, thirdLineFeed);
			remove(secondLineFeed, secondLine.length());
			insertString(firstLineFeed, secondLine, null);

			if (firstLineFeed == 0) {
				firstLineFeed--;
				remove(0, 1);
				insertString(secondLine.length() - 1, "\n", null);
			}

			return new int[] { firstLineFeed + 1, firstLineFeed + secondLine.length() };
		} catch (BadLocationException e1) {
			e1.printStackTrace();
			return new int[] { index, index };
		}
	}

	public int[] selectLine(int index) {
		try {
			String text = getText(0, getLength());

			int start = text.lastIndexOf('\n', index - 1) + 1;

			int end = text.indexOf('\n', index);

			if (end == -1)
				end = getLength();

			return new int[] { start, end };
		} catch (BadLocationException e) {
			e.printStackTrace();
			return new int[] { index, index };
		}
	}

	public int[] selectLineText(int index) {
		try {
			int[] lineLimits = selectLine(index);
			System.out.println(lineLimits[0] + "\t" + lineLimits[1]);
			String line = getText(lineLimits[0], lineLimits[1] - lineLimits[0]);
			int start = index, end = index;
			for (int i = 0; i < line.length(); i++) {
				if (line.charAt(i) != ' ' && line.charAt(i) != '\t') {
					start = i;
					break;
				}
			}

			for (int i = line.length() - 1; i >= 0; i--) {
				if (line.charAt(i) != ' ' && line.charAt(i) != '\t') {
					end = i;
					break;
				}
			}

			start = lineLimits[0] + start;
			end = lineLimits[0] + end + 1;
			System.out.println(start + "\t" + end);
			return new int[] { start, end };
		} catch (BadLocationException e) {
			e.printStackTrace();
			return new int[] { index, index };
		}
	}

	/**
	 * Sets the document name.
	 * 
	 * @param name
	 *            the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the document path.
	 * 
	 * @param path
	 *            the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * Sets the {@code saved} attribute.
	 * 
	 * @param saved
	 *            the flag to set
	 */
	public void setSaved(boolean saved) {
		this.newDocument = false;
		this.saved = saved;
	}

	/**
	 * Sets the {@code savedOnDisk} attribute.
	 * 
	 * @param savedOnDisk
	 *            the flag to set
	 */
	public void setSavedOnDisk(boolean savedOnDisk) {
		this.savedOnDisk = savedOnDisk;
	}

	public void toLowerCase(int start, int end) {
		int length = end - start;
		try {
			String text = getText(start, length);

			text = text.toLowerCase();

			remove(start, length);
			insertString(start, text, null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	public void toUpperCase(int start, int end) {
		int length = end - start;
		try {
			String text = getText(start, length);

			text = text.toUpperCase();

			remove(start, length);
			insertString(start, text, null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
}
