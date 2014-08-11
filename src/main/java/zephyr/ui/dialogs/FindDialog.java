package zephyr.ui.dialogs;

import static zephyr.Zephyr.STRINGS;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.miginfocom.swing.MigLayout;
import zephyr.text.Finder;
import zephyr.text.TextDocument;
import zephyr.text.exceptions.NoMatchesFoundException;
import zephyr.ui.components.TextArea;

public class FindDialog extends JDialog {

	/**
	 * Serial version ID.
	 */
	private static final long serialVersionUID = 1993216627917629331L;

	/**
	 * The last string searched. Used on dialogs initializer, in case of no word
	 * selected in the <code>content</code>.
	 */
	private static String lastSearch;

	/**
	 * Saves whether the <code>wholeWord</code> check box was checked on the
	 * last dialog's dispose.
	 */
	private static boolean lastWholeWord;

	/**
	 * Saves whether the <code>matchCase</code> check box was checked on the
	 * last dialog's dispose.
	 */
	private static boolean lastMatchCase;

	private static boolean lastRegularExpression;
	/**
	 * The text area used as target of the search.
	 */
	private JTextArea content;

	/**
	 * The button used to order a {@linkplain Finder#FIND_NEXT} direction to the
	 * {@linkplain FindDialog#find(byte)} method.
	 */
	private JButton findNext;

	/**
	 * The object used to make searches in a {@link TextDocument} and highlight
	 * (select) results on a {@link TextArea}.
	 */
	private Finder finder;

	/**
	 * The button used to order a {@linkplain Finder#FIND_PREVIOUS} direction to
	 * the {@linkplain FindDialog#find(byte)} method.
	 */
	private JButton findPrevious;

	/**
	 * Check box used to inform the finder whether searching perfect whole
	 * matches of the <code>search</code>.
	 */
	private JCheckBox wholeWord;

	/**
	 * Check box used to inform the finder whether consider matching the case of
	 * the <code>search</code> on find process.
	 */
	private JCheckBox matchCase;

	private JCheckBox regularExpression;

	/**
	 * Sets up the dialog.
	 * 
	 * @param owner
	 *            the owner frame or dialog.
	 * @param textArea
	 *            the text area used as target of the search.
	 */
	public FindDialog(JFrame owner, TextArea textArea) {
		super(owner, STRINGS.getString("find"), false);

		if (textArea == null) {
			throw new IllegalArgumentException("Parameter cannot be null");
		}

		String selectedText;
		if ((selectedText = textArea.getSelectedText()) != null) {
			lastSearch = selectedText;
		}

		finder = new Finder(textArea);

		setLayout(new MigLayout());

		initComponents();

		addWindowListener(new WindowListener() {

			@Override
			public void windowActivated(WindowEvent e) {

			}

			@Override
			public void windowClosed(WindowEvent e) {

			}

			@Override
			public void windowClosing(WindowEvent e) {
				// lastSearch = content.getText();
				// lastWholeWord = wholeWord.isSelected();
				// lastMatchCase = matchCase.isSelected();
			}

			@Override
			public void windowDeactivated(WindowEvent e) {

			}

			@Override
			public void windowDeiconified(WindowEvent e) {

			}

			@Override
			public void windowIconified(WindowEvent e) {

			}

			@Override
			public void windowOpened(WindowEvent e) {

			}
		});
		setLocationRelativeTo(owner);
		setResizable(false);
		pack();
	}

	private int defineModifiers() {
		return (wholeWord.isSelected() ? Finder.WHOLE_WORD : 0)
				+ (matchCase.isSelected() ? Finder.MATCH_CASE : 0);
	}

	/**
	 * Calls {@linkplain Finder#find(String, byte)}, with the given string and
	 * direction.
	 * 
	 * @param direction
	 *            the direction of the search (find next/previous).
	 */
	private void find(byte direction) {
		if (!content.getText().isEmpty()) {
			try {
				String search = content.getText();
				int modifiers = defineModifiers();
				finder.find(search, direction, modifiers);
			} catch (NoMatchesFoundException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Initializes the dialog's components.
	 */
	private void initComponents() {
		initTextArea();
		initOptions();

		findPrevious = new JButton(STRINGS.getString("find-previous"));
		findPrevious.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				find(Finder.FIND_PREVIOUS);
			}
		});
		add(findPrevious, "newline, tag ok");

		findNext = new JButton(STRINGS.getString("find-next"));
		findNext.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				find(Finder.FIND_NEXT);
			}
		});
		add(findNext, "grow, tag ok");

	}

	/**
	 * Initializes the options components, like 'Match Case', 'Whole Word', etc.
	 */
	private void initOptions() {
		// TODO
		JPanel optionsPanel = new JPanel(new MigLayout());

		wholeWord = new JCheckBox(STRINGS.getString("whole-word"));
		wholeWord.setToolTipText(STRINGS.getString("whole-word-tooltip"));
		wholeWord.setSelected(lastWholeWord);

		matchCase = new JCheckBox(STRINGS.getString("match-case"));
		matchCase.setToolTipText(STRINGS.getString("match-case-tooltip"));
		matchCase.setSelected(lastMatchCase);

		regularExpression = new JCheckBox(STRINGS.getString("regular-expression"));
		regularExpression.setToolTipText(STRINGS.getString("regular-expression-tooltip"));
		regularExpression.setSelected(lastRegularExpression);

		optionsPanel.add(wholeWord);
		optionsPanel.add(matchCase);
		optionsPanel.add(regularExpression);

		add(optionsPanel, "wrap");
	}

	/**
	 * Initializes the dialog's text area.
	 */
	private void initTextArea() {
		content = new JTextArea(5, 35);
		content.setLineWrap(true);

		if (lastSearch != null) {
			content.setText(lastSearch);
		}

		JScrollPane scroll = new JScrollPane();
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.getViewport().setBorder(null);
		scroll.getViewport().add(content);

		add(scroll, "grow, span");
	}
}
