<%@page import="java.util.regex.Matcher"%>
<%@page import="java.util.regex.Pattern"%>
<%@ page import="iPodia.Defaults" %>
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
		
<%


String s = "week" + week + "%";
PreparedStatement ps = dbConnection.prepareStatement("Select * From class_" + classId + " WHERE id LIKE ?");
ps.setString(1,s);
ResultSet rs = ps.executeQuery();

String pattern = "(Week\\d+)(Topic\\d+)(\\w+)?";
Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);

while (rs.next()) {
	String id = rs.getString("id");
	Matcher m = p.matcher(id);

	
	if (m.find()) {
		String topicFromDB = m.group(2);
		System.out.println(":"+topicFromDB+":");
		String questionFromDB = rs.getString("question");
		String answerAFromDB = rs.getString("answerA");
		String answerBFromDB = rs.getString("answerB");
		String answerCFromDB = rs.getString("answerC");
		String answerDFromDB = rs.getString("answerD");
		String answerEFromDB = rs.getString("answerE"); 
		String correctAnswerFromDB = rs.getString("correctAnswer");
%>
		/*Javascript to fill in existing form elements for one question*/

		//need quotes around in order to get the string that represents the name
		//without quotes, it returns the element
		var topic = document.getElementById("<%=topicFromDB%>"); 

		if (topic != null) {
			
			var questionName = "Week${param.num}" + topic.id + "Question" + topic.children.length;
			var container = document.createElement("div");
			container.classList.add("quiz-item");

			var question = container.appendChild(document.createElement("textarea"));
			question.classList.add("question");
			question.placeholder = "Question";
			question.value= "<%=questionFromDB%>";
			question.name = questionName;

			var answerOptions = ["A", "B", "C", "D", "E"];
			for (var i = 0; i < answerOptions.length; ++i) {
				var answer = container.appendChild(document.createElement("input"));
				answer.placeholder = "Answer " + answerOptions[i];
				answer.classList.add("answer");
				
				if (i == 0)
				 	answer.value = "<%=answerAFromDB %>";
				else if (i == 1)
					answer.value = "<%=answerBFromDB %>";
				else if (i == 2)
					answer.value = "<%=answerCFromDB %>";
				else if (i == 3)
					answer.value = "<%=answerDFromDB %>";
				else if (i == 4)
					answer.value = "<%=answerEFromDB %>";
				
				answer.name = questionName + "Answer" + answerOptions[i];
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
				var correctAnswerText = "<%=correctAnswerFromDB%>";
				if ( correctAnswerText === answerOptions[i]) 
					radioButton.checked = true;
			}

			topic.insertBefore(container, topic.lastElementChild);
		}
		
		/* end of the javascript code for this particular question */
		
<% 
	}

}
%>

</script>

<script >
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
			var questionName = "Week${param.num}" + topic.id + "Question" + topic.children.length;
			var container = document.createElement("div");
			container.classList.add("quiz-item");

			var question = container.appendChild(document.createElement("textarea"));
			question.classList.add("question");
			question.placeholder = "Question";
			question.name = questionName;

			var answerOptions = ["A", "B", "C", "D", "E"];
			for (var i = 0; i < answerOptions.length; ++i) {
				var answer = container.appendChild(document.createElement("input"));
				answer.classList.add("answer");
				answer.placeholder = "Answer " + answerOptions[i];
				answer.name = questionName + "Answer" + answerOptions[i];
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
			}

			topic.insertBefore(container, topic.lastElementChild);
		}
		Array.prototype.forEach.call(document.querySelectorAll("button[name=\"addQuestion\"]"), function(item) {
			item.addEventListener("click", addQuestion);
		});
	})();
</script>
<jsp:include page="/WEB-INF/templates/footer.jsp"/>