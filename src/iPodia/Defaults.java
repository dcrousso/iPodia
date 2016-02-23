package iPodia;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.regex.Pattern;

public class Defaults {
	public static final String dbDriver = "com.mysql.jdbc.Driver";
	public static final String dbURL = "jdbc:mysql://localhost:3306/ipodia";
	public static final String dbUsername = "root";
	public static final String dbPassword = "";

	public static final String beforeMatching = "__before";
	public static final String afterMatching = "__after";
	public static final String chatURL = "https://appear.in/iPodia/";

	public static final String INITIAL_PASSWORD = "305666274058833912233446123550405823599";
	public static final String DATA_DIRECTORY = "/iPodiaData";
	public static final String CSV_REGEXP = "\\s*,\\s*";

	public static final Pattern WEEK_PATTERN = Pattern.compile("(Week)(\\d+)(\\w+)", Pattern.CASE_INSENSITIVE);
	public static final Pattern QUESTION_PATTERN = Pattern.compile("(Week\\d+Topic\\d+Question\\d+)(\\w+)?", Pattern.CASE_INSENSITIVE);

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

	public static boolean columnExists(ResultSet rs, String columnName) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		for (int x = 1; x <= rsmd.getColumnCount(); x++) {
			if (columnName.equals(rsmd.getColumnName(x)))
				return true;
		}
		return false;
	}

	public static String urlEncode(String url) {
		if (isEmpty(url))
			return url;

		try {
			return URLEncoder.encode(url, "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}
		return url;
	}

	public static String urlDecode(String url) {
		if (isEmpty(url))
			return url;

		try {
			return URLDecoder.decode(url, "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}
		return url;
	}
}
