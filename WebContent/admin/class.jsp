<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.regex.Matcher" %>
<%@ page import="iPodia.Defaults" %>
<%@ include file="/WEB-INF/Database.jsp" %>
<%@ include file="/WEB-INF/Session.jsp" %>
<%
if (!user.isAuthenticated() || !user.isAdmin()) {
	response.sendRedirect(request.getContextPath() + "/");
	return;
}

String classId = request.getParameter("id");
if (Defaults.isEmpty(classId)) {
	response.sendRedirect(request.getContextPath() + "/");
	return;
}

String className = user.getClassName(classId);
if (Defaults.isEmpty(className)) {
	response.sendRedirect(request.getContextPath() + "/");
	return;
}

int numWeeks = 0;
ResultSet rs = dbConnection.prepareStatement("Select * From class_" + classId).executeQuery();
while (rs.next()) {
	String id = rs.getString("id");
	Matcher m = Defaults.WEEK_PATTERN.matcher(id);
	if (m.find()) {
		// group 2 represents the week number in the id
		numWeeks = Math.max(numWeeks, Integer.valueOf(m.group(2)));
	}
}
%>
<jsp:include page="/WEB-INF/templates/head.jsp">
	<jsp:param name="pagetype" value="admin"/>
	<jsp:param name="title" value="<%= className %>"/>
</jsp:include>
<jsp:include page="/WEB-INF/templates/header.jsp">
	<jsp:param name="username" value="${user.getName()}"/>
</jsp:include>
		<main>
			<h1><%= className %></h1>
			<ul>
<% for (int i = 1; i <= numWeeks; ++i) { %>
				<li><a href="${pageContext.request.contextPath}/admin/week?id=${param.id}&num=<%= Integer.toString(i) %>">Week <%= i %></a></li>
<% } %>
			</ul>
			<button>Add week</button>
		</main>
		<script>
			(function() {
				function makeNewWeek(event) {
					var listOfWeeks = this.previousElementSibling;
					var numWeeks = listOfWeeks.children.length + 1;
					var week = listOfWeeks.appendChild(document.createElement("li"));
					var link = week.appendChild(document.createElement("a"));
					link.href = "${pageContext.request.contextPath}/admin/week?id=${param.id}&num=" + numWeeks;
					link.textContent = "Week " + numWeeks;
				}
				Array.prototype.forEach.call(document.querySelectorAll("button"), function(item) {
					item.addEventListener("click", makeNewWeek);
				});
			})();
		</script>
<jsp:include page="/WEB-INF/templates/footer.jsp"/>
