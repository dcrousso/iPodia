<%@ page import="java.sql.DriverManager" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="java.sql.Connection" %>
<%
if (request != null) {
	String dbDriver = "com.mysql.jdbc.Driver";
	String dbURL = "jdbc:mysql://localhost:3306/ipodia";
	String dbUsername = "root";
	String dbPassword = "";
	Class.forName(dbDriver);
	Connection dbConnection = DriverManager.getConnection(dbURL, dbUsername, dbPassword);

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
		System.out.println("Invalid Email or Password");
	}
}
%>
<jsp:include page="/WEB-INF/header.jsp">
	<jsp:param name="pagetype" value="home"/>
	<jsp:param name="title" value="Home"/>
</jsp:include>
		<main>
			<h1>iPodia</h1>
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
<jsp:include page="/WEB-INF/footer.jsp"/>
