package iPodia;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class Defaults {
	public static final String dbDriver = "com.mysql.jdbc.Driver";
	public static final String dbURL = "jdbc:mysql://localhost:3306/ipodia";
	public static final String dbUsername = "iPodia";
	public static final String dbPassword = "iPodia";

	public static final String inClassMatching = "inClassMatching";
	public static final String beforeClassMatching = "beforeClassMatching";

	public static final String beforeMatching = "__before";
	public static final String afterMatching = "__after";
	public static final String chatURL = "https://appear.in/iPodia/";

	public static final String INITIAL_PASSWORD = "305666274058833912233446123550405823599";
	public static final String DATA_DIRECTORY = "/iPodiaData/";
	public static final String CSV_REGEXP = "\\s*,\\s*";

	public static final Pattern WEEK_PATTERN = Pattern.compile("(Week)(\\d+)(\\w+)", Pattern.CASE_INSENSITIVE);
	public static final Pattern TOPIC_PATTERN = Pattern.compile("(Week\\d+)((?:Topic|InClass)\\d*)", Pattern.CASE_INSENSITIVE);
	public static final Pattern QUESTION_PATTERN = Pattern.compile("(Week\\d+(?:Topic|InClass)\\d*Question\\d+)(\\w+)?", Pattern.CASE_INSENSITIVE);

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

	public static <T> boolean contains(Collection<T> collection, Predicate<T> callback) {
		for (T item : collection) {
			if (callback.test(item))
				return true;
		}
		return false;
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

	public static HashMap<String, User> getStudentsBySafeEmail() {
		try {
			Class.forName(dbDriver);
			final Connection dbConnection = DriverManager.getConnection(dbURL, dbUsername, dbPassword);

			HashMap<String, User> students = new HashMap<String, User>();
			ResultSet users = dbConnection.prepareStatement("SELECT * FROM users").executeQuery();
			while (users.next()) {
				User u = new User(users);
				if (u.isStudent())
					students.put(u.getSafeEmail(), u);
			}

			return students;
		} catch (SQLException e) {
		} catch (ClassNotFoundException e) {
		}
		return null;
	}

	public static LinkedList<HashMap.Entry<String, Integer>> buildSortedList(String classId, String questionId) {
		if (isEmpty(classId) || isEmpty(questionId))
			return null;

		try {
			Class.forName(dbDriver);
			final Connection dbConnection = DriverManager.getConnection(dbURL, dbUsername, dbPassword);

			PreparedStatement ps = dbConnection.prepareStatement("SELECT * FROM class_" + classId + " WHERE id LIKE ?");
			ps.setString(1, questionId + "%");
			ResultSet results = ps.executeQuery();
			ResultSetMetaData rsmd = results.getMetaData();

			HashSet<String> listOfStudents = new HashSet<String>();
			for (int i = 1; i <= rsmd.getColumnCount(); ++i) { // SQL columns start at index 1
				String name = rsmd.getColumnName(i);
				if (name.equals("id")
				 || name.equals("question")
				 || name.equals("answerA")
				 || name.equals("answerB")
				 || name.equals("answerC")
				 || name.equals("answerD")
				 || name.equals("answerE")
				 || name.equals("correctAnswer")
				 || name.equals("dueDate")
				 || name.equals("topic")
				 || name.contains(afterMatching) // only match based on answer before matching
				) {
					continue;
				}
				listOfStudents.add(name);
			}

			HashMap<String, Integer> map = new HashMap<String, Integer>();
			while (results.next()) {
				String correctAnswer = results.getString("correctAnswer");
				for (String email : listOfStudents) {
					if (!map.containsKey(email))
						map.put(email, 0); // initialize the user's email in the map with a score of 0

					String studentAnswer = results.getString(email);
					if (!isEmpty(studentAnswer) && studentAnswer.equals(correctAnswer))
						map.put(email, map.get(email) + 1);
				}
			}

			LinkedList<HashMap.Entry<String, Integer>> list = new LinkedList<HashMap.Entry<String, Integer>>(map.entrySet());
			Collections.sort(list, (left, right) -> left.getValue().compareTo(right.getValue()));
			return list;
		} catch (SQLException e) {
		} catch (ClassNotFoundException e) {
		}
		return null;
	}

	public static LinkedList<HashSet<String>> buildGroupsFromSortedList(LinkedList<String> sortedList) {
		LinkedList<HashSet<String>> groups = new LinkedList<HashSet<String>>();
		while (!sortedList.isEmpty()) {
			HashSet<String> group = new HashSet<String>();
			groups.add(group);

			group.add(sortedList.removeFirst());
			group.add(sortedList.removeLast());
			if (sortedList.size() < 4) { // 5 or less students left, so make a group of everyone
				while (!sortedList.isEmpty())
					group.add(sortedList.removeFirst());
			} else if (sortedList.size() > 4) { // 7+ students left, so take out 4 and make a group
				group.add(sortedList.removeFirst());
				group.add(sortedList.removeLast());
			} else // 6 students left, so split into 3 and 3
				group.add(sortedList.removeFirst());
		}
		return groups;
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
