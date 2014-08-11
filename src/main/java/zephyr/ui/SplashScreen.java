package zephyr.ui;

import static zephyr.Zephyr.STRINGS;

import java.awt.BorderLayout;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import zephyr.Zephyr;

public class SplashScreen extends JFrame {
	/**
	 * Serial version ID.
	 */
	private static final long serialVersionUID = -6625756157845782324L;

	private JPanel panel;

	private JProgressBar progressBar;

	/**
	 * Sets up the splash screen.
	 */
	public SplashScreen() {
		super("Zephyr");
		panel = new JPanel(new BorderLayout());

		initImage();
		initProgressBar();

		add(panel);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setIconImage(Toolkit.getDefaultToolkit().getImage(
				this.getClass().getResource(Zephyr.IMAGES + "zephyr-icon256.png")));
		setUndecorated(true);
		setResizable(false);
		setLocation(340, 295);
		setSize(550, 265);
	}

	/**
	 * Initializes the splash screen image.
	 */
	private void initImage() {
		panel.add(
				new JLabel(new ImageIcon(Toolkit.getDefaultToolkit()
						.getImage(
								this.getClass().getResource(
										Zephyr.IMAGES + "zephyr-splash-screen-old.png")))),
				BorderLayout.CENTER);
	}

	/**
	 * Initializes the progress bar.
	 */
	private void initProgressBar() {
		progressBar = new JProgressBar(0, 100);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		progressBar.setString(STRINGS.getString("loading") + "...");

		panel.add(progressBar, BorderLayout.SOUTH);
	}

	/**
	 * Sets the <code>progressBar</code> value.
	 * 
	 * @param value
	 *            The value to be set.
	 */
	public void setProgressBarValue(int value) {
		progressBar.setValue(value);
		progressBar.repaint();
	}
}
