<%@ include file="/WEB-INF/Session.jsp" %>
<%
if (!user.isAuthenticated() || !user.isAdmin()) {
	response.sendRedirect(request.getContextPath() + "/");
	return;
}
%>
<jsp:include page="/WEB-INF/templates/head.jsp">
	<jsp:param name="pagetype" value="student"/>
	<jsp:param name="title" value="Student"/>
</jsp:include>
<jsp:include page="/WEB-INF/templates/header.jsp">
	<jsp:param name="username" value="${user.getName()}"/>
</jsp:include>
		<main>
			<form method="post" action="uploadFile" enctype="multipart/form-data">
				<input type="text" name="className" value="${param.className}" hidden>
				<input type="text" name="week" value="${param.week}" hidden>
				
				Upload Files:
				<input type="file" name="fileToUpload"> <br>
				
				Topic 1:
				<div id = "Topic1">
					<div class ="addButtonWrapper">
						<input type = "button" name = "addQuestion" value = "add"> Add question for Topic 1 </input>
					</div>
					 
					<div id = "endOfTopic1"></div>
				</div>
				<br>
				
				Topic 2:
				<div id = "Topic2">
					<div class ="addButtonWrapper">
						<input type = "button" name = "addQuestion" value = "add"> Add question for Topic 2 </input>
					</div>
				
					<div id = "endOfTopic2"></div>
				</div>
				<br>
				
				Topic 3:
				<div id = "Topic3">
					<div class ="addButtonWrapper">
						<input type = "button" name = "addQuestion" value = "add"> Add question for Topic 3 </input>
					</div>
				
					<div id = "endOfTopic3"></div>
				</div>
				<br>
				
				Topic 4:
				<div id = "Topic4">
					<div class ="addButtonWrapper">
						<input type = "button" name = "addQuestion" value = "add"> Add question for Topic 4 </input>
					</div>
				
					<div id = "endOfTopic4"></div>
				</div>

				<input type="submit" value="Submit">
			</form>
		</main>
		
		<script>
			Array.prototype.forEach.call(document.querySelectorAll("input[name=\"fileToUpload\"]"), function(item) {
				item.addEventListener("change", fileUpload);
			});
			
			Array.prototype.forEach.call(document.querySelectorAll("input[name=\"addQuestion\"]"), function(item) {
				item.addEventListener("click", addQuestion);
			});
			
			function fileUpload(event) {
				var input = document.createElement("input");
				input.type = "file";
				input.name = "fileToUpload";
				input.addEventListener("change", fileUpload);
				event.target.removeEventListener("change", fileUpload);
				event.target.parentElement.insertBefore(input, event.target.nextElementSibling);
			}
			
			function addQuestion(event) {

				var questionWrapper = event.target.parentNode;
				var topic = questionWrapper.parentNode;
				var endOfTopic = questionWrapper.nextElementSibling;
				console.log(topic);
				
				var selector = "#" + topic.id + " .quiz-item" 
				var questionNumber = document.querySelectorAll(selector).length +1;

				var container = document.createElement("section");
				container.name="thing";
				container.classList.add("quiz-item");

				var question = document.createElement("textarea");
				question.rows = "4";
				question.cols = "50";
				question.classList.add("question");
				question.placeholder = "Question " + questionNumber  + ":" ;
				question.name = topic.id + "Question" +questionNumber;
				event.target.parentElement.insertBefore(question, event.target.nextElementSibling);
				container.appendChild(question);
				
				var answerArray = ["A", "B", "C", "D", "E"];
				for (var i = 0; i < answerArray.length; ++i) {
					var answer = document.createElement("input");
					answer.type = "text";
					answer.classList.add("answer");
					answer.placeholder = "Answer " + answerArray[i];
					answer.name = topic.id + "Question" + questionNumber + "Answer" +answerArray[i];
					container.appendChild(answer);
				}
				
				var correctAnswerContainer = document.createElement("div");
				
				for (var i = 0; i < answerArray.length; ++i) {
					var radioButton = document.createElement("input");
					radioButton.type = "radio";
					radioButton.name = topic.id + "Question" + questionNumber + "correctAnswer";
					var label = document.createElement("label");
					var textForLabel = document.createTextNode(answerArray[i] + ": ");
					label.appendChild(textForLabel);
					correctAnswerContainer.appendChild(label);
					correctAnswerContainer.appendChild(radioButton);

				}
				container.appendChild(correctAnswerContainer);
				
				//removes the button and then adds it af the end of the latest question
				questionWrapper.parentNode.removeChild(questionWrapper);
				endOfTopic.parentNode.insertBefore(container, endOfTopic);
				endOfTopic.parentNode.insertBefore(questionWrapper, endOfTopic);
				
			}
		</script>
<jsp:include page="/WEB-INF/templates/footer.jsp"/>