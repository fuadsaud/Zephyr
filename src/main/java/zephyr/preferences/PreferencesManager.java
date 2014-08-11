package zephyr.preferences;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import zephyr.ui.ZephyrUI;

public class PreferencesManager {

	public static final byte X_AXIS = 1;
	public static final byte Y_AXIS = 2;

	private final ZephyrUI ui;

	private final JFrame mainFrame;

	public PreferencesManager(ZephyrUI ui) {
		this.ui = ui;
		this.mainFrame = this.ui.getMainFrame();
	}

	public void growWindow(byte axis) {
		Dimension d = mainFrame.getSize();
		switch (axis) {
		case X_AXIS:
			mainFrame.setSize(d.width + 100, d.height);
			break;
		case Y_AXIS:
			mainFrame.setSize(d.width, d.height + 100);
			break;
		}
	}

	public void reduceWindow(byte axis) {
		Dimension d = mainFrame.getSize();
		switch (axis) {
		case X_AXIS:
			mainFrame.setSize(d.width - 100, d.height);
			break;
		case Y_AXIS:
			mainFrame.setSize(d.width, d.height - 100);
			break;
		}
	}

	public void setAlwaysOnTop(boolean alwaysOnTop) {
		mainFrame.setAlwaysOnTop(alwaysOnTop);
	}

	public void setLookAndFeel(String className) {
		try {
			UIManager.setLookAndFeel(className);
			SwingUtilities.updateComponentTreeUI(mainFrame);
			SwingUtilities.updateComponentTreeUI(ui.getFileChooser());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
