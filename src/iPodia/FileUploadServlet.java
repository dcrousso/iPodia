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
					continue;
				}

				// item is not a file
				String name = item.getFieldName();
				String value = item.getString();

				if (name.equals("id"))
					classId = value;
				else if (name.equals("num"))
					week = value;
				else {
					// if the item's filed name does not have the word Answer in it, you know that means it is a new question
					Matcher m = p.matcher(name);
					if (!m.find())
						continue;

					//prefix refers to Week?Topic?Question?
					String prefix = m.group(1);
					if (Defaults.isEmpty(prefix))
						continue;

					QuizQuestion quizQuestion = questionMap.get(prefix);
					if (quizQuestion == null) {
						quizQuestion = new QuizQuestion();
						quizQuestion.setId(prefix);
						questionMap.put(prefix, quizQuestion);
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
							quizQuestion.setCorrectAnswer(answerKey);
						else if (suffix.startsWith("Answer"))
							quizQuestion.setAnswer(answerKey, value);
					}
				}
			} // end for
		} catch (FileUploadException e) {
			throw new ServletException(e);
		}

		if (Defaults.isEmpty(classId) || Defaults.isEmpty(week)) {
			response.sendRedirect(request.getContextPath() + "/");
			return;
		}

		for (QuizQuestion question : questionMap.values())
			ProcessForm.processElements(question, classId);

		Defaults.createFolderIfNotExists(Defaults.DATA_DIRECTORY + File.separator + classId);
		Defaults.createFolderIfNotExists(Defaults.DATA_DIRECTORY + File.separator + classId + File.separator + week);

		for (FileItem item : uploadedFiles) {
			String fileName = item.getName();
			if (Defaults.isEmpty(fileName))
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