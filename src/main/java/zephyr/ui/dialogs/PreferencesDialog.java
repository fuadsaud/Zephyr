package zephyr.ui.dialogs;

import static zephyr.Zephyr.PREFERENCES;
import static zephyr.Zephyr.STRINGS;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.MaskFormatter;

import net.miginfocom.swing.MigLayout;
import zephyr.Zephyr;
import zephyr.preferences.Preferences;

/**
 * A dialog to set simple preferences.
 * 
 * @author Fuad Saud
 * 
 */

public class PreferencesDialog extends JDialog {

	/**
	 * Serial version ID
	 */
	private static final long serialVersionUID = 3252739958878696995L;

	/**
	 * A map containing LaF's classes names, based on the short name.
	 */
	private static final Map<String, String> lafs = new HashMap<String, String>();

	private static final Map<String, String> langs = new HashMap<String, String>();

	private static int lastOpenTab = 0;

	/**
	 * A {@link JTabbedPane} to organize the dialog.
	 */
	private JTabbedPane tabs;

	/**
	 * The panel containing appearance settings.
	 */
	private JPanel appearencePanel;

	/**
	 * The panel containing fonts settings.
	 */
	private JPanel editorPanel;

	/**
	 * The panel containing miscellaneous settings.
	 */
	private JPanel miscPanel;

	/**
	 * A combo box with the LaF options.
	 */
	private JComboBox<String> lafCombo;

	/**
	 * A combo box with language options.
	 */
	private JComboBox<String> langCombo;

	/**
	 * The {@link JTabbedPane} to separate settings by content.
	 */

	/**
	 * A {@link JSpinner} to set font size.
	 */
	private JSpinner fontSizeSpinner;

	private JComboBox<Integer> tabSizeCombo;

	private JFormattedTextField backgroundColorTextField;

	/**
	 * A {@link JCheckBox} to set remember current session preference.
	 */
	private JCheckBox rememberSessionCheckBox;

	private JCheckBox alwaysOnTopCheckBox;

	private JCheckBox showSplashScreenCheckBox;

	private JCheckBox lineWrapCheckBox;

	private JCheckBox saveOnFocusLostCheckBox;

	private JRadioButton saveOnFocusLostTargetAll;

	private JRadioButton saveOnFocusLostTargetCurrent;

	static {
		lafs.put("Metal", "javax.swing.plaf.metal.MetalLookAndFeel");
		lafs.put("Nimbus", "javax.swing.plaf.nimbus.NimbusLookAndFeel");
		lafs.put("System", UIManager.getSystemLookAndFeelClassName());

		langs.put("English", "en-US");
		langs.put("Português do Brasil", "pt-BR");
	}

