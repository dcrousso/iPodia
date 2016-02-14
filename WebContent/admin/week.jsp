<%@ include file="/WEB-INF/Session.jsp" %>
<%
if (!user.isAuthenticated() || !user.isAdmin()) {
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
			<form method="post" action="uploadFile" enctype="multipart/form-data">
				<input type="text" name="id" value="${param.id}" hidden>
				<input type="text" name="num" value="${param.num}" hidden>
				<input type="file" name="upload">
				<button>Submit</button>
			</form>
		</main>
		
		<script>
			(function() {
				function handleChange(event) {
					var input = document.createElement("input");
					input.type = "file";
					input.name = "upload";
					input.addEventListener("change", handleChange);
					this.removeEventListener("change", handleChange);
					this.parentElement.insertBefore(input, this.nextElementSibling);
				}
				Array.prototype.forEach.call(document.querySelectorAll("input[type=\"file\"]"), function(item) {
					item.addEventListener("change", handleChange);
				});
			})();
		</script>
<jsp:include page="/WEB-INF/templates/footer.jsp"/>