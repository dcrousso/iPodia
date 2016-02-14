<%@ page import="java.util.HashMap" %>
<%@ include file="/WEB-INF/Session.jsp" %>
<%@ include file="/WEB-INF/Database.jsp"%>
<%
if (!user.isAuthenticated() || !user.isRegistrar()) {
	response.sendRedirect(request.getContextPath() + "/");
	return;
}
if (request != null) {
	String newClassName = request.getParameter("newClassName");
	if (newClassName != null) {
		PreparedStatement ps;

		String query = "INSERT into classListing (name) values (?)";
		ps = dbConnection.prepareStatement(query);
		ps.setString(1, newClassName);
		ps.executeUpdate();

		// Get new class id
		ps = dbConnection.prepareStatement("SELECT * FROM classListing WHERE name = ?");
		ps.setString(1, newClassName);
		ResultSet classes = ps.executeQuery();
		while (classes.next()) {
			if (!classes.getString("name").equals(newClassName))
				continue;

			int newClassId = classes.getInt("id");
			user.addClass(newClassId, newClassName);
			
			ps = dbConnection.prepareStatement("CREATE TABLE IF NOT EXISTS " + "class_" + newClassId
				+ " ("
					+ "id varchar(255),"
					+ "question varchar(255),"
					+ "answer1 varchar(255),"
					+ "answer2 varchar(255),"
					+ "answer3 varchar(255),"
					+ "answer4 varchar(255),"
					+ "answer5 varchar(255),"
					+ "correctAnswer varchar(255),"
					+ "dueDate timestamp,"
					+ "topic varchar(255),"
					+ "PRIMARY KEY (id)"
				+ ");"
			);
			ps.execute();
			
			response.sendRedirect(request.getContextPath() + "/registrar/class?id=" + newClassId);
			return;
		}
	}
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
<% for (HashMap.Entry<Integer, String> entry : user.getClasses().entrySet()) { %>
				<li><a href="${pageContext.request.contextPath}/registrar/class?id=<%= entry.getKey() %>"><%= entry.getValue() %></a></li>
<% } %>
			</ul>
			<form method="post">
				Create a new class: <input type="text" name="newClassName">
				<button>Submit</button>
			</form>
		</main>
<jsp:include page="/WEB-INF/templates/footer.jsp"/>