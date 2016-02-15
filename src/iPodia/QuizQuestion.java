package iPodia;

public class QuizQuestion {

	private String id;
	private String question;
	private String answerA;
	private String answerB;
	private String answerC;
	private String answerD;
	private String answerE;
	private String correctAnswer;
	
	public QuizQuestion() {
		id = "";
		question = "";
		answerA = "";
		answerB = "";
		answerC = "";
		answerD = "";
		answerE = "";
		correctAnswer = "";
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public void setQuestion(String question) {
		this.question = question;
	}
	
	public void setAnswerA(String answerA) {
		this.answerA = answerA;
	}
	
	public void setAnswerB(String answerB) {
		this.answerB = answerB;
	}
	
	public void setAnswerC (String answerC) {
		this.answerC = answerC;
	}
	
	public void setAnswerD (String answerD) {
		this.answerD = answerD;
	}
	
	public void setAnswerE(String answerE) {
		this.answerE = answerE;
	}
	
	public void setCorrectAnswer(String correctAnswer) {
		this.correctAnswer = correctAnswer;
	}
	
	public String getId() {
		return id;
	}
	
	public String getQuestion() {
		return question;
	}
	
	public String getAnswerA() {
		return answerA;
	}
	
	public String getAnswerB() {
		return answerB;
	}
	
	public String getAnswerC() {
		return answerC;
	}
	
	public String getAnswerD() {
		return answerD;
	}
	
	public String getAnswerE() {
		return answerE;
	}
	
	public String getCorrectAnswer() {
		return correctAnswer;
	}
	
	public boolean isValid() {
		//makes sure all of the fields are set
		if (id.equals("") || question.equals("") || answerA.equals("") || answerB.equals("") || answerC.equals("") || answerD.equals("") || answerE.equals("") || correctAnswer.equals("")) {
			return false;
		}
		return true;
	}
	
}
