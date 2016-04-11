package iPodia;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class InClassMatching {
	public static LinkedList<HashSet<String>> mostFairMatch(String classId, String week) {
		return matchingHelper(classId, week, true);
	}
	
	public static LinkedList<HashSet<String>> recommendationMatch(String classId, String week) {
		return matchingHelper(classId, week, false);
	}
	
	private static LinkedList<HashSet<String>> matchingHelper(String classId, String week, boolean shouldSort) {
		if (Defaults.isEmpty(classId) || Defaults.isEmpty(week))
			return null;

		LinkedList<HashMap.Entry<String, Integer>> listOfStudents = Defaults.buildListOfStudents(classId, "Week" + week + "InClass", shouldSort);
		if (listOfStudents == null)
			return null;

		LinkedList<String> listOfStudentsEmails = listOfStudents.parallelStream().map((item) -> item.getKey()).collect(Collectors.toCollection(LinkedList<String>::new));
		return Defaults.buildGroupsFromListOfStudentsEmails(listOfStudentsEmails);
	}
}
