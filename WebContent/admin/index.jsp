<%@ include file="/WEB-INF/Session.jsp"%>
<%@ include file="/WEB-INF/Database.jsp"%>
<%@ page import="iPodia.User"%>
<%
	if (!user.isAuthenticated() || !user.isAdmin()) {
		response.sendRedirect(request.getContextPath() + "/");
		return;
	}
	if (request != null) {
		String newClassName = request.getParameter("newClassName");
		if (newClassName != null) {
			PreparedStatement ps;

			String query = "INSERT into classIndex(name, contact) values (?, ?)";
			ps = dbConnection.prepareStatement(query);
			ps.setString(1, newClassName);
			ps.setString(2, user.getEmail());
			ps.executeUpdate();

			// Get new class id
			int newClassID = -1;
			ps = dbConnection.prepareStatement("SELECT * FROM classIndex WHERE name = ?");
			ps.setString(1, newClassName);
			ResultSet classes = ps.executeQuery();
			while (classes.next() && newClassID < 0) {
				newClassID = classes.getInt("id");
				user.addClass(newClassName);
			}

			if (newClassID >= 0) {
				// Loop through students listed and add each email as a column
				String studentsColumns = "";

				ps = dbConnection.prepareStatement("CREATE TABLE IF NOT EXISTS " + "class_" + newClassID
						+ "_quiz (" + "id varchar(255)," + "question varchar(255)," + "answer1 varchar(255),"
						+ "answer2 varchar(255)," + "answer3 varchar(255)," + "answer4 varchar(255),"
						+ "answer5 varchar(255)," + "correctAnswer varchar(255)," + "dueDate timestamp,"
						+ "topic varchar(255)," + studentsColumns + "PRIMARY KEY (id)" + ");");
				ps.execute();

				ps = dbConnection.prepareStatement("CREATE TABLE IF NOT EXISTS " + "class_" + newClassID
						+ "_painIndex (" + "id varchar(255)," + "dueDate timestamp," + "topic varchar(255),"
						+ studentsColumns + "PRIMARY KEY (id)" + ");");
				ps.execute();
			}
		}
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
			<form method="post">
				Create a new class: <input type="text" name="newClassName"> <input type="submit" value="Submit" />
			</form>
		</main>
<jsp:include page="/WEB-INF/templates/footer.jsp" />
