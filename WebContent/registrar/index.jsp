<%@ include file="/WEB-INF/Session.jsp" %>
<%
if (!user.isAuthenticated() || !user.isRegistrar()) {
	response.sendRedirect(request.getContextPath() + "/");
	return;
}
%>
<jsp:include page="/WEB-INF/templates/head.jsp">
	<jsp:param name="pagetype" value="registrar"/>
	<jsp:param name="title" value="Registrar"/>
</jsp:include>
<jsp:include page="/WEB-INF/templates/header.jsp">
	<jsp:param name="username" value="${user.getName()}"/>
</jsp:include>
		<main>
			<h1>Welcome ${user.getName()}</h1>
			<h4>Please select a class:</h4>
			<ul>
			</ul>
		</main>
<jsp:include page="/WEB-INF/templates/footer.jsp"/>