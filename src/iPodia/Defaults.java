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
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class Defaults {
	public static final String dbDriver = "com.mysql.jdbc.Driver";
	public static final String dbURL = "jdbc:mysql://localhost:3306/ipodia";
	public static final String dbUsername = "iPodia";
	public static final String dbPassword = "iPodia";

	public static Connection dbConnection = null;

	public static void openDBConnection() {
		if (dbConnection != null)
			return;

		try {
			Class.forName(dbDriver);
			dbConnection = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
		} catch (SQLException e) {
		} catch (ClassNotFoundException e) {
		}
	}

	public static void closeDBConnection() {
		if (dbConnection == null)
			return;

		try {
			dbConnection.close();
			dbConnection = null;
		} catch (SQLException e) {
		}
	}

	public static Connection getDBConnection() {
		if (dbConnection == null)
			openDBConnection();

		return dbConnection;
	}

	public static void execute(String query) {
		PreparedStatement ps = null;
		try {
			ps = getDBConnection().prepareStatement(query);
			ps.execute();
		} catch (SQLException e) {
		} finally {
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
				}
			}
		}
	}

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

	public static HashMap<String, User> getStudentsBySafeEmailForClass(String classId) {
		if (isEmpty(classId))
			return null;

		HashMap<String, User> students = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			students = new HashMap<String, User>();
			ps = getDBConnection().prepareStatement("SELECT * FROM users");
			rs = ps.executeQuery();
			while (rs.next()) {
				String classes = rs.getString("classes");
				if (!isEmpty(classes) && !classes.contains(classId))
					continue;

				User u = new User(rs);
				if (u.isStudent())
					students.put(u.getSafeEmail(), u);
			}

			rs.close();
			ps.close();
		} catch (SQLException e) {
		} finally {
			try {
				if (rs != null && !rs.isClosed())
					rs.close();

				if (ps != null && !ps.isClosed())
					ps.close();
			} catch (SQLException e) {
			}
		}
		closeDBConnection();
		return students;
	}

	public static HashSet<String> getEmailsFromResultSet(ResultSet rs, Predicate<String> callback) throws SQLException {
		HashSet<String> students = new HashSet<String>();
		ResultSetMetaData rsmd = rs.getMetaData();
		for (int i = 1; i <= rsmd.getColumnCount(); ++i) { // SQL columns start at index 1
			String name = rsmd.getColumnName(i);
			if (callback.test(name))
				students.add(name);
		}
		return students;
	}

	public static HashMap<Integer, HashSet<String>> getStudentGroups(String classId, String weekId) {
		if (isEmpty(classId) || isEmpty(weekId))
			return null;

		HashMap<Integer, HashSet<String>> groups = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = getDBConnection().prepareStatement("SELECT * FROM class_" + classId + "_matching WHERE id = ?");
			ps.setString(1, weekId);
			rs = ps.executeQuery();

			HashSet<String> listOfStudents = getEmailsFromResultSet(rs, item -> !item.equals("id"));
			groups = new HashMap<Integer, HashSet<String>>();
			while (rs.next()) {
				for (String email : listOfStudents) {
					String group = rs.getString(email);
					if (isEmpty(group))
						continue;

					int i = Integer.parseInt(group);
					if (!groups.containsKey(i))
						groups.put(i, new HashSet<String>());

					groups.get(i).add(email.replace(beforeMatching, "").replace(afterMatching, ""));
				}
			}

			rs.close();
			ps.close();
		} catch (SQLException e) {
		} finally {
			try {
				if (rs != null && !rs.isClosed())
					rs.close();

				if (ps != null && !ps.isClosed())
					ps.close();
			} catch (SQLException e) {
			}
		}
		closeDBConnection();
		return groups;
	}

	public static HashMap<String, Integer> getStudentScores(String classId, String questionId, String type) {
		if (isEmpty(classId) || isEmpty(questionId))
			return null;

		HashMap<String, Integer> map = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = getDBConnection().prepareStatement("SELECT * FROM class_" + classId + " WHERE id LIKE ?");
			ps.setString(1, questionId + "%");
			rs = ps.executeQuery();

			HashSet<String> listOfStudents = getEmailsFromResultSet(rs, item -> item.contains(type));
			map = new HashMap<String, Integer>();
			while (rs.next()) {
				String correctAnswer = rs.getString("correctAnswer");
				for (String email : listOfStudents) {
					String safeEmail = email.replace(beforeMatching, "").replace(afterMatching, "");
					// initialize the user's email in the map with a score of 0
					if (!map.containsKey(safeEmail))
						map.put(safeEmail, 0);

					String studentAnswer = rs.getString(email);
					if (!isEmpty(studentAnswer) && studentAnswer.equals(correctAnswer))
						map.put(safeEmail, map.get(safeEmail) + 1);
				}
			}

			rs.close();
			ps.close();
		} catch (SQLException e) {
		} finally {
			try {
				if (rs != null && !rs.isClosed())
					rs.close();

				if (ps != null && !ps.isClosed())
					ps.close();
			} catch (SQLException e) {
			}
		}
		closeDBConnection();
		return map;
	}

	public static LinkedList<HashMap.Entry<String, Integer>> buildListOfStudents(String classId, String questionId, boolean shouldSort) {
		HashMap<String, Integer> scores = getStudentScores(classId, questionId, beforeMatching);
		if (scores == null)
			return null;

		LinkedList<HashMap.Entry<String, Integer>> list = new LinkedList<HashMap.Entry<String, Integer>>(scores.entrySet());

		// if doing the most-fair algorithm, need to sort the list of students so they are ordered in the list by their score
		// if you are doing the recommendation algorithm, you want random groups so don't sort the list
		if (shouldSort)
			Collections.sort(list, (left, right) -> left.getValue().compareTo(right.getValue()));
		else
			Collections.shuffle(list);

		return list;
	}

	public static LinkedList<HashSet<String>> buildGroupsFromListOfStudentsEmails(LinkedList<String> listOfStudentsEmails) {
		LinkedList<HashSet<String>> groups = new LinkedList<HashSet<String>>();
		while (!listOfStudentsEmails.isEmpty()) {
			HashSet<String> group = new HashSet<String>();
			groups.add(group);

			group.add(listOfStudentsEmails.removeFirst());
			group.add(listOfStudentsEmails.removeLast());
			if (listOfStudentsEmails.size() < 4) { // 5 or less students left, so make a group of everyone
				while (!listOfStudentsEmails.isEmpty())
					group.add(listOfStudentsEmails.removeFirst());
			} else if (listOfStudentsEmails.size() > 4) { // 7+ students left, so take out 4 and make a group
				group.add(listOfStudentsEmails.removeFirst());
				group.add(listOfStudentsEmails.removeLast());
			} else // 6 students left, so split into 3 and 3
				group.add(listOfStudentsEmails.removeFirst());
		}
		return groups;
	}

	public static HashSet<QuizQuestion> getQuestionsForWeekTopic(String classId, String week) {
		if (isEmpty(classId) || isEmpty(week))
			return null;

		HashSet<QuizQuestion> results = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = getDBConnection().prepareStatement("SELECT * FROM class_" + classId + " WHERE id LIKE ?");
			ps.setString(1, "Week" + week + "Topic%");
			rs = ps.executeQuery();

			results = new HashSet<QuizQuestion>();
			while (rs.next()) {
				QuizQuestion q = new QuizQuestion(rs);
				if (q.isValid())
					results.add(q);
			}

			rs.close();
			ps.close();
		} catch (SQLException e) {
		} finally {
			try {
				if (rs != null && !rs.isClosed())
					rs.close();

				if (ps != null && !ps.isClosed())
					ps.close();
			} catch (SQLException e) {
			}
		}
		closeDBConnection();
		return results;
	}

	public static void saveGroupNumbers(LinkedList<HashSet<String>> groups, String classId, String week, String typeOfQuiz) {
		if (isEmpty(classId) || isEmpty(week))
			return;

		PreparedStatement ps = null;
		try {
			for (int i = 0; i < groups.size(); ++i) {
				for (String email : groups.get(i)) {
					String safeEmail = email.replace(beforeMatching, "").replace(afterMatching, "");
					ps = getDBConnection().prepareStatement("UPDATE class_" + classId + "_matching SET " + safeEmail + " = ? WHERE id = ?");
					ps.setString(1, Integer.toString(i + 1));
					ps.setString(2, "Week" + week + typeOfQuiz);
					ps.executeUpdate();
					ps.close();
				}
			}
		} catch (SQLException e) {
		} finally {
			try {
				if (ps != null && !ps.isClosed())
					ps.close();
			} catch (SQLException e) {
			}
		}
		closeDBConnection();
	}

	public static HashMap<String, Integer> recommendTopicsForBeforeClassQuestions (LinkedList<HashSet<String>> groups, String classId, String week, String typeOfQuiz) {
		HashMap<String, User> students = getStudentsBySafeEmailForClass(classId);
		if (students == null)
			return null;

		HashMap<String, Integer> topic1Before = getStudentScores(classId, "Week" + week + "Topic1%", beforeMatching);
		HashMap<String, Integer> topic2Before = getStudentScores(classId, "Week" + week + "Topic2%", beforeMatching);
		HashMap<String, Integer> topic3Before = getStudentScores(classId, "Week" + week + "Topic3%", beforeMatching);
		HashMap<String, Integer> topic4Before = getStudentScores(classId, "Week" + week + "Topic4%", beforeMatching);
		HashMap<String, Integer> recommendations = new HashMap<String, Integer>();

		for (HashSet<String> group : groups) {
			int[] topicScoresForGroup = new int[4];
			for (int i = 0; i < 4; i ++)
				topicScoresForGroup[i] = 0;

			for (String student : group) {
				if (!students.containsKey(student))
					continue;

				topicScoresForGroup[0] += topic1Before.get(student);
				topicScoresForGroup[1] += topic2Before.get(student);
				topicScoresForGroup[2] += topic3Before.get(student);
				topicScoresForGroup[3] += topic4Before.get(student);	
			}

			//calculate the topic that this group did the worst on
			int minTopicScore = topicScoresForGroup[0];
			int recommendedTopicNumForGroup = 1;
			for (int i = 1; i < 4; ++i) {
				if (topicScoresForGroup[i] < minTopicScore) {
					minTopicScore = topicScoresForGroup[i];
					recommendedTopicNumForGroup = i+1;
				}
			}

			for (String student : group) {
				if (!students.containsKey(student))
					continue;

				//for each student in this group, give them the recommended topic that they should discuss
				recommendations.put(student, recommendedTopicNumForGroup);
			}
		}

		saveRecommendations(recommendations, classId, week, typeOfQuiz);
		return recommendations;
	}
	
	private static void saveRecommendations (HashMap<String, Integer> recommendations, String classId, String week, String typeOfQuiz) {
		if (isEmpty(classId) || isEmpty(week))
			return;

		PreparedStatement ps = null;
		try {
			for (Entry<String, Integer> entry : recommendations.entrySet()) {
				ps = getDBConnection().prepareStatement("UPDATE class_" + classId + "_matching SET " + entry.getKey() + " = ? WHERE id = ?");
				ps.setString(1, Integer.toString(entry.getValue()));
				ps.setString(2, "Week" + week + typeOfQuiz + "Recommendation");
				ps.executeUpdate();
				ps.close();
			}
		} catch (SQLException e) {
		} finally {
			try {
				if (ps != null && !ps.isClosed())
					ps.close();
			} catch (SQLException e) {
			}
		}
		closeDBConnection();
	}

	public static String getGroupEmail(HashSet<User> members) {
		String result = "";
		int count = 0;
		for (User u : members) {
			result += u.getEmail();
			if (count++ < members.size() - 1)
				result += ",";
		}
		return result;
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
