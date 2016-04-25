package iPodia;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.ArrayList;

public class QuizMatching {
	public static LinkedList<HashSet<String>> recommendationMatch(String classId, String week) {
		//when doing a recommendation match, you don't want to sort the students based on their scores so you 
		//generate random groups and then can provide a recommendation for each specific group
		LinkedList<HashMap.Entry<String, Integer>> listOfStudents = Defaults.buildListOfStudents(classId, "Week" + week + "Topic1" , false);
		LinkedList<String> listOfStudentsEmails = new LinkedList<String>();

		for (HashMap.Entry<String, Integer> student : listOfStudents) {
			String email = student.getKey();
			listOfStudentsEmails.add(email);	
		}

		return Defaults.buildGroupsFromListOfStudentsEmails(listOfStudentsEmails);
	}
	
	public static LinkedList<HashSet<String>> mostFairMatch(String classId, String week) {
		LinkedList<LinkedList<String>> allSortedLists = new LinkedList<LinkedList<String>>(); 
		ArrayList<Double> listOfAverageScores = new ArrayList<Double>();
		HashMap<String, ArrayList<Integer>> studentScoresForEachTopic = new HashMap<String, ArrayList<Integer>>();

		String [] topicNums = {"1", "2", "3", "4"};	
		for (int i = 0; i < 4; ++i) {
			LinkedList<HashMap.Entry<String, Integer>> sortedListForTopic = Defaults.buildListOfStudents(classId, "Week" + week + "Topic" + topicNums[i], true);
			LinkedList<String> sortedListForTopicKeys = new LinkedList<String>();

			for (HashMap.Entry<String, Integer> student : sortedListForTopic) {
				String email = student.getKey();
				sortedListForTopicKeys.add(email);
				ArrayList<Integer> studentScoreList = studentScoresForEachTopic.get(email);
				if (studentScoreList == null)
					studentScoreList = new ArrayList<Integer>();

				studentScoreList.add(student.getValue());
				studentScoresForEachTopic.put(email, studentScoreList);
			}

			// once you have the studentScores for each topic, this can serve as a look up table, so now you just need their email
			// as a key instead of a Map.Entry<String,Integer>
			allSortedLists.add(sortedListForTopicKeys);
			double avgScore = findClassAvgForTopic(sortedListForTopicKeys, studentScoresForEachTopic, i );
			listOfAverageScores.add(avgScore);
		}

		LinkedList<HashSet<String>> optimalGrouping = findOptimalInitialGrouping(allSortedLists, studentScoresForEachTopic, listOfAverageScores);
		for (int i = 0; i < 10000; ++i) {
			// if no groups swapped at all, end the for loop because already found the optimal grouping
			if (!performSwap(optimalGrouping, studentScoresForEachTopic, listOfAverageScores))
				break;
		}

		return optimalGrouping;
	}

	private static boolean performSwap(LinkedList<HashSet<String>> optimalGrouping, HashMap<String, ArrayList<Integer>> studentScoresForEachTopic, ArrayList<Double> listOfAverageScores) {
		boolean anySwapOccured = false;

		// the last group cannot swap with any other group as it is the last group
		for (int i = 0; i < optimalGrouping.size() - 1; ++i) {
			HashSet<String> group1 = optimalGrouping.get(i);
			HashSet<String> group2 = optimalGrouping.get(i + 1);

			double initialTotalDeviation = overallDeviationPerGroup(group1, studentScoresForEachTopic, listOfAverageScores) + overallDeviationPerGroup(group2, studentScoresForEachTopic, listOfAverageScores);
			boolean swapBetweenTwoGroupsOccured = false;

			HashSet<String> tempGroup1 = new HashSet<String>(group1);
			HashSet<String> tempGroup2 = new HashSet<String>(group2);
			for (String studentInGroup1 : group1) {
				for (String studnetInGroup2 : group2) {
					// can't remove students and add then just add them back to the proper groups if the postDeviation is worst.  This causes a concurency problem because
					// you are iterating over the set and by adding back to the students to the original groups at the same time.  So  Instead, use temporary groups to test out if this swap.
					// If It is a good swap, modify the actual groups, if it is a bad swap, revert the the temporary groups to their original state
					tempGroup1.remove(studentInGroup1);
					tempGroup2.remove(studnetInGroup2);
					tempGroup1.add(studnetInGroup2);
					tempGroup2.add(studentInGroup1);

					double postTotalDeviation = overallDeviationPerGroup(tempGroup1, studentScoresForEachTopic, listOfAverageScores) + overallDeviationPerGroup(tempGroup2, studentScoresForEachTopic, listOfAverageScores);
					if (postTotalDeviation < initialTotalDeviation) {
						anySwapOccured = true;
						swapBetweenTwoGroupsOccured = true;
						
						// swap was good so do the swap with the actual groups
						group1.remove(studentInGroup1);
						group2.remove(studnetInGroup2);

						group1.add(studnetInGroup2);
						group2.add(studentInGroup1);
						
						// if student in group 1 swaps with student in group 2, don't want to do any more swaps this iteration.  Because you are still comparing studentInGroup1 (who is actually in group 2 now)
						// with other students in group 2, and don't want to switch students within the same group.
						break;
					}

					tempGroup1.remove(studnetInGroup2);
					tempGroup2.remove(studentInGroup1);

					tempGroup1.add(studentInGroup1);
					tempGroup2.add(studnetInGroup2);
				}

				if (swapBetweenTwoGroupsOccured)
					break;
			}
		}

		return anySwapOccured;
	}

