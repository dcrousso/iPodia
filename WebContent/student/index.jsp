<%@ include file="/WEB-INF/Session.jsp" %>
<%
if (user.getType() == null || !user.isStudent()) {
	response.sendRedirect(request.getContextPath() + "/");
	return;
}
%>
<jsp:include page="/WEB-INF/templates/header.jsp">
	<jsp:param name="pagetype" value="student"/>
	<jsp:param name="title" value="Student"/>
</jsp:include>
		<main>
			<h1>Welcome <%=user.getName() %></h1>
			<h4>Please select a class:</h4>
			<ul>
<% for (int classID : user.getClasses()) { %>
				<li><a href="${pageContext.request.contextPath}/student/class.jsp?class=<%= Integer.toString(classID) %>"><%= Integer.toString(classID) %></a></li>
<% } %>
			</ul>
		</main>
<jsp:include page="/WEB-INF/templates/footer.jsp"/>