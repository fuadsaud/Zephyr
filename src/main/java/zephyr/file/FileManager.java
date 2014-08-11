package zephyr.file;

import static zephyr.Zephyr.PREFERENCES;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import zephyr.text.TextDocument;

/**
 * This class is used to read and write text files. It is responsible to manage
 * streams
 * 
 * @author Fuad Saud
 * 
 */
public class FileManager {

	public boolean delete(Path file) throws IOException {
		return Files.deleteIfExists(file);
	}

	/**
	 * Reads a file and returns it's contents (using a {@link BufferedReader}).
	 * 
	 * @param file
	 *            the {@link Path} representing the file to be read
	 * @return A {@link TextDocument} with the content and metadata retrieved
	 *         from the file
	 * @throws FileNotFoundException
	 *             if the file does not exist, is a directory rather than a
	 *             regular file, or for some other reason cannot be open for
	 *             reading
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public String read(Path file) throws FileNotFoundException, IOException {
		return read(new FileReader(file.toFile()));
	}

	public String read(Reader reader) throws IOException {
		BufferedReader br = new BufferedReader(reader);
		StringBuilder builder = new StringBuilder();
		String line = null;

		while ((line = br.readLine()) != null) {
			builder.append(line + "\n");
		}

		if (builder.length() != 0) {
			builder.deleteCharAt(builder.length() - 1);
		}

		br.close();

		return builder.toString();
	}

	/**
	 * Reads a file and returns it's contents (using a {@link BufferedReader}).
	 * The {@link Path} is referenced by the path.
	 * 
	 * @param path
	 *            the path of the {@link Path} to be read
	 * @return A string representing file's content
	 * @throws FileNotFoundException
	 *             if the file does not exist, is a directory rather than a
	 *             regular file, or for some other reason cannot be open for
	 *             reading
	 * @throws IOException
	 *             if an I/O error occurs
	 * 
	 * @see FileManager#read(Path)
	 */
	public String read(String path) throws FileNotFoundException, IOException {
		return read(Paths.get(path));
	}

	/**
	 * Writes a text file (using a {@link BufferedReader}).
	 * 
	 * @param content
	 *            the string to be written
	 * @param file
	 *            the destination {@link Path} (if it does not exists, it will
	 *            be created)
	 * @throws IOException
	 *             if the file exists but is a directory rather than a regular
	 *             file, does not exist but cannot be created, or cannot be
	 *             opened for any other reason or any other I/O error occurs
	 */
	public void write(String content, Path file) throws FileNotFoundException, IOException {

		BufferedWriter bw = new BufferedWriter(new FileWriter(file.toFile()));
		write(content, bw);
	}

	/**
	 * Writes a text file (using a {@link BufferedReader}).
	 * 
	 * @param content
	 *            the string to be written
	 * @param path
	 *            the path of the {@link Path} to be written (if it does not
	 *            exists, it will be created)
	 * @throws FileNotFoundException
	 *             if the given file object does not denote an existing,
	 *             writable regular file and a new regular file of that name
	 *             cannot be created, or if some other error occurs while
	 *             opening or creating the file
	 * @throws IOException
	 *             if an I/O error occurs
	 * 
	 * @see FileManager#write(String, Path)
	 */
	public void write(String content, String path) throws FileNotFoundException, IOException {
		write(content, Paths.get(path));
	}

	public void write(String content, Writer writer) throws IOException {
		// Defines which line separator should use
		String lineSeparator = PREFERENCES.lineSeparator();
		if (!lineSeparator.equals("\n")) {
			content = content.replace("\n", lineSeparator);
		}

		writer.write(content);
		writer.flush();
		writer.close();
	}
}
