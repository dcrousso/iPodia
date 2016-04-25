package iPodia;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map.Entry;

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
		User user = ((User) request.getSession().getAttribute("user"));
		if (user == null || !user.isAdmin()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		String type = request.getParameter("type");
		if (Defaults.isEmpty(type)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		String algorithm = request.getParameter("algorithm");
		if (Defaults.isEmpty(algorithm)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		String classId = request.getParameter("id");
		if (Defaults.isEmpty(classId) || !user.hasClass(classId)) {
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
		HashMap<String,Integer> recommendations = null;
		boolean recommendTopic = false;
		if (type.equals("inClassMatching")) {
			if (algorithm.equals("mostFair")){
				groups = InClassMatching.mostFairMatch(classId, week);
				Defaults.saveGroupNumbers(groups, classId, week, "InClass");
			} else if (algorithm.equals("recommendation")){
				//don't need to provide recommendations for in class questions because there is really only one topic
				//for in class questions.  So this will just generate random groups, not optimal groups
				groups = InClassMatching.recommendationMatch(classId, week);
				Defaults.saveGroupNumbers(groups, classId, week, "InClass");
			}
			
		} else if (type.equals("beforeClassMatching")) {
			if (algorithm.equals("mostFair")){
				groups = QuizMatching.mostFairMatch(classId, week);
				Defaults.saveGroupNumbers(groups, classId, week, "");
			} else if (algorithm.equals("recommendation")) {
				groups = QuizMatching.recommendationMatch(classId, week);
				Defaults.saveGroupNumbers(groups, classId, week, "");
				
				//only need to provide a recommendation for before class questions because there are multiple topics
				//for before class questions but not for in class questions
				recommendations = Defaults.recommendTopicsForBeforeClassQuestions(groups, classId, week, "");
				recommendTopic = true;
			}
			
		} else {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		JsonArrayBuilder result = Json.createArrayBuilder();
		for (HashSet<String> group : groups) {
			JsonObjectBuilder info = Json.createObjectBuilder();
			for (String safeEmail : group) {
				User u = students.get(safeEmail);
				if (u != null) {
					info.add(u.getEmail(), u.getName());
				}
			}
			result.add(info);
		}
		response.getWriter().write(result.build().toString());
	}
}
