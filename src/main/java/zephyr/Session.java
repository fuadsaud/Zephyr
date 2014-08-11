package zephyr;

import java.util.ArrayList;
import java.util.List;

public class Session extends Object {

	private List<String> recents;

	private List<String> open;

	public Session() {
		this.recents = new ArrayList<String>();
		this.open = new ArrayList<String>();
	}

	public void addRecent(String path) {
		if (path != null) {
			recents.add(path);
		}
	}

	public void addRecent(String[] paths) {
		for (String path : paths) {
			addRecent(path);
		}
	}

	public void addOpen(String path) {
		if (path != null) {
			open.add(path);
		}
	}

	public void addOpen(String[] paths) {
		for (String path : paths) {
			addOpen(path);
		}
	}

	public String[] getOpen() {
		return open.toArray(new String[open.size()]);
	}

	public String[] getRecents() {
		return recents.toArray(new String[recents.size()]);
	}

	public void clearOpen() {
		open.clear();
	}

	public void clearRecents() {
		recents.clear();
	}
}
