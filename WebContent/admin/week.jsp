<%@ page import="java.io.File" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@ page import="iPodia.Defaults" %>
<%@ page import="iPodia.ProcessForm" %>
<%@ page import="iPodia.QuizQuestion" %>
<%@ include file="/WEB-INF/Session.jsp" %>
<%
if (!user.isAuthenticated() || !user.isAdmin()) {
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

String week = request.getParameter("num");
if (Defaults.isEmpty(week)) {
	response.sendRedirect(request.getContextPath() + "/");
	return;
}

if (request.getMethod().equals("POST")) {
	HashMap<String, QuizQuestion> inClass = new HashMap<String, QuizQuestion>();
	for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet())
		QuizQuestion.processRequestItem(inClass, entry.getKey(), entry.getValue()[0]); // Only should be one value per entry
	for (QuizQuestion question : inClass.values())
		ProcessForm.processQuizUpload(question, classId, "InClass");
}

ArrayList<QuizQuestion> existing = new ArrayList<QuizQuestion>();
PreparedStatement ps = Defaults.getDBConnection().prepareStatement("SELECT * FROM class_" + classId + " WHERE id LIKE ?");
ps.setString(1, "Week" + week + "%");
ResultSet results = ps.executeQuery();
while (results.next())
	existing.add(new QuizQuestion(results));

results.close();
ps.close();
Defaults.closeDBConnection();

Collections.sort(existing);
boolean hasInClass = Defaults.contains(existing, question -> question.isInClass());
boolean hasAllTopics = Defaults.contains(existing, question -> question.getTopicId().equals("Topic1"))
                    && Defaults.contains(existing, question -> question.getTopicId().equals("Topic2"))
                    && Defaults.contains(existing, question -> question.getTopicId().equals("Topic3"))
                    && Defaults.contains(existing, question -> question.getTopicId().equals("Topic4"));
%>
<jsp:include page="/WEB-INF/templates/head.jsp">
	<jsp:param name="pagetype" value="admin"/>
	<jsp:param name="title" value="Upload"/>
</jsp:include>
<jsp:include page="/WEB-INF/templates/header.jsp">
	<jsp:param name="username" value="${user.getName()}"/>
</jsp:include>
		<main>
			<h1><a href="${pageContext.request.contextPath}/admin/class?id=${param.id}" title="Back to Class Page"><%= className %></a> - Week ${param.num}</h1>
			<div class="options">
				<%-- If there are in-class questions, we have already passed the point where matches could be made for that week's groupings --%>
				<button class="match<%= (hasInClass ? " in-class" : "") %>" title="<%= (!hasInClass && !hasAllTopics ? "Must have a question for each topic" : (hasInClass ? "In Class Matching" : "Before Class Matching")) %>"<%= (!hasInClass && !hasAllTopics ? " disabled" : "") %>>Match Students</button>
				<button class="add-in-class-question">Add In-Class Question</button>
				<button class="view-student-scores">View Student Scores</button>
			</div>
			<section class="results" hidden></section>
			<form class="in-class-questions" method="post"<%= (!hasInClass ? " hidden" : "") %>>
				<input type="text" name="id" value="${param.id}" hidden>
				<input type="text" name="num" value="${param.num}" hidden>
				<section id="InClass">
					<h4>In-Class</h4>
<% for (QuizQuestion item : existing) { if (item.isInClass()) { %>
					<%= item.generateAdminHTML() %>
<% } } %>
					<button>Submit</button>
				</section>
			</form>
			<form method="post" action="${pageContext.request.contextPath}/admin/uploadWeekData" enctype="multipart/form-data">
				<input type="text" name="id" value="${param.id}" hidden>
				<input type="text" name="num" value="${param.num}" hidden>
				<section id="Files">
					<h4>Upload Files</h4>
<% File folder = new File(Defaults.DATA_DIRECTORY + "/" + classId + "/" + week); %>
<% if (folder.exists() && folder.isDirectory()) { %>
					<ul>
<% for (File f : folder.listFiles()) { %>
						<li><a href="${pageContext.request.contextPath}/data?class=${param.id}&week=${param.num}&file=<%= Defaults.urlEncode(f.getName()) %>" target="_blank" title="<%= f.getName() %>"><%= f.getName() %></a></li>
<% } %>
					</ul>
<% } %>
					<input type="file" name="upload">
				</section>
				<section id="Topic1">
					<h4>Topic 1</h4>
<% for (QuizQuestion item : existing) { if (item.getId().contains("Topic1")) { %>
					<%= item.generateAdminHTML() %>
<% } } %>
					<button type="button" class="add-question">Add Question</button>
				</section>
				<section id="Topic2">
					<h4>Topic 2</h4>
<% for (QuizQuestion item : existing) { if (item.getId().contains("Topic2")) { %>
					<%= item.generateAdminHTML() %>
<% } } %>
					<button type="button" class="add-question">Add Question</button>
				</section>
				<section id="Topic3">
					<h4>Topic 3</h4>
<% for (QuizQuestion item : existing) { if (item.getId().contains("Topic3")) { %>
					<%= item.generateAdminHTML() %>
<% } } %>
					<button type="button" class="add-question">Add Question</button>
				</section>
				<section id="Topic4">
					<h4>Topic 4</h4>
<% for (QuizQuestion item : existing) { if (item.getId().contains("Topic4")) { %>
					<%= item.generateAdminHTML() %>
<% } } %>
					<button type="button" class="add-question">Add Question</button>
				</section>
				<button>Submit</button>
			</form>
		</main>
<jsp:include page="/WEB-INF/templates/footer.jsp">
	<jsp:param name="pagetype" value="admin"/>
</jsp:include>

