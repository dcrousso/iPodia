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
		if (Defaults.isEmpty(week)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		HashMap<String, User> students = Defaults.getStudentsBySafeEmailForClass(classId);
		if (students == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		HashMap<Integer, HashSet<String>> groups = Defaults.getStudentGroups(classId, "Week" + week);
		if (groups == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		HashMap<String, Integer> topic1Before = Defaults.getStudentScores(classId, "Week" + week + "Topic1%", Defaults.beforeMatching);
		HashMap<String, Integer> topic2Before = Defaults.getStudentScores(classId, "Week" + week + "Topic2%", Defaults.beforeMatching);
		HashMap<String, Integer> topic3Before = Defaults.getStudentScores(classId, "Week" + week + "Topic3%", Defaults.beforeMatching);
		HashMap<String, Integer> topic4Before = Defaults.getStudentScores(classId, "Week" + week + "Topic4%", Defaults.beforeMatching);

		HashMap<String, Integer> topic1After = Defaults.getStudentScores(classId, "Week" + week + "Topic1%", Defaults.afterMatching);
		HashMap<String, Integer> topic2After = Defaults.getStudentScores(classId, "Week" + week + "Topic2%", Defaults.afterMatching);
		HashMap<String, Integer> topic3After = Defaults.getStudentScores(classId, "Week" + week + "Topic3%", Defaults.afterMatching);
		HashMap<String, Integer> topic4After = Defaults.getStudentScores(classId, "Week" + week + "Topic4%", Defaults.afterMatching);

		JsonArrayBuilder result = Json.createArrayBuilder();
		for (HashMap.Entry<Integer, HashSet<String>> entry : groups.entrySet()) {
			JsonObjectBuilder info = Json.createObjectBuilder();
			for (String student : entry.getValue()) {
				if (!students.containsKey(student))
					continue;

				info.add(students.get(student).getEmail(), Json.createObjectBuilder()
					.add("name", students.get(student).getName())
					.add("topic1", Json.createObjectBuilder()
						.add("before", topic1Before.get(student))
						.add("after", topic1After.get(student))
					.build())
					.add("topic2", Json.createObjectBuilder()
						.add("before", topic2Before.get(student))
						.add("after", topic2After.get(student))
					.build())
					.add("topic3", Json.createObjectBuilder()
						.add("before", topic3Before.get(student))
						.add("after", topic3After.get(student))
					.build())
					.add("topic4", Json.createObjectBuilder()
						.add("before", topic4Before.get(student))
						.add("after", topic4After.get(student))
					.build())
				.build());
			}
			result.add(info.build());
		}

		response.getWriter().write(result.build().toString());
	}
}
