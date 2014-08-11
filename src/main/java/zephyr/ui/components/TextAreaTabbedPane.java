package zephyr.ui.components;

import java.awt.event.KeyListener;

import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import zephyr.text.TextDocument;

/**
 * 
 * @see TextArea
 * @see JTabbedPane
 * 
 * @author Fuad Saud
 * 
 */
public class TextAreaTabbedPane extends JTabbedPane {

	/**
	 * Serial version ID.
	 */
	private static final long serialVersionUID = -9160529171039898352L;

	/**
	 * Creates an empty TabbedPane with a default tab placement of
	 * JTabbedPane.TOP.
	 */
	public TextAreaTabbedPane() {
		super();
	}

	/**
	 * Adds a tab with a blank {@link TextArea} at the end of the components
	 * array;
	 * 
	 * @return the {@link TextArea} added.
	 */
	public TextArea addTab() {
		TextArea ta = new TextArea();
		super.addTab("Untitled", prepareTextArea(ta));
		return ta;
	}

	/**
	 * Adds a tab with a {@link TextArea} created with the passed arguments.
	 * 
	 * @param name
	 *            the name of the new {@link TextArea}'s document.
	 * @param path
	 *            the path of the new {@link TextArea}'s document.
	 * @param text
	 *            the content of the new {@link TextArea}'s document.
	 * @return the {@link TextArea} added.
	 */
	public TextArea addTab(String name, String path, String text) {
		TextArea ta = new TextArea(new TextDocument(name, path, text));
		super.addTab(name, prepareTextArea(ta));
		return ta;
	}

	public TextArea addTab(TextDocument document) {
		TextArea ta = new TextArea(document);
		super.addTab(document.getName(), prepareTextArea(ta));
		return ta;
	}

	/**
	 * Works like {@link JTabbedPane#getSelectedComponent()} but casts the
	 * component to {@link TextArea} before returning.
	 * 
	 * @return the {@link TextArea} inside the {@link GenericScrollPane} on the
	 *         <code>index</code>.
	 */
	@SuppressWarnings("unchecked")
	public TextArea getDocumentAt(int index) {
		return ((GenericScrollPane<TextArea>) super.getComponentAt(index)).getComponent();
	}

	/**
	 * Works like {@link JTabbedPane#getSelectedComponent()} but casts the
	 * component to {@link TextArea} before returning.
	 * 
	 * @return the {@link TextArea} inside the selected tab's
	 *         {@link GenericScrollPane}.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public TextArea getSelectedComponent() {
		return ((GenericScrollPane<TextArea>) super.getSelectedComponent()).getComponent();
	}

	/**
	 * Adds or removes the tab's title (referenced by <code>index</code> a '*'
	 * as the first character.
	 * 
	 * @param index
	 *            the index of the tab.
	 * @param b
	 *            if <code>true</code> adds a '*' (if the string don't already
	 *            starts with one). Removes, case <code>false</code>.
	 */
	public void setTitleStarAt(int index, boolean b) {
		if (b) {
			if (!getTitleAt(getSelectedIndex()).startsWith("*")) {
				setTitleAt(getSelectedIndex(), "*" + getTitleAt(getSelectedIndex()));
			}
		} else if (getTitleAt(getSelectedIndex()).charAt(0) == '*') {
			setTitleAt(getSelectedIndex(), getTitleAt(getSelectedIndex()).substring(1));
		}
	}

	/**
	 * Adds a {@link KeyListener} to the passed document and adds it to a
	 * {@link GenericScrollPane}. The {@link KeyListener} puts a '*' at the
	 * begin of the tab name when the document content is changed.
	 * 
	 * @param ta
	 *            the The {@link TextArea} to be prepared.
	 * @return The {@link GenericScrollPane} with the <code>ta</code> already
	 *         added.
	 */
	private GenericScrollPane<TextArea> prepareTextArea(TextArea ta) {
		GenericScrollPane<TextArea> scroll = new GenericScrollPane<TextArea>();

		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.getViewport().setBorder(null);
		ta.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void changedUpdate(DocumentEvent e) {
				setTitleStarAt(getSelectedIndex(), true);
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				setTitleStarAt(getSelectedIndex(), true);
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				setTitleStarAt(getSelectedIndex(), true);
			}
		});
		scroll.addOnViewPort(ta);
		return scroll;
	}
}
