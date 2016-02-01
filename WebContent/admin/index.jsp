<%@ include file="/WEB-INF/Session.jsp" %>
<%
User user = ((User) session.getAttribute("user"));
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
		</main>
<jsp:include page="/WEB-INF/templates/footer.jsp"/>