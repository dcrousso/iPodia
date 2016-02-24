package iPodia;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;
import java.sql.ResultSetMetaData;

public class InClassMatching {

	private static int GROUP_SIZE = 4;
	
	public static void match(String classId, String week) {
		
		LinkedList<Map.Entry<String,Integer>> list= buildSortedList(classId, week);
		ArrayList<ArrayList<Map.Entry<String, Integer>>> groups = new ArrayList<ArrayList<Map.Entry<String, Integer>>>();
		
		if (list != null) {
			while (!list.isEmpty()) {
				ArrayList<Map.Entry<String, Integer>> aGroup = new ArrayList<Map.Entry<String, Integer>>();
				if (list.size() < GROUP_SIZE ) {
					
					while (!list.isEmpty()) {
						aGroup.add(list.removeFirst());
					}
					groups.add(aGroup);
					break;
					
				} else {  // have 4 or more in the list, 
					//if you have 4 students, just forms 1 group, 
					//if you only have 5 students left then add the extra student to the group
					//if you have 6 students left, make two groups of 3
					//if you have 7 students left, just make a group of 4 here, and then the next iteration of the while loop
					//will form a group of 3 students remaining
					
					Map.Entry<String, Integer> lowestScoringStudent = list.removeFirst();
					Map.Entry<String, Integer> secondLowestScoringStudent = list.removeFirst();
					Map.Entry<String, Integer> highestScoringStudent = list.removeLast();
					Map.Entry<String, Integer> secondHighestScoringStudent = list.removeLast();
					
					aGroup.add(lowestScoringStudent);
					aGroup.add(secondLowestScoringStudent);
					aGroup.add(highestScoringStudent);
					aGroup.add(secondHighestScoringStudent);
					System.out.println(list.size());
					
					//only had 5 students left in the list
					if (list.size() == 1) {
						aGroup.add(list.removeFirst());	// this is the middle student
						
					} else if (list.size() == 2) { 
						// had 6 students left in the list so want two groups of 3 for the last two groups
						//the first group will have the lowest scoring student and the two highest scoring students
						//the second group will have the second lowest scoring student and the two students in the middle so remove the 
						//second lowest scoring student (index 1 of the aGroup) from the first group and add them to the second group
						
						aGroup.remove(1);
						ArrayList<Map.Entry<String, Integer>> anotherGroup = new ArrayList<Map.Entry<String, Integer>>();
						Map.Entry<String, Integer> middleStudent1 = list.removeFirst();
						Map.Entry<String, Integer> middleStudent2 = list.removeFirst();
						
						anotherGroup.add(secondLowestScoringStudent);
						anotherGroup.add(middleStudent1);
						anotherGroup.add(middleStudent2);
						groups.add(anotherGroup);
					}				
					groups.add(aGroup);		
				}
			}
		}
		
		for (int i = 0; i < groups.size(); i ++) {
			ArrayList<Map.Entry<String, Integer>> group = groups.get(i);
			System.out.println("GROUP: " + i);
			for (int j = 0; j < group.size();  j++) {
				Map.Entry<String, Integer> entry = group.get(j);
				System.out.println("Email: " + entry.getKey() + " -- Score: " + entry.getValue() );
			}
			System.out.println("");
		}
	}
	
	private static LinkedList<Map.Entry<String,Integer>> buildSortedList(String classId, String week) {
		
		try {
			Class.forName(Defaults.dbDriver);
			final Connection dbConnection = DriverManager.getConnection(Defaults.dbURL, Defaults.dbUsername, Defaults.dbPassword); 
			PreparedStatement ps = null;
			
			ps = dbConnection.prepareStatement("Select * From class_" + classId + " WHERE id LIKE ?");
			ps.setString(1, "Week" + week + "InClass" + "%");
			ResultSet results = ps.executeQuery();	
			ResultSetMetaData rsmd =  results.getMetaData();
			
			Set<String> listOfStudents = new HashSet<String>();
	
			//sql columns start at index 1
			for (int i = 1; i <= rsmd.getColumnCount(); i ++) {
				if (!rsmd.getColumnName(i).equals("id")  && !rsmd.getColumnName(i).equals("question") &&
					!rsmd.getColumnName(i).equals("answerA")  && !rsmd.getColumnName(i).equals("answerB") &&
					!rsmd.getColumnName(i).equals("answerC")  && !rsmd.getColumnName(i).equals("answerD") &&
					!rsmd.getColumnName(i).equals("answerE")  && !rsmd.getColumnName(i).equals("correctAnswer") &&
					!rsmd.getColumnName(i).equals("topic")) {
					
					listOfStudents.add(rsmd.getColumnName(i));
				}	
			}
			
			Map<String, Integer> map = new HashMap<String, Integer>();
		
			while (results.next()) {
				String correctAnswer = results.getString("correctAnswer");
				Iterator<String> it = listOfStudents.iterator();
				while (it.hasNext()) {
					String email = it.next();
					
					//initialize the user's email in the map with an initial score of 0
					if (!map.containsKey(email)) {
						map.put(email, 0);
					}
					
					String studentAnswer = results.getString(email);
					
					if (studentAnswer != null && studentAnswer.equals(correctAnswer)) {
						map.put(email, map.get(email) + 1); 
					}	
				}
			}
		
			LinkedList<Map.Entry<String,Integer>> list = new LinkedList<Map.Entry<String,Integer>>(map.entrySet());
			Collections.sort(list, new Comparator<Map.Entry<String, Integer>> () {
				public int compare( Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
				     return (o1.getValue()).compareTo( o2.getValue());
				}
			});

			return list;

		} catch (SQLException e) {
			
		} catch (ClassNotFoundException e) {
			
		}
		
		return null;
	}
}