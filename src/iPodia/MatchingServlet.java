package iPodia;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
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

		HashMap<String, User> students = Defaults.getStudentsBySafeEmailForClass(classId);
		if (students == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		LinkedList<HashSet<String>> groups = null;
		if (type.equals("inClassMatching"))
			groups = InClassMatching.match(classId, week);
		else if (type.equals("beforeClassMatching")) {
			groups = QuizMatching.match(classId, week);
			Defaults.saveGroupNumbers(groups, classId, week);
		} else {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		JsonArrayBuilder result = Json.createArrayBuilder();
		for (HashSet<String> group : groups) {
			JsonObjectBuilder info = Json.createObjectBuilder();
			for (String safeEmail : group) {
				User u = students.get(safeEmail);
				if (u != null)
					info.add(u.getEmail(), u.getName());
			}
			result.add(info);
		}

		response.getWriter().write(result.build().toString());
	}
}
