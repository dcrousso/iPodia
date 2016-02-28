package iPodia;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class InClassMatching {
	public static void match(String classId, String week) {
		if (Defaults.isEmpty(classId) || Defaults.isEmpty(week))
			return;

		LinkedList<HashMap.Entry<String, Integer>> unmatchedStudents = Defaults.buildSortedList(classId, "Week" + week + "InClass");
		if (unmatchedStudents == null)
			return;

		LinkedList<String> studentEmails = unmatchedStudents.parallelStream().map((item) -> item.getKey()).collect(Collectors.toCollection(LinkedList<String>::new));
		LinkedList<HashSet<String>> groups = Defaults.buildGroupsFromSortedList(studentEmails);
		for (HashSet<String> group : groups) {
			for (String entry : group)
				System.out.println("Email: " + entry);
			System.out.println();
		}
	}
}
