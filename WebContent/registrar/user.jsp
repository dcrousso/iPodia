<%@ page import="iPodia.Defaults" %>
<%@ include file="/WEB-INF/Database.jsp" %>
<%@ include file="/WEB-INF/Session.jsp" %>
<%
if (!user.isAuthenticated() || !user.isRegistrar()) {
	response.sendRedirect(request.getContextPath() + "/");
	return;
}

String type = request.getParameter("type");
String email = request.getParameter("email");
String firstName = request.getParameter("firstName");
String lastName = request.getParameter("lastName");
String university = request.getParameter("university");
if (!Defaults.isEmpty(type) && !Defaults.isEmpty(email) && !Defaults.isEmpty(firstName) && !Defaults.isEmpty(lastName) && !Defaults.isEmpty(university)) {
	PreparedStatement ps = dbConnection.prepareStatement("INSERT INTO users (email, level, firstName, lastName, password, university, classes) VALUES (?, ?, ?, ?, ?, ?, ?)");
	ps.setString(1, email);
	ps.setString(2, type.toLowerCase());
	ps.setString(3, firstName);
	ps.setString(4, lastName);
	ps.setString(5, Defaults.INITIAL_PASSWORD);
	ps.setString(6, university);
	ps.setString(7, "");
	ps.executeUpdate();

	response.sendRedirect(request.getContextPath() + user.getHome());
}
%>
<jsp:include page="/WEB-INF/templates/head.jsp">
	<jsp:param name="pagetype" value="registrar"/>
	<jsp:param name="title" value="Create User"/>
</jsp:include>
<jsp:include page="/WEB-INF/templates/header.jsp">
	<jsp:param name="username" value="${user.getName()}"/>
</jsp:include>
		<main>
			<h1>Create User</h1>
			<form method="post">
				<section>
					<select name="type" id="type">
						<option selected>Student</option>
						<option>Admin</option>
						<option>Registrar</option>
					</select>
				</section>
				<section>
					<label for="email">Email</label>
					<input type="email" name="email" id="email" required>
				</section>
				<section>
					<label for="firstName">First Name</label>
					<input type="text" name="firstName" id="firstName" required>
					<label for="lastName">Last Name</label>
					<input type="text" name="lastName" id="lastName" required>
				</section>
				<section>
					<label for="university">University</label>
					<input type="text" name="university" id="university" required>
				</section>
				<button>Submit</button>
			</form>
		</main>
<jsp:include page="/WEB-INF/templates/footer.jsp"/>
