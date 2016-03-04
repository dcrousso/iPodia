package iPodia;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

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
	private static final int MAX_REQUEST_SIZE = 1024 * 1024 * 10; // 10MB

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
				else
					QuizQuestion.processRequestItem(questionMap, name, value);
			}
		} catch (FileUploadException e) {
		}

		if (Defaults.isEmpty(classId) || Defaults.isEmpty(week)) {
			response.sendRedirect(request.getContextPath() + "/");
			return;
		}

		HashSet<QuizQuestion> existing = Defaults.getQuestionsForWeekTopic(classId, week);
		for (QuizQuestion question : questionMap.values()) {
			if (existing == null || !Defaults.contains(existing, item -> item.equalTo(question)))
				ProcessForm.processQuizUpload(question, classId);
		}

		Defaults.createFolderIfNotExists(Defaults.DATA_DIRECTORY + classId);
		Defaults.createFolderIfNotExists(Defaults.DATA_DIRECTORY + classId + File.separator + week);

		for (FileItem item : uploadedFiles) {
			String fileName = item.getName();
			if (Defaults.isEmpty(fileName))
				continue;

			String filePath = Defaults.DATA_DIRECTORY
			+ classId + File.separator
			+ week +  File.separator
			+ fileName;

			try {
				item.write(Defaults.createFileIfNotExists(filePath));
			} catch (Exception e) {
			}
		}

		response.sendRedirect(request.getContextPath() + "/admin/week?id=" + classId +"&num=" + week);
	}
}