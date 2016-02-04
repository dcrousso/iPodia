<%@ include file="/WEB-INF/Session.jsp" %>
<%@ page import="iPodia.User"%>
<%
if (user.getType() == null || !user.isAdmin()) {
	response.sendRedirect(request.getContextPath() + "/");
	return;
}
if (request != null) {
	String input = request.getParameter("new-class");
	if (input != null) {
		Integer classId = Integer.valueOf(input);
	}

	// now run a sql command to create a table in the database for this class
}
%>
<jsp:include page="/WEB-INF/templates/head.jsp">
	<jsp:param name="pagetype" value="admin"/>
	<jsp:param name="title" value="Admin"/>
</jsp:include>
<jsp:include page="/WEB-INF/templates/header.jsp">
	<jsp:param name="username" value="${user.getName()}"/>
</jsp:include>
		<main>
			<h1>Admin</h1>
			<h4>Please select a class:</h4>
			<ul>
<% for (String className : user.getClasses()) { %>
				<li><a href="${pageContext.request.contextPath}/admin/class.jsp?className=<%= className %>"><%= className %></a></li>
<% } %>
			</ul>
			
			<form method = "POST">
			Create a new class: <input type="text" name="new-class">
			<input type="submit" value="Submit" />
			</form>
		</main>
<jsp:include page="/WEB-INF/templates/footer.jsp"/>
