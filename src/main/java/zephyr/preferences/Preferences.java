package zephyr.preferences;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;

import zephyr.file.FileManager;
import zephyr.serialization.Serializer;

public class Preferences implements Serializable {

	/**
	 * Serial version ID.
	 */
	public static final long serialVersionUID = 4501298722751960692L;

	public static final int CURRENT = 0;

	public static final int ALL = 1;

	// Defaults
	public static final String LOOK_AND_FEEL = "javax.swing.plaf.metal.MetalLookAndFeel";

	public static final String LANGUAGE = "en-US";

	public static final String LINE_SEPARATOR = System.lineSeparator();

	public static final int FONT_SIZE = 15;

	public static final int TAB_SIZE = 4;

	public static final boolean LINE_WRAP = true;

	public static final Color BACKGROUND_COLOR = Color.WHITE;

	public static final boolean REMEMBER_CURRENT_SESSION = true;

	public static final boolean ALWAYS_ON_TOP = false;

	public static final boolean SHOW_SPLASH_SCREEN = true;

	public static final boolean SAVE_ON_FOCUS_LOST = false;

	public static final int SAVE_ON_FOCUS_LOST_TARGET = CURRENT;

	public static final Dimension DIMENSION = new Dimension(480, 600);

	public static final Point LOCATION = new Point(250, 150);

	public static Preferences load(File file) throws IOException {
		return new Serializer<Preferences>().fromJSON(new FileManager().read(file.toPath()));
	}

	public static void store(Preferences p, Writer writer) throws IOException {
		// String comment =
		// "//Zephyr general settings\n//Edit this file carefully. Parsing errors can halt system\n\n";
		new FileManager().write(new Serializer<Preferences>().toJSON(p, Serializer.NORMAL), writer);
	}

	private String lookAndFeel;

	private String language;

	private String lineSeparator;

	private Integer fontSize;

	private Integer tabSize;

	private Boolean lineWrap;

	private Color backgroundColor;

	private Boolean rememberCurrentSession;

	private Boolean alwaysOnTop;

	private Boolean showSplashScreen;

	private Boolean saveOnFocusLost;

	private Integer saveOnFocusLostTarget;

	private Dimension dimension;

	private Point location;

	public Preferences() {
		lookAndFeel = LOOK_AND_FEEL;
		language = LANGUAGE;

		lineSeparator = LINE_SEPARATOR;

		fontSize = FONT_SIZE;
		tabSize = TAB_SIZE;
		lineWrap = LINE_WRAP;
		backgroundColor = BACKGROUND_COLOR;

		rememberCurrentSession = REMEMBER_CURRENT_SESSION;
		alwaysOnTop = ALWAYS_ON_TOP;
		showSplashScreen = SHOW_SPLASH_SCREEN;
		saveOnFocusLost = SAVE_ON_FOCUS_LOST;
		saveOnFocusLostTarget = SAVE_ON_FOCUS_LOST_TARGET;

		dimension = DIMENSION;
		location = LOCATION;
	}

	public Color backgroundColor() {
		return backgroundColor;
	}

	public void backgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public Dimension dimension() {
		return dimension;
	}

	public void dimension(Dimension dimension) {
		this.dimension = dimension;
	}

	public Integer fontSize() {
		return fontSize;
	}

	public void fontSize(Integer fontSize) {
		this.fontSize = fontSize;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public Dimension getDimension() {
		return dimension;
	}

	public Integer getFontSize() {
		return fontSize;
	}

	public Boolean getAlwaysOnTop() {
		return alwaysOnTop;
	}

	public String getLanguage() {
		return language;
	}

	public String getLineSeparator() {
		return lineSeparator;
	}

	public Boolean getLineWrap() {
		return lineWrap;
	}

	public Point getLocation() {
		return location;
	}

	public String getLookAndFeel() {
		return lookAndFeel;
	}

	public Boolean getRememberCurrentSession() {
		return rememberCurrentSession;
	}

	public Boolean getSaveOnFocusLost() {
		return saveOnFocusLost;
	}

	public Integer getSaveOnFocusLostTarget() {
		return saveOnFocusLostTarget;
	}

	public Boolean getShowSplashScreen() {
		return showSplashScreen;
	}

	public Integer getTabSize() {
		return tabSize;
	}

	public Boolean alwaysOnTop() {
		return alwaysOnTop;
	}

	public void alwaysOnTop(Boolean alwaysOnTop) {
		this.alwaysOnTop = alwaysOnTop;
	}

	public String language() {
		return language;
	}

	public void language(String language) {
		this.language = language;
	}

	public String lineSeparator() {
		return lineSeparator;
	}

	public void lineSeparator(String lineSeparator) {
		this.lineSeparator = lineSeparator;
	}

	public Boolean lineWrap() {
		return lineWrap;
	}

	public void lineWrap(Boolean lineWrap) {
		this.lineWrap = lineWrap;
	}

	public Point location() {
		return location;
	}

	public void location(Point location) {
		this.location = location;
	}

	public String lookAndFeel() {
		return lookAndFeel;
	}

	public void lookAndFeel(String lookAndFeel) {
		this.lookAndFeel = lookAndFeel;
	}

	public Boolean rememberCurrentSession() {
		return rememberCurrentSession;
	}

	public void rememberCurrentSession(Boolean rememberCurrentSession) {
		this.rememberCurrentSession = rememberCurrentSession;
	}

	public Boolean saveOnFocusLost() {
		return getSaveOnFocusLost();
	}

	public void saveOnFocusLost(Boolean saveOnFocusLost) {
		this.setSaveOnFocusLost(saveOnFocusLost);
	}

	public Integer saveOnFocusLostTarget() {
		return getSaveOnFocusLostTarget();
	}

	public void saveOnFocusLostTarget(Integer saveOnFocusLostTarget) {
		this.setSaveOnFocusLostTarget(saveOnFocusLostTarget);
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public void setDimension(Dimension dimension) {
		this.dimension = dimension;
	}

	public void setFontSize(Integer fontSize) {
		this.fontSize = fontSize;
	}

	public void setAlwaysOnTop(Boolean alwaysOnTop) {
		this.alwaysOnTop = alwaysOnTop;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public void setLineSeparator(String lineSeparator) {
		this.lineSeparator = lineSeparator;
	}

	public void setLineWrap(Boolean lineWrap) {
		this.lineWrap = lineWrap;
	}

	public void setLocation(Point location) {
		this.location = location;
	}

	public void setLookAndFeel(String lookAndFeel) {
		this.lookAndFeel = lookAndFeel;
	}

	public void setRememberCurrentSession(Boolean rememberCurrentSession) {
		this.rememberCurrentSession = rememberCurrentSession;
	}

	public void setSaveOnFocusLost(Boolean saveOnFocusLost) {
		this.saveOnFocusLost = saveOnFocusLost;
	}

	public void setSaveOnFocusLostTarget(Integer saveOnFocusLostTarget) {
		this.saveOnFocusLostTarget = saveOnFocusLostTarget;
	}

	public void setShowSplashScreen(Boolean showSplashScreen) {
		this.showSplashScreen = showSplashScreen;
	}

	public void setTabSize(Integer tabSize) {
		this.tabSize = tabSize;
	}

	public Boolean showSplashScreen() {
		return showSplashScreen;
	}

	public void showSplashScreen(Boolean showSplashScreen) {
		this.showSplashScreen = showSplashScreen;
	}

	public Integer tabSize() {
		return tabSize;
	}

	public void tabSize(Integer tabSize) {
		this.tabSize = tabSize;
	}
}
