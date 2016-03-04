<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.util.HashSet" %>
<%@ page import="iPodia.Defaults" %>
<%@ page import="iPodia.MD5Encryption" %>
<%@ include file="/WEB-INF/Session.jsp" %>
<%
if (user.isAuthenticated()) {
	response.sendRedirect(request.getContextPath() + user.getHome());
	return;
}

boolean invalidCredentials = false;
String email = request.getParameter("email");
String password = request.getParameter("password");
if (!Defaults.isEmpty(email) && !Defaults.isEmpty(password)) {
	String encryptedPassword = MD5Encryption.encrypt(password);
	PreparedStatement ps = Defaults.getDBConnection().prepareStatement("SELECT * FROM users WHERE email = ?");
	ps.setString(1, email);
	ResultSet results = ps.executeQuery();
	while (results.next()) {
		if (!encryptedPassword.equals(results.getString("password")))
			continue;

		user.initializeFromResultSet(results);
		HashSet<String> classes = Defaults.arrayToHashSet(results.getString("classes").split(Defaults.CSV_REGEXP));

		results.close();
		ps.close();

		ps = Defaults.getDBConnection().prepareStatement("SELECT * FROM classListing");
		results = ps.executeQuery();
		while (results.next()) {
			String classId = results.getString("id");
			if (user.isRegistrar() || classes.contains(classId))
				user.addClass(classId, results.getString("name"));
		}

		results.close();
		ps.close();
		Defaults.closeDBConnection();

		response.sendRedirect(request.getContextPath() + user.getHome());
		return;
	}

	results.close();
	ps.close();
	Defaults.closeDBConnection();

	// User not found
	invalidCredentials = true;
}
%>
<jsp:include page="/WEB-INF/templates/head.jsp">
	<jsp:param name="pagetype" value="home"/>
	<jsp:param name="title" value="Home"/>
</jsp:include>
		<main>
			<h1>iPodia</h1>
<% if (invalidCredentials) { %>
			<h4>Email/Password Invalid</h4>
<% } %>
			<form method="post">
				<label for="email">Email</label>
				<input type="email" name="email" id="email" autofocus required>
				<label for="password">Password</label>
				<input type="password" name="password" id="password" required>
				<button>Submit</button>
			</form>
		</main>
<jsp:include page="/WEB-INF/templates/footer.jsp"/>
