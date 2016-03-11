package iPodia;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;

public class ProcessForm {
	public static void processQuizUpload(QuizQuestion question, String classId) {
		if (question == null || !question.isValid())
			return;

		PreparedStatement ps = null;
		try {
			ps = Defaults.getDBConnection().prepareStatement("REPLACE INTO class_" + classId + " (id, question, answerA, answerB, answerC, answerD, answerE, correctAnswer, topic) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
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
			ps.close();

			ps = Defaults.getDBConnection().prepareStatement("REPLACE INTO class_" + classId + "_matching (id) VALUES (?)");
			ps.setString(1, question.getWeekId());
			ps.executeUpdate();
			ps.close();
			
			ps = Defaults.getDBConnection().prepareStatement("REPLACE INTO class_" + classId + "_matching (id) VALUES (?)");
			ps.setString(1, question.getWeekId() + "InClass");
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
		} finally {
			try {
				if (ps != null && !ps.isClosed())
					ps.close();
			} catch (SQLException e) {
			}
		}
		Defaults.closeDBConnection();
	}

	public static void processAnswerSubmit(String question, String chosenAnswer, String user, String classId) {
		if (Defaults.isEmpty(question) || Defaults.isEmpty(chosenAnswer) || Defaults.isEmpty(user))
			return;

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			Matcher m = Defaults.WEEK_PATTERN.matcher(question);
			if (m.find()) {
				ps = Defaults.getDBConnection().prepareStatement("SELECT * FROM class_" + classId + " WHERE id LIKE ?");
				ps.setString(1, "Week" + (Integer.parseInt(m.group(2)) + 1) + "%");
				rs = ps.executeQuery();
				if (rs.next()) {
					rs.close();
					ps.close();
					Defaults.closeDBConnection();
					return;
				}

				rs.close();
				ps.close();
			}

			ps = Defaults.getDBConnection().prepareStatement("UPDATE class_" + classId + " SET " + Defaults.createSafeString(user) + " = ? WHERE id = ?");
			ps.setString(1, chosenAnswer);
			ps.setString(2, question);
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
		} finally {
			try {
				if (rs != null && !rs.isClosed())
					rs.close();

				if (ps != null && !ps.isClosed())
					ps.close();
			} catch (SQLException e) {
			}
		}
		Defaults.closeDBConnection();
	}
}
