package iPodia;

import java.io.File;
import java.io.IOException;
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
	private static final int MAX_REQUEST_SIZE = 1024 * 1024; // 1MB

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.sendRedirect(request.getContextPath() + "/");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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

		File dataFolder = new File(Defaults.DATA_DIRECTORY);
		if (!dataFolder.exists() || !dataFolder.isDirectory())
			dataFolder.mkdir();

		// Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload(factory);

		// Set overall request size constraint
		upload.setSizeMax(MAX_REQUEST_SIZE);

		String className = null;
		String week = null;
		HashSet<FileItem> uploadedFiles = new HashSet<FileItem>();

		try {
			for (FileItem item : upload.parseRequest(request)) {
				if (!item.isFormField())
					uploadedFiles.add(item);
				else if (item.getFieldName().equals("className"))
					className = item.getString();
				 else if (item.getFieldName().equals("week"))
					week = item.getString();
			}
		} catch (FileUploadException e) {
			throw new ServletException(e);
		}

		if (className == null || week == null) {
			response.sendRedirect(request.getContextPath() + "/");
			return;
		}

		for (FileItem item : uploadedFiles) {
			
			//make sure the file has a name
			if (!item.getName().equals("")) {
				String filePath = Defaults.DATA_DIRECTORY + File.separator
				+ className + File.separator
				+ week +  File.separator
				+ item.getName();

				File uploadedFile = new File(filePath);
				if (!uploadedFile.exists())
					uploadedFile.createNewFile();

				try {
					item.write(uploadedFile);
				} catch (Exception e) {
					throw new ServletException(e);
				}
			}
		}

		response.sendRedirect(request.getContextPath() + "/admin/week.jsp?className=" + className +"&week=" + week);
	}
}