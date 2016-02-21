<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.regex.Matcher" %>
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

HashMap<String, String> existing = new HashMap<String, String>();
ArrayList<QuizQuestion> questions = new ArrayList<QuizQuestion>();
ResultSet results = dbConnection.prepareStatement("Select * From class_" + classId).executeQuery();
while (results.next()) {
	QuizQuestion question = new QuizQuestion(results);
	if (!question.isValid())
		continue;

	questions.add(question);

	if (!Defaults.columnExists(results, user.getSafeEmail()))
		continue;

	String userAnswer = results.getString(user.getSafeEmail());
	if (Defaults.isEmpty(userAnswer))
		userAnswer = "";

	existing.put(question.getId(), userAnswer);
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
<% if (i == 0 || !question.getWeek().equals(questions.get(i - 1).getWeek())) { %>
			<section id="week<%= question.getWeek() %>">
				<h3>Week <%= question.getWeek() %></h3>
				<form method="post" action="submitAnswers" >
					<input type="text" name="id" value="${param.id}" hidden>
					<input type="text" name="user" value="${user.getEmail()}" hidden>
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
<% if (i == questions.size() - 1 || !question.getWeek().equals(questions.get(i + 1).getWeek())) { %>
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
