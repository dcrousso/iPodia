<%@ page import="iPodia.MD5Encryption"%>
<%@ include file="/WEB-INF/Session.jsp" %>
<%@ include file="/WEB-INF/Database.jsp" %>
<%
if (user.isAuthenticated()) {
	response.sendRedirect(request.getContextPath() + user.getHome());
	return;
}

boolean invalidCredentials = false;
if (request != null) {
	String email = request.getParameter("email");
	String password = request.getParameter("password");
	if (email != null && password != null) {
		String encryptedPassword = MD5Encryption.encrypt(password);
		PreparedStatement ps = null;

		// Check admins
		ps = dbConnection.prepareStatement("SELECT * FROM admins WHERE email = ?");
		ps.setString(1, email);
		ResultSet admins = ps.executeQuery();
		while (admins.next()) {
			if (!encryptedPassword.equals(admins.getString("password")))
				continue;

			user.setType(User.Type.Admin);
			user.setEmail(email);
			user.setName(admins.getString("firstName"), admins.getString("lastName"));
			user.setUniversity(admins.getString("university"));
			user.addClasses(admins.getString("classes"));

			response.sendRedirect(request.getContextPath() + "/admin");
			return;
		}

		// Check students
		ps = dbConnection.prepareStatement("SELECT * FROM students WHERE email = ?");
		ps.setString(1, email);
		ResultSet students = ps.executeQuery();
		while (students.next()) {
			if (!encryptedPassword.equals(students.getString("password")))
				continue;

			user.setType(User.Type.Student);
			user.setEmail(email);
			user.setName(students.getString("firstName"), students.getString("lastName"));
			user.setUniversity(students.getString("university"));
			user.addClasses(students.getString("classes"));

			response.sendRedirect(request.getContextPath() + "/student");
			return;
		}
		
		// Check registrars
		ps = dbConnection.prepareStatement("SELECT * FROM registrars WHERE email = ?");
		ps.setString(1, email);
		ResultSet registrars = ps.executeQuery();
		while (registrars.next()) {
			if (!encryptedPassword.equals(registrars.getString("password")))
				continue;

			user.setType(User.Type.Registrar);
			user.setEmail(email);
			user.setName(registrars.getString("firstName"), registrars.getString("lastName"));
			
			ResultSet classes  = dbConnection.prepareStatement("SELECT * FROM classListing").executeQuery();
			while (classes.next())
				user.addClass(classes.getString("id"));

			response.sendRedirect(request.getContextPath() + "/registrar");
			return;
		}

		// User not found
		invalidCredentials = true;
	}
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
