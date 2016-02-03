<%@ include file="/WEB-INF/Session.jsp" %>
<%
if (user.getType() == null || !user.isAdmin()) {
	response.sendRedirect(request.getContextPath() + "/");
	return;
}
if (request.getParameter("class") == null) {
	response.sendRedirect(request.getContextPath() + "/");
	return;
}
%>
<jsp:include page="/WEB-INF/templates/head.jsp">
	<jsp:param name="pagetype" value="admin"/>
	<jsp:param name="title" value="Admin class ${request.class}"/>
</jsp:include>
<jsp:include page="/WEB-INF/templates/header.jsp">
	<jsp:param name="username" value="${user.getName()}"/>
</jsp:include>
		<main>
			<p>Welcome to <%= request.getParameter("class") %></p>
		</main>
<jsp:include page="/WEB-INF/templates/footer.jsp"/>
