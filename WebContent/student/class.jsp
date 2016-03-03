<%@ page import="java.io.File" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.HashSet" %>
<%@ page import="iPodia.Defaults" %>
<%@ page import="iPodia.QuizQuestion" %>
<%@ include file="/WEB-INF/Database.jsp" %>
<%@ include file="/WEB-INF/Session.jsp" %>
<%
if (!user.isAuthenticated() || !user.isStudent()) {
	response.sendRedirect(request.getContextPath() + "/");
	return;
}

String classId = request.getParameter("id");
if (Defaults.isEmpty(classId)) {
	response.sendRedirect(request.getContextPath() + "/");
	return;
}

String className = user.getClassName(classId);
if (Defaults.isEmpty(className)) {
	response.sendRedirect(request.getContextPath() + "/");
	return;
}

ArrayList<QuizQuestion> questions = new ArrayList<QuizQuestion>();
HashMap<String, String> existing = new HashMap<String, String>();
HashMap<String, String> groups = new HashMap<String, String>();
ResultSet results = dbConnection.prepareStatement("SELECT * FROM class_" + classId).executeQuery();
while (results.next()) {
	QuizQuestion question = new QuizQuestion(results);
	if (!question.isValid() || !Defaults.columnExists(results, user.getSafeEmail() + Defaults.beforeMatching) || !Defaults.columnExists(results, user.getSafeEmail() + Defaults.afterMatching))
		continue;

	if (!groups.containsKey(question.getWeekId())) {
		PreparedStatement ps = dbConnection.prepareStatement("SELECT * FROM class_" + classId + "_matching where id = ?");
		ps.setString(1, question.getWeekId());
		ResultSet matches = ps.executeQuery();
		while (matches.next() && Defaults.columnExists(matches, user.getSafeEmail())) {
			String groupId = matches.getString(user.getSafeEmail());
			if (!Defaults.isEmpty(groupId))
				groups.put(question.getWeekId(), groupId);
		}
	}

	String userAnswer = null;
	if (groups.containsKey(question.getWeekId()))
		userAnswer = results.getString(user.getSafeEmail() + Defaults.afterMatching);
	else
		userAnswer = results.getString(user.getSafeEmail() + Defaults.beforeMatching);

	questions.add(question);
	existing.put(question.getId(), Defaults.isEmpty(userAnswer) ? "" : userAnswer);
}
HashMap<String, User> allStudents = Defaults.getStudentsBySafeEmailForClass(classId);
HashMap<String, HashSet<User>> members = new HashMap<String, HashSet<User>>();
for (HashMap.Entry<String, String> entry : groups.entrySet()) {
	HashMap<Integer, HashSet<String>> matched = Defaults.getStudentGroups(classId, entry.getKey());
	if (matched == null)
		continue;

	HashSet<User> emails = new HashSet<User>();
	for (String email : matched.get(Integer.parseInt(entry.getValue())))
		emails.add(allStudents.get(email));

	if (!emails.isEmpty())
		members.put(entry.getKey(), emails);
}
Collections.sort(questions);
%>
<jsp:include page="/WEB-INF/templates/head.jsp">
	<jsp:param name="pagetype" value="student"/>
	<jsp:param name="title" value="<%= className %>"/>
</jsp:include>
<jsp:include page="/WEB-INF/templates/header.jsp">
	<jsp:param name="username" value="${user.getName()}"/>
</jsp:include>
		<main>
			<h1><%= className %></h1>
<% for (int i = 0; i < questions.size(); ++i) { QuizQuestion question = questions.get(i); %>
<%-- ===== IN CLASS QUESTIONS ==== --%>
<% if (question.isInClass()) { %>
<% if (i == 0) { %>
			<section id="in-class" class="open">
				<h4>
					In-Class Questions
					<div title="Toggle Questions" class="chevron up"></div>
				</h4>
				<form class="container" method="post" action="${pageContext.request.contextPath}/student/submitAnswers">
					<input type="text" name="id" value="${param.id}" hidden>
					<input type="text" name="user" value="${user.getSafeEmail()}<%= Defaults.beforeMatching %>" hidden>
<% } %>
<% if (question.getWeekId().equals(questions.get(0).getWeekId())) { %>
					<%= question.generateStudentHTML(existing.get(question.getId())) %>
<% if (i == questions.size() - 1 || !questions.get(i + 1).isInClass()) { %>
					<button>Submit</button>
				</form>
			</section>
<% } %>
<% } %>
<% continue; // Ensures that in-class questions from previous weeks are skipped %>
<% } %>

<%-- ===== REGULAR QUESTIONS ==== --%>
<% if (i == 0 || !question.getWeekId().equals(questions.get(i - 1).getWeekId()) || questions.get(i - 1).isInClass()) { %>
			<section id="week<%= question.getWeekNumber() %>"<%= (i == 0 ? " class=\"open\"" : "") %>>
				<h4>
					Week <%= question.getWeekNumber() %>
					<div title="Toggle Questions" class="chevron"></div>
				</h4>
<% File folder = new File(Defaults.DATA_DIRECTORY + "/" + classId + "/" + question.getWeekNumber()); %>
<% if (folder.exists() && folder.isDirectory()) { %>
				<ul>
<% for (File f : folder.listFiles()) { %>
					<li><a href="${pageContext.request.contextPath}/data?class=${param.id}&week=<%= question.getWeekNumber() %>&file=<%= Defaults.urlEncode(f.getName()) %>" target="_blank" title="<%= f.getName() %>"><%= f.getName() %></a></li>
<% } %>
				</ul>
<% if (groups.containsKey(question.getWeekId())) { String chatId = Defaults.chatURL + Defaults.urlEncode(className) + "/" + question.getWeekId() + "/" + groups.get(question.getWeekId()); %>
				<a href="<%= chatId %>" title="Week <%= question.getWeekNumber() %> Group" target="_blank"><%= chatId %></a>
<% if (members.containsKey(question.getWeekId())) { %>
				<p>
					Teammates:
<% int count = 0; for (User teammate : members.get(question.getWeekId())) { %>
					<a href="mailto:<%= teammate.getEmail() %>?subject=<%= Defaults.urlEncode("iPodia " + className + " Week " + question.getWeekNumber()) %>" title="Email <%= teammate.getName() %>" target="_blank"><%= teammate.getName() %></a><%= (count < members.get(question.getWeekId()).size() - 1 ? ", " : "") %>
<% ++count; } %>
				</p>
<% } %>
<% } %>
<% } %>
<% if (question.getWeekId().equals(questions.get(0).getWeekId())) { %>
				<form method="post" action="${pageContext.request.contextPath}/student/submitAnswers">
					<input type="text" name="id" value="${param.id}" hidden>
					<input type="text" name="user" value="${user.getSafeEmail()}<%= groups.containsKey(question.getWeekId()) ? Defaults.afterMatching : Defaults.beforeMatching %>" hidden>
<% } else { %>
				<form class="past-week">
<% } %>
<% } %>
					<%= question.generateStudentHTML(existing.get(question.getId())) %>
<% if (i == questions.size() - 1 || !question.getWeekId().equals(questions.get(i + 1).getWeekId())) { %>
<% if (question.getWeekId().equals(questions.get(0).getWeekId())) { %>
					<button>Submit</button>
<% } %>
				</form>
			</section>
<% } %>
<% } %>
		</main>
<jsp:include page="/WEB-INF/templates/footer.jsp">
	<jsp:param name="pagetype" value="student"/>
</jsp:include>
