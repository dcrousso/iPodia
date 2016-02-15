package iPodia;

import java.util.HashMap;

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

	public String getId() {
		return m_id;
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
}
