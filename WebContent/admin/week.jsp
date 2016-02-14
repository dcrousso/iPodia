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

				<input type="file" name="fileToUpload">

				<input type="submit" value="Submit">
			</form>
		</main>
		
		<script>
			Array.prototype.forEach.call(document.querySelectorAll("input[name=\"fileToUpload\"]"), function(item) {
				item.addEventListener("change", handleChange);
			});
			

			function handleChange(event) {
				var input = document.createElement("input");
				input.type = "file";
				input.name = "fileToUpload";
				input.addEventListener("change", handleChange);
				event.target.removeEventListener("change", handleChange);
				event.target.parentElement.insertBefore(input, event.target.nextElementSibling);
			}
		</script>
<jsp:include page="/WEB-INF/templates/footer.jsp"/>