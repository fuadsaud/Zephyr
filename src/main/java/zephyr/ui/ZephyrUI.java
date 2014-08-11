package zephyr.ui;

import static zephyr.Zephyr.STRINGS;

import java.awt.Component;
import java.awt.Frame;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import zephyr.ui.components.FileChooser;
import zephyr.ui.dialogs.AboutDialog;
import zephyr.ui.dialogs.PreferencesDialog;

public class ZephyrUI {

	private final JFrame mainFrame;

	private FileChooser fileChooser = new FileChooser();

	public ZephyrUI(String... args) {
		mainFrame = new MainFrame(args);
	}

	public JDialog getAboutDialog(Frame owner) {
		return new AboutDialog(owner);
	}

	public FileChooser getFileChooser() {
		return fileChooser;
	}

	public JFrame getMainFrame() {
		return mainFrame;
	}

	public JDialog getPreferencesDialog(Frame owner) {
		return new PreferencesDialog(owner);
	}

	public JFrame getSplashScreen() {
		return new SplashScreen();
	}

	/**
	 * Displays a message with Yes and No options via {@link JOptionPane}.
	 * 
	 * @param owner
	 *            the component that owns the dialog
	 * @param message
	 *            the message to display
	 */
	public int showConfirmMessage(Component owner, String message) {
		return JOptionPane.showConfirmDialog(owner, message, null, JOptionPane.YES_NO_OPTION);
	}

	/**
	 * Displays an error message via {@link JOptionPane}.
	 * 
	 * @param owner
	 *            the component that owns the dialog
	 * @param message
	 *            the message to display
	 */
	public void showErrorMessage(Component owner, String message) {
		JOptionPane.showMessageDialog(owner, message, STRINGS.getString("error"),
				JOptionPane.ERROR_MESSAGE);
	}
}
