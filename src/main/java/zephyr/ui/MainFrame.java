package zephyr.ui;

import static zephyr.Zephyr.PREFERENCES;
import static zephyr.Zephyr.STRINGS;
import static zephyr.Zephyr.getPreferencesManager;
import static zephyr.Zephyr.getUI;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import zephyr.Zephyr;
import zephyr.file.FileManager;
import zephyr.preferences.Preferences;
import zephyr.preferences.PreferencesManager;
import zephyr.text.TextDocument;
import zephyr.text.TextDocumentStreamer;
import zephyr.ui.components.FileChooser;
import zephyr.ui.components.GenericScrollPane;
import zephyr.ui.components.TextArea;
import zephyr.ui.components.TextAreaTabbedPane;
import zephyr.ui.dialogs.AboutDialog;
import zephyr.ui.dialogs.FindDialog;
import zephyr.ui.dialogs.PreferencesDialog;

/**
 * @author Fuad Saud
 * 
 */
public class MainFrame extends JFrame {

	private class MenuBar extends JMenuBar {

		/**
		 * Serial version ID.
		 */
		private static final long serialVersionUID = -5235397452222826439L;

		private ButtonGroup lineSeparatorGroup;

		private JRadioButtonMenuItem caretReturnLineFeed;

		private JRadioButtonMenuItem caretReturn;

		private JRadioButtonMenuItem lineFeed;

		public MenuBar() {
			lineSeparatorGroup = new ButtonGroup();

			initMenus();
		}

		private String getSelectedLineSeparator() {
			if (caretReturn.isSelected()) {
				return "\r";
			}

			if (caretReturnLineFeed.isSelected()) {
				return "\r\n";
			}

			return "\n";
		}

