package iPodia;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

public class InClassMatching {
	public static String match(String classId, String week) {
		if (Defaults.isEmpty(classId) || Defaults.isEmpty(week))
			return null;

		HashMap<String, User> students = Defaults.getStudentsBySafeEmail();
		if (students == null)
			return null;

		LinkedList<HashMap.Entry<String, Integer>> unmatchedStudents = Defaults.buildSortedList(classId, "Week" + week + "InClass");
		if (unmatchedStudents == null)
			return null;

		LinkedList<String> studentEmails = unmatchedStudents.parallelStream().map((item) -> item.getKey()).collect(Collectors.toCollection(LinkedList<String>::new));
		LinkedList<HashSet<String>> groups = Defaults.buildGroupsFromSortedList(studentEmails);

		JsonArrayBuilder result = Json.createArrayBuilder();
		for (HashSet<String> group : groups) {
			JsonObjectBuilder info = Json.createObjectBuilder();
			for (String entry : group) {
				User u = students.get(entry.replace(Defaults.beforeMatching, ""));
				if (u != null)
					info.add(u.getEmail(), u.getName());
			}
			result.add(info);
		}
		return result.build().toString();
	}
}
