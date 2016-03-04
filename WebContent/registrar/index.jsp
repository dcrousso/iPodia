<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="iPodia.Defaults" %>
<%@ include file="/WEB-INF/Session.jsp" %>
<%
if (!user.isAuthenticated() || !user.isRegistrar()) {
	response.sendRedirect(request.getContextPath() + "/");
	return;
}

String newClass = request.getParameter("newClass");
if (!Defaults.isEmpty(newClass)) {
	PreparedStatement ps;

	ps = Defaults.getDBConnection().prepareStatement("INSERT into classListing (name) values (?)");
	ps.setString(1, newClass);
	ps.executeUpdate();
	ps.close();

	ps = Defaults.getDBConnection().prepareStatement("SELECT * FROM classListing WHERE name = ?");
	ps.setString(1, newClass);
	ResultSet classes = ps.executeQuery();
	while (classes.next()) {
		if (!classes.getString("name").equals(newClass))
			continue;

		int newClassId = classes.getInt("id");
		user.addClass(newClassId, newClass);
		Defaults.execute("CREATE TABLE IF NOT EXISTS " + "class_" + newClassId
			+ " ("
				+ "id varchar(255),"
				+ "question varchar(255),"
				+ "answerA varchar(255),"
				+ "answerB varchar(255),"
				+ "answerC varchar(255),"
				+ "answerD varchar(255),"
				+ "answerE varchar(255),"
				+ "correctAnswer varchar(255),"
				+ "topic varchar(255),"
				+ "PRIMARY KEY (id)"
			+ ");"
		);

		Defaults.execute("CREATE TABLE IF NOT EXISTS " + "class_" + newClassId + "_matching"
			+ " ("
				+ "id varchar(255),"
				+ "PRIMARY KEY (id)"
			+ ");"
		);

		classes.close();
		ps.close();

		response.sendRedirect(request.getContextPath() + "/registrar/class?id=" + newClassId);
		return;
	}

	classes.close();
	ps.close();
	Defaults.closeDBConnection();
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
			<h3><a href="${pageContext.request.contextPath}/registrar/user" title="Create User">Create User</a></h3>
			<h4>Existing Classes:</h4>
			<ul>
<% for (HashMap.Entry<Integer, String> entry : user.getClasses().entrySet()) { %>
				<li><a href="${pageContext.request.contextPath}/registrar/class?id=<%= entry.getKey() %>"><%= entry.getValue() %></a></li>
<% } %>
			</ul>
			<form method="post">
				<label for="newClass">Create Class:</label>
				<input type="text" id="newClass" name="newClass" placeholder="Class Name" required>
				<button>Submit</button>
			</form>
		</main>
<jsp:include page="/WEB-INF/templates/footer.jsp"/>
