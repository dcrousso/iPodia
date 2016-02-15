package iPodia;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
//import java.sql.Timestamp;
import java.sql.Connection;

public class ProcessForm {
	
	public static void processElements (QuizQuestion quizQuestion, String classId) {
		try {		
			Class.forName(Defaults.dbDriver);
			final Connection dbConnection = DriverManager.getConnection(Defaults.dbURL, Defaults.dbUsername, Defaults.dbPassword); 
			PreparedStatement ps = null;
			
			String id = quizQuestion.getId();
			String question = quizQuestion.getQuestion();
			String answerA = quizQuestion.getAnswerA();
			String answerB = quizQuestion.getAnswerB();
			String answerC = quizQuestion.getAnswerC();
			String answerD = quizQuestion.getAnswerD();
			String answerE = quizQuestion.getAnswerE();
			String correctAnswer = quizQuestion.getCorrectAnswer();
			String topic = "testTopic";
			
	
			ps = dbConnection.prepareStatement("INSERT INTO class_" + classId + " (id, question, answerA, answerB, answerC, answerD, answerE, correctAnswer, topic) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			ps.setString(1, id);
			ps.setString(2, question);
			ps.setString(3, answerA);
			ps.setString(4, answerB);
			ps.setString(5, answerC);
			ps.setString(6, answerD);
			ps.setString(7, answerE);
			ps.setString(8, correctAnswer);
			//ps.setTimestamp(9, new Timestamp(0));
			ps.setString(9, topic);
			ps.executeUpdate();
	
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
