<%@ include file="/WEB-INF/Session.jsp" %>
<%
if (user.getType() == null || !user.isStudent()) {
	response.sendRedirect(request.getContextPath() + "/");
	return;
}
if (request.getParameter("class") == null) {
	response.sendRedirect(request.getContextPath() + "/");
	return;
}
%>
<jsp:include page="/WEB-INF/templates/head.jsp">
	<jsp:param name="pagetype" value="student"/>
	<jsp:param name="title" value="Student class ${request.class}"/>
</jsp:include>
<jsp:include page="/WEB-INF/templates/header.jsp">
	<jsp:param name="username" value="${user.getName()}"/>
</jsp:include>
		<main>
			<h1>Welcome to <%= request.getParameter("class") %></h1>
		</main>
<jsp:include page="/WEB-INF/templates/footer.jsp"/>
