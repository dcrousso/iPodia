package iPodia;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.regex.Matcher;

public class QuizQuestion implements Comparable<QuizQuestion> {
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

	public static void processRequestItem(HashMap<String, QuizQuestion> existing, String key, String value) {
		if (existing == null || Defaults.isEmpty(key) || Defaults.isEmpty(value))
			return;

		// if the item's filed name does not have the word Answer in it, you know that means it is a new question
		Matcher m = Defaults.QUESTION_PATTERN.matcher(key);
		if (!m.find())
			return;

		// prefix refers to Week?Topic?Question?
		String prefix = m.group(1);
		if (Defaults.isEmpty(prefix))
			return;

		QuizQuestion quizQuestion = existing.get(prefix);
		if (quizQuestion == null) {
			quizQuestion = new QuizQuestion();
			quizQuestion.setId(prefix);
			existing.put(prefix, quizQuestion);
		}

		// m.group(2) represents answerA, answerB, ... CorrectAnswer in the name.
		// If it is null, then it is just the question.
		String suffix = m.group(2);
		if (Defaults.isEmpty(suffix))
			quizQuestion.setQuestion(value);
		else {
			// Gets the answer key (A, B, C, D, E)
			String answerKey = suffix.substring(suffix.length() - 1);
			if (suffix.startsWith("CorrectAnswer"))
				quizQuestion.setCorrectAnswer(value);
			else if (suffix.startsWith("Answer"))
				quizQuestion.setAnswer(answerKey, value);
		}
	}

	public String getId() {
		return m_id;
	}

	public boolean isInClass() {
		return m_id.contains("InClass");
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

	public String generateAdminHTML() {
		return "<div class=\"item\">"
		       	+ "<textarea class=\"question\" placeholder=\"Question\" name=\"" + getId() + "\" required>" + getQuestion() + "</textarea>"
		       	+ "<input class=\"answer\" placeholder=\"Answer A\" name=\"" + getId() + "AnswerA\" value=\"" + getAnswer("A") + "\" required>"
		       	+ "<input class=\"answer\" placeholder=\"Answer B\" name=\"" + getId() + "AnswerB\" value=\"" + getAnswer("B") + "\" required>"
		       	+ "<input class=\"answer\" placeholder=\"Answer C\" name=\"" + getId() + "AnswerC\" value=\"" + getAnswer("C") + "\" required>"
		       	+ "<input class=\"answer\" placeholder=\"Answer D\" name=\"" + getId() + "AnswerD\" value=\"" + getAnswer("D") + "\" required>"
		       	+ "<input class=\"answer\" placeholder=\"Answer E\" name=\"" + getId() + "AnswerE\" value=\"" + getAnswer("E") + "\" required>"
		       	+ "<div class=\"correct\">"
		       		+ "<label>A</label>"
		       		+ "<input type=\"radio\" name=\"" + getId() + "CorrectAnswer\" value=\"A\"" + (getCorrectAnswer().equals("A") ? " checked" : "") + " required>"
		       		+ "<label>B</label>"
		       		+ "<input type=\"radio\" name=\"" + getId() + "CorrectAnswer\" value=\"B\"" + (getCorrectAnswer().equals("B") ? " checked" : "") + " required>"
		       		+ "<label>C</label>"
		       		+ "<input type=\"radio\" name=\"" + getId() + "CorrectAnswer\" value=\"C\"" + (getCorrectAnswer().equals("C") ? " checked" : "") + " required>"
		       		+ "<label>D</label>"
		       		+ "<input type=\"radio\" name=\"" + getId() + "CorrectAnswer\" value=\"D\"" + (getCorrectAnswer().equals("D") ? " checked" : "") + " required>"
		       		+ "<label>E</label>"
		       		+ "<input type=\"radio\" name=\"" + getId() + "CorrectAnswer\" value=\"E\"" + (getCorrectAnswer().equals("E") ? " checked" : "") + " required>"
		       	+ "</div>"
		       + "</div>";
	}

	public String generateStudentHTML(String selected) {
		return "<div class=\"item\">"
		       	+ "<p>" + getQuestion() + "</p>"
		       	+ "<div class=\"answer\">"
		       		+ "<input type=\"radio\" name=\"" + getId() + "\" value=\"A\"" + (selected.equals("A") ? " checked" : "") + " required>"
		       		+ "<p>" + getAnswer("A") + "</p>"
		       	+ "</div>"
		       	+ "<div class=\"answer\">"
		       		+ "<input type=\"radio\" name=\"" + getId() + "\" value=\"B\"" + (selected.equals("B") ? " checked" : "") + " required>"
		       		+ "<p>" + getAnswer("B") + "</p>"
		       	+ "</div>"
		       	+ "<div class=\"answer\">"
		       		+ "<input type=\"radio\" name=\"" + getId() + "\" value=\"C\"" + (selected.equals("C") ? " checked" : "") + " required>"
		       		+ "<p>" + getAnswer("C") + "</p>"
		       	+ "</div>"
		       	+ "<div class=\"answer\">"
		       		+ "<input type=\"radio\" name=\"" + getId() + "\" value=\"D\"" + (selected.equals("D") ? " checked" : "") + " required>"
		       		+ "<p>" + getAnswer("D") + "</p>"
		       	+ "</div>"
		       	+ "<div class=\"answer\">"
		       		+ "<input type=\"radio\" name=\"" + getId() + "\" value=\"E\"" + (selected.equals("E") ? " checked" : "") + " required>"
		       		+ "<p>" + getAnswer("E") + "</p>"
		       	+ "</div>"
		       + "</div>";
	}

	@Override
	public int compareTo(QuizQuestion other) {
		// Ensures that questions in sooner weeks (meaning higher week numbers)
		// are first in the list, but questions in that week are ordered normally
		return m_id.compareTo(other.getId()) * (getWeekId().equals(other.getWeekId()) ? 1 : -1);
	}
}
