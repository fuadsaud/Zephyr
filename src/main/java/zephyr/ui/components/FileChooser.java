package zephyr.ui.components;

import java.awt.Component;
import java.awt.HeadlessException;
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * This class extends {@link JFileChooser} and the only function it adds to it
 * is checking file names to add right extension based on the file filters.
 * 
 * @author Fuad Saud
 * 
 */
public class FileChooser extends JFileChooser {

	/**
	 * Serial version ID.
	 */
	private static final long serialVersionUID = -1245645604994562256L;

	// File name extension filters
	private static final FileNameExtensionFilter C_FILTER = new FileNameExtensionFilter(
			"C/C++ source files", "c", "cpp", "h");
	private static final FileNameExtensionFilter CSS_FILTER = new FileNameExtensionFilter(
			"CSS files", "css");
	private static final FileNameExtensionFilter HTML_FILTER = new FileNameExtensionFilter(
			"HTML files", "html", "htm", "xhtml");
	private static final FileNameExtensionFilter JAVA_FILTER = new FileNameExtensionFilter(
			"Java files", "java");
	private static final FileNameExtensionFilter JSON_FILTER = new FileNameExtensionFilter(
			"JSON files", "json");
	private static final FileNameExtensionFilter PERL_FILTER = new FileNameExtensionFilter(
			"Perl files", "pl");
	private static final FileNameExtensionFilter PHP_FILTER = new FileNameExtensionFilter(
			"PHP files", "php");
	private static final FileNameExtensionFilter PROPERTIES_FILTER = new FileNameExtensionFilter(
			"Properties files", "properties", "plist");
	private static final FileNameExtensionFilter PYTHON_FILTER = new FileNameExtensionFilter(
			"Python files", "py");
	private static final FileNameExtensionFilter RUBY_FILTER = new FileNameExtensionFilter(
			"Ruby files", "rb");
	private static final FileNameExtensionFilter SQL_FILES = new FileNameExtensionFilter(
			"SQL files", "sql");
	private static final FileNameExtensionFilter TEXT_FILTER = new FileNameExtensionFilter(
			"Text files", "txt");
	private static final FileNameExtensionFilter XML_FILTER = new FileNameExtensionFilter(
			"XML files", "xml");
	private static final FileNameExtensionFilter YAML_FILTER = new FileNameExtensionFilter(
			"YAML files", "yml", "yaml");

	/**
	 * Calls the {@link JFileChooser} and adds all
	 * {@link FileNameExtensionFilter} used on Zephyr.
	 */
	public FileChooser() {
		super();

		// Adds file name extension filters
		addChoosableFileFilter(C_FILTER);
		addChoosableFileFilter(CSS_FILTER);
		addChoosableFileFilter(HTML_FILTER);
		addChoosableFileFilter(JAVA_FILTER);
		addChoosableFileFilter(JSON_FILTER);
		addChoosableFileFilter(PHP_FILTER);
		addChoosableFileFilter(PERL_FILTER);
		addChoosableFileFilter(PROPERTIES_FILTER);
		addChoosableFileFilter(PYTHON_FILTER);
		addChoosableFileFilter(RUBY_FILTER);
		addChoosableFileFilter(SQL_FILES);
		addChoosableFileFilter(TEXT_FILTER);
		addChoosableFileFilter(XML_FILTER);
		addChoosableFileFilter(YAML_FILTER);

		setFileFilter(getAcceptAllFileFilter());
	}

	/**
	 * Calls {@link JFileChooser#showOpenDialog(Component)} and checks if the
	 * selected file exits.
	 * 
	 * @param parent
	 *            the parent component of the dialog, can be null; see
	 *            {@code showDialog} for details
	 * @return the return state of the file chooser on popdown:
	 *         JFileChooser.CANCEL_OPTION <br/>
	 *         JFileChooser.APPROVE_OPTION <br/>
	 *         JFileChooser.ERROR_OPTION if an error occurs or the dialog is
	 *         dismissed
	 * @throws HeadlessException
	 *             if GraphicsEnvironment.isHeadless() returns true.
	 * 
	 * @throws FileNotFoundException
	 *             if selected file does not exists anymore (it might have been
	 *             deleted during openDialog was open).
	 * @see JFileChooser#showOpenDialog(Component)
	 */
	public int open(Component parent) throws HeadlessException, FileNotFoundException {
		int ret = super.showOpenDialog(parent);
		if (ret == APPROVE_OPTION) {
			if (!getSelectedFile().exists()) {
				throw new FileNotFoundException();
			}
		}
		return ret;
	}

	/**
	 * Calls JFileChooser#showSaveDialog(java.awt.Component) and checks if the
	 * typed file name contains the respective selected file name filter
	 * extensions. Case not, it adds default extension to the file name.
	 * 
	 * @see JFileChooser#showSaveDialog(java.awt.Component)
	 */
	public int save(Component parent) throws HeadlessException {
		int result = showSaveDialog(parent);
		if (result == APPROVE_OPTION) {
			if (!getFileFilter().equals(getAcceptAllFileFilter())) {
				FileNameExtensionFilter filter = (FileNameExtensionFilter) getFileFilter();
				File selectedFile = getSelectedFile();
				String fileName = selectedFile.getName();

				boolean fileNameHasExtension = false;

				// Tests if there is a '.' on the fileName and if it's not the
				// last char
				if (fileName.contains(".") && fileName.charAt(fileName.length() - 1) != ('.')) {
					for (String extension : filter.getExtensions()) {
						// Gets a substring of file name that starts with the
						// last '.' on the string
						if (fileName.toLowerCase().endsWith("." + extension)) {
							fileNameHasExtension = true;
						}
					}
				}

				if (!fileNameHasExtension) {
					selectedFile = new File(selectedFile.getAbsolutePath() + "."
							+ filter.getExtensions()[0]);
					setSelectedFile(selectedFile);
				}
			}
		}

		return result;
	}

}
