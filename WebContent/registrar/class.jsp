<%@ include file="/WEB-INF/Session.jsp" %>
<%
if (!user.isAuthenticated() || !user.isRegistrar()) {
	response.sendRedirect(request.getContextPath() + "/");
	return;
}
if (request.getParameter("className") == null) {
	response.sendRedirect(request.getContextPath() + "/");
	return;
}
%>
<jsp:include page="/WEB-INF/templates/head.jsp">
	<jsp:param name="pagetype" value="registrar"/>
	<jsp:param name="title" value="Class ${param.className}"/>
</jsp:include>
<jsp:include page="/WEB-INF/templates/header.jsp">
	<jsp:param name="username" value="${user.getName()}"/>
</jsp:include>
		<main>
			<h1>Welcome to ${param.className}</h1>
		</main>
<jsp:include page="/WEB-INF/templates/footer.jsp"/>
