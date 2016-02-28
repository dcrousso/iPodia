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
import java.util.Map;
import java.util.Map.Entry;
import java.util.ArrayList;

public class QuizMatching {

	public static void match(String classId, String week) {

		
		LinkedList<LinkedList<String>> allSortedLists = new LinkedList<LinkedList<String>>(); 
		ArrayList<Double> listOfAverageScores = new ArrayList<Double>();

		HashMap<String, ArrayList<Integer>> studentScoresForEachTopic = new HashMap<String, ArrayList<Integer>>();
		
		String [] topicNums = {"1", "2", "3", "4"};	
		for (int i = 0; i < 4; i ++) {
			LinkedList<HashMap.Entry<String,Integer>> sortedListForTopic = buildSortedList(classId, week, topicNums[i]);
			LinkedList<String> sortedListForTopicKeys = new LinkedList<String>();
	
			for (int j = 0; j < sortedListForTopic.size(); j++) {
				HashMap.Entry<String, Integer> student = sortedListForTopic.get(j);
				String email = student.getKey();
				int scoreForTopic = student.getValue();
			
				sortedListForTopicKeys.add(email);
				
				ArrayList<Integer> studentScoreList = studentScoresForEachTopic.get(email);
				if (studentScoreList == null) {
					studentScoreList = new ArrayList<Integer>();
				}
				studentScoreList.add(scoreForTopic);
				studentScoresForEachTopic.put(email, studentScoreList);
			}
			
			//once you have the studentScores for each topic, this can serve as a look up table, so now you just need their email
			//as a key instead of a Map.Entry<String,Integer>
			allSortedLists.add(sortedListForTopicKeys);
			double avgScore = findClassAvgForTopic(sortedListForTopicKeys, studentScoresForEachTopic, i );
			listOfAverageScores.add(avgScore);
		}

		LinkedList<HashSet<String>> optimalGrouping = findOptimalInitialGrouping(allSortedLists, studentScoresForEachTopic, listOfAverageScores);

		double optimalGroupingInitialDeviationForEntireSet = 0.0;
		for (int x = 0; x < optimalGrouping.size(); x++) {
			HashSet<String> group = optimalGrouping.get(x);
			
			optimalGroupingInitialDeviationForEntireSet += overallDeviationPerGroup(group, studentScoresForEachTopic, listOfAverageScores);
		}
		
		System.out.println("initial deviation for optimal = " + optimalGroupingInitialDeviationForEntireSet);
		for (int i = 0; i <optimalGrouping.size(); i ++ ){
			System.out.println("Group " + (i+1)+ ":");
			HashSet<String> group = optimalGrouping.get(i);
			for (String email: group) {
				System.out.println(email + "---");
				ArrayList<Integer> studentScoreList = studentScoresForEachTopic.get(email);
				for (int k = 0; k <studentScoreList.size(); k++) {
					System.out.print("Topic " + (k+1) +": " + studentScoreList.get(k) + " ");
					System.out.println("");
				}
			}
			System.out.println("");
		}
		
		performSwaps(optimalGrouping, studentScoresForEachTopic, listOfAverageScores);

		double deviationForEntireSet = 0.0;
		for (int i = 0; i < optimalGrouping.size(); i++) {
			HashSet<String> group = optimalGrouping.get(i);		
			deviationForEntireSet += overallDeviationPerGroup(group, studentScoresForEachTopic, listOfAverageScores);
		}
		
		System.out.println("After swaps " + deviationForEntireSet);	
		for (int i = 0; i <optimalGrouping.size(); i ++ ){
			System.out.println("Group " + (i+1)+ ":");
			HashSet<String> group = optimalGrouping.get(i);
			for (String email : group) {
				System.out.println(email+ "---");
				ArrayList<Integer> studentScoreList = studentScoresForEachTopic.get(email);
				for (int k = 0; k <studentScoreList.size(); k++) {
					System.out.print("Topic " + (k+1) +": " + studentScoreList.get(k) + "   ");
					System.out.println("");
				}
			}
			System.out.println("");
		}
	}
	
