<%@ include file="WEB-INF/Database.jsp" %>
<%
boolean invalidCredentials = false;
if (request != null) {
	String email = request.getParameter("email");
	String password = request.getParameter("password");
	if (email != null && password != null) {
		PreparedStatement ps = null;

		// Check students
		ps = dbConnection.prepareStatement("SELECT * FROM users WHERE email = ?");
		ps.setString(1, email);
		ResultSet users = ps.executeQuery();
		while (users.next()) {
			if (password.equals(users.getString("password"))) {
				response.sendRedirect(request.getContextPath() + "/student");
				return;
			}
		}

		// Check admins
		ps = dbConnection.prepareStatement("SELECT * FROM admins WHERE email = ?");
		ps.setString(1, email);
		ResultSet admins = ps.executeQuery();
		while (admins.next()) {
			if (password.equals(admins.getString("password"))) {
				response.sendRedirect(request.getContextPath() + "/admin");
				return;
			}
		}

		// User not found
		invalidCredentials = true;
	}
}
%>
<jsp:include page="/WEB-INF/templates/header.jsp">
	<jsp:param name="pagetype" value="home"/>
	<jsp:param name="title" value="Home"/>
</jsp:include>
		<main>
			<h1>iPodia</h1>
<% if (invalidCredentials) { %>
			<h4>Email/Password Invalid</h4>
<% } %>
			<form method="post">
				<div>
					<label for="email">Email</label>
					<input type="text" name="email" id="email">
				</div>
				<div>
					<label for="password">Password</label>
					<input type="password" name="password" id="password">
				</div>
				<input type="submit" value="Submit">
			</form>
		</main>
<jsp:include page="/WEB-INF/templates/footer.jsp"/>
