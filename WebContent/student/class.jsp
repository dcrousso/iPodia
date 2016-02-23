<%@ page import="java.io.File" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.HashMap" %>
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
ResultSet results = dbConnection.prepareStatement("Select * From class_" + classId).executeQuery();
while (results.next()) {
	QuizQuestion question = new QuizQuestion(results);
	if (!question.isValid())
		continue;

	questions.add(question);

	if (!Defaults.columnExists(results, user.getSafeEmail() + Defaults.beforeMatching) || !Defaults.columnExists(results, user.getSafeEmail() + Defaults.afterMatching))
		continue;

	String userAnswer = null;
	if (!groups.containsKey(question.getWeekId())) {
		PreparedStatement ps = dbConnection.prepareStatement("Select * From class_" + classId + "_matching where id = ?");
		ps.setString(1, question.getWeekId());
		ResultSet matches = ps.executeQuery();
		while (matches.next() && Defaults.columnExists(matches, user.getSafeEmail())) {
			String groupId = matches.getString(user.getSafeEmail());
			if (!Defaults.isEmpty(groupId))
				groups.put(question.getWeekId(), groupId);
		}
		userAnswer = results.getString(user.getSafeEmail() + Defaults.afterMatching);
	}

	if (Defaults.isEmpty(userAnswer))
		userAnswer = results.getString(user.getSafeEmail() + Defaults.beforeMatching);

	existing.put(question.getId(), Defaults.isEmpty(userAnswer) ? "" : userAnswer);
}
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
<% if (i == 0 || !question.getWeekId().equals(questions.get(i - 1).getWeekId())) { %>
			<section id="week<%= question.getWeekNumber() %>">
				<h3>
					Week <%= question.getWeekNumber() %>
					<a title="Toggle Questions" class="chevron down"></a>
				</h3>
<% File folder = new File(Defaults.DATA_DIRECTORY + "/" + classId + "/" + question.getWeekNumber()); %>
<% if (folder.exists() && folder.isDirectory()) { %>
				<ul>
<% for (File f : folder.listFiles()) { %>
					<li><a href="${pageContext.request.contextPath}/data?class=${param.id}&week=<%= question.getWeekNumber() %>&file=<%= Defaults.urlEncode(f.getName()) %>" target="_blank" title="<%= f.getName() %>"><%= f.getName() %></a></li>
<% } %>
				</ul>
<% if (groups.containsKey(question.getWeekId())) { String chatId = Defaults.chatURL + Defaults.createSafeString(className) + "/" + question.getWeekId() + "/" + groups.get(question.getWeekId()); %>
				<a href="<%= chatId %>" title="Week <%= question.getWeekNumber() %> Group" target="_blank"><%= chatId %></a>
<% } %>
<% } %>
				<form method="post" action="submitAnswers" >
					<input type="text" name="id" value="${param.id}" hidden>
					<input type="text" name="user" value="${user.getSafeEmail()}<%= groups.containsKey(question.getWeekId()) ? Defaults.afterMatching : Defaults.beforeMatching %>" hidden>
<% } %>
					<div class="question">
						<p><%= question.getQuestion() %></p>
						<div class="answer">
							<input type="radio" name="<%= question.getId() %>" value="A"<% if (existing.get(question.getId()).equals("A")) { %> checked<% } %>>
							<p><%= question.getAnswer("A") %></p>
						</div>
						<div class="answer">
							<input type="radio" name="<%= question.getId() %>" value="B"<% if (existing.get(question.getId()).equals("B")) { %> checked<% } %>>
							<p><%= question.getAnswer("B") %></p>
						</div>
						<div class="answer">
							<input type="radio" name="<%= question.getId() %>" value="C"<% if (existing.get(question.getId()).equals("C")) { %> checked<% } %>>
							<p><%= question.getAnswer("C") %></p>
						</div>
						<div class="answer">
							<input type="radio" name="<%= question.getId() %>" value="D"<% if (existing.get(question.getId()).equals("D")) { %> checked<% } %>>
							<p><%= question.getAnswer("D") %></p>
						</div>
						<div class="answer">
							<input type="radio" name="<%= question.getId() %>" value="E"<% if (existing.get(question.getId()).equals("E")) { %> checked<% } %>>
							<p><%= question.getAnswer("E") %></p>
						</div>
					</div>
<% if (i == questions.size() - 1 || !question.getWeekId().equals(questions.get(i + 1).getWeekId())) { %>
					<button>Submit</button>
				</form>
			</section>
<% } %>
<% } %>
		</main>
		<script>
			Array.prototype.forEach.call(document.querySelectorAll("section > h3"), function(item) {
				item.addEventListener("click", function(event) {
					item.parentNode.classList.toggle("open");
				});
			});
		</script>
<jsp:include page="/WEB-INF/templates/footer.jsp"/>
