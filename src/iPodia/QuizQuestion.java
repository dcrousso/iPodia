package iPodia;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.regex.Matcher;

public class QuizQuestion {
	private String m_id;
	private String m_question;
	private HashMap<String, String> m_answers;
	private String m_correctAnswer;
	private String m_topic;

	public QuizQuestion() {
		m_id = null;
		m_question = null;
		m_answers = new HashMap<String, String>();
		m_correctAnswer = null;
		m_topic = "testTopic"; // TODO: Replace with real topic
	}

	public QuizQuestion(ResultSet rs) throws SQLException {
		m_answers = new HashMap<String, String>();
		initializeFromResultSet(rs);
	}

	public void initializeFromResultSet(ResultSet rs) throws SQLException {
		setId(rs.getString("id"));
		setQuestion(rs.getString("question"));
		setAnswer("A", rs.getString("answerA"));
		setAnswer("B", rs.getString("answerB"));
		setAnswer("C", rs.getString("answerC"));
		setAnswer("D", rs.getString("answerD"));
		setAnswer("E", rs.getString("answerE"));
		setCorrectAnswer(rs.getString("correctAnswer"));
		setTopic(rs.getString("topic"));
	}

	public String getId() {
		return m_id;
	}

	public String getWeekNumber() {
		Matcher m = Defaults.WEEK_PATTERN.matcher(m_id);
		if (!m.find())
			return m_id;
		return m.group(2);
	}

	public String getWeekId() {
		Matcher m = Defaults.WEEK_PATTERN.matcher(m_id);
		if (!m.find())
			return m_id;
		return m.group(1) + m.group(2);
	}

	public String getQuestion() {
		return m_question;
	}

	public String getAnswer(String key) {
		return m_answers.get(key);
	}

	public String getCorrectAnswer() {
		return m_correctAnswer;
	}

	public String getTopic() {
		return m_topic;
	}

	public void setId(String id) {
		m_id = id;
	}

	public void setQuestion(String question) {
		m_question = question;
	}

	public void setAnswer(String key, String answer) {
		m_answers.put(key, answer);
	}

	public void setCorrectAnswer(String correctAnswer) {
		m_correctAnswer = correctAnswer;
	}

	public void setTopic(String topic) {
		m_topic = topic;
	}

	public boolean isValid() {
		return !Defaults.isEmpty(m_id)
		    && !Defaults.isEmpty(m_question)
		    && m_answers.size() == 5
		    && !Defaults.isEmpty(m_answers.get("A"))
		    && !Defaults.isEmpty(m_answers.get("B"))
		    && !Defaults.isEmpty(m_answers.get("C"))
		    && !Defaults.isEmpty(m_answers.get("D"))
		    && !Defaults.isEmpty(m_answers.get("E"))
		    && !Defaults.isEmpty(m_correctAnswer)
		    && !Defaults.isEmpty(m_topic);
	}

	public String generateHTML() {
		return "<div class=\"quiz-item\">"
		       	+ "<textarea class=\"question\" placeholder=\"Question\" name=\"" + getId() + "\">" + getQuestion() + "</textarea>"
		       	+ "<input class=\"answer\" placeholder=\"Answer A\" name=\"" + getId() + "AnswerA\" value=\"" + getAnswer("A") + "\">"
		       	+ "<input class=\"answer\" placeholder=\"Answer B\" name=\"" + getId() + "AnswerB\" value=\"" + getAnswer("B") + "\">"
		       	+ "<input class=\"answer\" placeholder=\"Answer C\" name=\"" + getId() + "AnswerC\" value=\"" + getAnswer("C") + "\">"
		       	+ "<input class=\"answer\" placeholder=\"Answer D\" name=\"" + getId() + "AnswerD\" value=\"" + getAnswer("D") + "\">"
		       	+ "<input class=\"answer\" placeholder=\"Answer E\" name=\"" + getId() + "AnswerE\" value=\"" + getAnswer("E") + "\">"
		       	+ "<div class=\"correct\">"
		       	+ "<label>A:</label>"
		       		+ "<input type=\"radio\" name=\"" + getId() + "CorrectAnswer\" value=\"A\"" + (getCorrectAnswer().equals("A") ? " checked" : "") + ">"
		       		+ "<label>B:</label>"
		       		+ "<input type=\"radio\" name=\"" + getId() + "CorrectAnswer\" value=\"B\"" + (getCorrectAnswer().equals("B") ? " checked" : "") + ">"
		       		+ "<label>C:</label>"
		       		+ "<input type=\"radio\" name=\"" + getId() + "CorrectAnswer\" value=\"C\"" + (getCorrectAnswer().equals("C") ? " checked" : "") + ">"
		       		+ "<label>D:</label>"
		       		+ "<input type=\"radio\" name=\"" + getId() + "CorrectAnswer\" value=\"D\"" + (getCorrectAnswer().equals("D") ? " checked" : "") + ">"
		       		+ "<label>E:</label>"
		       		+ "<input type=\"radio\" name=\"" + getId() + "CorrectAnswer\" value=\"E\"" + (getCorrectAnswer().equals("E") ? " checked" : "") + ">"
		       	+ "</div>"
		       + "</div>";
	}
}
