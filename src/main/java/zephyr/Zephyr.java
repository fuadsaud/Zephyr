package zephyr;

import java.awt.EventQueue;
import java.io.BufferedInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import zephyr.file.FileManager;
import zephyr.preferences.Preferences;
import zephyr.preferences.PreferencesManager;
import zephyr.serialization.Serializer;
import zephyr.ui.MainFrame;
import zephyr.ui.SplashScreen;
import zephyr.ui.ZephyrUI;
import flexjson.JSONException;

/**
 * Bootstraps the application.
 * 
 * @author Fuad Saud
 * 
 */
public class Zephyr {

	/**
	 * Zephyr's version.
	 */
	public static final String VERSION = "0.1.8 Alpha";

	/**
	 * The project home page.
	 */
	public static final String HOME_PAGE = "http://twitter.com/fuadsaud";

	/**
	 * The product's license, including third party software's licenses.
	 */
	public static final String LICENSE;

	// Preferences

	/**
	 * User preferences. The data is loaded from {@linkplain #SETTINGS}.
	 */
	public static final Preferences PREFERENCES;

	public static final Session SESSION;

	/**
	 * {@link ResourceBundle} containing GUI messages.
	 */
	public static final ResourceBundle STRINGS;

	// Paths
	/**
	 * The resources package path.
	 */
	public static final String RESOURCES = "/";

	/**
	 * The images package path.
	 */
	public static final String IMAGES = RESOURCES + "images/";

	/**
	 * The document files package path.
	 */
	public static final String DOCUMENTS = RESOURCES + "documents/";

	public static final Path SETTINGS_DIR = Paths.get("./settings/"
			+ System.getProperty("user.name"));
	/**
	 * The settings file name.
	 */
	public static final Path PREFERENCES_FILE = SETTINGS_DIR.resolve("preferences.json");

	public static final Path SESSION_FILE = SETTINGS_DIR.resolve("session.json");

	/**
	 * The splash screen.
	 */
	private static final SplashScreen SPLASH_SCREEN;

	/**
	 * The main window.
	 */
	private static MainFrame ui;

	private static ZephyrUI zui;

	private static PreferencesManager preferencesManager;

	/**
	 * A flag to indicates whether the application end loading (for disabling
	 * splash screen).
	 */
	private static boolean endLoading = false;

	static {
		createDirectories();

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		PREFERENCES = loadPreferences();

		SESSION = loadSession();

		STRINGS = ResourceBundle.getBundle("languages.strings",
				Locale.forLanguageTag(PREFERENCES.getLanguage()));

		LICENSE = loadLicense();

		try {
			UIManager.setLookAndFeel(PREFERENCES.lookAndFeel());
		} catch (Exception e) {
			e.printStackTrace();
		}

		SPLASH_SCREEN = new SplashScreen();
	}

	public static PreferencesManager getPreferencesManager() {
		return preferencesManager;
	}

	private static void createDirectories() {
		try {
			Files.createDirectories(SETTINGS_DIR);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * The main entry point for the application.
	 * 
	 * @param args
	 *            Command line arguments.
	 */
	public static void main(final String[] args) {
		EventQueue.invokeLater(new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					if (PREFERENCES.showSplashScreen()) {
						// Showing the splash screen
						Thread splashScreenThread = new Thread(new Runnable() {
							@Override
							public void run() {
								showSplashScreen();
							}
						});
						splashScreenThread.start();
					}

					zui = new ZephyrUI(args);

					preferencesManager = new PreferencesManager(zui);

					endLoading = true;

					getUI().getMainFrame().setVisible(true);
				} catch (Throwable t) {
					t.printStackTrace();
					System.exit(1);
				}
			}
		}));

	}

	/**
	 * Stores properties files and exits.
	 */
	public static void shutdown() {
		try {
			createDirectories();
			Preferences.store(PREFERENCES, new FileWriter(PREFERENCES_FILE.toFile()));
			new FileManager().write(new Serializer<Session>().toJSON(SESSION, Serializer.DEEP),
					SESSION_FILE);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

	/**
	 * Loads the license`s text from an internal file.
	 * 
	 * @return the license`s text
	 */
	private static String loadLicense() {
		// Reads up the license
		BufferedInputStream bis = new BufferedInputStream(
				Zephyr.class.getResourceAsStream(DOCUMENTS + "License"));
		Scanner scanner = new Scanner(bis);
		StringBuilder builder = new StringBuilder();

		while (scanner.hasNextLine()) {
			builder.append(scanner.nextLine() + "\n");
		}

		return builder.toString();
	}

	private static Preferences loadPreferences() {
		try {
			return Preferences.load(PREFERENCES_FILE.toFile());
		} catch (JSONException e) {
			e.printStackTrace();
			if (getUI()
					.showConfirmMessage(ui,
							"There was an error loading preferences file. Do you want to reset preferences?") == JOptionPane.YES_OPTION) {
				return new Preferences();
			}
			System.exit(0);
			return null;
		} catch (IOException e1) {
			return new Preferences();
		}
	}

	private static Session loadSession() {

		try {
			return new Serializer<Session>().fromJSON(new FileManager().read(SESSION_FILE));
		} catch (Exception e) {
			e.printStackTrace();
			return new Session();
		}
	}

	/**
	 * Shows the splash screen until the loading is finished.
	 * 
	 */
	private static void showSplashScreen() {

		SPLASH_SCREEN.setVisible(true);
		int i = 0;
		while (!endLoading)
			while (i++ <= 100) {
				try {
					Thread.sleep(0);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				SPLASH_SCREEN.setProgressBarValue(i++);

			}
		SPLASH_SCREEN.setVisible(false);
	}

	private Zephyr() {
	}

	public static ZephyrUI getUI() {
		return zui;
	}
}
