<%@ include file="/WEB-INF/Session.jsp"%>
<%
	if (!user.isAuthenticated() || !user.isAdmin()) {
		response.sendRedirect(request.getContextPath() + "/");
		return;
	}
%>
<jsp:include page="/WEB-INF/templates/head.jsp">
	<jsp:param name="pagetype" value="admin" />
	<jsp:param name="title" value="Admin" />
</jsp:include>
<jsp:include page="/WEB-INF/templates/header.jsp">
	<jsp:param name="username" value="${user.getName()}" />
</jsp:include>
		<main>
			<h1>Admin</h1>
			<h4>Please select a class:</h4>
			<ul>
<% for (String className : user.getClasses()) { %>
				<li><a href="${pageContext.request.contextPath}/admin/class.jsp?className=<%= className %>"><%=className%></a></li>
<% } %>
			</ul>
		</main>
<jsp:include page="/WEB-INF/templates/footer.jsp" />