		private void initEditMenu() {
			JMenu edit = new JMenu(STRINGS.getString("edit"));

			JMenuItem undo = new JMenuItem(STRINGS.getString("undo"));
			undo.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						((UndoManager) tabs.getSelectedComponent().getTextDocument()
								.getUndoableEditListeners()[0]).undo();
					} catch (CannotUndoException ex) {
						ex.printStackTrace();
					}
				}
			});
			undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK));
			edit.add(undo);

			JMenuItem redo = new JMenuItem(STRINGS.getString("redo"));
			redo.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						((UndoManager) tabs.getSelectedComponent().getTextDocument()
								.getUndoableEditListeners()[0]).redo();
					} catch (CannotRedoException ex) {
						ex.printStackTrace();
					}
				}
			});
			redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK
					+ KeyEvent.SHIFT_DOWN_MASK));
			edit.add(redo);

			edit.addSeparator();

			JMenuItem find = new JMenuItem(STRINGS.getString("find") + "...");
			find.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					find();
				}
			});
			find.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK));
			edit.add(find);

			edit.addSeparator();

			JMenu lineSeparator = new JMenu(STRINGS.getString("line-separator"));

			lineFeed = new JRadioButtonMenuItem(STRINGS.getString("line-feed"));
			lineSeparator.add(lineFeed);

			caretReturn = new JRadioButtonMenuItem(STRINGS.getString("caret-return"));
			lineSeparator.add(caretReturn);

			caretReturnLineFeed = new JRadioButtonMenuItem(
					STRINGS.getString("line-feed-caret-return"));
			lineSeparator.add(caretReturnLineFeed);

			lineSeparatorGroup.add(lineFeed);
			lineSeparatorGroup.add(caretReturn);
			lineSeparatorGroup.add(caretReturnLineFeed);

			// Defines which button set selected, based on preferences
			String ls = PREFERENCES.lineSeparator();
			ButtonModel buttonModel;
			if (ls.equals("\r\n")) {
				buttonModel = caretReturnLineFeed.getModel();
			} else if (ls.equals("\r")) {
				buttonModel = caretReturn.getModel();
			} else {
				buttonModel = lineFeed.getModel();
			}

			lineSeparatorGroup.setSelected(buttonModel, true);

			edit.add(lineSeparator);

			edit.addSeparator();

			JMenuItem preferences = new JMenuItem(STRINGS.getString("preferences") + "...");
			preferences.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					preferences();
				}
			});
			preferences.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE,
					KeyEvent.CTRL_DOWN_MASK));
			edit.add(preferences);

			add(edit);
		}

		private void initFileMenu() {
			final JMenu file = new JMenu(STRINGS.getString("file"));

			JMenuItem newDocument = new JMenuItem(STRINGS.getString("new"));
			newDocument.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					newDocument();
				}
			});
			newDocument.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
					KeyEvent.CTRL_DOWN_MASK));
			file.add(newDocument);

			JMenuItem open = new JMenuItem(STRINGS.getString("open") + "...");
			open.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					open();
				}
			});
			open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
			file.add(open);

			file.addSeparator();

			JMenuItem save = new JMenuItem(STRINGS.getString("save"));
			save.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					save(true);
				}
			});
			save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
			file.add(save);

			JMenuItem saveAs = new JMenuItem(STRINGS.getString("save-as") + "...");
			saveAs.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					saveAs();
				}
			});
			saveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK
					+ KeyEvent.ALT_DOWN_MASK));
			file.add(saveAs);

			JMenuItem saveAll = new JMenuItem(STRINGS.getString("save-all"));
			saveAll.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					saveAll(true);
				}
			});
			saveAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK
					+ KeyEvent.SHIFT_DOWN_MASK));
			file.add(saveAll);

			file.addSeparator();

			JMenuItem rename = new JMenuItem(STRINGS.getString("rename"));
			rename.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					rename();
				}
			});
			rename.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, KeyEvent.CTRL_DOWN_MASK));
			file.add(rename);

			JMenuItem delete = new JMenuItem(STRINGS.getString("delete"));
			delete.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					delete();
				}
			});
			delete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,
					KeyEvent.CTRL_DOWN_MASK));
			file.add(delete);

			file.addSeparator();

			JMenuItem close = new JMenuItem(STRINGS.getString("close"));
			close.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					close();
				}
			});
			close.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.CTRL_DOWN_MASK));
			file.add(close);

			JMenuItem closeAll = new JMenuItem(STRINGS.getString("close-all"));
			closeAll.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					closeAll();
				}
			});
			closeAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.CTRL_DOWN_MASK
					+ KeyEvent.ALT_DOWN_MASK));
			file.add(closeAll);

			JMenuItem closeAllOthers = new JMenuItem(STRINGS.getString("close-all-others"));
			closeAllOthers.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					closeAllOthers();
				}
			});
			closeAllOthers.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W,
					KeyEvent.CTRL_DOWN_MASK + KeyEvent.SHIFT_DOWN_MASK));
			file.add(closeAllOthers);

			JMenuItem closeAllToTheRight = new JMenuItem(
					STRINGS.getString("close-all-to-the-right"));
			closeAllToTheRight.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					closeAllToTheRight();
				}
			});
			closeAllToTheRight.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W,
					KeyEvent.CTRL_DOWN_MASK + KeyEvent.SHIFT_DOWN_MASK + KeyEvent.ALT_DOWN_MASK));
			file.add(closeAllToTheRight);

			file.addSeparator();

			recentDocumentsMenu = new JMenu(STRINGS.getString("recent"));

			JMenuItem openAllRecent = new JMenuItem(STRINGS.getString("open-all-recent"));
			openAllRecent.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					for (String path : recentDocumentsPaths) {
						open(path, true);
					}
				}
			});

			JMenuItem emptyRecent = new JMenuItem(STRINGS.getString("empty-recent"));
			emptyRecent.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					recentDocumentsPaths.clear();
					updateRecentDocumentsMenu();
				}
			});

			recentDocumentsMenu.add(openAllRecent);
			recentDocumentsMenu.add(emptyRecent);

			recentDocumentsMenu.addSeparator();

			updateRecentDocumentsMenu();
			file.add(recentDocumentsMenu);

			file.addSeparator();

			JMenuItem exit = new JMenuItem(STRINGS.getString("exit"));
			exit.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					exit();
				}
			});
			exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK));
			file.add(exit);

			add(file);
		}

		private void initHelpMenu() {
			JMenu help = new JMenu(STRINGS.getString("help"));

			JMenuItem about = new JMenuItem(STRINGS.getString("about-zephyr") + "...");
			about.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					about();
				}
			});
			about.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
			help.add(about);

			add(help);
		}

		private void initMenus() {
			initFileMenu();
			initEditMenu();
			initViewMenu();
			initTextMenu();
			initRunMenu();
			initWindowMenu();
			initHelpMenu();
		}

		private void initRunMenu() {
			JMenu run = new JMenu(STRINGS.getString("run"));

			JMenuItem directory = new JMenuItem(STRINGS.getString("current-directory"));
			directory.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					directory();

				}
			});
			directory.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));

			run.add(directory);

			JMenuItem terminal = new JMenuItem(STRINGS.getString("current-directory-terminal"));
			terminal.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					terminal();
				}
			});
			terminal.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0));

			run.add(terminal);

			add(run);
		}

		private void initTextMenu() {

			JMenu text = new JMenu(STRINGS.getString("text"));

			JMenu convertCase = new JMenu(STRINGS.getString("convert-case"));

			JMenuItem toUppercase = new JMenuItem(STRINGS.getString("to-upper-case"));
			toUppercase.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					tabs.getSelectedComponent().toUpperCase();
				}
			});
			toUppercase.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_UP,
					KeyEvent.CTRL_DOWN_MASK + KeyEvent.ALT_DOWN_MASK));
			convertCase.add(toUppercase);

			JMenuItem toLowerCase = new JMenuItem(STRINGS.getString("to-lower-case"));
			toLowerCase.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					tabs.getSelectedComponent().toLowerCase();
				}
			});
			toLowerCase.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,
					KeyEvent.CTRL_DOWN_MASK + KeyEvent.ALT_DOWN_MASK));
			convertCase.add(toLowerCase);

			text.add(convertCase);

			JMenu lineOperations = new JMenu(STRINGS.getString("line-operations"));

			JMenuItem selectLine = new JMenuItem(STRINGS.getString("select-line"));
			selectLine.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					tabs.getSelectedComponent().selectLine();
				}
			});
			selectLine.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_DOWN_MASK
					+ KeyEvent.SHIFT_DOWN_MASK));
			lineOperations.add(selectLine);

			JMenuItem selectLineText = new JMenuItem(STRINGS.getString("select-line-text"));
			selectLineText.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					tabs.getSelectedComponent().selectLineText();
				}
			});
			selectLineText.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
					KeyEvent.CTRL_DOWN_MASK + KeyEvent.SHIFT_DOWN_MASK + KeyEvent.ALT_DOWN_MASK));
			lineOperations.add(selectLineText);

			lineOperations.addSeparator();

			JMenuItem moveLineUp = new JMenuItem(STRINGS.getString("move-line-up"));
			moveLineUp.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					tabs.getSelectedComponent().moveLineUp();
				}
			});
			moveLineUp.setAccelerator(KeyStroke
					.getKeyStroke(KeyEvent.VK_UP, KeyEvent.ALT_DOWN_MASK));
			lineOperations.add(moveLineUp);

			JMenuItem moveLineDown = new JMenuItem(STRINGS.getString("move-line-down"));
			moveLineDown.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					tabs.getSelectedComponent().moveLineDown();
				}
			});
			moveLineDown.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,
					KeyEvent.ALT_DOWN_MASK));
			lineOperations.add(moveLineDown);

			lineOperations.addSeparator();

			JMenuItem duplicateLines = new JMenuItem(STRINGS.getString("duplicate-lines"));
			duplicateLines.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					tabs.getSelectedComponent().duplicateLines();
				}
			});
			duplicateLines.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D,
					KeyEvent.CTRL_DOWN_MASK));
			lineOperations.add(duplicateLines);

			JMenuItem joinLines = new JMenuItem(STRINGS.getString("join-lines"));
			joinLines.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {

					String pattern = showInputDialog(STRINGS.getString("enter-pattern-join-lines"),
							STRINGS.getString("enter-join-pattern"));
					if (pattern != null) {
						tabs.getSelectedComponent().joinLines(pattern);
					}
				}
			});
			joinLines
					.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_J, KeyEvent.CTRL_DOWN_MASK));
			lineOperations.add(joinLines);

			text.add(lineOperations);

			add(text);
		}

		private void initViewMenu() {
			JMenu view = new JMenu(STRINGS.getString("view"));

			add(view);
		}

		private void initWindowMenu() {
			JMenu window = new JMenu(STRINGS.getString("window"));

			JMenu resizeWindow = new JMenu(STRINGS.getString("resize-window"));

			JMenuItem growHorizontal = new JMenuItem(STRINGS.getString("grow-window-horizontal"));
			growHorizontal.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					getPreferencesManager().growWindow(PreferencesManager.X_AXIS);
				}
			});
			growHorizontal.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,
					KeyEvent.ALT_DOWN_MASK + KeyEvent.SHIFT_DOWN_MASK));
			resizeWindow.add(growHorizontal);

			JMenuItem growVertical = new JMenuItem(STRINGS.getString("grow-window-vertical"));
			growVertical.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					getPreferencesManager().growWindow(PreferencesManager.Y_AXIS);
				}
			});
			growVertical.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,
					KeyEvent.ALT_DOWN_MASK + KeyEvent.SHIFT_DOWN_MASK));
			resizeWindow.add(growVertical);

			JMenuItem reduceHorizontal = new JMenuItem(
					STRINGS.getString("reduce-window-horizontal"));
			reduceHorizontal.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					getPreferencesManager().reduceWindow(PreferencesManager.X_AXIS);
				}
			});
			reduceHorizontal.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,
					KeyEvent.ALT_DOWN_MASK + KeyEvent.SHIFT_DOWN_MASK));
			resizeWindow.add(reduceHorizontal);

			JMenuItem reduceVertical = new JMenuItem(STRINGS.getString("reduce-window-vertical"));
			reduceVertical.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					getPreferencesManager().reduceWindow(PreferencesManager.Y_AXIS);
				}
			});
			reduceVertical.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_UP,
					KeyEvent.ALT_DOWN_MASK + KeyEvent.SHIFT_DOWN_MASK));
			resizeWindow.add(reduceVertical);

			window.add(resizeWindow);

			add(window);
		}
	}

	/**
	 * Serial version ID.
	 */
	private static final long serialVersionUID = 3220666579206122227L;

	/**
	 * The text area's tabs pane.
	 */
	private final TextAreaTabbedPane tabs = new TextAreaTabbedPane();

	/**
	 * A simple file manager to handle streams and low level file treatment.
	 */
	private final TextDocumentStreamer textDocumentStreamer = new TextDocumentStreamer();

	/**
	 * The dialog for showing find options.
	 */
	private FindDialog findDialog = null;

	private ArrayDeque<String> recentDocumentsPaths = new ArrayDeque<String>(10);

	private JMenu recentDocumentsMenu;

	/**
	 * Initializes MainFrame and loads documents from previous sessions.
	 */
	public MainFrame() {
		loadRecentDocuments();
		initComponents();
		loadPreviousSession();
		updateTitle();
	}

	/**
	 * Initializes MainFrame and try to open files based on the
	 * <code>args</code> , an array of paths. If <code>args.length == 0</code>,
	 * open documents from last session.
	 * 
	 * @param args
	 *            The paths of the files to be open
	 */
	public MainFrame(String... args) {
		this();
		if (args != null)
			// Open command line arguments
			for (int i = 0; i < args.length; i++) {
				if (args[i] != null) {
					open(args[i], true);
				}
			}
	}

	/**
	 * Pop up an {@link AboutDialog}.
	 */
	private void about() {
		new AboutDialog(this).setVisible(true);
	}

	private void addRecentDocumentsPath(String path) {
		if (recentDocumentsPaths.contains(path)) {
			recentDocumentsPaths.remove(path);
		}
		recentDocumentsPaths.addFirst(path);
		updateRecentDocumentsMenu();
	}

	/**
	 * Close the selected document, prompting the user in case of the file is
	 * not saved.
	 * 
	 * @return <code>true</code> if the document was successfully closed
	 */
	private boolean close() {
		TextArea textArea = tabs.getSelectedComponent();
		if (!textArea.getTextDocument().isSaved()) {
			String path = textArea.getTextDocument().getPath() != null ? textArea.getTextDocument()
					.getPath() : textArea.getTextDocument().getName();
			int opcao = JOptionPane.showConfirmDialog(this, STRINGS.getString("save-changes") + " "
					+ path + "?", STRINGS.getString("close"), JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			if (opcao == JOptionPane.YES_OPTION) {
				save(true);
			} else if (opcao == JOptionPane.CANCEL_OPTION) {
				return false;
			}
		}

		if (tabs.getComponents().length == 1) {
			newDocument();
			tabs.remove(0);
		} else {
			tabs.remove(tabs.getSelectedIndex());
		}
		return true;
	}

	/**
	 * Closes all open documents and saves the current session on the "session"
	 * file.
	 * 
	 * @return <code>true</code> if all documents were successfully closed
	 */
	private boolean closeAll() {
		for (int i = tabs.getComponentCount() - 1; i >= 0; i--) {
			tabs.setSelectedIndex(i);
			if (!close()) {
				return false;
			}
		}
		return true;
	}

	private boolean closeAllOthers() {
		int current = tabs.getSelectedIndex();
		for (int i = tabs.getComponentCount() - 1; i >= 0; i--) {
			tabs.setSelectedIndex(i);
			if (tabs.getSelectedIndex() != current) {
				if (!close()) {
					return false;
				}
			}
		}
		return true;
	}

	private boolean closeAllToTheRight() {
		int current = tabs.getSelectedIndex();
		for (int i = tabs.getComponentCount() - 1; i > current; i--) {
			tabs.setSelectedIndex(i);
			if (tabs.getSelectedIndex() != current) {
				if (!close()) {
					return false;
				}
			}
		}
		return true;
	}

	private void delete() {
		int result = JOptionPane.showConfirmDialog(this, STRINGS.getString("delete-file-question"),
				STRINGS.getString("delete-file"), JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE);

		if (result == JOptionPane.YES_OPTION) {
			TextArea textArea = tabs.getSelectedComponent();
			TextDocument doc = textArea.getTextDocument();
			try {
				if (textDocumentStreamer.delete(doc)) {
					close();
				} else {
					getUI().showErrorMessage(this, STRINGS.getString("file-does-not-exists"));
				}
			} catch (IOException e) {
				getUI().showErrorMessage(this, STRINGS.getString("cannot-delete-file"));
				e.printStackTrace();
			}
		}
	}

	private void directory() {
		String path = tabs.getSelectedComponent().getTextDocument().getPath();
		if (path != null) {
			try {
				Desktop.getDesktop().open(Paths.get(path).getParent().toFile());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Closes all open documents and calls {@linkplain Zephyr#shutdown()};
	 */
	private void exit() {
		// Current session
		if (PREFERENCES.rememberCurrentSession()) {
			List<String> paths = new ArrayList<String>();
			for (int i = tabs.getComponentCount() - 1; i >= 0; i--) {
				tabs.setSelectedIndex(i);
				if (tabs.getSelectedComponent().getTextDocument().getPath() != null) {
					paths.add(tabs.getSelectedComponent().getTextDocument().getPath());
				}
			}

			Collections.reverse(paths);

			Zephyr.SESSION.clearOpen();
			Zephyr.SESSION.addOpen(paths.toArray(new String[paths.size()]));
		}

		// Close process
		if (closeAll()) {
			// Recent documents
			Zephyr.SESSION.clearRecents();
			Zephyr.SESSION.addRecent(recentDocumentsPaths.toArray(new String[recentDocumentsPaths
					.size()]));

			PREFERENCES.lineSeparator(getCastedMenuBar().getSelectedLineSeparator());
			PREFERENCES.location(getLocationOnScreen());
			PREFERENCES.dimension(getSize());

			Zephyr.shutdown();
		}
	}

	private void find() {
		if (findDialog == null) {
			findDialog = new FindDialog(this, tabs.getSelectedComponent());
			findDialog.addWindowListener(new WindowListener() {

				@Override
				public void windowActivated(WindowEvent e) {

				}

				@Override
				public void windowClosed(WindowEvent e) {

				}

				@Override
				public void windowClosing(WindowEvent e) {
					findDialog = null;
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
			findDialog.setVisible(true);
		}
	}

	private MenuBar getCastedMenuBar() {
		return (MenuBar) getJMenuBar();
	}

	/**
	 * Initializes all the components in the frame.
	 */
	private void initComponents() {
		initMenuBar();
		initTabs();

		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent arg0) {
				exit();
			}

			@Override
			public void windowLostFocus(WindowEvent e) {
				if (!isAncestorOf(e.getOppositeWindow())) {
					if (PREFERENCES.saveOnFocusLost()) {
						if (PREFERENCES.saveOnFocusLostTarget() == Preferences.CURRENT) {
							System.out.println("cur");
							save(false);
						} else if (PREFERENCES.saveOnFocusLostTarget() == Preferences.ALL) {
							System.out.println("all");
							saveAll(false);
						}
					}
				}
			}

		});

		setIconImage(Toolkit.getDefaultToolkit().getImage(
				this.getClass().getResource(Zephyr.IMAGES + "zephyr-icon32.png")));

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setAlwaysOnTop(PREFERENCES.alwaysOnTop());
		setLocation(PREFERENCES.location());
		setSize(PREFERENCES.dimension());

		newDocument();
	}

	/**
	 * Initializes the {@code menuBar}.
	 */
	private void initMenuBar() {

		setJMenuBar(new MenuBar());
	}

	/**
	 * Initializes the {@code tabs}.
	 */
	private void initTabs() {
		tabs.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				tabChanged();
			}
		});
		tabs.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON2) {
					close();
				} else if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
					newDocument();
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {

			}

			@Override
			public void mouseExited(MouseEvent e) {

			}

			@Override
			public void mousePressed(MouseEvent e) {

			}

			@Override
			public void mouseReleased(MouseEvent e) {

			}
		});
		getContentPane().add(tabs);
	}

	/**
	 * This methods checks if a document is already open in the application,
	 * based on file's path.
	 * 
	 * @param path
	 *            The file path to be checked
	 * @return the index of the text area on the <code>tabs</code> or -1, case
	 *         not open
	 */
	@SuppressWarnings("unchecked")
	private int isOpen(String path) {
		Component[] components = tabs.getComponents();

		for (int i = 0; i < components.length; i++) {
			String path1 = ((GenericScrollPane<TextArea>) components[i]).getComponent()
					.getTextDocument().getPath();
			if (path.equals(path1)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Open the documents from previous sessions, based on the "session" file.
	 */
	private void loadPreviousSession() {
		for (String path : Zephyr.SESSION.getOpen()) {
			open(path, false);
		}
	}

	private void loadRecentDocuments() {
		for (String path : Zephyr.SESSION.getRecents()) {
			recentDocumentsPaths.addLast(path);
		}
	}

	/**
	 * Creates a new tab with a blank {@link TextArea}.
	 */
	private void newDocument() {
		tabs.addTab();
		tabs.setSelectedIndex(tabs.getComponentCount() - 1);
	}

	private void terminal() {
		// TODO
	}

	/**
	 * Shows the {@code fileChooser} and creates a new tab and a document with
	 * the content of the chosen file. The content is copied to the memory (the
	 * stream don't remain open and other applications can use the file).
	 */
	private void open() {
		File[] files = null;
		FileChooser fileChooser = getUI().getFileChooser();
		fileChooser.setMultiSelectionEnabled(true);
		try {
			if (fileChooser.open(this) == JFileChooser.APPROVE_OPTION) {
				files = fileChooser.getSelectedFiles();

				for (File file2 : files) {
					open(file2.getPath(), true);
				}
			}
		} catch (FileNotFoundException e) {
			Zephyr.getUI().showErrorMessage(this,
					STRINGS.getString("cannot-find") + " " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Open a file and add the content to a new {@link TextArea}. The main
	 * difference between {@link #open()} and this is that this method receive,
	 * by passing an argument, the path of the file to be open, instead of
	 * showing the {@code fileChooser}. This method doesn't show any kind of
	 * dialog, even in case of an error.
	 * 
	 * @param path
	 *            The path of the file to be open
	 * @param popupErrorMessages
	 *            Case <code>true</code>, it shows error messages for I/O
	 *            exceptions, etc. Case <code>false</code>, it runs on
	 *            "silent mode"
	 */
	private void open(String path, boolean popupErrorMessages) {
		// Check if the document is already open
		int index;
		if ((index = isOpen(path)) != -1) {
			tabs.setSelectedIndex(index);
			return;
		}

		try {
			TextDocument doc = textDocumentStreamer.read(path);
			path = doc.getPath();
			tabs.addTab(doc);
			if (tabs.getSelectedComponent().getTextDocument().isNewDocument()) {
				tabs.remove(tabs.getSelectedIndex());
			}
			tabs.setSelectedIndex(tabs.getComponentCount() - 1);
			updateTitle();
			addRecentDocumentsPath(path);
		} catch (FileNotFoundException e) {
			if (popupErrorMessages) {
				Zephyr.getUI().showErrorMessage(this, STRINGS.getString("cant-find") + " " + path);
			}
			e.printStackTrace();
		} catch (IOException e) {
			if (popupErrorMessages) {
				Zephyr.getUI().showErrorMessage(this,
						STRINGS.getString("not-able-to-open") + " " + path);
			}
			e.printStackTrace();
		}
	}

	/**
	 * Shows the preferences dialog.
	 */
	private void preferences() {
		new PreferencesDialog(this).setVisible(true);
	}

	/**
	 * Saves the current document. If file is not {@code savedOnDisk}, prompts
	 * the {@code fileChooser} to select the destination file. If it's already
	 * on disk, it only overwrites it.
	 * 
	 * @param saveAs
	 *            indicates whether the user should be prompted to with a
	 *            "save as" dialog in case the document is not saved on disk
	 * @return <code>true</code> in case of the document was successfully saved
	 */
	private boolean save(boolean saveAs) {
		TextArea textArea = tabs.getSelectedComponent();
		TextDocument doc = textArea.getTextDocument();
		if (!doc.isSavedOnDisk()) {
			if (!saveAs) {
				return false;
			}
			return saveAs();
		} else {
			try {
				textDocumentStreamer.write(doc);
				doc.setSaved(true);
				doc.setSavedOnDisk(true);
				tabs.setTitleStarAt(tabs.getSelectedIndex(), false);
				return true;
			} catch (IOException e) {
				String path = doc.getPath() != null ? doc.getPath() : doc.getName();
				Zephyr.getUI().showErrorMessage(this,
						STRINGS.getString("occurred-an-error-saving") + " " + path);
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * This method class save methods to all open documents. If
	 * {@link ActionListener} save method returns false, saveAll returns too.
	 * 
	 * @param saveAs
	 *            indicates whether the user should be prompted to with a
	 *            "save as" dialog in case the document is not saved on disk
	 */
	private void saveAll(boolean saveAs) {
		Component[] components = tabs.getComponents();
		for (int i = 0; i < components.length; i++) {
			tabs.setSelectedIndex(i);
			if (!save(saveAs)) {
				return;
			}
		}
	}

	private void rename() {
		String path = tabs.getSelectedComponent().getTextDocument().getPath();
		boolean success = saveAs();
		String newPath = tabs.getSelectedComponent().getTextDocument().getPath();
		if (success && !newPath.equals(path)) {
			try {
				new FileManager().delete(Paths.get(path));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Saves the current document. It prompts the {@code fileChooser} to select
	 * the destination file.
	 * 
	 * @return <code>true</code> in case of the document was successfully saved
	 */
	private boolean saveAs() {
		TextArea textArea = tabs.getSelectedComponent();
		FileChooser fileChooser = getUI().getFileChooser();
		if (fileChooser.save(this) == JFileChooser.APPROVE_OPTION) {
			File file = null;
			try {
				file = fileChooser.getSelectedFile();
				// Flushes the content
				TextDocument doc = textArea.getTextDocument();
				doc.setName(file.getName());
				doc.setPath(file.getPath());
				textDocumentStreamer.write(doc);
				doc.setSaved(true);
				doc.setSavedOnDisk(true);
				updateTitle();
				tabs.setTitleStarAt(tabs.getSelectedIndex(), false);
				addRecentDocumentsPath(file.getPath());
				return true;
			} catch (IOException e) {
				String path = textArea.getTextDocument().getPath() != null ? textArea
						.getTextDocument().getPath() : textArea.getTextDocument().getName();
				Zephyr.getUI().showErrorMessage(this,
						STRINGS.getString("occurred-an-error-saving") + " " + path);
				e.printStackTrace();
			}
		}
		return false;
	}

	private String showInputDialog(String message, String title) {
		return JOptionPane.showInputDialog(this, message, title, JOptionPane.PLAIN_MESSAGE);
	}

	/**
	 * Changes the title of MainFrame based on the current document.
	 */
	private void tabChanged() {
		TextArea textArea = tabs.getSelectedComponent();
		updateTitle();
		// Workaround
		if (!textArea.getTextDocument().isSaved()) {
			tabs.setTitleStarAt(tabs.getSelectedIndex(), true);
		}
	}

	/**
	 * Update the recent documents menu item, on the menu bar. The documents
	 * paths are retrieved from <code>recentDocumentsPaths</code>.
	 */
	private void updateRecentDocumentsMenu() {
		int count = recentDocumentsMenu.getMenuComponentCount();
		for (int i = 3; i < count; i++) {
			recentDocumentsMenu.remove(3);
		}
		if (recentDocumentsPaths.isEmpty()) {
			recentDocumentsMenu.setVisible(false);
		} else {
			recentDocumentsMenu.setVisible(true);

			for (String path : recentDocumentsPaths) {
				final JMenuItem pathMenuItem = new JMenuItem(path);
				pathMenuItem.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						open(pathMenuItem.getText(), true);
					}
				});

				recentDocumentsMenu.add(pathMenuItem);
			}
		}
	}

	/**
	 * Updates the frame title based on selected document, on the format "
	 * document name - document path - Zephyr".
	 */
	private void updateTitle() {
		String docName = tabs.getSelectedComponent().getTextDocument().getName();
		String docPath = tabs.getSelectedComponent().getTextDocument().getPath();
		if (docPath == null) {
			setTitle(docName + " - Zephyr");
		} else {
			setTitle(docName
					+ " - "
					+ docPath.substring(0,
							docPath.lastIndexOf(System.getProperty("file.separator")) + 1)
					+ " - Zephyr");
		}

		try {
			tabs.setTitleAt(tabs.getSelectedIndex(), docName);
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		}
	}
}