	private static void performSwaps(LinkedList<HashSet<String>> optimalGrouping, HashMap<String, ArrayList<Integer>> studentScoresForEachTopic, ArrayList<Double> listOfAverageScores) {
		
		for (int a = 0; a < 10000; a++) {
			boolean anySwapOccured = false;
			for (int i = 0; i < optimalGrouping.size(); i++) {
				
				HashSet<String> group1 = optimalGrouping.get(i);
				HashSet<String> group2 = null;
				
				if (i != optimalGrouping.size() - 1) {
					group2 = optimalGrouping.get(i+1);
				}
				
				if (group2 == null) {
					break;
				}
				
				double initialTotalDeviation = overallDeviationPerGroup(group1, studentScoresForEachTopic, listOfAverageScores) + overallDeviationPerGroup(group2, studentScoresForEachTopic, listOfAverageScores);
				boolean swapBetweenTwoGroupsOccured = false;
				
				HashSet<String> tempGroup1 = new HashSet<String>(group1);
				HashSet<String> tempGroup2 = new HashSet<String>(group2);
				for (String studentInGroup1: group1) {
					for (String studnetInGroup2: group2) {
						//can't remove students and add then just add them back to the proper groups if the postDeviation is worst.  This causes a concurency problem because
						//you are iterating over the set and by adding back to the students to the original groups at the same time.  So  Instead, use temporary groups to test out if this swap.
						//If It is a good swap, modify the actual groups, if it is a bad swap, revert the the temporary groups to their original state
						tempGroup1.remove(studentInGroup1);
						tempGroup2.remove(studnetInGroup2);
						
						tempGroup1.add(studnetInGroup2);
						tempGroup2.add(studentInGroup1);
						
						double postTotalDeviation = overallDeviationPerGroup(tempGroup1, studentScoresForEachTopic, listOfAverageScores) + overallDeviationPerGroup(tempGroup2, studentScoresForEachTopic, listOfAverageScores);
					
						if (postTotalDeviation < initialTotalDeviation) {
							anySwapOccured = true;
							swapBetweenTwoGroupsOccured = true;
							
							//swap was good so do the swap with the actual groups
							group1.remove(studentInGroup1);
							group2.remove(studnetInGroup2);
							
							group1.add(studnetInGroup2);
							group2.add(studentInGroup1);
							
							//if student in group 1 swaps with student in group 2, don't want to do any more swaps this iteration.  Because you are still comparing studentInGroup1 (who is actually in group 2 now)
							//with other students in group 2, and don't want to switch students within the same group.
							break;
						} 
						else {
							tempGroup1.remove(studnetInGroup2);
							tempGroup2.remove(studentInGroup1);

							tempGroup1.add(studentInGroup1);
							tempGroup2.add(studnetInGroup2);
						}
					}
					if (swapBetweenTwoGroupsOccured) {
						break;
					}

				}	
				
			}
			
			double deviationForEntireSet = 0.0;
			for (int x = 0; x < optimalGrouping.size(); x++) {
				HashSet<String> group = optimalGrouping.get(x);		
				deviationForEntireSet += overallDeviationPerGroup(group, studentScoresForEachTopic, listOfAverageScores);	
			}
			
			System.out.println("Entire deviation for set = " + deviationForEntireSet);
			//no groups swapped at all, so can just end the for loop because already found the optimal grouping
			if (!anySwapOccured) {
				break;
			}
		}
		
	}
	
	private static double overallDeviationPerGroup(HashSet<String> group, HashMap<String, ArrayList<Integer>> studentScoresForEachTopic, ArrayList<Double> listOfAverageScores) {
		double[] combinedScoreForEachTopic = {0,0,0,0};
		for (String email : group) {
			ArrayList<Integer> studentScoreList = studentScoresForEachTopic.get(email);
			if (studentScoreList == null) {
				continue;
			}
			for (int j = 0; j < studentScoreList.size(); j++) {
				combinedScoreForEachTopic[j] += studentScoreList.get(j);
			}
		}
		
		double overallDeviationPerGroup = 0.0;
		for (int j = 0; j < 4; j ++) {
			double targetScoreForTopic = listOfAverageScores.get(j) * group.size();
			overallDeviationPerGroup += Math.abs(combinedScoreForEachTopic[j] - targetScoreForTopic);
		}
		return overallDeviationPerGroup;
	}
	
