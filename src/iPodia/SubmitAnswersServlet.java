package iPodia;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SubmitAnswersServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.sendRedirect(request.getContextPath() + "/");
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String classId = request.getParameter("id");
		if (Defaults.isEmpty(classId)) {
			response.sendRedirect(request.getContextPath() + "/");
			return;
		}

		String user = request.getParameter("user");
		if (Defaults.isEmpty(classId)) {
			response.sendRedirect(request.getContextPath() + "/");
			return;
		}

		for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
			if (Defaults.QUESTION_PATTERN.matcher(entry.getKey()).matches())
				ProcessForm.processAnswerSubmit(entry.getKey(), entry.getValue()[0], user, classId);
		}

		response.sendRedirect(request.getContextPath() + "/student/class?id=" + classId);
	}
}