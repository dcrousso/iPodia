<%@ page import="iPodia.Defaults"%>
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
		ResultSet results;

		// Check admins
		ps = dbConnection.prepareStatement("SELECT * FROM admins WHERE email = ?");
		ps.setString(1, email);
		results = ps.executeQuery();
		while (results.next()) {
			if (!encryptedPassword.equals(results.getString("password")))
				continue;

			user.setType(User.Type.Admin);
			user.setEmail(email);
			user.setName(results.getString("firstName"), results.getString("lastName"));
			user.setUniversity(results.getString("university"));
			for (String classId : results.getString("classes").split(Defaults.CSV_REGEXP)) {
				PreparedStatement cl = dbConnection.prepareStatement("SELECT * FROM classListing WHERE id = ?");
				cl.setString(1, classId);
				ResultSet classes = cl.executeQuery();
				while (classes.next())
					user.addClass(classId, classes.getString("name"));
			}

			ps.close();
			response.sendRedirect(request.getContextPath() + "/admin");
			return;
		}
		ps.close();

		// Check students
		ps = dbConnection.prepareStatement("SELECT * FROM students WHERE email = ?");
		ps.setString(1, email);
		results = ps.executeQuery();
		while (results.next()) {
			if (!encryptedPassword.equals(results.getString("password")))
				continue;

			user.setType(User.Type.Student);
			user.setEmail(email);
			user.setName(results.getString("firstName"), results.getString("lastName"));
			user.setUniversity(results.getString("university"));
			for (String classId : results.getString("classes").split(Defaults.CSV_REGEXP)) {
				PreparedStatement cl = dbConnection.prepareStatement("SELECT * FROM classListing WHERE id = ?");
				cl.setString(1, classId);
				ResultSet classes = cl.executeQuery();
				while (classes.next())
					user.addClass(classId, classes.getString("name"));
			}

			ps.close();
			response.sendRedirect(request.getContextPath() + "/student");
			return;
		}
		ps.close();
		
		// Check registrars
		ps = dbConnection.prepareStatement("SELECT * FROM registrars WHERE email = ?");
		ps.setString(1, email);
		results = ps.executeQuery();
		while (results.next()) {
			if (!encryptedPassword.equals(results.getString("password")))
				continue;

			user.setType(User.Type.Registrar);
			user.setEmail(email);
			user.setName(results.getString("firstName"), results.getString("lastName"));
			
			ResultSet classes = dbConnection.prepareStatement("SELECT * FROM classListing").executeQuery();
			while (classes.next())
				user.addClass(classes.getInt("id"), classes.getString("name"));

			ps.close();
			response.sendRedirect(request.getContextPath() + "/registrar");
			return;
		}
		ps.close();

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
				<section>
					<label for="email">Email</label>
					<input type="text" name="email" id="email">
				</section>
				<section>
					<label for="password">Password</label>
					<input type="password" name="password" id="password">
				</section>
				<button>Submit</button>
			</form>
		</main>
<jsp:include page="/WEB-INF/templates/footer.jsp"/>
