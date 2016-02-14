<%@ include file="/WEB-INF/Session.jsp" %>
<%
if (!user.isAuthenticated() || !user.isAdmin()) {
	response.sendRedirect(request.getContextPath() + "/");
	return;
}

String classId = request.getParameter("id");
if (classId == null || classId.trim().length() == 0) {
	response.sendRedirect(request.getContextPath() + "/");
	return;
}

String className = user.getClassName(classId);
if (className == null || className.trim().length() == 0) {
	response.sendRedirect(request.getContextPath() + "/");
	return;
}
%>
<jsp:include page="/WEB-INF/templates/head.jsp">
	<jsp:param name="pagetype" value="admin"/>
	<jsp:param name="title" value="Upload"/>
</jsp:include>
<jsp:include page="/WEB-INF/templates/header.jsp">
	<jsp:param name="username" value="${user.getName()}"/>
</jsp:include>
		<main>
			<h1><%= className %>, Week ${param.num}</h1>
			<form method="post" action="uploadFile" enctype="multipart/form-data">
				<input type="text" name="id" value="${param.id}" hidden>
				<input type="text" name="num" value="${param.num}" hidden>

				<h4>Upload Files:</h4>
				<section id="Files">
					<input type="file" name="upload">
				</section>

				<h4>Topic 1:</h4>
				<section id="Topic1">
					<button type="button" name="addQuestion" value="add">Add question for Topic 1</button>
				</section>

				<h4>Topic 2:</h4>
				<section id="Topic2">
					<button type="button" name="addQuestion" value="add">Add question for Topic 2</button>
				</section>

				<h4>Topic 3:</h4>
				<section id="Topic3">
					<button type="button" name="addQuestion" value="add">Add question for Topic 3</button>
				</section>

				<h4>Topic 4:</h4>
				<section id="Topic4">
					<button type="button" name="addQuestion" value="add">Add question for Topic 4</button>
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
					var questionNumber = topic.id + "Question" + topic.children.length;

					var container = document.createElement("div");
					container.classList.add("quiz-item");

					var question = container.appendChild(document.createElement("textarea"));
					question.classList.add("question");
					question.placeholder = "Question";
					question.name = questionNumber;

					var answerOptions = ["A", "B", "C", "D", "E"];
					for (var i = 0; i < answerOptions.length; ++i) {
						var answer = container.appendChild(document.createElement("input"));
						answer.classList.add("answer");
						answer.placeholder = "Answer " + answerOptions[i];
						answer.name = questionNumber + "Answer" + answerOptions[i];
					}

					var correctAnswerContainer = container.appendChild(document.createElement("div"));
					correctAnswerContainer.classList.add("correct");
					for (var i = 0; i < answerOptions.length; ++i) {
						var label = correctAnswerContainer.appendChild(document.createElement("label"));
						label.textContent = answerOptions[i] + ":";

						var radioButton = correctAnswerContainer.appendChild(document.createElement("input"));
						radioButton.type = "radio";
						radioButton.name = questionNumber + "CorrectAnswer";
					}

					topic.insertBefore(container, topic.children[topic.children.length - 1]);
				}
				Array.prototype.forEach.call(document.querySelectorAll("button[name=\"addQuestion\"]"), function(item) {
					item.addEventListener("click", addQuestion);
				});
			})();
		</script>
<jsp:include page="/WEB-INF/templates/footer.jsp"/>