	private static double overallDeviationPerGroup(HashSet<String> group, HashMap<String, ArrayList<Integer>> studentScoresForEachTopic, ArrayList<Double> listOfAverageScores) {
		double[] combinedScoreForEachTopic = {0, 0, 0, 0};
		for (String email : group) {
			ArrayList<Integer> studentScoreList = studentScoresForEachTopic.get(email);
			if (studentScoreList == null)
				continue;

			for (int j = 0; j < studentScoreList.size(); ++j)
				combinedScoreForEachTopic[j] += studentScoreList.get(j);
		}

		double overallDeviationPerGroup = 0.0;
		for (int j = 0; j < 4; ++j) {
			double targetScoreForTopic = listOfAverageScores.get(j) * group.size();
			overallDeviationPerGroup += Math.abs(combinedScoreForEachTopic[j] - targetScoreForTopic);
		}

		return overallDeviationPerGroup;
	}

	private static LinkedList<HashSet<String>> findOptimalInitialGrouping (LinkedList<LinkedList<String>> allSortedLists, HashMap<String, ArrayList<Integer>> studentScoresForEachTopic, ArrayList<Double> listOfAverageScores ) {
		double minimumDeviation = Double.MAX_VALUE;
		LinkedList<HashSet<String>> optimalInitialGrouping = null;
		for (int i = 0; i < allSortedLists.size(); ++i) {
			LinkedList<String> sortedListForTopicKeys = allSortedLists.get(i);
			if (sortedListForTopicKeys == null)
				continue;

			// need to find avgScore before forming groups for the topic, because when you form groups, you remove items from the list
			double avgScore = listOfAverageScores.get(i);
			LinkedList<HashSet<String>> groupsForTopic = Defaults.buildGroupsFromListOfStudentsEmails(sortedListForTopicKeys);

			double overallDeviationForTopic = 0.0;
			for (HashSet<String> group : groupsForTopic) {
				int groupSum = 0;
				int numGroupMembers = 0;
				for (String student : group) {
					groupSum += studentScoresForEachTopic.get(student).get(i);
					numGroupMembers++;
				}

				double targetScore = avgScore * numGroupMembers;
				overallDeviationForTopic += Math.abs((double) groupSum - targetScore);
			}

			// found a group with 0 deviation for a given topic, so it could be the best possible initial grouping.  However, need to make sure
			// the groupsForTopic is not empty which can occur if the teacher doesn't have a question for a topic because an empty grouping would
			// also have an overall deviation of 0.
			if (!groupsForTopic.isEmpty() && overallDeviationForTopic == 0) {
				// can't combine these two if statements with && because if the group is empty it will have an overallDeviationForTopic of 0. Since it is empty, we will
				// go to the else statement, but we don't want to set to set optimalIntialGrouping to an empty group
				return groupsForTopic;
			}

			if (overallDeviationForTopic != 0 && overallDeviationForTopic < minimumDeviation) {
				optimalInitialGrouping = new LinkedList<HashSet<String>>(groupsForTopic);
				minimumDeviation = overallDeviationForTopic;
			}
		}

		return optimalInitialGrouping;
	}

	private static double findClassAvgForTopic(LinkedList<String> sortedListForTopicKeys, HashMap<String, ArrayList<Integer>> studentScoresForEachTopic, int indexOfTopic ) {
		int sum = 0;
		for (String email : sortedListForTopicKeys)
			sum += studentScoresForEachTopic.get(email).get(indexOfTopic);

		if (sortedListForTopicKeys.isEmpty())
			return 0;

		return ((double) sum / sortedListForTopicKeys.size());
	}
}
