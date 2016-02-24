<%@ page import="java.io.File" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@ page import="iPodia.Defaults" %>
<%@ page import="iPodia.ProcessForm" %>
<%@ page import="iPodia.QuizQuestion" %>
<%@ include file="/WEB-INF/Session.jsp" %>
<%@ include file="/WEB-INF/Database.jsp" %>
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
		ProcessForm.processQuizUpload(question, classId);
}

ArrayList<QuizQuestion> existing = new ArrayList<QuizQuestion>();
PreparedStatement ps = dbConnection.prepareStatement("Select * From class_" + classId + " WHERE id LIKE ?");
ps.setString(1, "Week" + week + "%");
ResultSet results = ps.executeQuery();
while (results.next())
	existing.add(new QuizQuestion(results));

Collections.sort(existing);
boolean hasInClass = Defaults.contains(existing, question -> question.isInClass());
%>
<jsp:include page="/WEB-INF/templates/head.jsp">
	<jsp:param name="pagetype" value="admin"/>
	<jsp:param name="title" value="Upload"/>
</jsp:include>
<jsp:include page="/WEB-INF/templates/header.jsp">
	<jsp:param name="username" value="${user.getName()}"/>
</jsp:include>
		<main>
			<h1><a href="${pageContext.request.contextPath}/admin/class?id=${param.id}" title="Back to Class Page"><%= className %></a>, Week ${param.num}</h1>
			<div class="options">
				<button class="match">Match Students</button>
<% if (!hasInClass) { %>
				<button class="in-class">Add In-Class Question</button>
<% } %>
			</div>

			<form class="in-class-questions" method="post"<% if (!hasInClass) { %> hidden<% } %>>
				<input type="text" name="id" value="${param.id}" hidden>
				<input type="text" name="num" value="${param.num}" hidden>

				<h4>In-Class:</h4>
				<section id="InClass">
<% for (QuizQuestion item : existing) { if (item.isInClass()) { %>
					<%= item.generateAdminHTML() %>
<% } } %>
					<button type="button" class="add-question">Add In-Class Question</button>
				</section>

				<button>Submit</button>
			</form>

			<form method="post" action="uploadWeekData" enctype="multipart/form-data">
				<input type="text" name="id" value="${param.id}" hidden>
				<input type="text" name="num" value="${param.num}" hidden>

				<h4>Upload Files:</h4>
				<section id="Files">
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

				<h4>Topic 1:</h4>
				<section id="Topic1">
<% for (QuizQuestion item : existing) { if (item.getId().contains("Topic1")) { %>
					<%= item.generateAdminHTML() %>
<% } } %>
					<button type="button" class="add-question">Add Question for Topic 1</button>
				</section>

				<h4>Topic 2:</h4>
				<section id="Topic2">
<% for (QuizQuestion item : existing) { if (item.getId().contains("Topic2")) { %>
					<%= item.generateAdminHTML() %>
<% } } %>
					<button type="button" class="add-question">Add Question for Topic 2</button>
				</section>

				<h4>Topic 3:</h4>
				<section id="Topic3">
<% for (QuizQuestion item : existing) { if (item.getId().contains("Topic3")) { %>
					<%= item.generateAdminHTML() %>
<% } } %>
					<button type="button" class="add-question">Add Question for Topic 3</button>
				</section>

				<h4>Topic 4:</h4>
				<section id="Topic4">
<% for (QuizQuestion item : existing) { if (item.getId().contains("Topic4")) { %>
					<%= item.generateAdminHTML() %>
<% } } %>
					<button type="button" class="add-question">Add Question for Topic 4</button>
				</section>

				<button>Submit</button>
			</form>
		</main>
		<script>
			(function() {
				function addFileUpload(event) {
					var input = document.createElement("input");
					input.type = "file";
					input.name = "upload";
					input.addEventListener("change", addFileUpload);
					this.removeEventListener("change", addFileUpload);
					this.parentElement.insertBefore(input, this.nextElementSibling);
				}
				Array.prototype.forEach.call(document.querySelectorAll("input[type=\"file\"]"), function(item) {
					item.addEventListener("change", addFileUpload);
				});

				function addQuestion(event) {
					var topic = this.parentNode;
					var questionName = "Week${param.num}" + topic.id + "Question";
					var lastQuestion = topic.lastElementChild.previousElementSibling;
					if (!lastQuestion)
						questionName += "1";
					else {
						var match = lastQuestion.firstElementChild.name.match(/\d+$/);
						questionName += match ? parseInt(match[0]) + 1 : "1";
					}

					var container = document.createElement("div");
					container.classList.add("quiz-item");

					var question = container.appendChild(document.createElement("textarea"));
					question.classList.add("question");
					question.placeholder = "Question";
					question.name = questionName;
					question.required = true;

					var answerOptions = ["A", "B", "C", "D", "E"];
					for (var i = 0; i < answerOptions.length; ++i) {
						var answer = container.appendChild(document.createElement("input"));
						answer.classList.add("answer");
						answer.placeholder = "Answer " + answerOptions[i];
						answer.name = questionName + "Answer" + answerOptions[i];
						answer.required = true;
					}

					var correctAnswerContainer = container.appendChild(document.createElement("div"));
					correctAnswerContainer.classList.add("correct");
					for (var i = 0; i < answerOptions.length; ++i) {
						var label = correctAnswerContainer.appendChild(document.createElement("label"));
						label.textContent = answerOptions[i] + ":";

						var radioButton = correctAnswerContainer.appendChild(document.createElement("input"));
						radioButton.type = "radio";
						radioButton.name = questionName + "CorrectAnswer";
						radioButton.value = answerOptions[i];
						radioButton.required = true;
					}

					topic.insertBefore(container, topic.lastElementChild);
				}
				Array.prototype.forEach.call(document.querySelectorAll("button.add-question"), function(item) {
					item.addEventListener("click", addQuestion);
				});
<% if (!hasInClass) { %>
				document.querySelector("button.in-class").addEventListener("click", function() {
					document.querySelector(".in-class-questions").hidden = false;
					this.remove();
				});
<% } %>
			})();
		</script>
<jsp:include page="/WEB-INF/templates/footer.jsp"/>
