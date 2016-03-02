package iPodia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.regex.Matcher;

public class ProcessForm {
	public static void processQuizUpload(QuizQuestion question, String classId) {
		if (question == null || !question.isValid())
			return;

		try {
			Class.forName(Defaults.dbDriver);
			final Connection dbConnection = DriverManager.getConnection(Defaults.dbURL, Defaults.dbUsername, Defaults.dbPassword); 
			PreparedStatement ps;

			ps = dbConnection.prepareStatement("REPLACE INTO class_" + classId + " (id, question, answerA, answerB, answerC, answerD, answerE, correctAnswer, topic) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
			ps.setString(1, question.getId());
			ps.setString(2, question.getQuestion());
			ps.setString(3, question.getAnswer("A"));
			ps.setString(4, question.getAnswer("B"));
			ps.setString(5, question.getAnswer("C"));
			ps.setString(6, question.getAnswer("D"));
			ps.setString(7, question.getAnswer("E"));
			ps.setString(8, question.getCorrectAnswer());
			ps.setString(9, question.getTopic());
			ps.executeUpdate();

			ps = dbConnection.prepareStatement("REPLACE INTO class_" + classId + "_matching (id) VALUES (?)");
			ps.setString(1, question.getWeekId());
			ps.executeUpdate();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void processAnswerSubmit(String question, String chosenAnswer, String user, String classId) {
		if (Defaults.isEmpty(question) || Defaults.isEmpty(chosenAnswer) || Defaults.isEmpty(user))
			return;

		try {
			Class.forName(Defaults.dbDriver);
			final Connection dbConnection = DriverManager.getConnection(Defaults.dbURL, Defaults.dbUsername, Defaults.dbPassword); 

			Matcher m = Defaults.WEEK_PATTERN.matcher(question);
			if (m.find()) {
				PreparedStatement test = dbConnection.prepareStatement("SELECT * FROM class_" + classId + " WHERE id LIKE ?");
				test.setString(1, "Week" + (Integer.parseInt(m.group(2)) + 1) + "%");
				if (test.executeQuery().next())
					return;
			}

			PreparedStatement ps = dbConnection.prepareStatement("UPDATE class_" + classId + " SET " + Defaults.createSafeString(user) + " = ? WHERE id = ?");
			ps.setString(1, chosenAnswer);
			ps.setString(2, question);
			ps.executeUpdate();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
