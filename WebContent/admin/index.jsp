<%@ include file="/WEB-INF/Session.jsp" %>
<%
if (user.getType() == null || !user.isAdmin()) {
	response.sendRedirect(request.getContextPath() + "/");
	return;
}
%>
<jsp:include page="/WEB-INF/templates/header.jsp">
	<jsp:param name="pagetype" value="admin"/>
	<jsp:param name="title" value="Admin"/>
</jsp:include>
		<main>
			<h1>Admin</h1>
			<h4>Please select a class:</h4>
			<ul>
<% for (int classID : user.getClasses()) { %>
				<li><a href="${pageContext.request.contextPath}/admin?class=<%= Integer.toString(classID) %>"><%= Integer.toString(classID) %></a></li>
<% } %>
			</ul>
			
			<form method = "POST">
			Create a new class: <input type="text" name="new-class">
			<input type="submit" value="Submit" />
			</form>
			
			<%
				if (request != null) {
					String input = request.getParameter("new-class");
					if (input != null) {
						Integer classId = Integer.valueOf(input);		
					}
					
					//now run a sql command to create a table in the database for this class 
				}
			%>
		</main>
<jsp:include page="/WEB-INF/templates/footer.jsp"/>