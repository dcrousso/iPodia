<%@page import="iPodia.MD5Encryption"%>
<%@ include file="/WEB-INF/Session.jsp" %>
<%@ include file="/WEB-INF/Database.jsp" %>
<%
User user = ((User) session.getAttribute("user"));
if (user.getType() != null) {
	response.sendRedirect(request.getContextPath() + "/" + user.getType());
	return;
}

boolean invalidCredentials = false;
if (request != null) {
	String email = request.getParameter("email");
	String password = request.getParameter("password");
	String encryptedPassword = MD5Encryption.encrypt(password);
	if (email != null && password != null) {
		PreparedStatement ps = null;

		// Check students
		ps = dbConnection.prepareStatement("SELECT * FROM students WHERE email = ?");
		ps.setString(1, email);
		ResultSet students = ps.executeQuery();
		while (students.next()) {
			if (!encryptedPassword.equals(students.getString("password")))
				continue;

			user.setId(students.getInt("id"));
			user.setType(User.Student);
			user.setUsername(email);
			user.setName(students.getString("firstName"), students.getString("lastName"));
			user.setUniversity(students.getString("university"));
			user.setClasses(students.getString("classes"));

			response.sendRedirect(request.getContextPath() + "/student");
			return;
		}

		// Check admins
		ps = dbConnection.prepareStatement("SELECT * FROM admins WHERE email = ?");
		ps.setString(1, email);
		ResultSet admins = ps.executeQuery();
		while (admins.next()) {
			if (!password.equals(admins.getString("password")))
				continue;

			user.setId(admins.getInt("id"));
			user.setType(User.Admin);
			user.setUsername(email);
			user.setName(admins.getString("firstName"), admins.getString("lastName"));
			user.setUniversity(admins.getString("university"));
			user.setClasses(admins.getString("classes"));

			response.sendRedirect(request.getContextPath() + "/admin");
			return;
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
