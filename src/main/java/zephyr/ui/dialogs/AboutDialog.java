package zephyr.ui.dialogs;

import static zephyr.Zephyr.STRINGS;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.miginfocom.layout.CC;
import net.miginfocom.swing.MigLayout;
import zephyr.Zephyr;

/**
 * A dialog containing information about Zephyr Text Editor.
 * 
 * @author Fuad Saud
 * 
 */
public class AboutDialog extends JDialog {

	/**
	 * Serial version ID.
	 */
	private static final long serialVersionUID = -5597623599632133128L;

	/**
	 * The license's dialog.
	 */
	private final JDialog license;

	/**
	 * Creates a dialog with information about the software.
	 * 
	 * @param owner
	 *            the Frame from which the dialog is displayed
	 */
	public AboutDialog(Frame owner) {
		super(owner, STRINGS.getString("about-zephyr"), true);

		setLayout(new MigLayout("wrap 1", "[center][right][left][c]", "[top][center][b]"));

		license = new JDialog(this, "Zephyr Text Editor " + Zephyr.VERSION + " "
				+ STRINGS.getString("license"));
		// license.setLocationRelativeTo(owner);

		initLabels();
		initButton();
		initLicense();

		pack();
		Dimension d = getSize();
		setSize(d.width - 20, d.height);
		setResizable(false);
		setLocationRelativeTo(owner);
	}

	/**
	 * Calls the system default browser to open the Zephyr home page.
	 */
	private void browserCall() {
		try {
			Desktop.getDesktop().browse(new URI(Zephyr.HOME_PAGE));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initializes the 'license' button.
	 */
	private void initButton() {
		JButton license = new JButton(STRINGS.getString("license"));
		license.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				AboutDialog.this.license.setVisible(true);

			}
		});
		add(license, new CC().gapTop("10").alignX("center"));
	}

	/**
	 * Initializes the labels on the dialog.
	 */
	private void initLabels() {
		add(new JLabel(new ImageIcon(Toolkit.getDefaultToolkit().getImage(
				this.getClass().getResource(Zephyr.IMAGES + "zephyr-icon256.png")))),
				"align center");

		JLabel zephyr = new JLabel("Zephyr Text Editor");
		zephyr.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
		add(zephyr, "align center");

		JLabel version = new JLabel(Zephyr.VERSION);
		version.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
		add(version, "align center");

		add(new JLabel(STRINGS.getString("copyleft")), "align center");

		JLabel url = new JLabel(Zephyr.HOME_PAGE);
		url.setForeground(Color.BLUE);
		url.setCursor(new Cursor(Cursor.HAND_CURSOR));
		url.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				browserCall();
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
		add(url, "align center");

		JLabel binaryWarning = new JLabel(STRINGS.getString("binary-warning"));
		binaryWarning.setForeground(Color.RED);
		add(binaryWarning, "align center");

		JLabel settingsWarning = new JLabel(STRINGS.getString("settings-warning"));
		settingsWarning.setForeground(new Color(0xA5, 0x05, 0x67));
		add(settingsWarning);
	}

	/**
	 * Initializes the text area.
	 */
	private void initLicense() {
		JTextArea text = new JTextArea(Zephyr.LICENSE);
		text.setFont(new Font("Courier New", Font.PLAIN, 14));
		text.setWrapStyleWord(true);
		text.setEditable(false);
		text.setTabSize(4);
		JScrollPane scroll = new JScrollPane();
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.getViewport().setBorder(null);
		scroll.getViewport().add(text);
		license.add(scroll);
		license.setResizable(true);
		license.setSize(754, 800);
	}
}
