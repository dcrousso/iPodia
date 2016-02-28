package iPodia;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MatchingServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.sendRedirect(request.getContextPath() + "/");
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String type = request.getParameter("type");
		if (Defaults.isEmpty(type)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		String classId = request.getParameter("id");
		if (Defaults.isEmpty(classId)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		String week = request.getParameter("num");
		if (Defaults.isEmpty(classId)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		if (type.equals(Defaults.inClassMatching))
			InClassMatching.match(classId, week);
		else if (type.equals(Defaults.beforeClassMatching)) {
			QuizMatching.match(classId, week);
		} else {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		response.setStatus(HttpServletResponse.SC_OK);
	}
}