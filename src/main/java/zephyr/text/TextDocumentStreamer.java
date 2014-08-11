package zephyr.text;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.text.BadLocationException;

import zephyr.file.FileManager;

public class TextDocumentStreamer {

	private FileManager fileManager = new FileManager();

	public boolean delete(TextDocument doc) throws IOException {
		return fileManager.delete(Paths.get(doc.getPath()));
	}

	/**
	 * Reads a file and creates a {@link TextDocument} with it's content and
	 * metadata.
	 * 
	 * @param file
	 *            the {@link Path} representing the to be read.
	 * @return A {@link TextDocument} with the content and metadata retrieved
	 *         from the file.
	 * @throws FileNotFoundException
	 *             if the file does not exist, is a directory rather than a
	 *             regular file, or for some other reason cannot be open for
	 *             reading.
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public TextDocument read(Path file) throws FileNotFoundException, IOException {
		String name = file.getFileName().toString();
		String path = file.toString();
		String content = fileManager.read(file);

		return new TextDocument(name, path, content);
	}

	/**
	 * Reads a file and creates a {@link TextDocument} with it's content and
	 * metadata.
	 * 
	 * @param path
	 *            the path of the {@link Path} to be read.
	 * @return A {@link TextDocument} with the content and metadata retrieved
	 *         from the file.
	 * @throws FileNotFoundException
	 *             if the file does not exist, is a directory rather than a
	 *             regular file, or for some other reason cannot be open for
	 *             reading.
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public TextDocument read(String path) throws FileNotFoundException, IOException {
		return read(Paths.get(path));
	}

	/**
	 * Writes a file based on a {@link TextDocument}.
	 * 
	 * @param doc
	 *            the document to be dumped
	 * @param doc
	 *            the {@link TextDocument} to be written
	 * @throws FileNotFoundException
	 *             if the given file object does not denote an existing,
	 *             writable regular file and a new regular file of that name
	 *             cannot be created, or if some other error occurs while
	 *             opening or creating the file
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public void write(TextDocument doc) throws FileNotFoundException, IOException {
		// Defines which line separator should use
		try {
			String content = doc.getText(0, doc.getLength());

			fileManager.write(content, Paths.get(doc.getPath()));
		} catch (BadLocationException e) {
			e.printStackTrace();
			throw new IOException();
		}
	}
}
