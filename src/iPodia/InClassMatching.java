package iPodia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class InClassMatching {
	public static void match(String classId, String week) {
		if (Defaults.isEmpty(classId) || Defaults.isEmpty(week))
			return;

		LinkedList<HashMap.Entry<String,Integer>> unmatchedStudents = buildSortedList(classId, week);
		if (unmatchedStudents == null)
			return;

		HashSet<HashSet<HashMap.Entry<String, Integer>>> groups = new HashSet<HashSet<HashMap.Entry<String, Integer>>>();
		while (!unmatchedStudents.isEmpty()) {
			HashSet<HashMap.Entry<String, Integer>> group = new HashSet<HashMap.Entry<String, Integer>>();
			groups.add(group);

			group.add(unmatchedStudents.removeFirst());
			group.add(unmatchedStudents.removeLast());
			if (unmatchedStudents.size() < 4) // 5 or less students left, so make a group of everyone
				group.addAll(unmatchedStudents);
			else if (unmatchedStudents.size() == 4) // 6 students left, so split into 3 and 3
				group.add(unmatchedStudents.removeFirst());
			else { // 7+ students left, so take out 4 and make a group
				group.add(unmatchedStudents.removeFirst());
				group.add(unmatchedStudents.removeLast());
			}
		}

		for (HashSet<HashMap.Entry<String, Integer>> group : groups) {
			for (HashMap.Entry<String, Integer> entry : group)
				System.out.println("Email: " + entry.getKey() + " -- Score: " + entry.getValue() );
			System.out.println();
		}
	}

	private static LinkedList<HashMap.Entry<String,Integer>> buildSortedList(String classId, String week) {
		if (Defaults.isEmpty(classId) || Defaults.isEmpty(week))
			return null;

		try {
			Class.forName(Defaults.dbDriver);
			final Connection dbConnection = DriverManager.getConnection(Defaults.dbURL, Defaults.dbUsername, Defaults.dbPassword); 

			PreparedStatement ps = dbConnection.prepareStatement("Select * From class_" + classId + " WHERE id LIKE ?");
			ps.setString(1, "Week" + week + "InClass" + "%");
			ResultSet results = ps.executeQuery();	
			ResultSetMetaData rsmd = results.getMetaData();

			HashSet<String> listOfStudents = new HashSet<String>();
			for (int i = 1; i <= rsmd.getColumnCount(); i ++) { // SQL columns start at index 1
				String name = rsmd.getColumnName(i);
				if (name.equals("id")
				 || name.equals("question")
				 || name.equals("answerA")
				 || name.equals("answerB")
				 || name.equals("answerC")
				 || name.equals("answerD")
				 || name.equals("answerE")
				 || name.equals("correctAnswer")
				 || name.equals("topic")
				 || name.contains(Defaults.afterMatching) // only match based on answer before matching
				) {
					continue;
				}
				listOfStudents.add(name);
			}

			HashMap<String, Integer> map = new HashMap<String, Integer>();
			while (results.next()) {
				String correctAnswer = results.getString("correctAnswer");
				for (String email : listOfStudents) {
					String studentAnswer = results.getString(email);
					if (!Defaults.isEmpty(studentAnswer) && studentAnswer.equals(correctAnswer))
						map.put(email, map.get(email) + 1);
					else if (!map.containsKey(email))
						map.put(email, 0); // initialize the user's email in the map with a score of 0
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
}