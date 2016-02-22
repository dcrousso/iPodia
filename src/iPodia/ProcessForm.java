package iPodia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
//import java.sql.Timestamp;

public class ProcessForm {
	public static void processQuizUpload(QuizQuestion quizQuestion, String classId) {
		if (!quizQuestion.isValid())
			return;

		try {
			Class.forName(Defaults.dbDriver);
			final Connection dbConnection = DriverManager.getConnection(Defaults.dbURL, Defaults.dbUsername, Defaults.dbPassword); 
			PreparedStatement ps;

			ps = dbConnection.prepareStatement("REPLACE INTO class_" + classId + " (id, question, answerA, answerB, answerC, answerD, answerE, correctAnswer, topic) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
			ps.setString(1, quizQuestion.getId());
			ps.setString(2, quizQuestion.getQuestion());
			ps.setString(3, quizQuestion.getAnswer("A"));
			ps.setString(4, quizQuestion.getAnswer("B"));
			ps.setString(5, quizQuestion.getAnswer("C"));
			ps.setString(6, quizQuestion.getAnswer("D"));
			ps.setString(7, quizQuestion.getAnswer("E"));
			ps.setString(8, quizQuestion.getCorrectAnswer());
			//ps.setTimestamp(9, new Timestamp(0)); // TODO: Implement relaseDate functionality
			ps.setString(9, quizQuestion.getTopic());
			ps.executeUpdate();

			ps = dbConnection.prepareStatement("REPLACE INTO class_" + classId + "_matching (id) VALUES (?)");
			ps.setString(1, quizQuestion.getWeekId());
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