	private static LinkedList<HashSet<String>> findOptimalInitialGrouping (LinkedList<LinkedList<String>> allSortedLists, HashMap<String, ArrayList<Integer>> studentScoresForEachTopic, ArrayList<Double> listOfAverageScores ) {
		double minimumDeviation = Double.MAX_VALUE;
		
		LinkedList<HashSet<String>> optimalInitialGrouping = null;
		for (int i = 0; i < allSortedLists.size(); i++) {
			LinkedList<String> sortedListForTopicKeys = allSortedLists.get(i);	
			if (sortedListForTopicKeys != null) {
				
				//need to find avgScore before forming groups for the topic, because when you form groups, you remove items from the list
				double avgScore = listOfAverageScores.get(i);
				LinkedList<HashSet<String>> groupsForTopic = buildGroupsForTopic(sortedListForTopicKeys);
				
				double overallDeviationForTopic = 0.0;
				for (HashSet<String> group : groupsForTopic) {
					
					int groupSum = 0;
					int numGroupMembers = 0;
					for (String student : group) {
						ArrayList<Integer> studentScoreList = studentScoresForEachTopic.get(student);
						int studentScoreForTopic = studentScoreList.get(i);
						
						groupSum += studentScoreForTopic;
						numGroupMembers++;
					}
					
					double targetScore = avgScore * numGroupMembers;
					double groupDeviationFromTarget = Math.abs((double)groupSum - targetScore);
					overallDeviationForTopic += groupDeviationFromTarget;	
				}
				
				
				
				//found a group with 0 deviation for a given topic, so it could be the best possible initial grouping.  However, need to make sure
				//the groupsForTopic is not empty which can occur if the teacher doesn't have a question for a topic because an empty grouping would
				//also have an overall deviation of 0.
				if (overallDeviationForTopic == 0 ) {
					if (!groupsForTopic.isEmpty()) {
						//can't combine these two if statements with && because if the group is empty it will have an overallDeviationForTopic of 0. Since it is empty, we will
						//go to the else statement, but we don't want to set to set optimalIntialGrouping to an empty group
						return groupsForTopic;
					}
				} else {
					if (overallDeviationForTopic < minimumDeviation) {
						optimalInitialGrouping = new LinkedList<HashSet<String>>(groupsForTopic);
						minimumDeviation = overallDeviationForTopic;
					}
				}
			}
		}
		return optimalInitialGrouping;
	}
	
	private static LinkedList<HashSet<String>>  buildGroupsForTopic(LinkedList<String> sortedListForTopicKeys) {
		LinkedList<HashSet<String>> groupsForTopic = new LinkedList<HashSet<String>>();
		while (!sortedListForTopicKeys.isEmpty()) {
			HashSet<String> group = new HashSet<String>();
			groupsForTopic.add(group);

			group.add(sortedListForTopicKeys.removeFirst());
			group.add(sortedListForTopicKeys.removeLast());
			if (sortedListForTopicKeys.size() < 4) { // 5 or less students left, so make a group of everyone
				while (!sortedListForTopicKeys.isEmpty())
					group.add(sortedListForTopicKeys.removeFirst());
			} else if (sortedListForTopicKeys.size() > 4) { // 7+ students left, so take out 4 and make a group
				group.add(sortedListForTopicKeys.removeFirst());
				group.add(sortedListForTopicKeys.removeLast());
			} else // 6 students left, so split into 3 and 3
				group.add(sortedListForTopicKeys.removeFirst());
		}
		return groupsForTopic;
	}
	
	private static double findClassAvgForTopic(LinkedList<String> sortedListForTopicKeys, HashMap<String, ArrayList<Integer>> studentScoresForEachTopic, int indexOfTopic ) {
		
		int sum = 0;
		for (int i = 0; i <sortedListForTopicKeys.size(); i++) {
			String email = sortedListForTopicKeys.get(i);
			ArrayList<Integer> studentScoreList = studentScoresForEachTopic.get(email);
			
			sum += studentScoreList.get(indexOfTopic);		
		}
		

		if (sortedListForTopicKeys.size() == 0)
			return 0;
		else
			return (double)sum / sortedListForTopicKeys.size();
		
	}
	
	private static LinkedList<HashMap.Entry<String,Integer>> buildSortedList(String classId, String week, String topicNum) {
		if (Defaults.isEmpty(classId) || Defaults.isEmpty(week))
			return null;

		try {
			Class.forName(Defaults.dbDriver);
			final Connection dbConnection = DriverManager.getConnection(Defaults.dbURL, Defaults.dbUsername, Defaults.dbPassword); 

			PreparedStatement ps = dbConnection.prepareStatement("Select * From class_" + classId + " WHERE id LIKE ?");
			ps.setString(1, "Week" + week + "Topic" + topicNum + "%");
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
					if (!map.containsKey(email)) {
						map.put(email, 0); // initialize the user's email in the map with a score of 0
					}

					if (!Defaults.isEmpty(studentAnswer) && studentAnswer.equals(correctAnswer)) {
						map.put(email, map.get(email) + 1);
					}
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