package iPodia;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class InClassMatching {
	public static LinkedList<HashSet<String>> match(String classId, String week) {
		if (Defaults.isEmpty(classId) || Defaults.isEmpty(week))
			return null;

		LinkedList<HashMap.Entry<String, Integer>> unmatchedStudents = Defaults.buildSortedList(classId, "Week" + week + "InClass");
		if (unmatchedStudents == null)
			return null;

		LinkedList<String> studentEmails = unmatchedStudents.parallelStream().map((item) -> item.getKey()).collect(Collectors.toCollection(LinkedList<String>::new));
		return Defaults.buildGroupsFromSortedList(studentEmails);
	}
}
