package iPodia;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AnalyticsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.sendRedirect(request.getContextPath() + "/");
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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

		HashMap<Integer, HashSet<String>> groups = Defaults.getStudentGroups(classId, "Week" + week);
		if (groups.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		HashMap<String, Integer> topic1 = Defaults.getStudentScores(classId, "Week" + week + "Topic1%");
		HashMap<String, Integer> topic2 = Defaults.getStudentScores(classId, "Week" + week + "Topic2%");
		HashMap<String, Integer> topic3 = Defaults.getStudentScores(classId, "Week" + week + "Topic3%");
		HashMap<String, Integer> topic4 = Defaults.getStudentScores(classId, "Week" + week + "Topic4%");

		JsonArrayBuilder result = Json.createArrayBuilder();
		for (HashMap.Entry<Integer, HashSet<String>> entry : groups.entrySet()) {
			JsonObjectBuilder info = Json.createObjectBuilder();
			for (String student : entry.getValue()) {
				User u = students.get(student);
				if (u != null) {
					info.add(u.getEmail(), Json.createObjectBuilder()
						.add("name", u.getName())
						.add("topic1", topic1.get(student))
						.add("topic2", topic2.get(student))
						.add("topic3", topic3.get(student))
						.add("topic4", topic4.get(student))
					);
				}
			}
			result.add(info);
		}

		response.getWriter().write(result.build().toString());
	}
}