	/**
	 * Initializes the dialog.
	 * 
	 * @param owner
	 *            the Frame from which the dialog is displayed
	 */
	public PreferencesDialog(Frame owner) {
		super(owner, STRINGS.getString("preferences"), true);

		initComponents();

		setSize(600, 300);
		pack();
		setResizable(false);
		setLocationRelativeTo(owner);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosed(WindowEvent e) {
				lastOpenTab = tabs.getSelectedIndex();
			}

		});
	}

	/**
	 * Cancel action.
	 */
	private void cancel() {
		dispose();
	}

	/**
	 * Initialize appearance settings panel.
	 */
	private void initAppearance() {
		JLabel lafLabel = new JLabel(STRINGS.getString("choose-laf"));
		lafLabel.setToolTipText(STRINGS.getString("choose-laf-tooltip"));
		lafCombo = new JComboBox<String>(new String[] { "Metal", "Nimbus", "System" });

		String name = UIManager.getLookAndFeel().getClass().getSimpleName()
				.replace("LookAndFeel", "");
		if (name.equals("Windows")) {
			name = "System";
		}
		lafCombo.setToolTipText(STRINGS.getString("choose-laf-tooltip"));
		lafCombo.setSelectedItem(name);
		appearencePanel.add(lafLabel);
		appearencePanel.add(lafCombo);

		JLabel langLabel = new JLabel(STRINGS.getString("choose-language"));
		langLabel.setToolTipText(STRINGS.getString("choose-language-tooltip"));
		langCombo = new JComboBox<String>(new String[] { "English", "Português do Brasil" });
		langCombo.setSelectedItem(STRINGS.getString("language"));
		langCombo.setToolTipText(STRINGS.getString("choose-language-tooltip"));
		appearencePanel.add(langLabel);
		appearencePanel.add(langCombo);
	}

	/**
	 * Initializes dialog's buttons.
	 */
	private void initButtons() {
		JPanel buttonsPanel = new JPanel(new MigLayout());

		buttonsPanel.add(new JLabel(STRINGS.getString("preferences-changes")), "skip, gap 20px");

		JButton ok = new JButton(STRINGS.getString("ok"));
		ok.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ok();
			}
		});
		buttonsPanel.add(ok, "newline push, skip 3, split, align right");

		JButton cancel = new JButton(STRINGS.getString("cancel"));
		cancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				cancel();
			}
		});

		buttonsPanel.add(cancel);

		add(buttonsPanel, BorderLayout.SOUTH);
	}

	private void initComponents() {
		initTabs();

		initAppearance();
		initEditor();
		initMisc();
		initButtons();
	}

	/**
	 * Initializes the editor's settings panel.
	 */
	private void initEditor() {
		JLabel fontSizeLabel = new JLabel(STRINGS.getString("font-size"));
		fontSizeLabel.setToolTipText(STRINGS.getString("font-size-tooltip"));
		fontSizeSpinner = new JSpinner(new SpinnerNumberModel(PREFERENCES.fontSize().intValue(), 8,
				96, 1));
		fontSizeSpinner.setToolTipText(STRINGS.getString("font-size-tooltip"));
		editorPanel.add(fontSizeLabel);
		editorPanel.add(fontSizeSpinner);

		JLabel tabSizeLabel = new JLabel(STRINGS.getString("tab-size"));
		tabSizeLabel.setToolTipText(STRINGS.getString("tab-size-tooltip"));
		tabSizeCombo = new JComboBox<Integer>(new Integer[] { 2, 3, 4, 8 });
		tabSizeCombo.setSelectedItem(PREFERENCES.tabSize());
		tabSizeCombo.setToolTipText(STRINGS.getString("tab-size-tooltip"));
		editorPanel.add(tabSizeLabel);
		editorPanel.add(tabSizeCombo);

		lineWrapCheckBox = new JCheckBox(STRINGS.getString("line-wrap"));
		lineWrapCheckBox.setToolTipText(STRINGS.getString("line-wrap-tooltip"));
		lineWrapCheckBox.setSelected(PREFERENCES.lineWrap());
		lineWrapCheckBox.setToolTipText(STRINGS.getString("line-wrap-tooltip"));
		editorPanel.add(lineWrapCheckBox, "wrap");

		MaskFormatter mask = null;
		try {
			mask = new MaskFormatter("HHHHHH");
		} catch (ParseException e) {
			e.printStackTrace();
		}

		JLabel backgroundColorLabel = new JLabel(STRINGS.getString("background-color"));
		backgroundColorLabel.setToolTipText(STRINGS.getString("background-color-tooltip"));
		backgroundColorTextField = new JFormattedTextField(mask);
		backgroundColorTextField.setText(Integer
				.toHexString(PREFERENCES.backgroundColor().getRGB()));
		backgroundColorTextField.setToolTipText(STRINGS.getString("background-color-tooltip"));
		editorPanel.add(backgroundColorLabel);
		editorPanel.add(backgroundColorTextField, "wrap");
	}

	/**
	 * Initializes miscellaneous settings.
	 */
	private void initMisc() {
		rememberSessionCheckBox = new JCheckBox(STRINGS.getString("remember-current-session"));
		rememberSessionCheckBox.setToolTipText(STRINGS
				.getString("remember-current-session-tooltip"));
		rememberSessionCheckBox.setSelected(PREFERENCES.rememberCurrentSession());
		miscPanel.add(rememberSessionCheckBox);

		alwaysOnTopCheckBox = new JCheckBox(STRINGS.getString("always-on-top"));
		alwaysOnTopCheckBox.setToolTipText(STRINGS.getString("always-on-top-tooltip"));
		alwaysOnTopCheckBox.setSelected(PREFERENCES.alwaysOnTop());
		miscPanel.add(alwaysOnTopCheckBox);

		showSplashScreenCheckBox = new JCheckBox(STRINGS.getString("show-splash-screen"));
		showSplashScreenCheckBox.setToolTipText(STRINGS.getString("show-splash-screen-tooltip"));
		showSplashScreenCheckBox.setSelected(PREFERENCES.showSplashScreen());
		miscPanel.add(showSplashScreenCheckBox);

		saveOnFocusLostCheckBox = new JCheckBox(STRINGS.getString("save-on-focus-lost"));
		saveOnFocusLostCheckBox.setToolTipText(STRINGS.getString("save-on-focus-lost-tooltip"));
		saveOnFocusLostCheckBox.setSelected(PREFERENCES.saveOnFocusLost());
		saveOnFocusLostCheckBox.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				if (saveOnFocusLostCheckBox.isSelected()) {
					saveOnFocusLostTargetCurrent.setEnabled(true);
					saveOnFocusLostTargetAll.setEnabled(true);
				} else {
					saveOnFocusLostTargetCurrent.setEnabled(false);
					saveOnFocusLostTargetAll.setEnabled(false);
				}
			}
		});
		miscPanel.add(saveOnFocusLostCheckBox);

		saveOnFocusLostTargetCurrent = new JRadioButton(
				STRINGS.getString("save-on-focus-lost-target-current"));
		saveOnFocusLostTargetCurrent.setToolTipText(STRINGS
				.getString("save-on-focus-lost-target-current-tooltip"));
		saveOnFocusLostTargetCurrent
				.setSelected(PREFERENCES.saveOnFocusLostTarget() == Preferences.CURRENT);
		miscPanel.add(saveOnFocusLostTargetCurrent);

		saveOnFocusLostTargetAll = new JRadioButton(
				STRINGS.getString("save-on-focus-lost-target-all"));
		saveOnFocusLostTargetAll.setToolTipText(STRINGS
				.getString("save-on-focus-lost-target-all-tooltip"));
		saveOnFocusLostTargetAll
				.setSelected(PREFERENCES.saveOnFocusLostTarget() == Preferences.ALL);
		miscPanel.add(saveOnFocusLostTargetAll);

		if (saveOnFocusLostCheckBox.isSelected()) {
			saveOnFocusLostTargetCurrent.setEnabled(true);
			saveOnFocusLostTargetAll.setEnabled(true);
		} else {
			saveOnFocusLostTargetCurrent.setEnabled(false);
			saveOnFocusLostTargetAll.setEnabled(false);
		}

		ButtonGroup saveOnFocusLostTarget = new ButtonGroup();
		saveOnFocusLostTarget.add(saveOnFocusLostTargetCurrent);
		saveOnFocusLostTarget.add(saveOnFocusLostTargetAll);

	}

	/**
	 * Initializes the tab's panel.
	 */
	private void initTabs() {
		tabs = new JTabbedPane();

		appearencePanel = new JPanel(new MigLayout("wrap 2, gap 7px"));
		tabs.addTab(STRINGS.getString("appearance-settings"), appearencePanel);

		editorPanel = new JPanel(new MigLayout("wrap 2, gap 7px"));
		tabs.addTab(STRINGS.getString("editor-settings"), editorPanel);

		miscPanel = new JPanel(new MigLayout("wrap 1, gap 7px"));
		tabs.addTab(STRINGS.getString("misc-settings"), miscPanel);
		add(tabs, BorderLayout.CENTER);

		tabs.setSelectedIndex(lastOpenTab);
	}

	/**
	 * OK action.
	 */
	private void ok() {
		upadtePreferences();
		updateRuntimePreferences();
		dispose();
	}

	private void updateRuntimePreferences() {
		Zephyr.getPreferencesManager().setAlwaysOnTop(alwaysOnTopCheckBox.isSelected());
		Zephyr.getPreferencesManager().setLookAndFeel(lafs.get(lafCombo.getSelectedItem()));
	}

	/**
	 * Updates the {@linkplain Zephyr#PREFERENCES}, based on the changes made.
	 */
	private void upadtePreferences() {
		PREFERENCES.lookAndFeel(lafs.get(lafCombo.getSelectedItem()));
		PREFERENCES.language(langs.get(langCombo.getSelectedItem()));

		PREFERENCES.fontSize(Integer.valueOf(fontSizeSpinner.getValue().toString()));
		PREFERENCES.tabSize(Integer.valueOf(tabSizeCombo.getSelectedItem().toString()));
		PREFERENCES.lineWrap(lineWrapCheckBox.isSelected());
		PREFERENCES.backgroundColor(Color.decode('#' + backgroundColorTextField.getText()));

		PREFERENCES.rememberCurrentSession(rememberSessionCheckBox.isSelected());
		PREFERENCES.alwaysOnTop(alwaysOnTopCheckBox.isSelected());
		PREFERENCES.showSplashScreen(showSplashScreenCheckBox.isSelected());
		PREFERENCES.saveOnFocusLost(saveOnFocusLostCheckBox.isSelected());
		PREFERENCES
				.saveOnFocusLostTarget(saveOnFocusLostTargetCurrent.isSelected() ? Preferences.CURRENT
						: Preferences.ALL);
	}
}
