package iPodia;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

public class Defaults {
	public static final String DATA_DIRECTORY = "/iPodiaData";
	public static final String CSV_REGEXP = "\\s*,\\s*";

	public static String generateClassesRegExp(String classId) {
		String regexp = "";
		regexp += "(?:";
		regexp += "^\\s*" + classId + "(?:\\s*$|" + CSV_REGEXP + ")";
		regexp += "|";
		regexp += "\\s+" + classId + "\\s*,";
		regexp += "|";
		regexp += "(?:^\\s*|" + CSV_REGEXP + ")" + classId + "\\s*$";
		regexp += ")";
		return regexp;
	}

	public static boolean isEmpty(String s) {
		return s == null || s.length() == 0 || s.trim().length() == 0;
	}

	public static String createSafeString(String s) {
		return s.replaceAll("\\W", "_");
	}

	public static HashSet<String> arrayToHashSet(String[] s) {
		return new HashSet<String>(Arrays.asList(Optional.ofNullable(s).orElse(new String[0])));
	}

	public static void createFolderIfNotExists(String path) {
		File file = new File(path);
		if (!file.exists() || !file.isDirectory())
			file.mkdir();
	}

	public static File createFileIfNotExists(String path) throws IOException {
		File file = new File(path);
		if (!file.exists() || !file.isFile())
			file.createNewFile();

		return file;
	}
}
