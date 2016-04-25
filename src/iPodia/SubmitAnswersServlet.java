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
		User user = ((User) request.getSession().getAttribute("user"));
		if (user == null || !user.isStudent()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		String classId = request.getParameter("id");
		if (Defaults.isEmpty(classId) || !user.hasClass(classId)) {
			response.sendRedirect(request.getContextPath() + "/");
			return;
		}

		String userId = request.getParameter("user");
		if (Defaults.isEmpty(userId) || !user.getSafeEmail().equals(userId)) {
			response.sendRedirect(request.getContextPath() + "/");
			return;
		}

		for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
			if (Defaults.QUESTION_PATTERN.matcher(entry.getKey()).matches())
				ProcessForm.processAnswerSubmit(entry.getKey(), entry.getValue()[0], userId, classId);
		}

		response.sendRedirect(request.getContextPath() + "/student/class?id=" + classId);
	}
}