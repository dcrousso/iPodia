<%@ page import="java.util.HashMap" %>
<%@ include file="/WEB-INF/Session.jsp" %>
<%
if (!user.isAuthenticated() || !user.isStudent()) {
	response.sendRedirect(request.getContextPath() + "/");
	return;
}
%>
<jsp:include page="/WEB-INF/templates/head.jsp">
	<jsp:param name="pagetype" value="student"/>
	<jsp:param name="title" value="Student"/>
</jsp:include>
<jsp:include page="/WEB-INF/templates/header.jsp">
	<jsp:param name="username" value="${user.getName()}"/>
</jsp:include>
		<main>
			<h1>Welcome ${user.getName()}</h1>
<% if (user.getClasses().isEmpty()) { %>
			<p>No Enrolled Classes</p>
<% } else { %>
			<h4>Please select a class:</h4>
<% } %>
			<ul>
<% for (HashMap.Entry<Integer, String> entry : user.getClasses().entrySet()) { %>
				<li><a href="${pageContext.request.contextPath}/student/class?id=<%= entry.getKey() %>"><%= entry.getValue() %></a></li>
<% } %>
			</ul>
		</main>
<jsp:include page="/WEB-INF/templates/footer.jsp"/>
