package iPodia;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class FileUploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final int MAX_MEMORY_SIZE = 1024 * 1024 * 10; // 10MB
	private static final int MAX_REQUEST_SIZE = 1024 * 1024; // 1MB

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.sendRedirect(request.getContextPath() + "/");
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Check that we have a file upload request
		if (!ServletFileUpload.isMultipartContent(request))
			return;

		// Create a factory for disk-based file items
		DiskFileItemFactory factory = new DiskFileItemFactory();

		// Sets the size threshold beyond which files are written directly to disk
		factory.setSizeThreshold(MAX_MEMORY_SIZE);

		// Sets the directory used to temporarily store files that are larger than
		// the configured size threshold. We use temporary directory for java
		factory.setRepository(new File(System.getProperty("java.io.tmpdir")));

		Defaults.createFolderIfNotExists(Defaults.DATA_DIRECTORY);

		// Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload(factory);

		// Set overall request size constraint
		upload.setSizeMax(MAX_REQUEST_SIZE);

		String classId = null;
		String week = null;
		HashSet<FileItem> uploadedFiles = new HashSet<FileItem>();
		HashMap<String, QuizQuestion> questionMap = new HashMap<String, QuizQuestion>();
		String pattern = "(Week\\d+Topic\\d+Question\\d+)(\\w+)?";
		Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);

		try {
			for (FileItem item : upload.parseRequest(request)) {
				if (!item.isFormField()) {
					uploadedFiles.add(item);
				} else {
					if (item.getFieldName().equals("id")) {
						classId = item.getString();
					} else if (item.getFieldName().equals("num")) {
						week = item.getString();
					} else {
						//if the item's filed name does not have the word Answer in it, you know that means it is a new question
						String name = item.getFieldName();
						String value = item.getString();
						
						Matcher m = p.matcher(name);
						if (m.find()) {
					
							//prefix refers to Week?Topic?Question?
							String prefix = m.group(1);
							if (prefix != null) {	
					
								//m.group(2) represents answerA, answerB, ... CorrectAnswer in the name. if it is null, then it is just the question
								if (m.group(2) == null) {	
									QuizQuestion quizQuestion = questionMap.get(prefix);
									if (quizQuestion == null) {
										
										//the first item should just be the question, so create the quiz question object
										quizQuestion = new QuizQuestion();
										quizQuestion.setId(prefix);
										quizQuestion.setQuestion(value);
										questionMap.put(prefix, quizQuestion);
									} else {
										
										//in case one of the answers created the quiz question object first, need to set the object's question here
										quizQuestion.setQuestion(value);
									}
								} else {
									
									//this refers to the answers related to the specific question. The quiz question should have already been created
									//when the answers are being processed, but just double check
									QuizQuestion quizQuestion = questionMap.get(prefix);
									if (quizQuestion == null) {
										quizQuestion = new QuizQuestion();
										quizQuestion.setId(prefix);
										questionMap.put(prefix, quizQuestion);
									} else {
										String suffix = m.group(2);
										if (suffix.equals("AnswerA")) {
											quizQuestion.setAnswerA(value);
										} else if (suffix.equals("AnswerB")) {
											quizQuestion.setAnswerB(value);
										} else if (suffix.equals("AnswerC")) {
											quizQuestion.setAnswerC(value);
										} else if (suffix.equals("AnswerD")) {
											quizQuestion.setAnswerD(value);
										} else if (suffix.equals("AnswerE")) {
											quizQuestion.setAnswerE(value);
										} else if (suffix.contains("CorrectAnswer")) {
											
											//gets whether the correct answer is A, B, C, D, or E
											String correctAnswer = suffix.substring(suffix.length()-1);
											quizQuestion.setCorrectAnswer(correctAnswer);
										}
									}
								}
							}
						}// end if(m.find())
					}	
				}
			} //end for loop
		
			for (HashMap.Entry<String, QuizQuestion> entry : questionMap.entrySet()) {
				QuizQuestion quizQuestion = entry.getValue();
				if (quizQuestion.isValid()  && classId != null) {
					ProcessForm.processElements(quizQuestion, classId);
				}
				
			}
		} catch (FileUploadException e) {
			throw new ServletException(e);
		}

		if (classId == null || week == null) {
			response.sendRedirect(request.getContextPath() + "/");
			return;
		}

		Defaults.createFolderIfNotExists(Defaults.DATA_DIRECTORY + File.separator + classId);
		Defaults.createFolderIfNotExists(Defaults.DATA_DIRECTORY + File.separator + classId + File.separator + week);

		for (FileItem item : uploadedFiles) {
			String fileName = item.getName();
			if (fileName == null || fileName.trim().length() == 0)
				continue;

			String filePath = Defaults.DATA_DIRECTORY + File.separator
			+ classId + File.separator
			+ week +  File.separator
			+ fileName;

			try {
				item.write(Defaults.createFileIfNotExists(filePath));
			} catch (Exception e) {
				throw new ServletException(e);
			}
		}

		response.sendRedirect(request.getContextPath() + "/admin/week?id=" + classId +"&num=" + week);
	}
}