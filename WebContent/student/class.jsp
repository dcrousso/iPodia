<%@ include file="/WEB-INF/Session.jsp" %>
<%
if (!user.isAuthenticated() || !user.isStudent()) {
	response.sendRedirect(request.getContextPath() + "/");
	return;
}

String classId = request.getParameter("id");
if (classId == null || classId.trim().length() == 0) {
	response.sendRedirect(request.getContextPath() + "/");
	return;
}

String className = user.getClassName(classId);
if (className == null || className.trim().length() == 0) {
	response.sendRedirect(request.getContextPath() + "/");
	return;
}
%>
<jsp:include page="/WEB-INF/templates/head.jsp">
	<jsp:param name="pagetype" value="student"/>
	<jsp:param name="title" value="<%= className %>"/>
</jsp:include>
<jsp:include page="/WEB-INF/templates/header.jsp">
	<jsp:param name="username" value="${user.getName()}"/>
</jsp:include>
		<main>
			<h1>Welcome to <%= className %></h1>
		</main>
<jsp:include page="/WEB-INF/templates/footer.jsp"/>
